package competition.controller.match;

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
public class QuestionPreview implements ReloadPane{
    
    
    @FXML
    private StackPane backPane;
    
    @FXML
    private AnchorPane container;
    
    
    @FXML
    private HBox scoreBoard;
    
    
    @FXML
    VBox tiganBox;
    
    @FXML
    private Text tiganLabel;
    @FXML
    private Text daanLabel;
    
    @Resource
    private ConfigsMapper configsMapper;
    
    
    private int timeVal;
    
    private static Questions current;
    

    public static void setCurrent(Questions current) {
        QuestionPreview.current = current;
    }

    @Override
    public Node getPane() {
        return container;
    }
    
    @FXML
    public void initialize(){
        
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 鼠标右键打开详细的界面设置
                if (event.getButton() == MouseButton.SECONDARY) {

                }
            }
        });
        showData();
    }
    
    
    private void showData(){
        backPane.setVisible(GlobalCache.showAnswer);
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
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
                chA.setFill(Color.valueOf(split[0]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceB())) {
                Text chB = new Text("B." + current.getChoiceB());
                chB.prefWidth(Integer.parseInt(split[7]));
                chB.setFill(Color.valueOf(split[0]));
                tiganBox.getChildren().add(chB);
            }

            if (!StringUtils.isEmpty(current.getChoiceC())) {
                Text chA = new Text("C." + current.getChoiceB());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceD())) {
                Text chA = new Text("D." + current.getChoiceD());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceE())) {
                Text chA = new Text("E." + current.getChoiceE());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                tiganBox.getChildren().add(chA);
            }

            if (!StringUtils.isEmpty(current.getChoiceF())) {
                Text chA = new Text("F." + current.getChoiceF());
                chA.prefWidth(Integer.parseInt(split[7]));
                chA.setFill(Color.valueOf(split[0]));
                tiganBox.getChildren().add(chA);
            }

            
            // 题型
            String tx = split[9];
            // 题型标注
            String txbz = split[10];
            
            // 无题型标注
            if (txbz.equals("4")) {
                tiganLabel.setText(current.getQuestionNumber() + "、" + current.getQuestion());
            } else if (txbz.equals("3")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(current.getQuestionNumber()  + "、"  + current.getQuestion() + " " + current.getQuestionType());
                } else if (tx.equals("2")) {
                    tiganLabel.setText(current.getQuestionNumber() + "、"  + " " + current.getQuestionType() + current.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText(current.getQuestionType()  + "、"  + " " + current.getQuestionNumber() + current.getQuestion());
                }

            } else if (txbz.equals("2")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(current.getQuestionNumber()  + "、"  + current.getQuestion() + "(" + current.getQuestionType() + ")");
                } else if (tx.equals("2")) {
                    tiganLabel.setText(current.getQuestionNumber()  + "、"  + "(" + current.getQuestionType() + ")" + current.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText("(" + current.getQuestionType() + ")" + current.getQuestionNumber()  + "、"  + current.getQuestion());
                }
            } else if (txbz.equals("1")) {
                if (tx.equals("3")) {
                    tiganLabel.setText(current.getQuestionNumber()  + "、"  + current.getQuestion() + "[" + current.getQuestionType() + "]");
                } else if (tx.equals("2")) {
                    tiganLabel.setText(current.getQuestionNumber()  + "、"  + "[" + current.getQuestionType() + "]" + current.getQuestion());
                } else if (tx.equals("1")) {
                    tiganLabel.setText("[" + current.getQuestionType() + "]" + current.getQuestionNumber()  + "、"  + current.getQuestion());
                }
            }
            // 答案
            daanLabel.setText(split[4] + " " + current.getAnswer());
            backPane.setLayoutX((int) tiganBox.getLayoutX());
            backPane.setLayoutY((int) (tiganBox.getLayoutX() + tiganBox.getPrefHeight() + 10));
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

                        backPane.setLayoutX(Integer.parseInt(split1[0]));
                        backPane.setLayoutY(Integer.parseInt(split1[1]));
                        backPane.prefWidth(Integer.parseInt(split1[2]));
                        backPane.prefHeight(Integer.parseInt(split1[3]));
                        daanLabel.setWrappingWidth(Integer.parseInt(split1[2]));


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

//                        if (Boolean.valueOf(split1[4])) {
//                            // 填充背景图片
//                            if (!StringUtils.isEmpty(split1[6])) {
//                                try {
//                                    daanLabel.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(split1[6])),
//                                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                                            new BackgroundSize(Integer.parseInt(split1[2]), Integer.parseInt(split1[3]), false, false, true, true))));
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } else {
//                            // 填充背景颜色
//                            daanLabel.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]), CornerRadii.EMPTY, Insets.EMPTY)));
//                        }
                        
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
        }
    }
    
    
    
    public void showAnswer(){
        backPane.setVisible(GlobalCache.showAnswer);
    }
    
    public void hideTiming(){
    }
    
    public void startTiming(int value){
    }
    
    public void stopTiming(){
    }
    
    @Override
    public void reload() {
        showData();
    }
}
