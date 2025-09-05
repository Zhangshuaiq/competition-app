package competition.controller.match;

import competition.controller.ControlPane;
import competition.controller.preview.CluePreviewPreview;
import competition.controller.preview.PreviewPane;
import competition.controller.preview.QuestionPreviewPreview;
import competition.entity.Configs;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.mapper.ConfigsMapper;
import competition.mapper.QuestionsMapper;
import competition.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component

public class QuestionPreviewAttr {
    
    
    // 环节名称设置
    @FXML
    private TextField stageXInput, stageYInput, stageWidthInput, 
            stageHeightInput, stageBackgroundPath, stageFontSize, stageSpaceInput;
    
    @FXML
    private ColorPicker stageColorInput;
    
    
    @FXML
    private CheckBox stageBackgroundChoice, stageDeleteChoice, stageBoldChoice, 
            stageIticChoice, stageUnderlineChoice;
    
    @FXML
    private ComboBox<String> stageFontSelector, stageTextAlignSelector;


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
    private TextField wordSpaceInput;

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
    

    @Resource
    private QuestionsMapper questionsMapper;

    @FXML
    private CheckBox spaceLineChoice;


    private static Questions questions;
    @Autowired
    private ControlPane controlPane;


    public static void setQuestions(Questions questions) {
        QuestionPreviewAttr.questions = questions;
    }


    @FXML
    public void initialize() {
        // 读取问题的样式
        questionStyleList = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);
        
        // 初始化展示所有的字体和居中样式

        stageFontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        stageTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        nameTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        nameFontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        answerTextAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        answerFontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        // 回显数据
        showConfigData();
    }
    
    @FXML
    public void selectStageImg(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file!= null) {
            stageBackgroundPath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void cancelStageImg(){
        stageBackgroundPath.setText("");
    }

    @FXML
    public void selectNameImg(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file!= null) {
            nameBackgroundPath.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void cancelNameImg(){
        nameBackgroundPath.setText("");
    }

    @FXML
    public void selectCodeImg(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file!= null) {
            answerBackgroundPath.setText(file.getAbsolutePath());
        }
    }


    @FXML
    public void cancelCodeImg(){
        answerBackgroundPath.setText("");
    }


    private void showConfigData() {
        Configs configs = configsMapper.selectById(1);
        showTiganConfig(configs);
        showAnswerConfig(configs);
        showStageConfig();
    }
    
    
    private void showStageConfig(){
        stageXInput.setText("100");
        stageYInput.setText("100");
        stageWidthInput.setText("100");
        stageHeightInput.setText("100");

       
        stageFontSelector.setValue(FontMap.defaultFontName());
        stageFontSize.setText(12 + "");

        String style1 = "2%%2%%300%%100%%false%%0xb31a1aff%%%%默认.ttf%%24%%false%%false%%false%%false%%居中对齐%%";
        if (!questionStyleList.isEmpty()) {
            QuestionStyle questionStyle = questionStyleList.get(questions.getSort());
            if (questionStyle != null) {
                if (!StringUtils.isEmpty(questionStyle.getStyle3())) {
                    style1 = questionStyle.getStyle3();
                }
            }
        }


        // 解析 excel 的样式
        String[] split = style1.split(ConfigSpliter.SPLITER);
        stageXInput.setText(split[0]);
        stageYInput.setText(split[1]);
        stageWidthInput.setText(split[2]);
        stageHeightInput.setText(split[3]);
        stageBackgroundChoice.setSelected(Boolean.valueOf(split[4]));
        stageColorInput.setValue(Color.valueOf(split[5]));
        stageBackgroundPath.setText(split[6]);
        stageFontSelector.setValue(split[7]);
        stageFontSize.setText(Integer.parseInt(split[8]) + "");
        stageDeleteChoice.setSelected(Boolean.valueOf(split[9]));
        stageBoldChoice.setSelected(Boolean.valueOf(split[10]));
        stageIticChoice.setSelected(Boolean.valueOf(split[11]));
        stageUnderlineChoice.setSelected(Boolean.valueOf(split[12]));
        stageTextAlignSelector.setValue(split[13]);
        if (split.length >= 15) {
            stageSpaceInput.setText(split[14]);
        }
    }


    private void showTiganConfig(Configs configs) {
        nameText.setText("");
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // 默认的字体
            nameFontSelector.setValue(FontMap.defaultFontName());
            nameFontSize.setText(12 + "");
            // x y w h
            nameSpaceInput.setText("0");
            wordSpaceInput.setText("0");
            nameXInput.setText(cfgArr[5]);
            nameYInput.setText(cfgArr[6]);
            nameWidthInput.setText(cfgArr[7]);
            nameHeightInput.setText(cfgArr[8]);
            nameText.setText(questions.getQuestion());
            if (!StringUtils.isEmpty(questions.getChoiceA())) {
                nameText.appendText(System.lineSeparator() + questions.getChoiceA());
            }
            if (!StringUtils.isEmpty(questions.getChoiceB())) {
                nameText.appendText(System.lineSeparator() + questions.getChoiceB());
            }
            if (!StringUtils.isEmpty(questions.getChoiceC())) {
                nameText.appendText(System.lineSeparator() + questions.getChoiceC());
            }
            if (!StringUtils.isEmpty(questions.getChoiceD())) {
                nameText.appendText(System.lineSeparator() + questions.getChoiceD());
            }
            if (!StringUtils.isEmpty(questions.getChoiceE())) {
                nameText.appendText(System.lineSeparator() + questions.getChoiceE());
            }
            if (!StringUtils.isEmpty(questions.getChoiceF())) {
                nameText.appendText(System.lineSeparator() + questions.getChoiceF());
            }
            nameTextAlignSelector.setValue("左对齐");
            // 根据配置详情设置样式

            String style1 = "200%%200%%1550%%200%%false%%0xffffcc00%%%%默认.ttf%%30%%false%%false%%false%%false%%左对齐%%10%%0";
            if (!questionStyleList.isEmpty()) {
                QuestionStyle questionStyle = questionStyleList.get(questions.getSort());
                if (questionStyle != null) {
                    if (!StringUtils.isEmpty(questionStyle.getStyle1())) {
                        style1 = questionStyle.getStyle1();
                    } 
                }
            }

            String[] split = style1.split(ConfigSpliter.SPLITER);
            nameXInput.setText(split[0]);
            nameYInput.setText(split[1]);
            nameWidthInput.setText(split[2]);
            nameHeightInput.setText(split[3]);
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
            if (split.length >= 16) {
                wordSpaceInput.setText(split[15]);
            }
            if (split.length >= 17) {
                spaceLineChoice.setSelected(Boolean.valueOf(split[16]));
            }
        }
    }
    
    
    
    @FXML
    public void saveCurrentConfig(){
        String style1 =
                stageXInput.getText() + ConfigSpliter.SPLITER +
                        stageYInput.getText() + ConfigSpliter.SPLITER +
                        stageWidthInput.getText() + ConfigSpliter.SPLITER +
                        stageHeightInput.getText() + ConfigSpliter.SPLITER +
                        stageBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageColorInput.getValue() + ConfigSpliter.SPLITER +
                        stageBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        stageFontSelector.getValue() + ConfigSpliter.SPLITER +
                        stageFontSize.getText() + ConfigSpliter.SPLITER +
                        stageDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageTextAlignSelector.getValue()  + ConfigSpliter.SPLITER +
                        stageSpaceInput.getText()
                ;
        questionStyleList.get(questions.getSort()).setStyle3(style1);
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);
        
        PreviewStack.reloadStyle();
    }

    @FXML
    public void saveCurrentSConfig(){
        String style1 =
                stageXInput.getText() + ConfigSpliter.SPLITER +
                        stageYInput.getText() + ConfigSpliter.SPLITER +
                        stageWidthInput.getText() + ConfigSpliter.SPLITER +
                        stageHeightInput.getText() + ConfigSpliter.SPLITER +
                        stageBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageColorInput.getValue() + ConfigSpliter.SPLITER +
                        stageBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        stageFontSelector.getValue() + ConfigSpliter.SPLITER +
                        stageFontSize.getText() + ConfigSpliter.SPLITER +
                        stageDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        stageSpaceInput.getText()
                ;

        for (QuestionStyle questionStyle : questionStyleList) {
            if (questionStyle.getStageNo().equals(questions.getStageNumber())) {
                questionStyle.setStyle3(style1);
            }
        }
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);

        PreviewStack.reloadStyle();
    }


    @FXML
    public void saveAllSConfig(){
        String style1 =
                stageXInput.getText() + ConfigSpliter.SPLITER +
                        stageYInput.getText() + ConfigSpliter.SPLITER +
                        stageWidthInput.getText() + ConfigSpliter.SPLITER +
                        stageHeightInput.getText() + ConfigSpliter.SPLITER +
                        stageBackgroundChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageColorInput.getValue() + ConfigSpliter.SPLITER +
                        stageBackgroundPath.getText() + ConfigSpliter.SPLITER +
                        stageFontSelector.getValue() + ConfigSpliter.SPLITER +
                        stageFontSize.getText() + ConfigSpliter.SPLITER +
                        stageDeleteChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageBoldChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageIticChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageUnderlineChoice.isSelected() + ConfigSpliter.SPLITER +
                        stageTextAlignSelector.getValue() + ConfigSpliter.SPLITER +
                        stageSpaceInput.getText()
                ;
        for (QuestionStyle questionStyle : questionStyleList) {
            questionStyle.setStyle3(style1);
        }
        ExcelParser.write(ExcelParser.S_PATH, questionStyleList, QuestionStyle.class);

        PreviewStack.reloadStyle();
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
                        (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText()) + ConfigSpliter.SPLITER +
                        (StringUtils.isEmpty(wordSpaceInput.getText())?"0":wordSpaceInput.getText()) + ConfigSpliter.SPLITER +
                        spaceLineChoice.isSelected()
                        ;
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
        PreviewStack.reloadStyle();
    }
    
    private void closeStage(){
        Stage stage = (Stage) (nameXInput.getScene().getWindow());
        stage.close();
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
                        (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText()) + ConfigSpliter.SPLITER +
                        (StringUtils.isEmpty(wordSpaceInput.getText())?"0":wordSpaceInput.getText()) + ConfigSpliter.SPLITER +
                        spaceLineChoice.isSelected()
                ;
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
        PreviewStack.reloadStyle();
    }
    
    
    @FXML
    public void updateAnswerText(){
        // 写入（更新）题干内容到 excel 里
        String group = questions.getGroup();
        List<Questions> parse =  ExcelParser.parse(BaseDir.TIKU_DIR + group, Questions.class);
        questions.setAnswer(answerText.getText());
        // 更新到数据库
        questionsMapper.updateById(questions);
        parse.set(questions.getSort(), questions);
        ExcelParser.write(BaseDir.TIKU_DIR + group, parse, Questions.class);
        PreviewStack.reloadStyle();
    }
    
    @FXML
    public void updateQuestionContent(){
        // 写入（更新）题干内容到 excel 里
        String group = questions.getGroup();
        List<Questions> parse =  ExcelParser.parse(BaseDir.TIKU_DIR + group, Questions.class);
        String[] split = nameText.getText().split("\n");
        questions.setQuestion(split[0]);
        if (split.length >= 2) {
            questions.setChoiceA(split[1]);
        } else {
            questions.setChoiceA("");
        }
        if (split.length >= 3) {
            questions.setChoiceB(split[2]);
        } else {
            questions.setChoiceB("");
        }
        if (split.length >= 4) {
            questions.setChoiceC(split[3]);
        } else {
            questions.setChoiceC("");
        }
        if (split.length >= 5) {
            questions.setChoiceD(split[4]);
        } else {
            questions.setChoiceD("");
        }
        if (split.length >= 6) {
            questions.setChoiceE(split[5]);
        } else {
            questions.setChoiceE("");
        }
        if (split.length >= 7) {
            questions.setChoiceF(split[6]);
        } else {
            questions.setChoiceF("");
        }
        parse.set(questions.getSort(), questions);
        // 更新到数据库
        questionsMapper.updateById(questions);
        ExcelParser.write(BaseDir.TIKU_DIR + group, parse, Questions.class);
        PreviewStack.reloadStyle();
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
                        (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText()) + ConfigSpliter.SPLITER +
                        (StringUtils.isEmpty(wordSpaceInput.getText())?"0":wordSpaceInput.getText()) + ConfigSpliter.SPLITER +
                        spaceLineChoice.isSelected()
                ;
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
        PreviewStack.reloadStyle();
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

        PreviewStack.reloadStyle();
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
        PreviewStack.reloadStyle();
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
        PreviewStack.reloadStyle();
    }


    private void showAnswerConfig(Configs configs) {
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            // 默认的字体
            answerFontSelector.setValue(FontMap.defaultFontName());
            answerFontSize.setText(12 + "");
            // x y w h
            // 
            PreviewPane previewPane = controlPane.getPreviewPane();
            VBox tiganBox = null;
            if (previewPane instanceof  QuestionPreviewPreview) {
                QuestionPreviewPreview preview =(QuestionPreviewPreview)previewPane;
                tiganBox = preview.tiganBox;
            } else {
                CluePreviewPreview preview =(CluePreviewPreview)previewPane;
                tiganBox = preview.tiganBox;
            }
            
            answerSpaceInput.setText("0");
            answerXInput.setText((int) tiganBox.getLayoutX() + "");
            answerYInput.setText((int) (tiganBox.getLayoutX() + tiganBox.getPrefHeight() + 10) + "");
            answerWidthInput.setText(cfgArr[7]);
            answerHeightInput.setText(cfgArr[8]);
            answerText.setText(questions.getAnswer());
            answerTextAlignSelector.setValue("左对齐");
            String style1 = "200%%700%%1550%%60%%false%%0xffffffff%%%%默认.ttf%%36%%false%%false%%false%%false%%居中对齐%%0";
            // 根据配置详情设置样式
            if (!questionStyleList.isEmpty()) {
                QuestionStyle questionStyle = questionStyleList.get(questions.getSort());
                if (questionStyle != null) {
                    if (!StringUtils.isEmpty(questionStyle.getStyle2())) {
                        style1 = questionStyle.getStyle2();
                        // 解析 excel 的样式
                    }
                }
            }

            String[] split = style1.split(ConfigSpliter.SPLITER);
            answerXInput.setText(split[0]);
            answerYInput.setText(split[1]);
            answerWidthInput.setText(split[2]);
            answerHeightInput.setText(split[3]);
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
