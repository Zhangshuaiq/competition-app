package competition.controller.match;


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
    private ColorPicker colorInput;

    @FXML
    private ComboBox<String> textAlignSelector;

    @FXML
    private TextField yInput;

    @FXML
    private TextField widthInput;

    @FXML
    private ComboBox<String> fontSelector;

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
    
    
    
    private static int stageId;
    
    private static ReloadPane reloadPane;

    public static void setReloadPane(ReloadPane reloadPane) {
        StageNamePreviewAttr.reloadPane = reloadPane;
    }

    public static void setConfigId(int stageId) {
        StageNamePreviewAttr.stageId = stageId;
    }

    @FXML
    public void initialize(){
        textAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        previewData();
    }


    @FXML
    public void selectImg(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.IMAGE_DIR));
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
        if (!StringUtils.isEmpty(config)) {
            final String[] detailArr = config.split(ConfigSpliter.SPLITER);
            xInput.setText(detailArr[0]);
            yInput.setText(detailArr[1]);
            widthInput.setText(detailArr[2]);
            heightInput.setText(detailArr[3]);
            backgroundChoice.setSelected(Boolean.valueOf(detailArr[4]));
            colorInput.setValue(Color.valueOf(detailArr[5].replace("0x", "#")));
            backgroundPath.setText(detailArr[6]);
            fontSelector.setValue(detailArr[7]);
            fontSize.setText(Integer.parseInt(detailArr[8]) + "");
            deleteChoice.setSelected(Boolean.valueOf(detailArr[9]));
            boldChoice.setSelected(Boolean.valueOf(detailArr[10]));
            iticChoice.setSelected(Boolean.valueOf(detailArr[11]));
            underlineChoice.setSelected(Boolean.valueOf(detailArr[12]));
            textAlignSelector.setValue(detailArr[13]);
            spaceInput.setText(detailArr[14]);
        } else {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // 获取默认字体
            xInput.setText(ConfigSpliter.DEFAULT_X + "");
            yInput.setText(ConfigSpliter.DEFAULT_Y +"");
            widthInput.setText(ConfigSpliter.DEFAULT_W +"");
            heightInput.setText(ConfigSpliter.DEFAULT_H +"");
            textAlignSelector.setValue(AlignmentUtil.alignNames().get(0));
            Font defaultFont = new JLabel().getFont();
            fontSelector.setValue(defaultFont.getFamily());
            fontSize.setText(defaultFont.getSize() + "");
            iticChoice.setSelected(false);
            deleteChoice.setSelected(false);
            spaceInput.setText("0");
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
        String backgroundImage = backgroundPath.getText();
        final String fontFamily = fontSelector.getValue();
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
                + rowSpace
                ;
        stages.setStyle(detail);
        stagesMapper.updateById(stages);
        if (reloadPane != null) {
            reloadPane.reload();
        }
        PreviewStack.reload();
    }
}
