package competition.controller.preview;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.CluePreview;
import competition.controller.match.QuestionPreview;
import competition.controller.match.QuestionPreviewAttr;
import competition.controller.match.ReloadPane;
import competition.entity.Configs;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CluePreviewPreview extends PreviewPane<Questions>{


    @FXML
    private VBox buzzBox;
    
    @FXML
    private StackPane backPane, stagePane, buttonPane;

    @FXML
    private Text daanLabel, stageLabel;

    @Resource
    private ControlPane controlPane;

    @FXML
    private AnchorPane container;

    @FXML
    public VBox tiganBox;



    @FXML
    public ScrollPane scrollPane;


    @FXML
    private Text tiganLabel;


    @Resource
    private ConfigsMapper configsMapper;
    
    @Resource
    private WsMessageHandler messageHandler;

    private Questions inner;

    private String txbz;

    @Override
    public void setData(Questions inner) {
        this.inner = inner;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }


    @Override
    public void scrollDown() {
        scrollPane.setVvalue(scrollPane.getVvalue() + 0.1);
        if (ExtendedScreen.hasShow()) {
            ReloadPane questionPreview = controlPane.getQuestionPreview();
            if (questionPreview != null) {
                questionPreview.scrollDown();
            }
        }
    }

    @Override
    public void scrollDownAnswer() {
    }

    @Override
    public void scrollUpAnswer() {
    }


    @Override
    public void scrollUp() {
        scrollPane.setVvalue(scrollPane.getVvalue() - 0.1);
        //tiganBox.setTranslateY(tiganBox.getTranslateY() - ScreenAdapter.calculateMainH(GlobalCache.SCROLL_STEP));
        if (ExtendedScreen.hasShow()) {
            ReloadPane questionPreview = controlPane.getQuestionPreview();
            if (questionPreview != null) {
                questionPreview.scrollUp();
            }
        }
    }

    @FXML
    public void initPage() {
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() != 0) {
                event.consume();
            }
        });


        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: rgba(0, 0, 0, 0); -fx-background-color: transparent; -fx-border-color: transparent; -fx-border-radius: 10px;-fx-background-radius: 10px; -fx-padding: 0;");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        
        scrollPane.toFront();
        tiganBox.toFront();
        backPane.toFront();
        buttonPane.prefWidth(Navigator.getWidth());
        buttonPane.setPrefWidth(Navigator.getWidth());
        buttonPane.setLayoutY(ScreenAdapter.calculateMainY(buttonPane.getLayoutY()));
        PreviewStack.addPreview(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 鼠标右键打开详细的界面设置
                if (event.getButton() == MouseButton.SECONDARY) {
                    QuestionPreviewAttr.setQuestions(inner);
                    Navigator.navigatorTo("/view/match/QuestionPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        showData();
    }

    @Override
    public void reloadStyle() {
        this.reload();
        controlPane.timeReset();
    }


    private void showData() {
        tiganBox.setPadding(new Insets(ScreenAdapter.calculateMainX(20)));
        backPane.setPadding(new Insets(0, ScreenAdapter.calculateMainX(20), 0, ScreenAdapter.calculateMainX(20)));
        Questions questions = JSON.parseObject(JSON.toJSONString(inner), Questions.class);

//        questions.setChoiceA("");
//        questions.setChoiceB("");
//        questions.setChoiceC("");
//        questions.setChoiceD("");
//        questions.setChoiceE("");
//        questions.setChoiceF("");
//        messageHandler.notifyPage("clue", questions);
        if (ExtendedScreen.hasShow()) {
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/CluePreview.fxml", inner);
            if (res != null) {
                controlPane.setQuestionPreview((CluePreview) res);
            }
        }

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
        }

        tiganBox.getChildren().clear();

        Configs configs = configsMapper.selectById(1);
        String cfgVal = configs.getConfig();
        if (!StringUtils.isEmpty(cfgVal)) {
            String[] split = cfgVal.split(ConfigSpliter.SPLITER);

            // 题干颜色
            tiganLabel.setFill(Color.valueOf(split[0]));
            // 题目行距
            tiganLabel.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7]) -20));
            tiganLabel.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
            // 题目位置和大小
            tiganLabel.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])) - 20);


            tiganBox.getChildren().add(tiganLabel);

            tiganBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[5])));
            tiganBox.setLayoutY( ScreenAdapter.calculateMainY(Integer.parseInt(split[6])));
            tiganBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
            tiganBox.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[8])));
            tiganBox.setSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));

            // 选项
            if (!StringUtils.isEmpty(inner.getChoiceA())) {
                Text chA = new Text(inner.getChoiceA());
                chA.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceB())) {
                Text chB = new Text(inner.getChoiceB());
                chB.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chB.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chB.setFill(Color.valueOf(split[0]));
                chB.setVisible(false);
                tiganBox.getChildren().add(chB);
            }

            if (!StringUtils.isEmpty(inner.getChoiceC())) {
                Text chA = new Text(inner.getChoiceC());
                chA.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceD())) {
                Text chA = new Text(inner.getChoiceD());
                chA.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceE())) {
                Text chA = new Text(inner.getChoiceE());
                chA.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceF())) {
                Text chA = new Text(inner.getChoiceF());
                chA.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }


            // 题型
            String tx = split[9];
            // 题型标注
            String txbz = split[10];
            this.txbz = txbz;
            questions.setTxbz(txbz);
            messageHandler.notifyPage("clue", questions);
            // 无题型标注
            if (txbz.equals("4")) {
                tiganLabel.setText(inner.getOrder() + "、" + inner.getQuestion());
            } else if (txbz.equals("3")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(inner.getOrder() + "、" + inner.getQuestion() + " " + inner.getQuestionType());
                } else if (tx.equals("2")) {
                    tiganLabel.setText(inner.getOrder() + "、" + " " + inner.getQuestionType() + inner.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText(inner.getQuestionType() + "、" + " " + inner.getOrder() + inner.getQuestion());
                }

            } else if (txbz.equals("2")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(inner.getOrder() + "、" + inner.getQuestion() + "(" + inner.getQuestionType() + ")");
                } else if (tx.equals("2")) {
                    tiganLabel.setText(inner.getOrder() + "、" + "(" + inner.getQuestionType() + ")" + inner.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText("(" + inner.getQuestionType() + ")" + inner.getOrder() + "、" + inner.getQuestion());
                }
            } else if (txbz.equals("1")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(inner.getOrder() + "、" + inner.getQuestion() + "[" + inner.getQuestionType() + "]");
                } else if (tx.equals("2")) {
                    tiganLabel.setText(inner.getOrder() + "、" + "[" + inner.getQuestionType() + "]" + inner.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText("[" + inner.getQuestionType() + "]" + inner.getOrder() + "、" + inner.getQuestion());
                }
            }
            if (controlPane.getCurrentStage().getShowNumber() == 0) {
                tiganLabel.setText(tiganLabel.getText().replace(inner.getOrder() + "、", ""));
            }
            if (GlobalCache.MAX_TEAM_NUMBER <= 0) {
                tiganLabel.setText(tiganLabel.getText() + "(未注册)");
            }
            tiganLabel.setText(tiganLabel.getText().replace("\u00A0", " "));

            // 答案
            daanLabel.setText(split[4] + " " + inner.getAnswer().replace("\u00A0", " "));
            backPane.setLayoutX(tiganBox.getLayoutX());
            backPane.setLayoutY((tiganBox.getLayoutY() + tiganBox.getPrefHeight() + ScreenAdapter.calculateMainY(10)));
            // 答案颜色
            daanLabel.setFill(Color.valueOf(split[1]));



            String style1 = "200%%200%%1550%%200%%false%%0xffffcc00%%%%默认.ttf%%30%%false%%false%%false%%false%%左对齐%%10%%0";
            String style2 = "200%%700%%1550%%60%%false%%0x990000ff%%%%默认.ttf%%36%%false%%false%%false%%false%%居中对齐%%0";
            String style3 = "2%%2%%300%%100%%false%%0xb31a1aff%%%%默认.ttf%%24%%false%%false%%false%%false%%居中对齐%%";
            List<QuestionStyle> parse = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);

            if (!parse.isEmpty()) {
                QuestionStyle questionStyle = parse.get(inner.getSort());
                if (questionStyle != null) {
                    if (!StringUtils.isEmpty(questionStyle.getStyle1())) {
                        style1 = questionStyle.getStyle1();
                    }

                    // 答案样式

                    if (!StringUtils.isEmpty(questionStyle.getStyle2())) {
                        style2 = questionStyle.getStyle2();
                    }

                    // 标题展示
                    if (!StringUtils.isEmpty(questionStyle.getStyle3())) {
                        style3 = questionStyle.getStyle3();
                    }
                }
            }

            {

                String[] split1 = style1.split(ConfigSpliter.SPLITER);

                tiganBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split1[0])));
                tiganBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split1[1])));
                tiganBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
                tiganBox.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));
                if (split1.length >= 16) {
                    int wordSpace = Integer.parseInt(split1[15]);
                    if (wordSpace > 0) {
                        String sli = "";
                        for (int m = 0; m < wordSpace; m++) {
                            sli = sli + " ";
                        }
                        List<String> cs = new ArrayList<>();
                        for (char aChar : tiganLabel.getText().toCharArray()) {
                            cs.add(aChar + "");
                        }
                        tiganLabel.setText(String.join(sli, cs));
                        for (Node child : tiganBox.getChildren()) {
                            Text text = (Text) child;
                            List<String> cs1 = new ArrayList<>();
                            for (char aChar : text.getText().toCharArray()) {
                                cs1.add(aChar + "");
                            }
                            text.setText(String.join(sli, cs1));
                        }

                    }
                }

                if (Boolean.valueOf(split1[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(split1[6])) {
                        try {
                            tiganBox.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                            ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])),
                                            ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])),
                                            false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            tiganBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                        }
                    } else {
                        tiganBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    tiganBox.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                }

                tiganBox.setSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));
                Font font = FontMap.load(split1[7], ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
                tiganLabel.setFont(font);
                tiganLabel.setUnderline(Boolean.valueOf(split1[12]));
                tiganLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                if (Boolean.valueOf(split1[10])) {
                    tiganLabel.setStroke(Color.valueOf(split[0]));
                    tiganLabel.setStrokeWidth(1);
                } else {
                    tiganLabel.setStroke(null);

                }
                tiganLabel.getTransforms().clear();
                if (Boolean.valueOf(split1[11])) {
                    tiganLabel.getTransforms().add(new Shear(-0.3, 0));
                }
                tiganLabel.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                tiganLabel.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));

                for (Node child : tiganBox.getChildren()) {
                    child.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
                    if (child instanceof Text) {
                        Text ch = (Text) child;
                        ch.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
                        ch.setFont(font);
                        ch.setUnderline(Boolean.valueOf(split1[12]));
                        ch.setStrikethrough(Boolean.valueOf(split1[9]));
                        if (Boolean.valueOf(split1[10])) {
                            ch.setStroke(ch.getFill());
                            ch.setStrokeWidth(1);
                        } else {
                            ch.setStroke(null);

                        }
                        ch.getTransforms().clear();
                        if (Boolean.valueOf(split1[11])) {
                            ch.getTransforms().add(new Shear(-0.3, 0));
                        }
                        ch.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                        ch.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));
                    }
                }

                // 如果有一行空行
                if (split1.length > 16 && Boolean.parseBoolean(split1[16])) {
                    Text blankLine = new Text("11");
                    blankLine.setFont(font);
                    blankLine.setVisible(false);
                    tiganBox.getChildren().add(1, blankLine);
                }
            }



            {

                String[] split1 = style2.split(ConfigSpliter.SPLITER);

                backPane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split1[0])));
                backPane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split1[1])));
                backPane.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
                backPane.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));


                if (Boolean.valueOf(split1[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(split1[6])) {
                        try {
                            backPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                            ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])) ,
                                            ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])),
                                            false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                        }
                    } else {
                        backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    backPane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]),  new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                }


                daanLabel.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2]) - 40) );
                daanLabel.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])) );
                Font font = FontMap.load(split1[7], ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
                daanLabel.setFont(font);
                daanLabel.setUnderline(Boolean.valueOf(split1[12]));
                daanLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                if (Boolean.valueOf(split1[10])) {
                    daanLabel.setStroke(Color.valueOf(split[1]));
                    daanLabel.setFill(Color.valueOf(split[1]));
                    daanLabel.setStrokeWidth(1);
                } else {
                    daanLabel.setStroke(null);
                }
                daanLabel.getTransforms().clear();
                if (Boolean.valueOf(split1[11])) {
                    daanLabel.getTransforms().add(new Shear(-0.3, 0));
                }
                daanLabel.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                daanLabel.setLineSpacing(Integer.parseInt(split1[14]));
            }

            stagePane.setVisible(Boolean.valueOf(split[12]));
            if (stagePane.isVisible()) {
                stageLabel.setText(controlPane.getCurrentStage().getName());
                stageLabel.setFill(Color.valueOf(split[11]));
                if (!StringUtils.isEmpty(style3)) {
                    String[] split1 = style3.split(ConfigSpliter.SPLITER);

                    stagePane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split1[0])));
                    stagePane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split1[1])));
                    stagePane.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
                    stagePane.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));


                    if (Boolean.valueOf(split1[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(split1[6])) {
                            try {
                                stagePane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(
                                                ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])) ,
                                                ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])) ,
                                                false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                stagePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                            }
                        } else {
                            stagePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        stagePane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]),  new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
                    }


                    stageLabel.setWrappingWidth( ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])) );
                    if (split1.length >= 15) {
                        stageLabel.setLineSpacing( ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])) );
                    }
                    Font font = FontMap.load(split1[7], ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
                    stageLabel.setFont(font);
                    stageLabel.setUnderline(Boolean.valueOf(split1[12]));
                    stageLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                    if (Boolean.valueOf(split1[10])) {
                        stageLabel.setStroke(Color.valueOf(split[0]));
                        stageLabel.setStrokeWidth(1);
                    } else {
                        stageLabel.setStroke(null);
                    }
                    stageLabel.getTransforms().clear();
                    if (Boolean.valueOf(split1[11])) {
                        stageLabel.getTransforms().add(new Shear(-0.3, 0));
                    }
                    stageLabel.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                }
            }
        }

        scrollPane.setLayoutX(tiganBox.getLayoutX());
        scrollPane.setLayoutY(tiganBox.getLayoutY());
       // scrollPane.setPrefWidth(tiganBox.getPrefWidth());
        scrollPane.setFitToWidth(true);
        for (Node child : tiganBox.getChildren()) {
            Text t = (Text) child;
            t.setWrappingWidth(tiganBox.getPrefWidth() - ScreenAdapter.calculateMainX(40));
        }
        //scrollPane.setPrefHeight(tiganBox.getPrefHeight());
        scrollPane.setFitToHeight(true);

    }


    public void showAnswer() {
        backPane.setVisible(GlobalCache.showAnswer);
        ReloadPane questionPreview = controlPane.getQuestionPreview();
        if (ExtendedScreen.hasShow()) {
            if (questionPreview != null) {
                questionPreview.showAnswer();
            }
        }
    }

    @Override
    public void reload() {
        backPane.setVisible(GlobalCache.showAnswer);
        showData();
    }

    
    @FXML
    public void showClue(){
        ObservableList<Node> children = tiganBox.getChildren();
        for (Node child : children) {
            if (child instanceof Text) {
                Text text = (Text) child;
                if (!text.isVisible() && !Objects.equals(text.getText(), "11")) {
                    text.setVisible(true);
                    break;
                }
            }
        }
        Questions questions = JSON.parseObject(JSON.toJSONString(inner), Questions.class);
        questions.setChoiceA("");
        questions.setChoiceB("");
        questions.setChoiceC("");
        questions.setChoiceD("");
        questions.setChoiceE("");
        questions.setChoiceF("");
        for (Node child : children) {
            if (child == tiganLabel) {
                continue;
            }
            Text text = (Text) child;
            if (child.isVisible()) {
                if (StringUtils.isEmpty(questions.getChoiceA())) {
                    questions.setChoiceA(text.getText());
                } else if (StringUtils.isEmpty(questions.getChoiceB())) {
                    questions.setChoiceB(text.getText());
                } else if (StringUtils.isEmpty(questions.getChoiceC())) {
                    questions.setChoiceC(text.getText());
                } else if (StringUtils.isEmpty(questions.getChoiceD())) {
                    questions.setChoiceD(text.getText());
                } else if (StringUtils.isEmpty(questions.getChoiceE())) {
                    questions.setChoiceE(text.getText());
                } else if (StringUtils.isEmpty(questions.getChoiceF())) {
                    questions.setChoiceF(text.getText());
                }
            }
        }
        questions.setTxbz(txbz);
        messageHandler.sendClue( questions);

        ReloadPane questionPreview = controlPane.getQuestionPreview();
        if (questionPreview != null) {
            questionPreview.reload(0, "");
            CluePreview cluePreview = (CluePreview) questionPreview;
            cluePreview.showClue();
        }
    }

    @Override
    public Questions getData() {
        return inner;
    }
}
