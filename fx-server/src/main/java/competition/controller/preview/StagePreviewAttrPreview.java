package competition.controller.preview;


import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.AlignmentUtil;
import competition.util.ConfigSpliter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@Component
public class StagePreviewAttrPreview {

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
    private ConfigsMapper configsMapper;
    
    private static int configId;
    
    private static PreviewPane reloadPane;

    public static void setReloadPane(PreviewPane reloadPane) {
        StagePreviewAttrPreview.reloadPane = reloadPane;
    }

    public static void setConfigId(int configId) {
        StagePreviewAttrPreview.configId = configId;
    }

    @FXML
    public void initialize(){
        textAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        fontSelector.setItems(FXCollections.observableList(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())));
        previewData();
    }
    
    private void previewData(){
        final Configs configs = configsMapper.selectById(configId);
        final String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
            //  hjbh.setValue(Color.valueOf(huanjieArr[0]));
            //            bhbj.setValue(Color.valueOf(huanjieArr[1]));
            //            hjmc.setValue(Color.valueOf(huanjieArr[2]));
            //            mcbj.setValue(Color.valueOf(huanjieArr[3]));
            //            hjx.setText(huanjieArr[4]);
            //            hjy.setText(huanjieArr[5]);
            //            hjk.setText(huanjieArr[6]);
            //            hjg.setText(huanjieArr[7]);
            //            xscjan.setSelected(Boolean.valueOf(huanjieArr[8]));
            //            xshjlb.setSelected(Boolean.valueOf(huanjieArr[9]));
            widthInput.setText(huanjieArr[6]);
            heightInput.setText(huanjieArr[7]);
            xInput.setText(huanjieArr[4]);
            yInput.setText(huanjieArr[5]);
            textAlignSelector.setValue(AlignmentUtil.alignNames().get(0));
          //  fontSelector
        }
        
        final String details = configs.getDetails();
        if (StringUtils.isEmpty(details)) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // 获取默认字体
            Font defaultFont = new JLabel().getFont();
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

        final Configs configs = configsMapper.selectById(configId);
        final String config = configs.getConfig();
        final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
        String huanjieVal = huanjieArr[0] + ConfigSpliter.SPLITER
                + huanjieArr[1]  + ConfigSpliter.SPLITER
                + huanjieArr[2]  + ConfigSpliter.SPLITER
                + huanjieArr[3]  + ConfigSpliter.SPLITER
                + x + ConfigSpliter.SPLITER
                + y + ConfigSpliter.SPLITER
                + width + ConfigSpliter.SPLITER
                + height + ConfigSpliter.SPLITER
                + huanjieArr[8]  + ConfigSpliter.SPLITER
                + huanjieArr[9] ;
        
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
        configs.setConfig(huanjieVal);
        configs.setDetails(detail);
        configsMapper.updateById(configs);

        Stage stage = (Stage) ((Node)xInput).getScene().getWindow();
        stage.close();
        reloadPane.reload();
    }
}
