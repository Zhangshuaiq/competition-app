package competition.controller.preview;

import competition.controller.ControlPane;
import competition.controller.match.Model60;
import competition.controller.match.Model61;
import competition.controller.match.ReloadPane;
import competition.entity.Configs;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import competition.ws.Model61RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 答题模式-个人
@Component
@Scope("prototype")
public class Model61Preview extends PreviewPane {

    @FXML
    private AnchorPane container;

    @FXML
    private Label timeLabel;

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

    private String timeValue = "00秒";

    @Resource
    private ControlPane controlPane;
    
    @Resource
    private WsMessageHandler messageHandler;

    private static Questions current;

    public static void setCurrent(Questions current) {
        Model61Preview.current = current;
    }


    @Override
    public AnchorPane getPane() {
        return container;
    }
    
    @FXML
    public void initialize(){
        PreviewStack.addPreview(this);

        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 鼠标右键打开详细的界面设置
                if (event.getButton() == MouseButton.SECONDARY) {
//                    QuestionPreviewAttr.setReloadPane(QuestionPreview.this);
//                    QuestionPreviewAttr.setQuestions(current);
//                    Navigator.navigatorTo("/view/match/QuestionPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        showData();
    }


    private void showData(){
        if (ExtendedScreen.hasShow()) {
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/Model61.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((Model61) res);
            }
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

        showScoreBoard();
    }

    private void showScoreBoard(){
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);
        scoreBoard.getChildren().clear();
        scoreBoard.setSpacing(20);
        List<Teams> teams = TeamRuntime.getTeams();
        List<Model61RuntimeData.ModelData> runtimeData = Model61RuntimeData.getRuntimeData();
        Map<String, Model61RuntimeData.ModelData> dataMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName() + "-" + x.getUserName(), x -> x));
        for (Teams team : teams) {
            VBox box = new VBox();
            box.setPrefWidth(Integer.parseInt(cfgArr[19]));
            box.setPrefHeight(Integer.parseInt(cfgArr[20]) * 2 + 20);
            box.setSpacing(10);
            Label teamName = new Label(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
            teamName.setAlignment(Pos.CENTER);
            teamName.setPrefWidth(Integer.parseInt(cfgArr[19]));
            teamName.setPrefHeight(Integer.parseInt(cfgArr[20]));
            teamName.setTextFill(Color.valueOf(cfgArr[15]));
            teamName.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]), CornerRadii.EMPTY, Insets.EMPTY)));
            box.getChildren().add(teamName);

            Label teamScore = new Label(team.getCurrentScore() + "");
            teamScore.setTextAlignment(TextAlignment.CENTER);
            teamScore.setAlignment(Pos.CENTER);
            teamScore.setPrefWidth(Integer.parseInt(cfgArr[19]));
            teamScore.setPrefHeight(Integer.parseInt(cfgArr[20]));
            teamScore.setTextFill(Color.valueOf(cfgArr[21]));
            teamScore.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));
            box.getChildren().add(teamScore);
            scoreBoard.getChildren().add(box);
            
            // 团队内每个人的答题状态
            HBox memberStatus = new HBox();
            memberStatus.setSpacing(1);
            memberStatus.setPadding(new Insets(1, 1, 1,1));
            memberStatus.setPrefWidth(Integer.parseInt(cfgArr[19]));
            String members = team.getMembers();
            if (!StringUtils.isEmpty(members)) {
                String[] ms = members.split(",");
                double w = Integer.parseInt(cfgArr[19]) * 1.0 / ms.length;
                for (int i = 0; i < ms.length; i++) {
                    Model61RuntimeData.ModelData modelData = dataMap.get(team.getFullName() + "-" + ms[i]);
                    Label l = new Label((i + 1) + "");
                    l.setPrefHeight(40);
                    l.setPrefWidth(w);
                    l.setTextFill(Color.WHITE);
                    if (StringUtils.isEmpty(modelData.getUserSelect())) {
                        l.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        if (modelData.getUserSelect().equals(modelData.getAnswer())) {
                            l.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                        } else {
                            l.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                    // 居中
                    l.setAlignment(Pos.CENTER);
                    memberStatus.getChildren().add(l);
                }
            }
            box.getChildren().add(memberStatus);
        }


        scoreBoard.setLayoutX(Integer.parseInt(cfgArr[17]));
        scoreBoard.setLayoutY(Integer.parseInt(cfgArr[18]));
        scoreBoard.setPrefWidth((Integer.parseInt(cfgArr[19]) + 20) * teams.size());
        scoreBoard.setPrefHeight(Integer.parseInt(cfgArr[20]) * 2 + 30);
    }


    public void showAnswer(){
        daanLabel.setVisible(!daanLabel.isVisible());
    }

    public void hideTiming(){
        timeLabel.setVisible(false);
    }

    public void startTiming(int value){
        timeVal = value;
        timeLabel.setText("答题剩余时间:" + timeVal + " 秒");
        messageHandler.sendQuestionTime(timeVal + "");
        timeLabel.setVisible(true);
    }

    public void stopTiming(){
    }

    @Override
    public void reload() {
        showData();
    }

    @Override
    public void reload(int time, String timeText) {
        this.timeValue = timeText;
        showData();
        ReloadPane questionPreview = controlPane.getQuestionPreview();
        if (questionPreview != null) {
            questionPreview.reload(time, timeText);
        }
    }
}
