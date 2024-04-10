package competition.controller.match;

import competition.controller.preview.Model3;
import competition.entity.Configs;
import competition.entity.Model3Style;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.Model3Mapper;
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

public class Model3Attr {

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
    private ComboBox<String> numberFontSelector;

    @FXML
    private TextField nameXInput;

    @FXML
    private TextField numberXInput;

    @FXML
    private TextField nameBackgroundPath;

    @FXML
    private ColorPicker nameColorInput, nfColorInput;

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
    private ComboBox<String> nameFontSelector;

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
    
    @FXML
    private TextField numberColSpaceInput;
    
    @FXML
    private ColorPicker numberfColorInput;


    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private StagesMapper stagesMapper;


    // 风险题目
    @Resource
    private Model3Mapper mapper;

    private static ReloadPane reloadPane;

    public static void setReloadPane(ReloadPane reloadPane) {
        Model3Attr.reloadPane = reloadPane;
    }

    private static Stages currentStage;

    public static void setCurrentStage(Stages currentStage) {
        Model3Attr.currentStage = currentStage;
    }


    @FXML
    public void selectNameImg() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.IMAGE_DIR));
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
        chooser.setInitialDirectory(new File(BaseDir.IMAGE_DIR));
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
        nameTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        nameFontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));

        numberTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        numberFontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));


        Model3Style model3Style = mapper.selectById(1);

        showNameConfig(model3Style.getTitleStyle());
        showNumberConfig(model3Style.getQStyle());
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
        final String fontFamily = nameFontSelector.getValue();
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
        if (reloadPane != null) {
            reloadPane.reload();
        }
        PreviewStack.reload();
        closeStage();
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
        final String fontFamily = numberFontSelector.getValue();
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
                + nfColorInput.getValue();


        Model3Style model3Style = mapper.selectById(1);
        model3Style.setTitleStyle(detail);
        mapper.updateById(model3Style);

        if (reloadPane != null) {
            reloadPane.reload();
        }
        PreviewStack.reload();
        closeStage();
    }


    private void showNumberConfig(String config) {
        Font defaultFont = new JLabel().getFont();
        numberFontSelector.setValue(defaultFont.getFamily());
        numberFontSize.setText(defaultFont.getSize() + "");
        numberIticChoice.setSelected(false);
        numberDeleteChoice.setSelected(false);
        numberSpaceInput.setText("0");
        colNumber.setText("1");

        nameXInput.setText(Model3.DEFAULT_X + "");
        nameYInput.setText(Model3.DEFAULT_Y + "");
        nameWidthInput.setText(Model3.DEFAULT_W + "");
        nameHeightInput.setText(Model3.DEFAULT_H + "");
        nameColorInput.setValue(Color.YELLOW);
        nfColorInput.setValue(Color.BLACK);
        nameTextAlignSelector.setValue("居中对齐");
        
        
        
        
        
        if (!StringUtils.isEmpty(config)) {
            String[] split = config.split(ConfigSpliter.DIFFER);
            if (split.length > 1) {
                String numberCfg = split[1];
                if (!ConfigSpliter.EMPTY.equals(numberCfg)) {
                    String[] cfgArr = numberCfg.split(ConfigSpliter.SPLITER);
                    numberBackgroundChoice.setSelected(Boolean.valueOf(cfgArr[4]));
                    numberColorInput.setValue(Color.valueOf(cfgArr[5].replace("0x", "#")));
                    numberBackgroundPath.setText(cfgArr[6]);
                    numberFontSelector.setValue(cfgArr[7]);
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

            } else {
                // 默认数据
            }
        } else {
            // 默认数据
        }
    }


    private void showNameConfig(String style) {
        Font defaultFont = new JLabel().getFont();
        nameFontSelector.setValue(defaultFont.getFamily());
        nameFontSize.setText(defaultFont.getSize() + "");
        nameIticChoice.setSelected(false);
        nameDeleteChoice.setSelected(false);
        nameSpaceInput.setText("0");
        nameXInput.setText(Model3.DEFAULT_X + "");
        nameYInput.setText(Model3.DEFAULT_Y + "");
        nameWidthInput.setText(Model3.DEFAULT_W + "");
        nameHeightInput.setText(Model3.DEFAULT_H + "");
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
                nameFontSelector.setValue(cfgArr[7]);
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
