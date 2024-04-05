package competition.controller.match;


import competition.controller.ControlPane;
import competition.entity.Configs;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class CluePreview implements ReloadPane {


    @FXML
    private StackPane backPane, stagePane;

    @FXML
    private Text daanLabel, stageLabel;

    @Resource
    private ControlPane controlPane;

    @FXML
    private AnchorPane container;

    @FXML
    public VBox tiganBox;

    @FXML
    private Text tiganLabel;


    @Resource
    private ConfigsMapper configsMapper;


    private static Questions current;

    private Questions inner;


    public static void setCurrent(Questions current) {
        CluePreview.current = current;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }


    @FXML
    public void initialize() {
        inner = current;
        showData();
    }


    private void showData() {
        inner = current;
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        tiganBox.getChildren().clear();

        Configs configs = configsMapper.selectById(1);
        String cfgVal = configs.getConfig();
        if (!StringUtils.isEmpty(cfgVal)) {
            String[] split = cfgVal.split(ConfigSpliter.SPLITER);

            // 题干颜色
            tiganLabel.setFill(Color.valueOf(split[0]));
            // 题目行距
            tiganLabel.setWrappingWidth(Integer.parseInt(split[7]));
            tiganLabel.setLineSpacing(Integer.parseInt(split[3]));
            // 题目位置和大小
            tiganLabel.prefWidth(Integer.parseInt(split[7]));


            tiganBox.getChildren().add(tiganLabel);

            tiganBox.setLayoutY(Integer.parseInt(split[5]));
            tiganBox.setLayoutY(Integer.parseInt(split[6]));
            tiganBox.setPrefWidth(Integer.parseInt(split[7]));
            tiganBox.setPrefHeight(Integer.parseInt(split[8]));
            tiganBox.setSpacing(Integer.parseInt(split[3]));

            // 选项
            if (!StringUtils.isEmpty(inner.getChoiceA())) {
                Text chA = new Text(inner.getChoiceA());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceB())) {
                Text chB = new Text(inner.getChoiceB());
                chB.prefWidth(Integer.parseInt(split[7]));
                chB.setFill(Color.valueOf(split[0]));
                chB.setVisible(false);
                tiganBox.getChildren().add(chB);
            }

            if (!StringUtils.isEmpty(inner.getChoiceC())) {
                Text chA = new Text(inner.getChoiceC());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceD())) {
                Text chA = new Text(inner.getChoiceD());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceE())) {
                Text chA = new Text(inner.getChoiceE());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(inner.getChoiceF())) {
                Text chA = new Text(inner.getChoiceF());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                chA.setVisible(false);
                tiganBox.getChildren().add(chA);
            }


            // 题型
            String tx = split[9];
            // 题型标注
            String txbz = split[10];

            // 无题型标注
            if (txbz.equals("4")) {
                tiganLabel.setText(inner.getQuestionNumber() + "、" + inner.getQuestion());
            } else if (txbz.equals("3")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(inner.getQuestionNumber() + "、" + inner.getQuestion() + " " + inner.getQuestionType());
                } else if (tx.equals("2")) {
                    tiganLabel.setText(inner.getQuestionNumber() + "、" + " " + inner.getQuestionType() + inner.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText(inner.getQuestionType() + "、" + " " + inner.getQuestionNumber() + inner.getQuestion());
                }

            } else if (txbz.equals("2")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(inner.getQuestionNumber() + "、" + inner.getQuestion() + "(" + inner.getQuestionType() + ")");
                } else if (tx.equals("2")) {
                    tiganLabel.setText(inner.getQuestionNumber() + "、" + "(" + inner.getQuestionType() + ")" + inner.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText("(" + inner.getQuestionType() + ")" + inner.getQuestionNumber() + "、" + inner.getQuestion());
                }
            } else if (txbz.equals("1")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(inner.getQuestionNumber() + "、" + inner.getQuestion() + "[" + inner.getQuestionType() + "]");
                } else if (tx.equals("2")) {
                    tiganLabel.setText(inner.getQuestionNumber() + "、" + "[" + inner.getQuestionType() + "]" + inner.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText("[" + inner.getQuestionType() + "]" + inner.getQuestionNumber() + "、" + inner.getQuestion());
                }
            }
            if (controlPane.getCurrentStage().getShowNumber() == 0) {
                tiganLabel.setText(tiganLabel.getText().replace(inner.getQuestionNumber() + "、", ""));
            }
            // 答案
            daanLabel.setText(split[4] + " " + inner.getAnswer());
            backPane.setLayoutX((int) tiganBox.getLayoutX());
            backPane.setLayoutY((int) (tiganBox.getLayoutX() + tiganBox.getPrefHeight() + 10));
            // 答案颜色
            daanLabel.setFill(Color.valueOf(split[1]));

            List<QuestionStyle> parse = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);
            if (!parse.isEmpty()) {
                QuestionStyle questionStyle = parse.get(inner.getSort());
                if (questionStyle != null) {
                    String style1 = questionStyle.getStyle1();
                    if (!StringUtils.isEmpty(style1)) {

                        String[] split1 = style1.split(ConfigSpliter.SPLITER);

                        tiganBox.setLayoutX(Integer.parseInt(split1[0]));
                        tiganBox.setLayoutY(Integer.parseInt(split1[1]));
                        tiganBox.setPrefWidth(Integer.parseInt(split1[2]));
                        tiganBox.setPrefHeight(Integer.parseInt(split1[3]));
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
                                            new BackgroundSize(Integer.parseInt(split1[2]), Integer.parseInt(split1[3]), false, false, false, false))));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            // 填充背景颜色
                            tiganBox.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                        }

                        tiganBox.setSpacing(Integer.parseInt(split1[14]));
                        Font font = Font.font(FontMap.getFont(split1[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split1[8]));
                        tiganLabel.setFont(font);
                        tiganLabel.setUnderline(Boolean.valueOf(split1[12]));
                        tiganLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                        if (Boolean.valueOf(split1[10])) {
                            tiganLabel.setStroke(Color.valueOf(split[0]));
                        } else {
                            tiganLabel.setStroke(null);

                        }
                        tiganLabel.getTransforms().clear();
                        if (Boolean.valueOf(split1[11])) {
                            tiganLabel.getTransforms().add(new Shear(-0.3, 0));
                        }
                        tiganLabel.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                        tiganLabel.setLineSpacing(Integer.parseInt(split1[14]));

                        for (Node child : tiganBox.getChildren()) {
                            if (child instanceof Text) {
                                Text ch = (Text) child;
                                ch.setFont(font);
                                ch.setUnderline(Boolean.valueOf(split1[12]));
                                ch.setStrikethrough(Boolean.valueOf(split1[9]));
                                if (Boolean.valueOf(split1[10])) {
                                    ch.setStroke(ch.getFill());
                                } else {
                                    ch.setStroke(null);

                                }
                                ch.getTransforms().clear();
                                if (Boolean.valueOf(split1[11])) {
                                    ch.getTransforms().add(new Shear(-0.3, 0));
                                }
                                ch.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                                ch.setLineSpacing(Integer.parseInt(split1[14]));
                            }
                        }
                    }

                    // 答案样式
                    String style2 = questionStyle.getStyle2();
                    if (!StringUtils.isEmpty(style2)) {

                        String[] split1 = style2.split(ConfigSpliter.SPLITER);

                        backPane.setLayoutX(Integer.parseInt(split1[0]));
                        backPane.setLayoutY(Integer.parseInt(split1[1]));
                        backPane.prefWidth(Integer.parseInt(split1[2]));
                        backPane.prefHeight(Integer.parseInt(split1[3]));


                        if (Boolean.valueOf(split1[4])) {
                            // 填充背景图片
                            if (!StringUtils.isEmpty(split1[6])) {
                                try {
                                    backPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                            new BackgroundSize(Integer.parseInt(split1[2]), Integer.parseInt(split1[3]), false, false, false, false))));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            // 填充背景颜色
                            backPane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                        }


                        daanLabel.setWrappingWidth(Integer.parseInt(split1[2]));
                        daanLabel.setLineSpacing(Integer.parseInt(split1[14]));
                        Font font = Font.font(FontMap.getFont(split1[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split1[8]));
                        daanLabel.setFont(font);
                        daanLabel.setUnderline(Boolean.valueOf(split1[12]));
                        daanLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                        if (Boolean.valueOf(split1[10])) {
                            daanLabel.setStroke(Color.valueOf(split[0]));
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


                    String style3 = questionStyle.getStyle3();
                    // 标题展示
                    stagePane.setVisible(Boolean.valueOf(split[12]));
                    if (stagePane.isVisible()) {
                        stageLabel.setText(controlPane.getCurrentStage().getName());
                        stageLabel.setFill(Color.valueOf(split[11]));
                        if (!StringUtils.isEmpty(style3)) {
                            String[] split1 = style3.split(ConfigSpliter.SPLITER);

                            stagePane.setLayoutX(Integer.parseInt(split1[0]));
                            stagePane.setLayoutY(Integer.parseInt(split1[1]));
                            stagePane.setPrefWidth(Integer.parseInt(split1[2]));
                            stagePane.setPrefHeight(Integer.parseInt(split1[3]));


                            if (Boolean.valueOf(split1[4])) {
                                // 填充背景图片
                                if (!StringUtils.isEmpty(split1[6])) {
                                    try {
                                        stagePane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                                new BackgroundSize(Integer.parseInt(split1[2]), Integer.parseInt(split1[3]), false, false, false, false))));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                // 填充背景颜色
                                stagePane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                            }


                            stageLabel.setWrappingWidth(Integer.parseInt(split1[2]));
                            stageLabel.setLineSpacing(Integer.parseInt(split1[14]));
                            Font font = Font.font(FontMap.getFont(split1[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split1[8]));
                            stageLabel.setFont(font);
                            stageLabel.setUnderline(Boolean.valueOf(split1[12]));
                            stageLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                            if (Boolean.valueOf(split1[10])) {
                                stageLabel.setStroke(Color.valueOf(split[0]));
                            } else {
                                stageLabel.setStroke(null);
                            }
                            stageLabel.getTransforms().clear();
                            if (Boolean.valueOf(split1[11])) {
                                stageLabel.getTransforms().add(new Shear(-0.3, 0));
                            }
                            stageLabel.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                            stageLabel.setLineSpacing(Integer.parseInt(split1[14]));
                        }
                    }

                }
            }
        }
    }


    public void showAnswer() {
        GlobalCache.showAnswer = !GlobalCache.showAnswer;
        backPane.setVisible(GlobalCache.showAnswer);
        ReloadPane questionPreview = controlPane.getQuestionPreview();
        if (ExtendedScreen.hasShow()) {
            if (questionPreview != null) {
                questionPreview.showAnswer();
            }
        }
    }

    public void hideTiming() {
        //timeLabel.setVisible(false);
    }

    public void startTiming(int value) {
//        timeVal = value;
//        timeLabel.setText("答题剩余时间:" + timeVal + " 秒");
//        timeLabel.setVisible(true);
//        timeline.setCycleCount(-1);
//        timeline.play();
    }

    public void stopTiming() {
//        if (timeline != null) {
//            timeline.stop();
//        }
    }

    @Override
    public void reload(int time, String timeTxt) {
        ObservableList<Node> children = tiganBox.getChildren();
        for (Node child : children) {
            if (!child.isVisible()) {
                child.setVisible(true);
                break;
            }
        }
    }

    public Questions getInner() {
        return inner;
    }

    @Override
    public void reload() {
        backPane.setVisible(GlobalCache.showAnswer);
        showData();
    }
    
    
}
