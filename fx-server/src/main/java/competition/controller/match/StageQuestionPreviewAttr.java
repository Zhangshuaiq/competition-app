package competition.controller.match;

import competition.controller.ControlPane;
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

@Component
public class StageQuestionPreviewAttr {

    @FXML
    private TextField colNumber;

    @FXML
    private TextField nameWidthInput;

    @FXML
    private TextField nameHeightInput;

    @FXML
    private CheckBox nameUnderlineChoice;

    @FXML
    private CheckBox numberBackgroundChoice;

    @FXML
    private GroupedComboBox<String> numberFontSelector;

    @FXML
    private TextField nameXInput;

    @FXML
    private TextField numberXInput;

    @FXML
    private TextField nameBackgroundPath;

    @FXML
    private ColorPicker nameColorInput, fontColorInput;

    @FXML
    private CheckBox nameIticChoice;

    @FXML
    private CheckBox numberDeleteChoice;

    @FXML
    private CheckBox nameDeleteChoice;

    @FXML
    private ColorPicker numberColorInput;

    @FXML
    private ComboBox<String> numberTextAlignSelector;

    @FXML
    private GroupedComboBox<String> nameFontSelector;

    @FXML
    private CheckBox nameBoldChoice;

    @FXML
    private CheckBox numberBoldChoice;

    @FXML
    private TextField numberSpaceInput;

    @FXML
    private TextField numberHeightInput;

    @FXML
    private ComboBox<String> nameTextAlignSelector;

    @FXML
    private TextField numberFontSize;

    @FXML
    private TextField nameSpaceInput;

    @FXML
    private CheckBox numberUnderlineChoice;

    @FXML
    private TextField numberWidthInput;

    @FXML
    private TextField nameYInput;

    @FXML
    private TextField numberBackgroundPath;

    @FXML
    private CheckBox numberIticChoice;

    @FXML
    private TextField nameFontSize;

    @FXML
    private TextField numberYInput;

    @FXML
    private CheckBox nameBackgroundChoice;


    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private StagesMapper stagesMapper;


    @Resource
    private ControlPane controlPane;

    private static Stages currentStage;

    public static void setCurrentStage(Stages currentStage) {
        StageQuestionPreviewAttr.currentStage = currentStage;
    }


    @FXML
    public void selectNameImg() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            nameBackgroundPath.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void cancelNameImg() {
        nameBackgroundPath.setText("");
    }

    @FXML
    public void selectCodeImg() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            numberBackgroundPath.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void cancelCodeImg() {
        numberBackgroundPath.setText("");
    }


    @FXML
    public void initialize() {
        Configs configs = configsMapper.selectById(4);

        FontMap.fontNames().forEach((key, value) -> {
            nameFontSelector.addGroup(key, value);
            numberFontSelector.addGroup(key, value);
        });
        nameTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        numberTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));

        showNameConfig();
        showNumberConfig(configs);
    }

    @FXML
    public void saveNameConfig() {
        String x = nameXInput.getText();
        String y = nameYInput.getText();
        String width = nameWidthInput.getText();
        String height = nameHeightInput.getText();
        boolean fillBackground = nameBackgroundChoice.isSelected();
        final String backgroundColor = nameColorInput.getValue().toString();
        final String fontColor = fontColorInput.getValue().toString();
        String backgroundImage = nameBackgroundPath.getText();
        final String fontFamily = nameFontSelector.getSelectedValue();
        String fsize = nameFontSize.getText();
        final boolean delete = nameDeleteChoice.isSelected();
        final boolean bold = nameBoldChoice.isSelected();
        final boolean itic = nameIticChoice.isSelected();
        final boolean underlne = nameUnderlineChoice.isSelected();
        final String alignValue = nameTextAlignSelector.getValue();
        final String rowSpace = nameSpaceInput.getText();

        final String config = currentStage.getQuestionStyle();
        if (!StringUtils.isEmpty(config)) {
            final String[] nameArr = config.split(ConfigSpliter.DIFFER);
            String detail = x + ConfigSpliter.SPLITER
                    + y + ConfigSpliter.SPLITER
                    + width + ConfigSpliter.SPLITER
                    + height + ConfigSpliter.SPLITER
                    + fillBackground + ConfigSpliter.SPLITER
                    + backgroundColor + ConfigSpliter.SPLITER
                    + backgroundImage + ConfigSpliter.SPLITER
                    + fontFamily + ConfigSpliter.SPLITER
                    + fsize + ConfigSpliter.SPLITER
                    + delete + ConfigSpliter.SPLITER
                    + bold + ConfigSpliter.SPLITER
                    + itic + ConfigSpliter.SPLITER
                    + underlne + ConfigSpliter.SPLITER
                    + alignValue + ConfigSpliter.SPLITER
                    + (StringUtils.isEmpty(nameSpaceInput.getText()) ? "0" : nameSpaceInput.getText()) + ConfigSpliter.SPLITER
                    + fontColor;
            if (nameArr.length > 1) {
                detail = detail + ConfigSpliter.DIFFER + nameArr[1];
            } else {
                detail = detail + ConfigSpliter.DIFFER + ConfigSpliter.EMPTY;
            }
            currentStage.setQuestionStyle(detail);
            stagesMapper.updateById(currentStage);

        } else {
            String detail = x + ConfigSpliter.SPLITER
                    + y + ConfigSpliter.SPLITER
                    + width + ConfigSpliter.SPLITER
                    + height + ConfigSpliter.SPLITER
                    + fillBackground + ConfigSpliter.SPLITER
                    + backgroundColor + ConfigSpliter.SPLITER
                    + backgroundImage + ConfigSpliter.SPLITER
                    + fontFamily + ConfigSpliter.SPLITER
                    + fsize + ConfigSpliter.SPLITER
                    + delete + ConfigSpliter.SPLITER
                    + bold + ConfigSpliter.SPLITER
                    + itic + ConfigSpliter.SPLITER
                    + underlne + ConfigSpliter.SPLITER
                    + alignValue + ConfigSpliter.SPLITER
                    + rowSpace;
            detail = detail + ConfigSpliter.DIFFER + ConfigSpliter.EMPTY;
            currentStage.setQuestionStyle(detail);
            stagesMapper.updateById(currentStage);
        }
        controlPane.reloadStage();
        PreviewStack.reloadStyle();

    }

    private void closeStage() {
        Stage stage = (Stage) (nameXInput.getScene().getWindow());
        stage.close();
    }

    @FXML
    public void closeDialog(MouseEvent event) {
        Stage stage = (Stage) ((Node) (event.getTarget())).getScene().getWindow();
        stage.close();
    }


    @FXML
    public void saveNumberConfig() {
        String x = numberXInput.getText();
        String y = numberYInput.getText();
        String width = numberWidthInput.getText();
        String height = numberHeightInput.getText();
        boolean fillBackground = numberBackgroundChoice.isSelected();
        final String backgroundColor = numberColorInput.getValue().toString();
        String backgroundImage = numberBackgroundPath.getText();
        final String fontFamily = numberFontSelector.getSelectedValue();
        String fsize = numberFontSize.getText();
        final boolean delete = numberDeleteChoice.isSelected();
        final boolean bold = numberBoldChoice.isSelected();
        final boolean itic = numberIticChoice.isSelected();
        final boolean underlne = numberUnderlineChoice.isSelected();
        final String alignValue = numberTextAlignSelector.getValue();
        final String rowSpace = numberSpaceInput.getText();

        final String config = currentStage.getQuestionStyle();
        if (!StringUtils.isEmpty(config)) {
            final String[] nameArr = config.split(ConfigSpliter.DIFFER);
            String detail = x + ConfigSpliter.SPLITER
                    + y + ConfigSpliter.SPLITER
                    + width + ConfigSpliter.SPLITER
                    + height + ConfigSpliter.SPLITER
                    + fillBackground + ConfigSpliter.SPLITER
                    + backgroundColor + ConfigSpliter.SPLITER
                    + backgroundImage + ConfigSpliter.SPLITER
                    + fontFamily + ConfigSpliter.SPLITER
                    + fsize + ConfigSpliter.SPLITER
                    + delete + ConfigSpliter.SPLITER
                    + bold + ConfigSpliter.SPLITER
                    + itic + ConfigSpliter.SPLITER
                    + underlne + ConfigSpliter.SPLITER
                    + alignValue + ConfigSpliter.SPLITER
                    + rowSpace + ConfigSpliter.SPLITER
                    + colNumber.getText();
            if (nameArr.length > 1) {
                detail = nameArr[0] + ConfigSpliter.DIFFER + detail;
            } else {
                detail = ConfigSpliter.EMPTY + ConfigSpliter.DIFFER + detail;
            }
            currentStage.setQuestionStyle(detail);
            stagesMapper.updateById(currentStage);

            // 保存 x y width  height
            Configs xuantiConfig = configsMapper.selectById(4);
            String configVal = xuantiConfig.getConfig();
            if (!StringUtils.isEmpty(configVal)) {
                String[] cfgArr = configVal.split(ConfigSpliter.SPLITER);
                cfgArr[3] = x + "";
                cfgArr[4] = y + "";
                cfgArr[5] = width + "";
                cfgArr[6] = height + "";
                String join = String.join(ConfigSpliter.SPLITER, cfgArr);
                xuantiConfig.setConfig(join);
                configsMapper.updateById(xuantiConfig);
            }
        } else {
            String detail = x + ConfigSpliter.SPLITER
                    + y + ConfigSpliter.SPLITER
                    + width + ConfigSpliter.SPLITER
                    + height + ConfigSpliter.SPLITER
                    + fillBackground + ConfigSpliter.SPLITER
                    + backgroundColor + ConfigSpliter.SPLITER
                    + backgroundImage + ConfigSpliter.SPLITER
                    + fontFamily + ConfigSpliter.SPLITER
                    + fsize + ConfigSpliter.SPLITER
                    + delete + ConfigSpliter.SPLITER
                    + bold + ConfigSpliter.SPLITER
                    + itic + ConfigSpliter.SPLITER
                    + underlne + ConfigSpliter.SPLITER
                    + alignValue + ConfigSpliter.SPLITER
                    + rowSpace + ConfigSpliter.SPLITER
                    + colNumber.getText();


            detail = ConfigSpliter.EMPTY + ConfigSpliter.DIFFER + detail;
            currentStage.setQuestionStyle(detail);
            stagesMapper.updateById(currentStage);

            // 保存 x y width  height
            Configs xuantiConfig = configsMapper.selectById(4);
            String configVal = xuantiConfig.getConfig();
            if (!StringUtils.isEmpty(configVal)) {
                String[] cfgArr = configVal.split(ConfigSpliter.SPLITER);
                cfgArr[3] = x + "";
                cfgArr[4] = y + "";
                cfgArr[5] = width + "";
                cfgArr[6] = height + "";
                String join = String.join(ConfigSpliter.SPLITER, cfgArr);
                xuantiConfig.setConfig(join);
                configsMapper.updateById(xuantiConfig);
            }

        }
        controlPane.reloadStage();
        PreviewStack.reloadStyle();
    }


    private void showNumberConfig(Configs configs) {
        String details;
        if (currentStage.getQuestionType() == 4) {
            details = "560%%240%%800%%100%%false%%0xffffffff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0###920%%400%%80%%80%%false%%0xffffffff%%%%默认.ttf%%36%%false%%true%%false%%false%%居中对齐%%0%%1";
        }
        else {
            details = "760%%240%%400%%80%%false%%0xffffffff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%10###415%%370%%100%%100%%false%%0xffffffff%%%%默认.ttf%%30%%false%%false%%false%%false%%居中对齐%%10%%10";
        }

        String[] split = details.split(ConfigSpliter.DIFFER);
        if (!StringUtils.isEmpty(currentStage.getQuestionStyle())) {
            //details = currentStage.getQuestionStyle();
            String[] sps = currentStage.getQuestionStyle().split(ConfigSpliter.DIFFER);
            if (!ConfigSpliter.EMPTY.equals(sps[0])) {
                split[0] = sps[0];
            }

            if (!ConfigSpliter.EMPTY.equals(sps[1])) {
                split[1] = sps[1];
            }
        }
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            numberColorInput.setValue(Color.WHITE);
            numberWidthInput.setText(cfgArr[5]);
            numberHeightInput.setText(cfgArr[6]);
            numberXInput.setText(cfgArr[3]);
            numberYInput.setText(cfgArr[4]);
        }
        numberFontSelector.setSelectedValue(FontMap.defaultFontName());
        numberFontSize.setText(12 + "");
        numberIticChoice.setSelected(false);
        numberDeleteChoice.setSelected(false);
        numberSpaceInput.setText("0");
        colNumber.setText("1");
        
       
        if (split.length > 1) {
            String numberCfg = split[1];
            if (!ConfigSpliter.EMPTY.equals(numberCfg)) {
                String[] cfgArr = numberCfg.split(ConfigSpliter.SPLITER);

                numberXInput.setText(cfgArr[0]);
                numberYInput.setText(cfgArr[1]);
                numberWidthInput.setText(cfgArr[2]);
                numberHeightInput.setText(cfgArr[3]);
                numberBackgroundChoice.setSelected(Boolean.valueOf(cfgArr[4]));
                numberColorInput.setValue(Color.valueOf(cfgArr[5].replace("0x", "#")));
                numberBackgroundPath.setText(cfgArr[6]);
                numberFontSelector.setSelectedValue(cfgArr[7]);
                numberFontSize.setText(Integer.parseInt(cfgArr[8]) + "");
                numberDeleteChoice.setSelected(Boolean.valueOf(cfgArr[9]));
                numberBoldChoice.setSelected(Boolean.valueOf(cfgArr[10]));
                numberIticChoice.setSelected(Boolean.valueOf(cfgArr[11]));
                numberUnderlineChoice.setSelected(Boolean.valueOf(cfgArr[12]));
                numberTextAlignSelector.setValue(cfgArr[13]);
                numberSpaceInput.setText(cfgArr[14]);
                if (cfgArr.length >= 16) {
                    colNumber.setText(cfgArr[15]);
                }
            }

        }
    }


    private void showNameConfig() {
        nameFontSelector.setSelectedValue(FontMap.defaultFontName());
        nameFontSize.setText(12 + "");
        nameIticChoice.setSelected(false);
        nameDeleteChoice.setSelected(false);
        nameSpaceInput.setText("0");
        nameXInput.setText(ConfigSpliter.DEFAULT_X + "");
        nameYInput.setText(ConfigSpliter.DEFAULT_Y + "");
        nameWidthInput.setText(ConfigSpliter.DEFAULT_W + "");
        nameHeightInput.setText(ConfigSpliter.DEFAULT_H + "");


        String details;
        if (currentStage.getQuestionType() == 4) {
            details = "560%%240%%800%%100%%false%%0xffffffff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0###920%%400%%80%%80%%false%%0x990000ff%%%%默认.ttf%%36%%false%%true%%false%%false%%居中对齐%%0%%1";
        } else {
            details = "760%%240%%400%%80%%false%%0xffffffff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%10###415%%370%%100%%100%%false%%0x990000ff%%%%默认.ttf%%30%%false%%false%%false%%false%%居中对齐%%10%%10";
        }
        String[] split = details.split(ConfigSpliter.DIFFER);

        if (!StringUtils.isEmpty(currentStage.getQuestionStyle())) {
            String[] sps  = currentStage.getQuestionStyle().split(ConfigSpliter.DIFFER);
            if (!ConfigSpliter.EMPTY.equals(sps[0])) {
                split[0] = sps[0];
            }

            if (!ConfigSpliter.EMPTY.equals(sps[1])) {
                split[1] = sps[1];
            }
        }
        

        String nameCfg = split[0];
        String[] cfgArr = nameCfg.split(ConfigSpliter.SPLITER);
        nameXInput.setText(cfgArr[0]);
        nameYInput.setText(cfgArr[1]);
        nameWidthInput.setText(cfgArr[2]);
        nameHeightInput.setText(cfgArr[3]);
        nameBackgroundChoice.setSelected(Boolean.valueOf(cfgArr[4]));
        nameColorInput.setValue(Color.valueOf(cfgArr[5]));
        nameBackgroundPath.setText(cfgArr[6]);
        nameFontSelector.setSelectedValue(cfgArr[7]);
        nameFontSize.setText(Integer.parseInt(cfgArr[8]) + "");
        nameDeleteChoice.setSelected(Boolean.valueOf(cfgArr[9]));
        nameBoldChoice.setSelected(Boolean.valueOf(cfgArr[10]));
        nameIticChoice.setSelected(Boolean.valueOf(cfgArr[11]));
        nameUnderlineChoice.setSelected(Boolean.valueOf(cfgArr[12]));
        nameTextAlignSelector.setValue(cfgArr[13]);
        nameSpaceInput.setText(cfgArr[14]);
        if (cfgArr.length >= 16) {
            fontColorInput.setValue(Color.valueOf(cfgArr[15]));
        }
    }

}
