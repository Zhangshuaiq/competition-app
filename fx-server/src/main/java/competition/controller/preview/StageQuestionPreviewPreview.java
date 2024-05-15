package competition.controller.preview;


import competition.controller.ControlPane;
import competition.controller.match.*;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Shear;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class StageQuestionPreviewPreview extends PreviewPane {


    @FXML
    private AnchorPane container;

    @Resource
    private ConfigsMapper configsMapper;
    
    @Resource
    private ControlPane controlPane;
    
    @Resource
    private WsMessageHandler messageHandler;


    private static Stages currentStage;

    private  Stages inner;

    public static void setCurrentStage(Stages currentStage) {
        StageQuestionPreviewPreview.currentStage = currentStage;
    }


    @Override
    public AnchorPane getPane() {
        return container;
    }
    
    @FXML
    public void initialize() {
        inner = currentStage;
        PreviewStack.addPreview(this);
        PreviewStack.addReturn(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    StageQuestionPreviewAttr.setReloadPane(controlPane.getQuestionPreview());
                    StageQuestionPreviewAttr.setCurrentStage(inner);
                    Navigator.navigatorTo("/view/match/StageQuestionPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        showData();
    }


    private void showData() {
        if (ExtendedScreen.hasShow()) {
            StageQuestionPreview.setCurrentStage(inner);
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/StageQuestionPreview.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((StageQuestionPreview) res);
            }
        }
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.选题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
        container.getChildren().clear();
        // 未选择阶段，不展示
        if (inner == null) {
            return;
        }

        GridPane layout = new GridPane();
        // 选题字色的配置
        Configs configs = configsMapper.selectById(4);
        // 环节字体颜色的配置
        Configs stageConfig = configsMapper.selectById(2);
        String config = configs.getConfig();
        String stageConfigAttr = stageConfig.getConfig();
        String[] stageConfigAttrSplit = stageConfigAttr.split(ConfigSpliter.SPLITER);
        List<Questions> questionList = CurrentQuestions.getQuestionList();
        questionList.sort(new Comparator<Questions>() {
            @Override
            public int compare(Questions o1, Questions o2) {
                return o1.getQuestionNumber().compareTo(o2.getQuestionNumber());
            }
        });
        if (!StringUtils.isEmpty(config)){

            String questionStyle = inner.getQuestionStyle();
            String[] styleArr = {};
            if(!StringUtils.isEmpty(questionStyle)) {
                styleArr = questionStyle.split(ConfigSpliter.DIFFER);
            }


            // 标题名称
            StackPane title = new StackPane();
            Text stageName = new Text(inner.getName());
            title.getChildren().add(stageName);
            // 环节标题的字体颜色和背景色
            stageName.setFill(Color.valueOf(stageConfigAttrSplit[2]));
            title.setBackground(new Background(new BackgroundFill(Color.valueOf(stageConfigAttrSplit[3]), CornerRadii.EMPTY, Insets.EMPTY)));
            title.setLayoutX(ConfigSpliter.DEFAULT_X);
            title.setLayoutY(ConfigSpliter.DEFAULT_Y);
            title.setPrefWidth(ConfigSpliter.DEFAULT_W);
            title.setPrefHeight(ConfigSpliter.DEFAULT_H);
            stageName.setLayoutX(ConfigSpliter.DEFAULT_X);
            stageName.setLayoutY(ConfigSpliter.DEFAULT_Y);
            stageName.prefWidth(ConfigSpliter.DEFAULT_W);
            stageName.prefHeight(ConfigSpliter.DEFAULT_H);


            if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[0])) {
                String[] detailArr = styleArr[0].split(ConfigSpliter.SPLITER);
                title.setLayoutX(Integer.parseInt(detailArr[0]));
                title.setLayoutY(Integer.parseInt(detailArr[1]));
                title.setPrefWidth(Integer.parseInt(detailArr[2]));
                title.setPrefHeight(Integer.parseInt(detailArr[3]));

                if (Boolean.valueOf(detailArr[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(detailArr[6])) {
                        try {
                            title.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(Integer.parseInt(detailArr[2]), Integer.parseInt(detailArr[3]), false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 填充背景颜色
                    title.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                }

                stageName.setLayoutX(Integer.parseInt(detailArr[0]));
                stageName.setLayoutY(Integer.parseInt(detailArr[1]));
                stageName.prefWidth(Integer.parseInt(detailArr[2]));
                stageName.prefHeight(Integer.parseInt(detailArr[3]));
                stageName.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
                title.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(detailArr[8]));
                stageName.setFont(font);
                if (Boolean.valueOf(detailArr[10])) {
                    stageName.setStroke(Color.valueOf(stageConfigAttrSplit[2]));
                } else {
                    stageName.setStroke(null);

                }
                stageName.getTransforms().clear();
                if (Boolean.valueOf(detailArr[11])) {
                    stageName.getTransforms().add(new Shear(-0.3, 0));
                }
                stageName.setUnderline(Boolean.valueOf(detailArr[12]));
                stageName.setStrikethrough(Boolean.valueOf(detailArr[9]));
            }

            container.getChildren().add(title);

            // 以后台配置为准

            String[] cfgArr = config.split(ConfigSpliter.SPLITER);

            int col = 1;
            if (styleArr.length > 1 &&!ConfigSpliter.EMPTY.equals(styleArr[1])) {
                String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);
                if (detailArr.length >= 16) {
                    col = Integer.parseInt(detailArr[15]);
                }
            }
            

            layout.setHgap(20);
            layout.setVgap(20);
            layout.setLayoutX(Integer.parseInt(cfgArr[3]));
            layout.setLayoutY(Integer.parseInt(cfgArr[4]));


            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("stage", inner);
            dataMap.put("questions", questionList);
            messageHandler.notifyPage("stageQuestion", dataMap);
            
            for (int i = 0; i < questionList.size(); i++) {
                StackPane item = new StackPane();
                item.setPrefSize(Integer.parseInt(cfgArr[5]), Integer.parseInt(cfgArr[6]));
                Questions questions = questionList.get(i);
                Text text = new Text(questions.getQuestionNumber().toString());

                if (CurrentQuestions.hasAnswer(questions.getQuestionNumber().toString())) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                }
                if (styleArr.length > 1 &&!ConfigSpliter.EMPTY.equals(styleArr[1])) {
                    String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);

                    layout.setLayoutX(Integer.parseInt(detailArr[0]));
                    layout.setLayoutY(Integer.parseInt(detailArr[1]));

                    item.setPrefSize(Integer.parseInt(detailArr[2]), Integer.parseInt(detailArr[3]));
                    item.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                    item.setLayoutX(Integer.parseInt(detailArr[0]));
                    item.setLayoutY(Integer.parseInt(detailArr[1]));
                    layout.setVgap(Integer.parseInt(detailArr[14]));
                    layout.setHgap(Integer.parseInt(detailArr[14]));
                    if (Boolean.valueOf(detailArr[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(detailArr[6])) {
                            try {
                                item.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(Integer.parseInt(detailArr[2]), Integer.parseInt(detailArr[3]), false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // 填充背景颜色
                        item.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    
                    
                    Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(detailArr[8]));
                    text.setFont(font);
                    if (Boolean.valueOf(detailArr[10])) {
                        text.setStroke(text.getFill());
                    } else {
                        text.setStroke(null);

                    }
                    text.getTransforms().clear();
                    if (Boolean.valueOf(detailArr[11])) {
                        text.getTransforms().add(new Shear(-0.3, 0));
                    }
                    text.setUnderline(Boolean.valueOf(detailArr[12]));
                    text.setStrikethrough(Boolean.valueOf(detailArr[9]));
                }
                text.setFill(Color.valueOf(cfgArr[0]));
                item.getChildren().add(text);
                layout.add(item, i % col, i / col);
                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() != MouseButton.PRIMARY) {
                            return;
                        }
                        // 给题目设置分值
                        // 扩展屏幕展示并排答题界面（竞技模式）
                        for (Questions q : CurrentQuestions.getQuestionList()) {
                            q.setScore(inner.getScorePerQ());
                        }
                        if (inner.getQuestionType() == 7) {
                            Model7RuntimeData.setTeams(TeamRuntime.getTeams());
                            // 发送试题列表给前端
                            controlPane.setPreviewPane(
                                    Navigator.preview("/view/preview/Model7Preview.fxml", "", "/view/style.css", true, true)
                            );
                            controlPane.updateScoreBoard();

                            //messageHandler.sendQuestionGroup();
                            // 扩展屏显示题目信息
                        } else if (inner.getQuestionType() == 6) {
                            // 重置当前的答题状态
                            TeamRuntime.resetStatus();
                            Integer m6 = inner.getM6();
                            String tihao = questions.getQuestionNumber().toString();
                            Questions questions = CurrentQuestions.getQ(tihao);
                            if (questions == null) {
                                Information.error("请选择题号");
                                return;
                            }
                            if (m6 == 0) {
                                // 答题模式 -- 团队模式
                                CurrentQuestions.recordAnswer(tihao);
                                // 设置当前需要显示的题目
                                QuestionPreviewPreview.setCurrent(questions);
                                // 设置选手信息
                                Model60RuntimeData.setTeams(TeamRuntime.getTeams());

                                controlPane.setPreviewPane(
                                        Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true)
                                );
                                //controlPane.setPreviewPane(Navigator.preview("/view/preview/Model60.fxml", "", "/view/style.css", true, true));
                                controlPane.updateScoreBoard();
                                Model60RuntimeData.setRuntimeData(questions);
                                messageHandler.notifyPage("model60", questions);
                               // messageHandler.sendQuestion(questions);
                            } else {
                                // 答题模式 -- 个人模式
                                CurrentQuestions.recordAnswer(tihao);
                                // 设置当前需要显示的题目
                                QuestionPreviewPreview.setCurrent(questions);
                                // 设置选手信息
                                Model61RuntimeData.setTeams(TeamRuntime.getTeams());
                                controlPane.setPreviewPane(
                                        Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true)
                                );
                               // controlPane.setPreviewPane(Navigator.preview("/view/preview/Model61.fxml", "", "/view/style.css", true, true));
                                controlPane.updateScoreBoard();
                                Model61RuntimeData.setRuntimeData(questions);
                                messageHandler.notifyPage("model61", questions);
                               // messageHandler.sendQuestion(questions);
                            }
                            controlPane.selectQuestion(tihao);
                        } else {
                            String tihao = questions.getQuestionNumber().toString();
                            Questions questions = CurrentQuestions.getQ(tihao);
                            CurrentQuestions.recordAnswer(tihao);
                            // 设置当前需要显示的题目
                            // 内屏更新
                           
                            Integer questionType = inner.getQuestionType();
                            if (questionType == 2 || questionType == 1) {
                                // 正常自选题
                                QuestionPreviewPreview.setCurrent(questions);
                                controlPane.setPreviewPane(
                                        Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true)
                                );
                            } else if (questionType == 5){
                                // 线索题
                                CluePreviewPreview.setCurrent(questions);
                                controlPane.setPreviewPane(
                                        Navigator.preview("/view/preview/CluePreview.fxml", "", "/view/style.css", true, true)
                                );
                            }
                           
                            // 控制面板选择题号
                            controlPane.selectQuestion(tihao);
                        }

                    }
                });
            }



        }

        container.getChildren().add(layout);
    }

    public Stages getInner() {
        return inner;
    }

    @Override
    public void reload() {
        showData();
    }
}
