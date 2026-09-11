package competition.controller.match;


import competition.controls.GroupedComboBox;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

@Component

public class StageNamePreviewAttr {

    @FXML
    private TextField backgroundPath;

    @FXML
    private CheckBox deleteChoice;

    @FXML
    private ColorPicker colorInput, fontColorInput;

    @FXML
    private ComboBox<String> textAlignSelector;

    @FXML
    private TextField yInput;

    @FXML
    private TextField widthInput;

    @FXML
    private GroupedComboBox<String> fontSelector;

    @FXML
    private CheckBox iticChoice;

    @FXML
    private TextField xInput;

    @FXML
    private TextField fontSize;

    @FXML
    private CheckBox underlineChoice;

    @FXML
    private TextField heightInput;

    @FXML
    private CheckBox boldChoice;
    
    @FXML
    private CheckBox backgroundChoice;

    @FXML
    private TextField spaceInput;
    
    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;
    
    
    private static int stageId;
    

    public static void setConfigId(int stageId) {
        StageNamePreviewAttr.stageId = stageId;
    }

    @FXML
    public void initialize(){
        textAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));

        FontMap.fontNames().forEach((key, value) -> {
            fontSelector.addGroup(key, value);
        });
        previewData();
    }


    @FXML
    public void selectImg(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file!= null) {
            backgroundPath.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void cancelImg(){
        backgroundPath.setText("");
    }
    
    private void previewData(){
        final Stages stage = stagesMapper.selectById(stageId);
        final String config = stage.getStyle();
        String[] detailArr = ConfigSpliter.split("750%%400%%400%%150%%false%%0xffffffff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0");
        if (!StringUtils.isEmpty(config)) {
            detailArr = ConfigSpliter.split(config);
        }
        xInput.setText(detailArr[0]);
        yInput.setText(detailArr[1]);
        widthInput.setText(detailArr[2]);
        heightInput.setText(detailArr[3]);
        backgroundChoice.setSelected(Boolean.valueOf(detailArr[4]));
        colorInput.setValue(Color.valueOf(detailArr[5].replace("0x", "#")));
        backgroundPath.setText(detailArr[6]);
        fontSelector.setSelectedValue(detailArr[7]);
        fontSize.setText(Integer.parseInt(detailArr[8]) + "");
        deleteChoice.setSelected(Boolean.valueOf(detailArr[9]));
        boldChoice.setSelected(Boolean.valueOf(detailArr[10]));
        iticChoice.setSelected(Boolean.valueOf(detailArr[11]));
        underlineChoice.setSelected(Boolean.valueOf(detailArr[12]));
        textAlignSelector.setValue(detailArr[13]);
        spaceInput.setText(detailArr[14]);
        if (detailArr.length > 15) {
            fontColorInput.setValue(Color.valueOf(detailArr[15].replace("0x", "#")));
        } else {
            // 读取全局配置
            final Configs stageConfig = configsMapper.selectById(2);
            final String globalConfig = stageConfig.getConfig();
            final String[] huanjieArr = ConfigSpliter.split(globalConfig);
            fontColorInput.setValue(Color.valueOf(huanjieArr[2].replace("0x", "#")));
        }

    }

    public void closeStage(MouseEvent event) {
        Stage stage = (Stage) ((Node)(event.getTarget())).getScene().getWindow();
        stage.close();
    }
    
    
    @FXML
    public void saveConfig(){
        String x = xInput.getText();
        String y = yInput.getText();
        String width = widthInput.getText();
        String height = heightInput.getText();
        boolean fillBackground = backgroundChoice.isSelected();
        final String backgroundColor = colorInput.getValue().toString();
        final String fontColor = fontColorInput.getValue().toString();
        String backgroundImage = backgroundPath.getText();
        final String fontFamily = fontSelector.getSelectedValue();
        String fsize = fontSize.getText();
        final boolean delete = deleteChoice.isSelected();
        final boolean bold = boldChoice.isSelected();
        final boolean itic = iticChoice.isSelected();
        final boolean underlne = underlineChoice.isSelected();
        final String alignValue = textAlignSelector.getValue();
        final String rowSpace = spaceInput.getText();

        Stages stages = stagesMapper.selectById(stageId);
        
        String detail =  x + ConfigSpliter.SPLITER
                + y  + ConfigSpliter.SPLITER
                + width  + ConfigSpliter.SPLITER
                + height  + ConfigSpliter.SPLITER
                + fillBackground + ConfigSpliter.SPLITER
                + backgroundColor + ConfigSpliter.SPLITER
                + backgroundImage + ConfigSpliter.SPLITER
                + fontFamily + ConfigSpliter.SPLITER
                + fsize  + ConfigSpliter.SPLITER
                + delete + ConfigSpliter.SPLITER
                + bold + ConfigSpliter.SPLITER
                + itic + ConfigSpliter.SPLITER
                + underlne + ConfigSpliter.SPLITER
                + alignValue + ConfigSpliter.SPLITER
                + rowSpace + ConfigSpliter.SPLITER
                + fontColor
                ;
        stages.setStyle(detail);
        stagesMapper.updateById(stages);
        PreviewStack.reloadStyle();
    }
}
