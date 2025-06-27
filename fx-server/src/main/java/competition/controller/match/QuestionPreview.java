package competition.controller.match;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.entity.*;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Shear;
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
public class QuestionPreview implements ReloadPane<Questions>{


    @FXML
    private VBox buzzBox;

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

    @FXML
    public ScrollPane scrollPane;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private StagesMapper stagesMapper;



    private Questions inner;

    @Override
    public void setData(Questions data) {
        inner = data;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }


    @Override
    public void scrollDown() {
        scrollPane.setVvalue(scrollPane.getVvalue() + 0.1);
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
    }

    @FXML
    public void initPage() {
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
        showData();
    }


    private void showData() {
        tiganBox.setPadding(new Insets(ScreenAdapter.calculateExX(20)));
        backPane.setPadding(new Insets(0, ScreenAdapter.calculateExX(20), 0, ScreenAdapter.calculateExX(20)));
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
        }
        tiganBox.getChildren().clear();
        Configs configs = configsMapper.selectById(1);
        String cfgVal = configs.getConfig();
        if (!StringUtils.isEmpty(cfgVal)) {
            String[] split = cfgVal.split(ConfigSpliter.SPLITER);

            // 题干颜色
            tiganLabel.setFill(Color.valueOf(split[0]));
            // 题目行距
            tiganLabel.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7]) - 20));
            tiganLabel.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split[3])));
            // 题目位置和大小
            tiganLabel.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7]) - 20));



            tiganBox.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split[5])));
            tiganBox.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split[6])));
            tiganBox.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
            tiganBox.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(split[8])));
            tiganBox.setSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split[3])));


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

            if (inner.getShowNumber() == 0) {
                tiganLabel.setText(tiganLabel.getText().replace(inner.getOrder() + "、", ""));
            }

            if (GlobalCache.MAX_TEAM_NUMBER <= 0) {
                tiganLabel.setText(tiganLabel.getText() + "(未注册)");
            }
            tiganLabel.setText(tiganLabel.getText().replace("\u00A0", " "));

            // 只有选择题展示选项
            tiganBox.getChildren().add(tiganLabel);
            if (questionType.equals("单选题") || questionType.equals("多选题") || questionType.equals("不定项选择题")) {
                if (!StringUtils.isEmpty(inner.getChoiceA())) {
                    Text chA = new Text("A." + inner.getChoiceA());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceB())) {
                    Text chB = new Text("B." + inner.getChoiceB());
                    chB.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chB.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chB.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chB);
                }

                if (!StringUtils.isEmpty(inner.getChoiceC())) {
                    Text chA = new Text("C." + inner.getChoiceC());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceD())) {
                    Text chA = new Text("D." + inner.getChoiceD());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceE())) {
                    Text chA = new Text("E." + inner.getChoiceE());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceF())) {
                    Text chA = new Text("F." + inner.getChoiceF());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

            }
            else {
                // 不显示题号
                if (!StringUtils.isEmpty(inner.getChoiceA())) {
                    Text chA = new Text(inner.getChoiceA());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceB())) {
                    Text chB = new Text(inner.getChoiceB());
                    chB.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chB.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chB.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chB);
                }

                if (!StringUtils.isEmpty(inner.getChoiceC())) {
                    Text chA = new Text(inner.getChoiceC());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceD())) {
                    Text chA = new Text(inner.getChoiceD());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceE())) {
                    Text chA = new Text(inner.getChoiceE());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }

                if (!StringUtils.isEmpty(inner.getChoiceF())) {
                    Text chA = new Text(inner.getChoiceF());
                    chA.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split[7])));
                    chA.setFill(Color.valueOf(split[0]));
                    tiganBox.getChildren().add(chA);
                }
            }


            // 答案
            daanLabel.setText(split[4] + " " + inner.getAnswer());
            backPane.setLayoutX(tiganBox.getLayoutX());
            backPane.setLayoutY((tiganBox.getLayoutY() + tiganBox.getPrefHeight() + ScreenAdapter.calculateExY(10)));
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
                tiganBox.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split1[0])));
                tiganBox.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split1[1])));
                tiganBox.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2])));
                tiganBox.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(split1[3])));
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
                            if (child instanceof AnchorPane) {
                                continue;
                            }
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
                                            ScreenAdapter.calculateExX(Integer.parseInt(split1[2])),
                                            ScreenAdapter.calculateExY(Integer.parseInt(split1[3])),
                                            false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            tiganBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                        }
                    } else {
                        tiganBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    tiganBox.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]),new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                }

                tiganBox.setSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split1[14])));
                Font font = FontMap.load(split1[7], ScreenAdapter.calculateExX(Integer.parseInt(split1[8])));
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
                tiganLabel.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split1[14])));

                for (Node child : tiganBox.getChildren()) {
                    child.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2])));
                    if (child instanceof Text) {
                        Text ch = (Text) child;
                        ch.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2])));
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
                        ch.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split1[14])));
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

                backPane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split1[0])));
                backPane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split1[1])));
                backPane.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2])));
                backPane.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(split1[3])));


                if (Boolean.valueOf(split1[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(split1[6])) {
                        try {
                            backPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                            ScreenAdapter.calculateExX(Integer.parseInt(split1[2])),
                                            ScreenAdapter.calculateExY(Integer.parseInt(split1[3])), false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                        }
                    } else {
                        backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    backPane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                }


                daanLabel.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2]) - 40));
                daanLabel.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split1[14])));
                Font font = FontMap.load(split1[7] ,ScreenAdapter.calculateExX(Integer.parseInt(split1[8])));
                daanLabel.setFont(font);
                daanLabel.setUnderline(Boolean.valueOf(split1[12]));
                daanLabel.setStrikethrough(Boolean.valueOf(split1[9]));
                if (Boolean.valueOf(split1[10])) {
                    daanLabel.setStroke(Color.valueOf(split[1]));
                    daanLabel.setStrokeWidth(1);
                } else {
                    daanLabel.setStroke(null);
                }
                daanLabel.getTransforms().clear();
                if (Boolean.valueOf(split1[11])) {
                    daanLabel.getTransforms().add(new Shear(-0.3, 0));
                }
                daanLabel.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
                daanLabel.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split1[14])));
            }


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
                    stagePane.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2])));
                    stagePane.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(split1[3])));


                    if (Boolean.valueOf(split1[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(split1[6])) {
                            try {
                                stagePane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(
                                                ScreenAdapter.calculateExX(Integer.parseInt(split1[2])),
                                                ScreenAdapter.calculateExY(Integer.parseInt(split1[3])), false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                stagePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                            }
                        } else {
                            stagePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        stagePane.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), new CornerRadii(ScreenAdapter.calculateExX(10)), Insets.EMPTY)));
                    }


                    stageLabel.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(split1[2])));
                    if (split1.length >= 15) {
                        stageLabel.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(split1[14])));
                    }
                    Font font = FontMap.load(split1[7], ScreenAdapter.calculateExX(Integer.parseInt(split1[8])));
                   
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
        scrollPane.setFitToWidth(true);
        for (Node child : tiganBox.getChildren()) {
            if (child instanceof AnchorPane) {
                continue;
            }
            Text t = (Text) child;
            t.setWrappingWidth(tiganBox.getPrefWidth() - ScreenAdapter.calculateExX(40));
        }
        scrollPane.setFitToHeight(true);
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
                backPane.setVisible(false);
            } catch (FileNotFoundException e) {
                backPane.setVisible(GlobalCache.showAnswer);
            }

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
                    backPane.setVisible(false);
                } catch (FileNotFoundException e) {
                    backPane.setVisible(true);
                    imageQ.setVisible(false);
                }

            } else {
                backPane.setVisible(true);
                imageQ.setVisible(false);
            }
        } else {
            backPane.setVisible(false);
        }
    }
}
