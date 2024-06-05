package competition.controller.match;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.preview.QuestionPreviewPreview;
import competition.entity.*;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Shear;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


// 试题显示大屏内容
@Component
@Scope("prototype")
public class QuestionPreview implements ReloadPane{


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

    @FXML
    private ImageView imageQ;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private StagesMapper stagesMapper;


    private static Questions current;

    private Questions inner;


    public static void setCurrent(Questions current) {
        QuestionPreview.current = current;
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

        Stages currentStage = controlPane.getCurrentStage();

        // 是否自动计时
        if (currentStage.getTimeCType() == 2) {
            //controlPane.timeReset();
            controlPane.startTiming();
        }

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
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
            tiganLabel.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
            tiganLabel.setLineSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split[3])));
            // 题目位置和大小
            tiganLabel.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));



            tiganBox.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split[5])));
            tiganBox.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split[6])));
            tiganBox.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
            tiganBox.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(split[8])));
            tiganBox.setSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split[3])));


            // 根据题型展示不同的内容
            String questionType = inner.getQuestionType();
            String resourceType = inner.getResourceType();

            if (resourceType.equals("音频")) {
                tiganLabel.setVisible(true);
                tiganBox.setVisible(true);
                imageQ.setVisible(false);
            } else if (resourceType.equals("视频")) {
                tiganLabel.setVisible(true);
                tiganBox.setVisible(true);
                imageQ.setVisible(false);
            } else if (resourceType.equals("图片")) {
                // 展示图片题目
                tiganLabel.setVisible(false);
                tiganBox.setVisible(false);
                imageQ.setVisible(true);
                imageQ.setLayoutX(0);
                imageQ.setLayoutY(0);
                imageQ.setFitWidth(ExtendedScreen.getWidth());
                imageQ.setFitHeight(ExtendedScreen.getHeight());
                try {
                    imageQ.setImage(new Image(new FileInputStream(new File( BaseDir.TIKU_RES_DIR +  inner.getQuestion())),
                        ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resourceType.equals("文字")) {
                tiganLabel.setVisible(true);
                tiganBox.setVisible(true);
                imageQ.setVisible(false);
            }

//            if (questionType.equals("单选题")) {
//                
//            } else if (questionType.equals("多选题")) {
//                
//            }  else if (questionType.equals("填空题")) {
//
//            }  else if (questionType.equals("简答题")) {
//
//            }  else if (questionType.equals("判断题")) {
//
//            }


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

            // 只有选择题展示选项
            tiganBox.getChildren().add(tiganLabel);
            if (questionType.equals("单选题") || questionType.equals("多选题") || questionType.equals("不定项选择")) {
                if (!StringUtils.isEmpty(inner.getChoiceA())) {
                    Text chA = new Text("A." + inner.getChoiceA());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceB())) {
                    Text chB = new Text("B." + inner.getChoiceB());
                    chB.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chB.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chB.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chB);
                }

                if (!StringUtils.isEmpty(inner.getChoiceC())) {
                    Text chA = new Text("C." + inner.getChoiceC());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceD())) {
                    Text chA = new Text("D." + inner.getChoiceD());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceE())) {
                    Text chA = new Text("E." + inner.getChoiceE());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceF())) {
                    Text chA = new Text("F." + inner.getChoiceF());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

            }
            else {
                // 不显示题号
                if (!StringUtils.isEmpty(inner.getChoiceA())) {
                    Text chA = new Text(inner.getChoiceA());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceB())) {
                    Text chB = new Text(inner.getChoiceB());
                    chB.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chB.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chB.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chB);
                }

                if (!StringUtils.isEmpty(inner.getChoiceC())) {
                    Text chA = new Text(inner.getChoiceC());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceD())) {
                    Text chA = new Text(inner.getChoiceD());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceE())) {
                    Text chA = new Text(inner.getChoiceE());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceF())) {
                    Text chA = new Text(inner.getChoiceF());
                    chA.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }
            }


            // 答案
            daanLabel.setText(split[4] + " " + inner.getAnswer());
            backPane.setLayoutX(tiganBox.getLayoutX());
            backPane.setLayoutY((tiganBox.getLayoutY() + tiganBox.getPrefHeight() + ScreenAdapter.calculateExH(10)));
            // 答案颜色
            daanLabel.setFill(Color.valueOf(split[1]));

            List<QuestionStyle> parse = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);
            if (!parse.isEmpty()) {
                QuestionStyle questionStyle = parse.get(inner.getSort());
                if (questionStyle != null) {
                    String style1 = questionStyle.getStyle1();
                    if (!StringUtils.isEmpty(style1)) {
                        String[] split1 = style1.split(ConfigSpliter.SPLITER);
                        tiganBox.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split1[0])));
                        tiganBox.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split1[1])));
                        tiganBox.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
                        tiganBox.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(split1[3])));
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
                                                ScreenAdapter.calculateExW(Integer.parseInt(split1[2])),
                                                ScreenAdapter.calculateExH(Integer.parseInt(split1[3])),
                                                false, false, false, false))));
                                } catch (FileNotFoundException e) {
                                    tiganBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                                }
                            } else {
                                tiganBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            // 填充背景颜色
                            tiganBox.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]),new CornerRadii(10), Insets.EMPTY)));
                        }

                        tiganBox.setSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split1[14])));
                        Font font = Font.font(FontMap.getFont(split1[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                            ScreenAdapter.calculateExW(Integer.parseInt(split1[8])));
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
                        tiganLabel.setLineSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split1[14])));

                        for (Node child : tiganBox.getChildren()) {
                            child.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
                            if (child instanceof Text) {
                                Text ch = (Text) child;
                                ch.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
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
                                ch.setLineSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split1[14])));
                            }
                        }
                    }

                    // 答案样式
                    String style2 = questionStyle.getStyle2();
                    if (!StringUtils.isEmpty(style2)) {
                        String[] split1 = style2.split(ConfigSpliter.SPLITER);

                        backPane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split1[0])));
                        backPane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split1[1])));
                        backPane.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
                        backPane.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(split1[3])));


                        if (Boolean.valueOf(split1[4])) {
                            // 填充背景图片
                            if (!StringUtils.isEmpty(split1[6])) {
                                try {
                                    backPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                            new BackgroundSize(
                                                ScreenAdapter.calculateExW(Integer.parseInt(split1[2])),
                                                ScreenAdapter.calculateExH(Integer.parseInt(split1[3])), false, false, false, false))));
                                } catch (FileNotFoundException e) {
                                    backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                                }
                            } else {
                                backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            // 填充背景颜色
                            backPane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), new CornerRadii(10), Insets.EMPTY)));
                        }


                        daanLabel.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
                        daanLabel.setLineSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split1[14])));
                        Font font = Font.font(FontMap.getFont(split1[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                            ScreenAdapter.calculateExW(Integer.parseInt(split1[8])));
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
                        daanLabel.setLineSpacing(ScreenAdapter.calculateExH(Integer.parseInt(split1[14])));
                    }


                    String style3 = questionStyle.getStyle3();
                    // 标题展示
                    stagePane.setVisible(Boolean.valueOf(split[12]));
                    String stageNumber = inner.getStageNumber();
                    Stages stages = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>().eq(Stages::getSortVal, stageNumber).last(" limit 1"));
                    if (stagePane.isVisible()) {
                        Stages currentStage1 = controlPane.getCurrentStage();
                        if (currentStage1.getQuestionType() == 3) {
                            stageLabel.setText(currentStage1.getName() + " " + stages.getName());
                        } else {
                            stageLabel.setText(stages.getName());
                        }

                        stageLabel.setFill(Color.valueOf(split[11]));
                        if (!StringUtils.isEmpty(style3)) {
                            String[] split1 = style3.split(ConfigSpliter.SPLITER);

                            stagePane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split1[0])));
                            stagePane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split1[1])));
                            stagePane.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
                            stagePane.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(split1[3])));


                            if (Boolean.valueOf(split1[4])) {
                                // 填充背景图片
                                if (!StringUtils.isEmpty(split1[6])) {
                                    try {
                                        stagePane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                                new BackgroundSize(
                                                    ScreenAdapter.calculateExW(Integer.parseInt(split1[2])),
                                                    ScreenAdapter.calculateExH(Integer.parseInt(split1[3])), false, false, false, false))));
                                    } catch (FileNotFoundException e) {
                                        stagePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                                    }
                                } else {
                                    stagePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                                }
                            } else {
                                // 填充背景颜色
                                stagePane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), new CornerRadii(10), Insets.EMPTY)));
                            }


                            stageLabel.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(split1[2])));
                            //stageLabel.setLineSpacing(Integer.parseInt(split1[14]));
                            Font font = Font.font(FontMap.getFont(split1[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                                ScreenAdapter.calculateExW(Integer.parseInt(split1[8])));
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
                            //stageLabel.setLineSpacing(Integer.parseInt(split1[14]));
                        }
                    }

                }
            }
        }
    }


    public void showAnswer() {
        if (inner.getResourceType().equals("图片")) {
            imageQ.setVisible(true);
            imageQ.setLayoutX(0);
            imageQ.setLayoutY(0);
            imageQ.setFitHeight(ExtendedScreen.getHeight());
            imageQ.setFitWidth(ExtendedScreen.getWidth());
            try {
                if (GlobalCache.showAnswer) {
                    imageQ.setImage(new Image(new FileInputStream(new File( BaseDir.TIKU_RES_DIR +  inner.getAnswer())),
                            ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, true));
                } else {
                    imageQ.setImage(new Image(new FileInputStream(new File( BaseDir.TIKU_RES_DIR +  inner.getQuestion())),
                        ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, true));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            backPane.setVisible(false);
        } else {
            backPane.setVisible(GlobalCache.showAnswer);
        }
    }

    public Questions getInner() {
        return inner;
    }

    @Override
    public void reload() {
        showData();
        if (GlobalCache.showAnswer) {
            if (inner.getResourceType().equals("图片")) {
                imageQ.setVisible(true);
                imageQ.setLayoutX(0);
                imageQ.setLayoutY(0);
                imageQ.setFitHeight(ExtendedScreen.getHeight());
                imageQ.setFitWidth(ExtendedScreen.getWidth());
                try {
                    imageQ.setImage(new Image(new FileInputStream(new File( BaseDir.TIKU_RES_DIR +  inner.getAnswer())),
                        ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                backPane.setVisible(false);
            } else {
                backPane.setVisible(true);
                imageQ.setVisible(false);
            }
        } else {
            backPane.setVisible(false);
        }
    }
}
