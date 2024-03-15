package competition.controller.preview;

import competition.entity.Configs;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.mapper.ConfigsMapper;
import competition.util.AlignmentUtil;
import competition.util.ConfigSpliter;
import competition.util.ExcelParser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Component
public class QuestionPreviewAttrPreview {


    @FXML
    private TextField nameWidthInput;

    @FXML
    private TextField answerWidthInput;

    @FXML
    private TextField nameHeightInput;

    @FXML
    private CheckBox nameUnderlineChoice;

    @FXML
    private TextField answerFontSize;

    @FXML
    private TextField nameXInput;

    @FXML
    private TextField nameBackgroundPath;

    @FXML
    private CheckBox answerUnderlineChoice;

    @FXML
    private ColorPicker nameColorInput;

    @FXML
    private CheckBox nameIticChoice;

    @FXML
    private CheckBox nameDeleteChoice;

    @FXML
    private CheckBox answerDeleteChoice;

    @FXML
    private TextArea answerText;

    @FXML
    private CheckBox answerBackgroundChoice;

    @FXML
    private ComboBox<String> answerTextAlignSelector;

    @FXML
    private ComboBox<String> nameFontSelector;

    @FXML
    private CheckBox nameBoldChoice;

    @FXML
    private CheckBox answerBoldChoice;

    @FXML
    private ComboBox<String> nameTextAlignSelector;

    @FXML
    private TextArea nameText;

    @FXML
    private TextField answerHeightInput;

    @FXML
    private TextField answerXInput;

    @FXML
    private TextField nameSpaceInput;

    @FXML
    private TextField answerBackgroundPath;

    @FXML
    private TextField answerSpaceInput;

    @FXML
    private TextField nameYInput;

    @FXML
    private TextField answerYInput;

    @FXML
    private ColorPicker answerColorInput;

    @FXML
    private CheckBox answerIticChoice;

    @FXML
    private ComboBox<String> answerFontSelector;

    @FXML
    private TextField nameFontSize;

    @FXML
    private CheckBox nameBackgroundChoice;


    @Resource
    private ConfigsMapper configsMapper;

    private List<QuestionStyle> questionStyleList;


    private static PreviewPane reloadPane;

    private static Questions questions;

    public static void setQuestions(Questions questions) {
        QuestionPreviewAttrPreview.questions = questions;
    }

    public static void setReloadPane(PreviewPane reloadPane) {
        QuestionPreviewAttrPreview.reloadPane = reloadPane;
    }

    @FXML
    public void initialize() {
        
        // 读取问题的样式
        questionStyleList = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);
        // 初始化展示所有的字体和居中样式
        nameTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        nameFontSelector.setItems(FXCollections.observableList(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())));
        answerTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        answerFontSelector.setItems(FXCollections.observableList(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())));
        // 回显数据
        showConfigData();
    }


    private void showConfigData() {
        Configs configs = configsMapper.selectById(1);
        showTiganConfig(configs);
        showAnswerConfig(configs);
    }


    private void showTiganConfig(Configs configs) {
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // 默认的字体
            Font defaultFont = new JLabel().getFont();
            nameFontSelector.setValue(defaultFont.getFamily());
            nameFontSize.setText(defaultFont.getSize() + "");
            // x y w h
            nameXInput.setText(cfgArr[5]);
            nameYInput.setText(cfgArr[6]);
            nameWidthInput.setText(cfgArr[7]);
            nameHeightInput.setText(cfgArr[8]);
            nameText.setText(questions.getQuestion());
            nameTextAlignSelector.setValue("左对齐");
            // 根据配置详情设置样式
            if (!questionStyleList.isEmpty()) {
                QuestionStyle questionStyle = questionStyleList.get(questions.getSort());
                if (questionStyle != null) {
                    String style1 = questionStyle.getStyle1();
                    if (!StringUtils.isEmpty(style1)) {
                        // 解析 excel 的样式
                        String[] split = style1.split(ConfigSpliter.SPLITER);
                        nameBackgroundChoice.setSelected(Boolean.valueOf(split[4]));
                        nameColorInput.setValue(Color.valueOf(split[5]));
                        nameBackgroundPath.setText(split[6]);
                        nameFontSelector.setValue(split[7]);
                        nameFontSize.setText(Integer.parseInt(split[8]) + "");
                        nameDeleteChoice.setSelected(Boolean.valueOf(split[9]));
                        nameBoldChoice.setSelected(Boolean.valueOf(split[10]));
                        nameIticChoice.setSelected(Boolean.valueOf(split[11]));
                        nameUnderlineChoice.setSelected(Boolean.valueOf(split[12]));
                        nameTextAlignSelector.setValue(split[13]);
                        nameSpaceInput.setText(split[14]);
                    }
                }
            }
        }
    }


    @FXML
    public void saveStageNameConfig() {
        String style1 =
                nameXInput.getText() + ConfigSpliter.SPLITER +
                        nameYInput.getText() + ConfigSpliter.SPLITER +
                        nameWidthInput.getText() + ConfigSpliter.SPLITER +
                        nameHeightInput.getText() + ConfigSpliter.SPLITER +
                        nameBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameColorInput.getValue() + ConfigSpliter.SPLITER +
                        nameBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        nameFontSelector.getValue() + ConfigSpliter.SPLITER +
                        nameFontSize.getText() + ConfigSpliter.SPLITER +
                        nameDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText());
        for (QuestionStyle questionStyle : questionStyleList) {
            if (questionStyle.getStageNo().equals(questions.getStageNumber())) {
                questionStyle.setStyle1(style1);
            }
        }
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);
        // 保存对应的 x y 等到数据库配置中
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // x y w h
            cfgArr[5] = nameXInput.getText();
            cfgArr[6] = nameYInput.getText();
            cfgArr[7] = nameWidthInput.getText();
            cfgArr[8] = nameHeightInput.getText();
            configs.setConfig(String.join(ConfigSpliter.SPLITER, cfgArr));
            configsMapper.updateById(configs);
        }

        // 写入（更新）题干内容到 excel 里
        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        questions.setQuestion(nameText.getText());
        parse.set(questions.getSort(), questions);
        ExcelParser.write(ExcelParser.Q_PATH, parse, Questions.class);
        reloadPane.reload();

    }


    @FXML
    public void saveAllNameConfig() {
        String style1 =
                nameXInput.getText() + ConfigSpliter.SPLITER +
                        nameYInput.getText() + ConfigSpliter.SPLITER +
                        nameWidthInput.getText() + ConfigSpliter.SPLITER +
                        nameHeightInput.getText() + ConfigSpliter.SPLITER +
                        nameBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameColorInput.getValue() + ConfigSpliter.SPLITER +
                        nameBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        nameFontSelector.getValue() + ConfigSpliter.SPLITER +
                        nameFontSize.getText() + ConfigSpliter.SPLITER +
                        nameDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText());
        for (QuestionStyle questionStyle : questionStyleList) {
            questionStyle.setStyle1(style1);
        }
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);
        // 保存对应的 x y 等到数据库配置中
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // x y w h
            cfgArr[5] = nameXInput.getText();
            cfgArr[6] = nameYInput.getText();
            cfgArr[7] = nameWidthInput.getText();
            cfgArr[8] = nameHeightInput.getText();
            configs.setConfig(String.join(ConfigSpliter.SPLITER, cfgArr));
            configsMapper.updateById(configs);
        }

        // 写入（更新）题干内容到 excel 里
        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        questions.setQuestion(nameText.getText());
        parse.set(questions.getSort(), questions);
        ExcelParser.write(ExcelParser.Q_PATH, parse, Questions.class);
        reloadPane.reload();

    }

    @FXML
    public void saveNameConfig() {
        String style1 =
                nameXInput.getText() + ConfigSpliter.SPLITER +
                        nameYInput.getText() + ConfigSpliter.SPLITER +
                        nameWidthInput.getText() + ConfigSpliter.SPLITER +
                        nameHeightInput.getText() + ConfigSpliter.SPLITER +
                        nameBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameColorInput.getValue() + ConfigSpliter.SPLITER +
                        nameBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        nameFontSelector.getValue() + ConfigSpliter.SPLITER +
                        nameFontSize.getText() + ConfigSpliter.SPLITER +
                        nameDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        nameTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText());
        questionStyleList.get(questions.getSort()).setStyle1(style1);
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);
        // 保存对应的 x y 等到数据库配置中
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // x y w h
            cfgArr[5] = nameXInput.getText();
            cfgArr[6] = nameYInput.getText();
            cfgArr[7] = nameWidthInput.getText();
            cfgArr[8] = nameHeightInput.getText();
            configs.setConfig(String.join(ConfigSpliter.SPLITER, cfgArr));
            configsMapper.updateById(configs);
        }

        // 写入（更新）题干内容到 excel 里
        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        questions.setQuestion(nameText.getText());
        parse.set(questions.getSort(), questions);
        ExcelParser.write(ExcelParser.Q_PATH, parse, Questions.class);
        reloadPane.reload();

    }


    @FXML
    public void saveAnswerConfig() {
        String style1 =
                answerXInput.getText() + ConfigSpliter.SPLITER +
                        answerYInput.getText() + ConfigSpliter.SPLITER +
                        answerWidthInput.getText() + ConfigSpliter.SPLITER +
                        answerHeightInput.getText() + ConfigSpliter.SPLITER +
                        answerBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerColorInput.getValue() + ConfigSpliter.SPLITER +
                        answerBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        answerFontSelector.getValue() + ConfigSpliter.SPLITER +
                        answerFontSize.getText() + ConfigSpliter.SPLITER +
                        answerDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        answerSpaceInput.getText();
        questionStyleList.get(questions.getSort()).setStyle2(style1);
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);

        // 写入（更新）题干内容到 excel 里
        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        questions.setAnswer(answerText.getText());
        parse.set(questions.getSort(), questions);
        ExcelParser.write(ExcelParser.Q_PATH, parse, Questions.class);
        reloadPane.reload();

    }

    @FXML
    public void saveStageAnswerConfig() {
        String style1 =
                answerXInput.getText() + ConfigSpliter.SPLITER +
                        answerYInput.getText() + ConfigSpliter.SPLITER +
                        answerWidthInput.getText() + ConfigSpliter.SPLITER +
                        answerHeightInput.getText() + ConfigSpliter.SPLITER +
                        answerBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerColorInput.getValue() + ConfigSpliter.SPLITER +
                        answerBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        answerFontSelector.getValue() + ConfigSpliter.SPLITER +
                        answerFontSize.getText() + ConfigSpliter.SPLITER +
                        answerDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        answerSpaceInput.getText();

        for (QuestionStyle questionStyle : questionStyleList) {
            if (questionStyle.getStageNo().equals(questions.getStageNumber())) {
                questionStyle.setStyle2(style1);
            }
        }
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);

        // 写入（更新）题干内容到 excel 里
        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        questions.setAnswer(answerText.getText());
        parse.set(questions.getSort(), questions);
        ExcelParser.write(ExcelParser.Q_PATH, parse, Questions.class);
        reloadPane.reload();

    }

    @FXML
    public void saveAllAnswerConfig() {
        String style1 =
                answerXInput.getText() + ConfigSpliter.SPLITER +
                        answerYInput.getText() + ConfigSpliter.SPLITER +
                        answerWidthInput.getText() + ConfigSpliter.SPLITER +
                        answerHeightInput.getText() + ConfigSpliter.SPLITER +
                        answerBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerColorInput.getValue() + ConfigSpliter.SPLITER +
                        answerBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        answerFontSelector.getValue() + ConfigSpliter.SPLITER +
                        answerFontSize.getText() + ConfigSpliter.SPLITER +
                        answerDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        answerTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        answerSpaceInput.getText();

        for (QuestionStyle questionStyle : questionStyleList) {
            questionStyle.setStyle2(style1);
        }
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);
        // 写入（更新）题干内容到 excel 里
        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        questions.setAnswer(answerText.getText());
        parse.set(questions.getSort(), questions);
        ExcelParser.write(ExcelParser.Q_PATH, parse, Questions.class);
        reloadPane.reload();

    }


    private void showAnswerConfig(Configs configs) {
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // 默认的字体
            Font defaultFont = new JLabel().getFont();
            answerFontSelector.setValue(defaultFont.getFamily());
            answerFontSize.setText(defaultFont.getSize() + "");
            // x y w h
            QuestionPreviewPreview preview = (QuestionPreviewPreview) reloadPane;
            answerXInput.setText((int) preview.tiganBox.getLayoutX() + "");
            answerYInput.setText((int) (preview.tiganBox.getLayoutX() + preview.tiganBox.getPrefHeight() + 10) + "");
//            answerWidthInput.setText(cfgArr[7]);
//            answerHeightInput.setText(cfgArr[8]);
            answerText.setText(questions.getAnswer());
            answerTextAlignSelector.setValue("左对齐");
            // 根据配置详情设置样式
            if (!questionStyleList.isEmpty()) {
                QuestionStyle questionStyle = questionStyleList.get(questions.getSort());
                if (questionStyle != null) {
                    String style1 = questionStyle.getStyle2();
                    if (!StringUtils.isEmpty(style1)) {
                        // 解析 excel 的样式
                        String[] split = style1.split(ConfigSpliter.SPLITER);
                        answerBackgroundChoice.setSelected(Boolean.valueOf(split[4]));
                        answerColorInput.setValue(Color.valueOf(split[5]));
                        answerBackgroundPath.setText(split[6]);
                        answerFontSelector.setValue(split[7]);
                        answerFontSize.setText(Integer.parseInt(split[8]) + "");
                        answerDeleteChoice.setSelected(Boolean.valueOf(split[9]));
                        answerBoldChoice.setSelected(Boolean.valueOf(split[10]));
                        answerIticChoice.setSelected(Boolean.valueOf(split[11]));
                        answerUnderlineChoice.setSelected(Boolean.valueOf(split[12]));
                        answerTextAlignSelector.setValue(split[13]);
                        answerSpaceInput.setText(split[14]);
                    }
                }
            }
        }
    }
}
