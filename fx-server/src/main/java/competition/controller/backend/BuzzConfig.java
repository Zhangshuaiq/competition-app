package competition.controller.backend;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.entity.Buzz;
import competition.entity.Configs;
import competition.mapper.BuzzMapper;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BuzzConfig {

    @FXML
    private Label buzzSpeedText;

    @FXML
    private CheckBox errorMusicCheck;

    @FXML
    private TextField fontSizeInput;

    @FXML
    private TextField paneBackgroundText;

    @FXML
    private CheckBox playErrorCheck;

    @FXML
    private ComboBox<String> fontSelector;

    @FXML
    private CheckBox lastSuccessCheck;

    @FXML
    private CheckBox successMusicCheck;

    @FXML
    private ComboBox<String> buzzCombox;

    @FXML
    private CheckBox fontItic;

    @FXML
    private CheckBox fontBold;

    @FXML
    private CheckBox lightCheck;

    @FXML
    private Slider speedAdjust;

    @FXML
    private TextField timeLimitText;

    @FXML
    private ColorPicker fontColor;

    @FXML
    private CheckBox fontUnderline, xuhaoChecked, jianchengChecked, quanchengChecked;

    @FXML
    private ColorPicker backColor, xuhaoColor, shortColor, fullColor, successColor, failColor;

    @FXML
    private TextField xInput, xLoc, yLoc, yInput, widthInput, heightInput, spaceInput, xuhaoSize, jianchengSize, quanchengSize, successSize, errorSize;

    @FXML
    private ComboBox<String> xuhaoFont, jianchengFont, quanchengFont, successFont, errorFont;

    @Resource
    private BuzzMapper buzzMapper;

    @Resource
    private ConfigsMapper configsMapper;

    @FXML
    public void initialize() {
        buzzCombox.getItems().addAll(
                "直接抢答",
                "标准模式",
                "淘汰模式"
        );


        backColor.setValue(Color.BLACK);
        widthInput.setText("200");
        heightInput.setText("200");
        spaceInput.setText("10");
        xuhaoColor.setValue(Color.BLACK);
        shortColor.setValue(Color.BLACK);
        fullColor.setValue(Color.BLACK);
        successColor.setValue(Color.BLACK);
        failColor.setValue(Color.BLACK);

        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        xuhaoFont.getItems().addAll(FXCollections.observableList(FontMap.fontNames()));
        jianchengFont.setItems(FXCollections.observableList(FontMap.fontNames()));
        quanchengFont.setItems(FXCollections.observableList(FontMap.fontNames()));
        successFont.setItems(FXCollections.observableList(FontMap.fontNames()));
        errorFont.setItems(FXCollections.observableList(FontMap.fontNames()));

        fontSelector.setValue("默认.ttf");
        xuhaoFont.setValue("默认.ttf");
        jianchengFont.setValue("默认.ttf");
        quanchengFont.setValue("默认.ttf");
        successFont.setValue("默认.ttf");
        errorFont.setValue("默认.ttf");

        xuhaoSize.setText("12");
        jianchengSize.setText("12");
        quanchengSize.setText("12");
        successSize.setText("12");
        errorSize.setText("12");

        DecimalFormat format = new DecimalFormat("#.##");

        speedAdjust.valueProperty().addListener((observable, oldValue, newValue) -> {
            buzzSpeedText.setText(format.format(newValue.doubleValue()));
        });
        speedAdjust.setMin(0);
        speedAdjust.setMax(1);
        Buzz buzz = buzzMapper.selectById(1);
        if (buzz != null) {
            buzzCombox.setValue(buzz.getMode());
            buzzSpeedText.setText(String.valueOf(buzz.getSpeed()));
            speedAdjust.setValue(Double.valueOf(buzz.getSpeed()));

            xLoc.setText(StringUtils.isEmpty(buzz.getX())? "0":buzz.getX());
            yLoc.setText(StringUtils.isEmpty(buzz.getY())? "0":buzz.getY());
            fontUnderline.setSelected(buzz.getUnderline() != null && buzz.getUnderline() == 1);
            fontItic.setSelected(buzz.getItic() != null && buzz.getItic() == 1);
            fontColor.setValue(Color.valueOf(buzz.getColor()));
            fontSizeInput.setText(String.valueOf(buzz.getFontSize()));
            fontBold.setSelected(buzz.getBold() != null && buzz.getBold() == 1);
            fontSelector.setValue(String.valueOf(buzz.getFont()));

            errorMusicCheck.setSelected(buzz.getErrorMusic() != null && buzz.getErrorMusic() == 1);
            successMusicCheck.setSelected(buzz.getSuccessMusic() != null && buzz.getSuccessMusic() == 1);
            playErrorCheck.setSelected(buzz.getPlayError() != null && buzz.getPlayError() == 1);
            lastSuccessCheck.setSelected(buzz.getLastSuccess() != null && buzz.getLastSuccess() == 1);
            lightCheck.setSelected(buzz.getLight() != null && buzz.getLight() == 1);
            timeLimitText.setText(String.valueOf(buzz.getTimeLimit()));
            paneBackgroundText.setText(buzz.getBackground());
        }

        // 抢答结果面板信息
        Configs configs = configsMapper.selectById(10);
        if (configs != null) {
            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                JSONObject jsonObject = JSON.parseObject(config);


                xuhaoChecked.setSelected(Boolean.parseBoolean(jsonObject.getOrDefault("xuhao", "false").toString()));
                jianchengChecked.setSelected(Boolean.parseBoolean(jsonObject.getOrDefault("jiancheng", "false").toString()));
                quanchengChecked.setSelected(Boolean.parseBoolean(jsonObject.getOrDefault("quancheng", "false").toString()));

                backColor.setValue(Color.valueOf(jsonObject.getOrDefault("backColor", "BLACK").toString()));


                xInput.setText(jsonObject.getOrDefault("x", "200").toString());
                yInput.setText(jsonObject.getOrDefault("y", "200").toString());
                widthInput.setText(jsonObject.getOrDefault("width", "200").toString());
                heightInput.setText(jsonObject.getOrDefault("height", "200").toString());
                spaceInput.setText(jsonObject.getOrDefault("rowSpace", "10").toString());
                xuhaoColor.setValue(Color.valueOf(jsonObject.getOrDefault("xuhaoColor", "BLACK").toString()));
                shortColor.setValue(Color.valueOf(jsonObject.getOrDefault("shortColor", "BLACK").toString()));
                fullColor.setValue(Color.valueOf(jsonObject.getOrDefault("fullColor", "BLACK").toString()));
                successColor.setValue(Color.valueOf(jsonObject.getOrDefault("successColor", "BLACK").toString()));
                failColor.setValue(Color.valueOf(jsonObject.getOrDefault("failColor", "BLACK").toString()));

                fontSelector.setValue(jsonObject.getOrDefault("fontSelector", "默认.ttf").toString());
                xuhaoFont.setValue(jsonObject.getOrDefault("xuhaoFont", "默认.ttf").toString());
                jianchengFont.setValue(jsonObject.getOrDefault("jianchengFont", "默认.ttf").toString());
                quanchengFont.setValue(jsonObject.getOrDefault("quanchengFont", "默认.ttf").toString());
                successFont.setValue(jsonObject.getOrDefault("successFont", "默认.ttf").toString());
                errorFont.setValue(jsonObject.getOrDefault("errorFont", "默认.ttf").toString());

                xuhaoSize.setText(jsonObject.getOrDefault("xuhaoSize", "12").toString());
                jianchengSize.setText(jsonObject.getOrDefault("jianchengSize", "12").toString());
                quanchengSize.setText(jsonObject.getOrDefault("quanchengSize", "12").toString());
                successSize.setText(jsonObject.getOrDefault("successSize", "12").toString());
                errorSize.setText(jsonObject.getOrDefault("errorSize", "12").toString());
            }
        }

    }

    @FXML
    public void selectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.SUCAI_DIR));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            String name = file.getName();
            paneBackgroundText.setText(name);
        }
    }

    public List<TextField> getAllTextFields() {
        List<TextField> textFields = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (TextField.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    TextField textField = (TextField) field.get(this);
                    if (textField != null) {
                        textFields.add(textField);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return textFields;
    }

    @FXML
    public void updateConfig() {
        boolean valid = true;
        List<TextField> inputs = getAllTextFields();
        for (TextField input : inputs) {
            if (StringUtils.isEmpty(input.getText())) {
                valid = false;
                break;
            }
        }

        if (!valid) {
            Information.error("请填写界面的输入框");
            return;
        }
        //          buzzCombox.setValue(buzz.getMode());
        //            buzzSpeedText.setText(String.valueOf(buzz.getSpeed()));
        //            speedAdjust.setValue(Double.valueOf(buzz.getSpeed()));
        //
        //            fontUnderline.setSelected(buzz.getUnderline() != null && buzz.getUnderline() == 1);
        //            fontItic.setSelected(buzz.getItic() != null && buzz.getItic() == 1);
        //            fontColor.setValue(Color.valueOf(buzz.getColor()));
        //            fontSizeInput.setText(String.valueOf(buzz.getFontSize()));
        //            fontBold.setSelected(buzz.getBold() != null && buzz.getBold() == 1);
        //            fontSelector.setValue(String.valueOf(buzz.getFont()));
        //
        //            errorMusicCheck.setSelected(buzz.getErrorMusic() != null && buzz.getErrorMusic() == 1);
        //            successMusicCheck.setSelected(buzz.getSuccessMusic() != null && buzz.getSuccessMusic() == 1);
        //            playErrorCheck.setSelected(buzz.getPlayError() != null && buzz.getPlayError() == 1);
        //            lastSuccessCheck.setSelected(buzz.getLastSuccess() != null && buzz.getLastSuccess() == 1);
        //            timeLimitText.setText(String.valueOf(buzz.getTimeLimit()));
        //            paneBackgroundText.setText(buzz.getBackground());
        Buzz buzz = buzzMapper.selectById(1);
        boolean exist = true;
        if (buzz == null) {
            exist = false;
            buzz = new Buzz();
            buzz.setId(1);
        }
        String mode = buzzCombox.getValue();
        String speed = buzzSpeedText.getText();
        if (StringUtils.isEmpty(speed)) {
            speed = "0";
        }
        boolean underlineSelected = fontUnderline.isSelected();
        boolean fontIticSelected = fontItic.isSelected();
        String color = fontColor.getValue().toString();
        String sizeInputText = fontSizeInput.getText();
        if (StringUtils.isEmpty(sizeInputText)) {
            sizeInputText = "12";
        }
        boolean boldSelected = fontBold.isSelected();
        String fontFile = fontSelector.getValue();
        boolean errorMusicCheckSelected = errorMusicCheck.isSelected();
        boolean successMusicCheckSelected = successMusicCheck.isSelected();
        boolean playErrorCheckSelected = playErrorCheck.isSelected();
        boolean lastSuccessCheckSelected = lastSuccessCheck.isSelected();
        boolean lightCheckSelected = lightCheck.isSelected();
        String limitTextText = timeLimitText.getText();
        if (StringUtils.isEmpty(limitTextText)) {
            limitTextText = "3";
        }
        String text = paneBackgroundText.getText();

        buzz.setMode(mode);
        buzz.setSpeed(speed);
        buzz.setUnderline(underlineSelected ? 1 : 0);
        buzz.setItic(fontIticSelected ? 1 : 0);
        buzz.setColor(color);
        buzz.setFontSize(Integer.parseInt(sizeInputText));
        buzz.setBold(boldSelected ? 1 : 0);
        buzz.setFont(fontFile);
        buzz.setErrorMusic(errorMusicCheckSelected ? 1 : 0);
        buzz.setSuccessMusic(successMusicCheckSelected ? 1 : 0);
        buzz.setPlayError(playErrorCheckSelected ? 1 : 0);
        buzz.setLastSuccess(lastSuccessCheckSelected ? 1 : 0);
        buzz.setLight(lightCheckSelected ? 1 : 0);
        buzz.setTimeLimit(Integer.parseInt(limitTextText));
        buzz.setBackground(text);
        buzz.setX(xLoc.getText());
        buzz.setY(yLoc.getText());

        if (exist) {
            buzzMapper.updateById(buzz);
        } else {
            buzzMapper.insert(buzz);
        }

        // 更新面板信息

        Configs configs = configsMapper.selectById(10);
        boolean isNew = configs == null;
        if (isNew) {
            configs = new Configs();
            configs.setId(10);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("backColor", backColor.getValue().toString());
        jsonObject.put("xuhaoColor", xuhaoColor.getValue().toString());
        jsonObject.put("shortColor", shortColor.getValue().toString());
        jsonObject.put("fullColor", fullColor.getValue().toString());
        jsonObject.put("successColor", successColor.getValue().toString());
        jsonObject.put("failColor", failColor.getValue().toString());
        jsonObject.put("width", widthInput.getText());
        jsonObject.put("height", heightInput.getText());
        jsonObject.put("x", xInput.getText());
        jsonObject.put("y", yInput.getText());
        jsonObject.put("rowSpace", spaceInput.getText());
        jsonObject.put("fontSelector", fontSelector.getValue());
        jsonObject.put("xuhaoFont", xuhaoFont.getValue());
        jsonObject.put("jianchengFont", jianchengFont.getValue());
        jsonObject.put("quanchengFont", quanchengFont.getValue());
        jsonObject.put("successFont", successFont.getValue());
        jsonObject.put("errorFont", errorFont.getValue());

        jsonObject.put("xuhaoSize", xuhaoSize.getText());
        jsonObject.put("jianchengSize", jianchengSize.getText());
        jsonObject.put("quanchengSize", quanchengSize.getText());
        jsonObject.put("successSize", successSize.getText());
        jsonObject.put("errorSize", errorSize.getText());
        jsonObject.put("xuhao", xuhaoChecked.isSelected());
        jsonObject.put("jiancheng", jianchengChecked.isSelected());
        jsonObject.put("quancheng", quanchengChecked.isSelected());

        configs.setConfig(jsonObject.toString());
        if (isNew) {
            configsMapper.insert(configs);
        } else {
            configsMapper.updateById(configs);
        }
    }


}
