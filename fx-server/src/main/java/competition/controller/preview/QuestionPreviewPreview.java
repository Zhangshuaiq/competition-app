package competition.controller.preview;

import competition.controller.ControlPane;
import competition.controller.match.QuestionPreview;
import competition.controller.match.QuestionPreviewAttr;
import competition.controller.match.ReloadPane;
import competition.controller.match.StageQuestionPreview;
import competition.entity.Configs;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Shear;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


// 试题显示大屏内容
@Component
@Scope("prototype")
public class QuestionPreviewPreview extends PreviewPane {

    @Resource
    private ControlPane controlPane;

    @FXML
    private AnchorPane container;

    @FXML
    private Label timeLabel;


    @FXML
    public VBox tiganBox;

    @FXML
    private Text tiganLabel;
    @FXML
    private Text daanLabel;

    @Resource
    private ConfigsMapper configsMapper;
    


    private static Questions current;
    
    private Questions inner;


    public static void setCurrent(Questions current) {
        QuestionPreviewPreview.current = current;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
    
    
    @FXML
    public void initialize() {
        inner = current;
        PreviewStack.addPreview(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 鼠标右键打开详细的界面设置
                if (event.getButton() == MouseButton.SECONDARY) {
                    QuestionPreviewAttr.setQuestions(current);
                    QuestionPreviewAttr.setReloadPane(controlPane.getQuestionPreview());
                    Navigator.navigatorTo("/view/match/QuestionPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        showData();
    }


    private void showData() {
        inner = current;
        if (ExtendedScreen.hasShow()) {
            QuestionPreview.setCurrent(inner);
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/QuestionPreview.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((QuestionPreview) res);
            }
        }

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答题背景)), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, true, true)
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
            if (!StringUtils.isEmpty(current.getChoiceA())) {
                Text chA = new Text("A." + current.getChoiceA());
                chA.prefWidth(Integer.parseInt(split[7]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceB())) {
                Text chB = new Text("B." + current.getChoiceB());
                chB.prefWidth(Integer.parseInt(split[7]));
                tiganBox.getChildren().add(chB);
            }

            if (!StringUtils.isEmpty(current.getChoiceC())) {
                Text chA = new Text("C." + current.getChoiceB());
                chA.prefWidth(Integer.parseInt(split[7]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceD())) {
                Text chA = new Text("D." + current.getChoiceD());
                chA.prefWidth(Integer.parseInt(split[7]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceE())) {
                Text chA = new Text("E." + current.getChoiceE());
                chA.prefWidth(Integer.parseInt(split[7]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceF())) {
                Text chA = new Text("F." + current.getChoiceF());
                chA.prefWidth(Integer.parseInt(split[7]));
                tiganBox.getChildren().add(chA);
            }


            // 题型
            String tx = split[9];
            // 题型标注
            String txbz = split[10];

            // 无题型标注
            if (txbz.equals("4")) {
                tiganLabel.setText(current.getQuestionNumber() + current.getQuestion());
            } else if (txbz.equals("3")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(current.getQuestionNumber()  + current.getQuestion() + " " + current.getQuestionType());
                } else  if (tx.equals("2")) {
                    tiganLabel.setText(current.getQuestionNumber() + " " + current.getQuestionType()  + current.getQuestion());
                } else  if (tx.equals("1")) {
                    tiganLabel.setText( current.getQuestionType()  + " " + current.getQuestionNumber() +  current.getQuestion());
                }

            }else if (txbz.equals("2")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(current.getQuestionNumber()  + current.getQuestion() + "(" + current.getQuestionType() + ")");
                } else  if (tx.equals("2")) {
                    tiganLabel.setText(current.getQuestionNumber() + "(" + current.getQuestionType() + ")" + current.getQuestion());
                } else  if (tx.equals("1")) {
                    tiganLabel.setText("(" + current.getQuestionType()  + ")" + current.getQuestionNumber() +  current.getQuestion());
                }
            }else if (txbz.equals("1")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(current.getQuestionNumber()  + current.getQuestion() + "[" + current.getQuestionType() + "]");
                } else  if (tx.equals("2")) {
                    tiganLabel.setText(current.getQuestionNumber() + "[" + current.getQuestionType() + "]" + current.getQuestion());
                } else  if (tx.equals("1")) {
                    tiganLabel.setText("[" + current.getQuestionType()  + "]" + current.getQuestionNumber() +  current.getQuestion());
                }
            }
            // 答案
            daanLabel.setText(split[4] + " " + current.getAnswer());
            daanLabel.setLayoutX((int) tiganBox.getLayoutX());
            daanLabel.setLayoutY((int) (tiganBox.getLayoutX() + tiganBox.getPrefHeight() + 10));
            // 答案颜色
            daanLabel.setFill(Color.valueOf(split[1]));

            List<QuestionStyle> parse = ExcelParser.parse(ExcelParser.S_PATH,  QuestionStyle.class);
            if (!parse.isEmpty()) {
                QuestionStyle questionStyle = parse.get(current.getSort());
                if (questionStyle != null) {
                    String style1 = questionStyle.getStyle1();
                    if (!StringUtils.isEmpty(style1)) {

                        String[] split1 = style1.split(ConfigSpliter.SPLITER);

                        tiganBox.setLayoutX(Integer.parseInt(split1[0]));
                        tiganBox.setLayoutY(Integer.parseInt(split1[1]));
                        tiganBox.setPrefWidth(Integer.parseInt(split1[2]));
                        tiganBox.setPrefHeight(Integer.parseInt(split1[3]));
                        tiganBox.setSpacing(Integer.parseInt(split1[14]));
                        Font font = Font.font(split1[7], FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split1[8]));
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

                        daanLabel.setLayoutX(Integer.parseInt(split1[0]));
                        daanLabel.setLayoutY(Integer.parseInt(split1[1]));
                        // daanLabel.prefWidth(Integer.parseInt(split1[2]));
                        // daanLabel.prefHeight(Integer.parseInt(split1[3]));
                        daanLabel.setLineSpacing(Integer.parseInt(split1[14]));
                        Font font = Font.font(split1[7], FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split1[8]));
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
                }
            }
            // 倒计时
            timeLabel.setTextFill(Color.valueOf(split[23]));
            // TODO 字号可能有调整，因为字体可能需要在详情页发生变化 
            timeLabel.setFont(Font.font(Integer.parseInt(split[24])));
            timeLabel.setLayoutX(Integer.parseInt(split[29]));
            timeLabel.setLayoutY(Integer.parseInt(split[30]));
        }
    }



    public void showAnswer() {
        GlobalCache.showAnswer = !GlobalCache.showAnswer;
        daanLabel.setVisible(GlobalCache.showAnswer);
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
    public void reload() {
        daanLabel.setVisible(GlobalCache.showAnswer);
        showData();
    }
}
