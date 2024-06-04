package competition.controller.match;


import competition.controller.ControlPane;
import competition.controller.preview.StageRulePreviewPreview;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.AlignmentUtil;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import competition.util.PreviewStack;
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



@Component
public class StageRulePreviewAttr {

    @FXML
    private TextField headBackgroundPath;

    @FXML
    private CheckBox headDeleteChoice;

    @FXML
    private ColorPicker headColorInput;

    @FXML
    private ComboBox<String> headTextAlignSelector;

    @FXML
    private TextField headYInput;

    @FXML
    private TextField headWidthInput;

    @FXML
    private ComboBox<String> headFontSelector;

    @FXML
    private CheckBox headIticChoice;

    @FXML
    private TextField headXInput;

    @FXML
    private TextField headFontSize;

    @FXML
    private CheckBox headUnderlineChoice;

    @FXML
    private TextField headHeightInput;

    @FXML
    private CheckBox headBoldChoice;

    @FXML
    private CheckBox headBackgroundChoice;

    @FXML
    private TextField headSpaceInput;
    

    @FXML
    private TextField backgroundPath;

    @FXML
    private CheckBox deleteChoice;

    @FXML
    private ColorPicker colorInput;
    
    @FXML
    private ColorPicker fontColorInput;
    
    @FXML
    private ColorPicker headFontColorInput;

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
    private ConfigsMapper configsMapper;
    
    @Resource
    private StagesMapper stagesMapper;
    
    private static int configId = 5;
    
    private static int stageId;
    
    @Resource
    private ControlPane controlPane;
    


    public static void setStageId(int stageId) {
        StageRulePreviewAttr.stageId = stageId;
    }

    @FXML
    public void initialize(){
        textAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));

        headTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        headFontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        previewData();
        previewHeadData();
    }

    private void previewHeadData(){

        Stages stages = stagesMapper.selectById(stageId);

        final String details = stages.getTitleRuleStyle();
        if (StringUtils.isEmpty(details)) {

            Font defaultFont = headSpaceInput.getFont();
            headFontSelector.setValue(defaultFont.getFamily());
            headFontSize.setText(defaultFont.getSize() + "");
            headIticChoice.setSelected(false);
            headDeleteChoice.setSelected(false);
            headSpaceInput.setText("0");
            headXInput.setText(StageRulePreviewPreview.TITLE_X+ "");
            headYInput.setText(StageRulePreviewPreview.TITLE_Y + "");
            headWidthInput.setText(StageRulePreviewPreview.TITLE_W + "");
            headHeightInput.setText(StageRulePreviewPreview.TITLE_H + "");
            headColorInput.setValue(Color.YELLOW);
            headTextAlignSelector.setValue("居中对齐");
            headSpaceInput.setText("0");
            headFontColorInput.setValue(Color.BLACK);
            
        } else {
            final String[] detailArr = details.split(ConfigSpliter.SPLITER);

            headXInput.setText(detailArr[0]);
            headYInput.setText(detailArr[1]);
            headWidthInput.setText(detailArr[2]);
            headHeightInput.setText(detailArr[3]);
            headBackgroundChoice.setSelected(Boolean.valueOf(detailArr[4]));
            headColorInput.setValue(Color.valueOf(detailArr[5].replace("0x", "#")));
            headBackgroundPath.setText(detailArr[6]);
            headFontSelector.setValue(detailArr[7]);
            headFontSize.setText(Double.parseDouble(detailArr[8]) + "");
            headDeleteChoice.setSelected(Boolean.valueOf(detailArr[9]));
            headBoldChoice.setSelected(Boolean.valueOf(detailArr[10]));
            headIticChoice.setSelected(Boolean.valueOf(detailArr[11]));
            headUnderlineChoice.setSelected(Boolean.valueOf(detailArr[12]));
            headTextAlignSelector.setValue(detailArr[13]);
            headSpaceInput.setText(detailArr[14]);
            if (detailArr.length >= 16) {
                headFontColorInput.setValue(Color.valueOf(detailArr[15]));
            }
            
        }

    }


    private void previewData(){
        final Configs configs = configsMapper.selectById(configId);
        final String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
            xInput.setText(huanjieArr[1]);
            yInput.setText(huanjieArr[2]);
            widthInput.setText(huanjieArr[3]);
            heightInput.setText(huanjieArr[4]);
            textAlignSelector.setValue(AlignmentUtil.alignNames().get(0));
            fontColorInput.setValue(Color.valueOf(huanjieArr[0]));
            //  fontSelector
        }

        final String details = configs.getDetails();
        if (StringUtils.isEmpty(details)) {
            // 获取默认字体
            Font defaultFont = xInput.getFont();
            fontSelector.setValue(defaultFont.getFamily());
            fontSize.setText(defaultFont.getSize() + "");
            iticChoice.setSelected(false);
            deleteChoice.setSelected(false);
            spaceInput.setText("0");
        } else {
            final String[] detailArr = details.split(ConfigSpliter.SPLITER);
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
            if (detailArr.length >= 16) {
                fontColorInput.setValue(Color.valueOf(detailArr[15]));
            }
        }

    }

    public void closeStage(MouseEvent event) {
        Stage stage = (Stage) ((Node)(event.getTarget())).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void saveHeadConfig(){
        String x = headXInput.getText();
        String y = headYInput.getText();
        String width = headWidthInput.getText();
        String height = headHeightInput.getText();
        boolean fillBackground = headBackgroundChoice.isSelected();
        final String backgroundColor = headColorInput.getValue().toString();
        String backgroundImage = headBackgroundPath.getText();
        final String fontFamily = headFontSelector.getValue();
        String fsize = headFontSize.getText();
        final boolean delete = headDeleteChoice.isSelected();
        final boolean bold = headBoldChoice.isSelected();
        final boolean itic = headIticChoice.isSelected();
        final boolean underlne = headUnderlineChoice.isSelected();
        final String alignValue = headTextAlignSelector.getValue();
        final String rowSpace = headSpaceInput.getText();
        final String fontColor = headFontColorInput.getValue().toString();

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

        Stages stages = stagesMapper.selectById(stageId);
        stages.setTitleRuleStyle(detail);
        stagesMapper.updateById(stages);
        PreviewStack.reload();
        controlPane.getPreviewPane().reload();
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
        final String fontColor = fontColorInput.getValue().toString();
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
                + fontColor
                ;
        configs.setConfig(huanjieVal);
        configs.setDetails(detail);
        configsMapper.updateById(configs);
        PreviewStack.reload();
        controlPane.getPreviewPane().reload();
    }
}
