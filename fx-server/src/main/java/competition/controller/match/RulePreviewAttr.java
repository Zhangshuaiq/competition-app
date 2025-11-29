package competition.controller.match;


import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.controls.GroupedComboBox;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RulePreviewAttr {

    @FXML
    private TextField backgroundPath;

    @FXML
    private CheckBox deleteChoice;

    @FXML
    private ColorPicker colorInput, backColorInput;

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
    private ConfigsMapper configsMapper;
    
    private static int configId = 5;
    
    
    @Resource
    private ControlPane controlPane;

    @FXML
    public void initialize(){
        textAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        Map<String, List<String>> stringListMap = FontMap.fontNames();
        stringListMap.forEach((key, value) -> {
            fontSelector.addGroup(key, value);
        });
        previewData();
    }
    
    private void previewData(){
        final Configs configs = configsMapper.selectById(configId);
        spaceInput.setText("0");
        final String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
            xInput.setText(huanjieArr[1]);
            yInput.setText(huanjieArr[2]);
            widthInput.setText(huanjieArr[3]);
            heightInput.setText(huanjieArr[4]);
            textAlignSelector.setValue(AlignmentUtil.alignNames().get(0));
            //  fontSelector
        }

        final String details = configs.getDetails();
        if (StringUtils.isEmpty(details)) {
            // 获取默认字体
            fontSelector.setSelectedValue(FontMap.defaultFontName());
            fontSize.setText(12 + "");
            iticChoice.setSelected(false);
            deleteChoice.setSelected(false);
            spaceInput.setText("0");
        } else {
            final String[] detailArr = details.split(ConfigSpliter.SPLITER);
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
                backColorInput.setValue(Color.valueOf(detailArr[15].replace("0x", "#")));
            }
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
        final String fontFamily = fontSelector.getSelectedValue();
        String fsize = fontSize.getText();
        final boolean delete = deleteChoice.isSelected();
        final boolean bold = boldChoice.isSelected();
        final boolean itic = iticChoice.isSelected();
        final boolean underlne = underlineChoice.isSelected();
        final String alignValue = textAlignSelector.getValue();
        final String rowSpace = spaceInput.getText();

        final Configs configs = configsMapper.selectById(configId);
        final String config = configs.getConfig();
        final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
        String huanjieVal = huanjieArr[0] + ConfigSpliter.SPLITER
                + x + ConfigSpliter.SPLITER
                + y + ConfigSpliter.SPLITER
                + width + ConfigSpliter.SPLITER
                + height ;

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
                + backColorInput.getValue().toString()
                ;
        configs.setConfig(huanjieVal);
        configs.setDetails(detail);
        configsMapper.updateById(configs);
        PreviewPane previewPane = controlPane.getPreviewPane();
        if (previewPane != null) {
            previewPane.reload();
        }
    }
}
