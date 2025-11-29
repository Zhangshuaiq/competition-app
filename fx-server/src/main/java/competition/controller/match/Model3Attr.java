package competition.controller.match;

import competition.controller.preview.Model3Preview;
import competition.controls.GroupedComboBox;
import competition.entity.Model3Style;
import competition.entity.Stages;
import competition.mapper.Model3Mapper;
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
public class Model3Attr {

    @FXML
    private TextField nameHeightInput;

    @FXML
    private CheckBox nameUnderlineChoice;

    @FXML
    private CheckBox numberBackgroundChoice;

    @FXML
    private ColorPicker headfColorInput;

    @FXML
    private TextField nameXInput;

    @FXML
    private CheckBox headIticChoice;

    @FXML
    private TextField colNumber;

    @FXML
    private ColorPicker nfColorInput;

    @FXML
    private CheckBox nameIticChoice;

    @FXML
    private ColorPicker numberfColorInput;

    @FXML
    private CheckBox nameDeleteChoice;

    @FXML
    private ColorPicker numberColorInput;

    @FXML
    private ComboBox<String> numberTextAlignSelector;

    @FXML
    private TextField numberSpaceInput;

    @FXML
    private TextField numberHeightInput;

    @FXML
    private ComboBox<String> nameTextAlignSelector;

    @FXML
    private CheckBox headBackgroundChoice;

    @FXML
    private TextField numberFontSize;

    @FXML
    private TextField nameSpaceInput;

    @FXML
    private CheckBox numberUnderlineChoice;

    @FXML
    private TextField numberWidthInput;

    @FXML
    private TextField headHeightInput;

    @FXML
    private TextField numberBackgroundPath;

    @FXML
    private CheckBox headBoldChoice;

    @FXML
    private CheckBox numberIticChoice;

    @FXML
    private CheckBox headDeleteChoice;

    @FXML
    private TextField numberYInput;

    @FXML
    private TextField nameWidthInput;

    @FXML
    private GroupedComboBox<String> numberFontSelector;

    @FXML
    private TextField numberXInput;

    @FXML
    private CheckBox headUnderlineChoice;

    @FXML
    private TextField headXInput;

    @FXML
    private TextField nameBackgroundPath;

    @FXML
    private ColorPicker headColorInput;

    @FXML
    private TextField headWidthInput;

    @FXML
    private ComboBox<String> headTextAlignSelector;

    @FXML
    private ColorPicker nameColorInput;

    @FXML
    private CheckBox numberDeleteChoice;

    @FXML
    private TextField numberColSpaceInput;

    @FXML
    private GroupedComboBox<String> nameFontSelector;

    @FXML
    private TextField headFontSize;

    @FXML
    private CheckBox nameBoldChoice;

    @FXML
    private CheckBox numberBoldChoice;

    @FXML
    private TextField headBackgroundPath;

    @FXML
    private TextField nameYInput;

    @FXML
    private GroupedComboBox<String> headFontSelector;

    @FXML
    private TextField headSpaceInput;

    @FXML
    private TextField nameFontSize;

    @FXML
    private CheckBox nameBackgroundChoice;

    @FXML
    private TextField headYInput;
    
    


    // 风险题目
    @Resource
    private Model3Mapper mapper;


    private static Stages currentStage;

    public static void setCurrentStage(Stages currentStage) {
        Model3Attr.currentStage = currentStage;
    }

    @FXML
    public void selectHeadImg() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            headBackgroundPath.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void cancelHeadImg() {
        headBackgroundPath.setText("");
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


        FontMap.fontNames().forEach((key, value) -> {
            nameFontSelector.addGroup(key, value);
            numberFontSelector.addGroup(key, value);
            headFontSelector.addGroup(key, value);
        });

        numberTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        headTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        nameTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));


        Model3Style model3Style = mapper.selectById(1);

        if (!StringUtils.isEmpty(model3Style.getTitleStyle())) {
            showNameConfig(model3Style.getTitleStyle());
        } else {
            showNameConfig("250%%300%%150%%150%%false%%0xffffffff%%%%默认.ttf%%32%%false%%false%%false%%false%%居中对齐%%30%%0x000000ff");
        }

        if (!StringUtils.isEmpty(model3Style.getQStyle())) {
            showNumberConfig(model3Style.getQStyle());
        } else {
            showNumberConfig("500%%300%%60%%60%%false%%0xffffffff%%%%默认.ttf%%36%%false%%false%%false%%false%%居中对齐%%30%%8%%40%%0x000000ff");
        }

        if (!StringUtils.isEmpty(model3Style.getHeadStyle())) {
            showHeadConfig(model3Style.getHeadStyle());
        } else {
            showHeadConfig("750%%150%%400%%100%%false%%0xffffffff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0%%0x000000ff");
        }
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
        final String fontFamily = headFontSelector.getSelectedValue();
        String fsize = headFontSize.getText();
        final boolean delete = headDeleteChoice.isSelected();
        final boolean bold = headBoldChoice.isSelected();
        final boolean itic = headIticChoice.isSelected();
        final boolean underlne = headUnderlineChoice.isSelected();
        final String alignValue = headTextAlignSelector.getValue();
        final String rowSpace = headSpaceInput.getText();

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
                + headfColorInput.getValue();

        Model3Style model3Style = mapper.selectById(1);
        model3Style.setHeadStyle(detail);
        mapper.updateById(model3Style);
        PreviewStack.reloadStyle();
    }
    
    @FXML
    public void saveNameConfig() {
        String x = nameXInput.getText();
        String y = nameYInput.getText();
        String width = nameWidthInput.getText();
        String height = nameHeightInput.getText();
        boolean fillBackground = nameBackgroundChoice.isSelected();
        final String backgroundColor = nameColorInput.getValue().toString();
        String backgroundImage = nameBackgroundPath.getText();
        final String fontFamily = nameFontSelector.getSelectedValue();
        String fsize = nameFontSize.getText();
        final boolean delete = nameDeleteChoice.isSelected();
        final boolean bold = nameBoldChoice.isSelected();
        final boolean itic = nameIticChoice.isSelected();
        final boolean underlne = nameUnderlineChoice.isSelected();
        final String alignValue = nameTextAlignSelector.getValue();
        final String rowSpace = nameSpaceInput.getText();

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
                + nfColorInput.getValue();

        Model3Style model3Style = mapper.selectById(1);
        model3Style.setTitleStyle(detail);
        mapper.updateById(model3Style);
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
                + colNumber.getText() + ConfigSpliter.SPLITER
                + numberColSpaceInput.getText() + ConfigSpliter.SPLITER
                + numberfColorInput.getValue();


        Model3Style model3Style = mapper.selectById(1);
        model3Style.setQStyle(detail);
        mapper.updateById(model3Style);

        PreviewStack.reloadStyle();
    }


    private void showNumberConfig(String config) {
        Font defaultFont = new JLabel().getFont();
        numberFontSelector.setSelectedValue(FontMap.defaultFontName());
        numberFontSize.setText(12 + "");
        numberIticChoice.setSelected(false);
        numberDeleteChoice.setSelected(false);
        colNumber.setText("1");

        numberXInput.setText(Model3Preview.DEFAULT_X + Model3Preview.DEFAULT_W + 50 + "");
        numberYInput.setText(Model3Preview.DEFAULT_Y + "");
        numberWidthInput.setText(50 + "");
        numberHeightInput.setText(50 + "");
        numberColorInput.setValue(Color.YELLOW);
        numberfColorInput.setValue(Color.BLACK);
        numberTextAlignSelector.setValue("居中对齐");
        numberColSpaceInput.setText("10");
        numberSpaceInput.setText("0");


        if (!StringUtils.isEmpty(config)) {
            String[] split = config.split(ConfigSpliter.SPLITER);
            if (split.length > 0) {
                String[] cfgArr = split;
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
                if (cfgArr.length >= 17) {
                    numberColSpaceInput.setText(cfgArr[16]);
                }
                if (cfgArr.length >= 18) {
                    numberfColorInput.setValue(Color.valueOf(cfgArr[17]));
                }

            } else {
                // 默认数据
            }
        } else {
            // 默认数据
        }
    }


    private void showHeadConfig(String style) {
        headFontSelector.setSelectedValue(FontMap.defaultFontName());
        headFontSize.setText(12 + "");
        headIticChoice.setSelected(false);
        headDeleteChoice.setSelected(false);
        headSpaceInput.setText("0");
        headXInput.setText(Model3Preview.HEAD_X + "");
        headYInput.setText(Model3Preview.HEAD_Y + "");
        headWidthInput.setText(Model3Preview.HEAD_W + "");
        headHeightInput.setText(Model3Preview.HEAD_H + "");
        headColorInput.setValue(Color.YELLOW);
        headfColorInput.setValue(Color.BLACK);
        headTextAlignSelector.setValue("居中对齐");

        if (!StringUtils.isEmpty(style)) {
            String[] split = style.split(ConfigSpliter.DIFFER);
            if (split.length > 0 && !ConfigSpliter.EMPTY.equals(split[0])) {
                String headCfg = split[0];
                String[] cfgArr = headCfg.split(ConfigSpliter.SPLITER);
                headXInput.setText(cfgArr[0]);
                headYInput.setText(cfgArr[1]);
                headWidthInput.setText(cfgArr[2]);
                headHeightInput.setText(cfgArr[3]);
                headBackgroundChoice.setSelected(Boolean.valueOf(cfgArr[4]));
                headColorInput.setValue(Color.valueOf(cfgArr[5]));
                headBackgroundPath.setText(cfgArr[6]);
                headFontSelector.setSelectedValue(cfgArr[7]);
                headFontSize.setText(Integer.parseInt(cfgArr[8]) + "");
                headDeleteChoice.setSelected(Boolean.valueOf(cfgArr[9]));
                headBoldChoice.setSelected(Boolean.valueOf(cfgArr[10]));
                headIticChoice.setSelected(Boolean.valueOf(cfgArr[11]));
                headUnderlineChoice.setSelected(Boolean.valueOf(cfgArr[12]));
                headTextAlignSelector.setValue(cfgArr[13]);
                headSpaceInput.setText(cfgArr[14]);
                headfColorInput.setValue(Color.valueOf(cfgArr[15]));
            }
        }
    }


    private void showNameConfig(String style) {
        nameFontSelector.setSelectedValue(FontMap.defaultFontName());
        nameFontSize.setText(12 + "");
        nameIticChoice.setSelected(false);
        nameDeleteChoice.setSelected(false);
        nameSpaceInput.setText("0");
        nameXInput.setText(Model3Preview.DEFAULT_X + "");
        nameYInput.setText(Model3Preview.DEFAULT_Y + "");
        nameWidthInput.setText(Model3Preview.DEFAULT_W + "");
        nameHeightInput.setText(Model3Preview.DEFAULT_H + "");
        nameColorInput.setValue(Color.YELLOW);
        nfColorInput.setValue(Color.BLACK);
        nameTextAlignSelector.setValue("居中对齐");

        if (!StringUtils.isEmpty(style)) {
            String[] split = style.split(ConfigSpliter.DIFFER);
            if (split.length > 0 && !ConfigSpliter.EMPTY.equals(split[0])) {
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
                nfColorInput.setValue(Color.valueOf(cfgArr[15]));
            }
        }
    }

}
