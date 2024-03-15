package competition.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.board.BoardPane;
import competition.controller.match.Model60;
import competition.controller.match.Model61;
import competition.controller.match.ReloadPane;
import competition.controller.preview.PreviewPane;
import competition.controller.preview.QuestionPreviewPreview;
import competition.controller.preview.StageRulePreviewPreview;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.mapper.TeamsMapper;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ControlPane {


    // 内衬预览屏
    private PreviewPane previewPane;

    @FXML
    private VBox stageList, stageChildList;


    @FXML
    private Button startAnswerBtn;


    @FXML
    private AnchorPane stageName;


    @FXML
    private Label stageText;


    @FXML
    private ComboBox<String> questionCombox;

    @FXML
    private Button secondScreenBtn;

    @FXML
    private Button timingBtn, controlBtn;

    @FXML
    private TextField timeSecondText;


    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private TeamsMapper teamsMapper;

    private Stages currentStage;


    // 外接显示屏展示题目
    private ReloadPane questionPreview;

    // 主屏幕端计分板
    private BoardPane scoreBoardPreview;

    // 计时器
    private ReloadPane timeBoardPreview;
    
    // 外屏幕端计分板
    private BoardPane scoreBoard;
    // 外屏幕端计时器
    private ReloadPane timeBoard;

    //private ReloadPane modelBoard;

    // 计分控制
    @FXML
    private ComboBox<String> teamComBox, memberComBox;

    @FXML
    private TextField scoreInput;

    @FXML
    public Label teamScore, memberScore;

    // 计时时间，默认 60 s
    private int time = 60;

    @FXML
    // 抢答按钮
    private Button buzzBtn;

    // 计时显示控制
    @FXML
    private Button timeVisibleBtn;

    // 更新客户端展示的数据（与客户端实时交互）
    @Resource
    private WsMessageHandler messageHandler;


    @FXML
    public void clickVis() {
        if (timeVisibleBtn.getText().equals("不消失")) {
            timeVisibleBtn.setText("消失");
            timeVisibleBtn.setTextFill(Color.RED);
        } else {
            timeVisibleBtn.setText("不消失");
            timeVisibleBtn.setTextFill(Color.GREEN);
        }
    }

    @FXML
    public void initialize() {

        if (timeVisibleBtn.getText().equals("不消失")) {
            timeVisibleBtn.setTextFill(Color.GREEN);
        } else {
            timeVisibleBtn.setText("不消失");
            timeVisibleBtn.setTextFill(Color.RED);
        }

        if (ExtendedScreen.hasShow()) {
            secondScreenBtn.setText("第二屏幕关");
        } else {
            secondScreenBtn.setText("第二屏幕开");
        }

        teamComBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
                String members = TeamRuntime.getTeams().get(selectedIndex).getMembers();
                if (!StringUtils.isEmpty(members)) {
                    memberComBox.setItems(FXCollections.observableList(Arrays.asList(members.split(","))));
                } else {
                    memberComBox.setItems(FXCollections.emptyObservableList());
                }
                memberComBox.setValue("");
                teamScore.setText(TeamRuntime.getTeams().get(selectedIndex).getCurrentScore() + "");
            }
        });

        memberComBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
                int selectedMemberIndex = memberComBox.getSelectionModel().getSelectedIndex();
                if (selectedMemberIndex >= 0) {
                    Integer memScore = TeamRuntime.getTeams().get(selectedIndex).getMemberScores().get(selectedMemberIndex);
                    memberScore.setText(memScore + "");
                } else {
                    memberScore.setText("0");
                }

            }
        });
    }

    private void showStage() {
        final List<Stages> stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .isNull(Stages::getParentId)
                .orderByAsc(Stages::getSortVal)
        );
        showStageData(stages, stageList);
        showTopStage();
    }


    // 抢答按钮点击
    @FXML
    public void buzzClicked() {
        String text = buzzBtn.getText();
        if (text.equals("开始抢答")) {
            buzzBtn.setText("停止抢答");
        } else {
            buzzBtn.setText("开始抢答");
        }
    }

    // 加分
    @FXML
    public void addScore() {
        String text = scoreInput.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请输入分数");
            return;
        }

        // 面板快速添加
        List<Teams> teamLists = TeamRuntime.getTeams();
        Set<String> selected = scoreBoardPreview.getSelected();
        for (Teams teamList : teamLists) {
            for (String s : selected) {
                if (teamList.getShortName().equals(s)) {
                    teamList.setCurrentScore(teamList.getCurrentScore() + Integer.parseInt(text));
                }
            }
        }


        // 添加团队分数
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Teams teams = TeamRuntime.getTeams().get(selectedIndex);

            // 给控制面板内加分，如果已选择则忽略，避免二次加分
            if (!selected.contains(teams.getShortName())) {
                teams.setCurrentScore(teams.getCurrentScore() + Integer.parseInt(text));
            }
            teamScore.setText(teams.getCurrentScore().toString());

            // 添加个人分数
            int selectedIndex1 = memberComBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex1 > -1) {
                teams.getMemberScores().set(selectedIndex1, teams.getMemberScores().get(selectedIndex1) + Integer.parseInt(text));
                memberScore.setText(teams.getMemberScores().get(selectedIndex1).toString());
            } else {
                memberScore.setText("0");
            }
        }

        scoreBoardPreview.getSelected().clear();
        scoreBoardPreview.reload();
        if (questionPreview != null) {
            questionPreview.reload();
        }

    }

    // 减分
    @FXML
    public void descScore() {

        String text = scoreInput.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请输入分数");
            return;
        }

        // 面板快速减少
        List<Teams> teamLists = TeamRuntime.getTeams();
        Set<String> selected = scoreBoardPreview.getSelected();
        for (Teams teamList : teamLists) {
            for (String s : selected) {
                if (teamList.getShortName().equals(s)) {
                    int currScore = teamList.getCurrentScore() - Integer.parseInt(text);
                    if (currScore < 0) {
                        currScore = 0;
                    }
                    teamList.setCurrentScore(currScore);
                }
            }
        }


        // 减少团队分数
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Teams teams = TeamRuntime.getTeams().get(selectedIndex);
            if (!selected.contains(teams.getShortName())) {
                int currScore = teams.getCurrentScore() - Integer.parseInt(text);
                if (currScore < 0) {
                    currScore = 0;
                }
                teams.setCurrentScore(currScore);
            }

            teamScore.setText(teams.getCurrentScore().toString());

            // 减少个人分数
            int selectedIndex1 = memberComBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex1 > -1) {
                int memItemScore = teams.getMemberScores().get(selectedIndex1) - Integer.parseInt(text);
                if (memItemScore < 0) {
                    memItemScore = 0;
                }
                teams.getMemberScores().set(selectedIndex1, memItemScore);
                memberScore.setText(teams.getMemberScores().get(selectedIndex1).toString());
            } else {
                memberScore.setText("0");
            }
        }
        scoreBoardPreview.getSelected().clear();
        scoreBoardPreview.reload();
        if (questionPreview == null) {
            return;
        }
        questionPreview.reload();
    }

    @FXML
    public void showTopStage() {
        stageList.setVisible(true);
        stageName.setVisible(false);
        stageChildList.setVisible(false);
    }


    @FXML
    public void clickName() {
        showChildList();
    }


    @FXML
    public void startAnswerQuestion() {
        String tihao = questionCombox.getValue();
        Questions questions = CurrentQuestions.getQ(tihao);
        // 给题目设置分值
        // 扩展屏幕展示并排答题界面（竞技模式）
        if (currentStage.getTimeCType() == 2) {
            // 自动计时
            startTiming();
        }
        if (currentStage.getQuestionType() == null) {

        } else if (currentStage.getQuestionType() == 7) {
            if (Model7RuntimeData.getTeams().isEmpty()) {
                Information.error("请先显示试题");
                return;
            }
            messageHandler.sendModel7Start();
            // 扩展屏显示题目信息
        } else if (currentStage.getQuestionType() == 6){
            if (currentStage.getM6() == 1) {
                if (Model61RuntimeData.getRuntimeData().isEmpty() || questions == null) {
                    Information.error("请先显示试题");
                    return;
                }
                messageHandler.sendModel61Start();
            } else if (currentStage.getM6() == 0) {
                if (Model60RuntimeData.getRuntimeData().isEmpty() || questions == null) {
                    Information.error("请先显示试题");
                    return;
                }
                messageHandler.sendModel60Start();
            }
        }

    }

    @FXML
    public void timingControl() {
        if (timeBoardPreview != null) {
            if (controlBtn.getText().equals("继续")) {
                controlBtn.setText("暂停");
                timeBoardPreview.continueTiming();
                if (questionPreview != null) {
                    questionPreview.continueTiming();
                }
            } else {
                controlBtn.setText("继续");
                timeBoardPreview.stopTiming();
            }
        }
    }

    @FXML
    public void startTiming() {

        String text = timeSecondText.getText();
        if (!StringUtils.isEmpty(text)) {
            time = Integer.parseInt(text);
        }
        if (timeBoardPreview == null) {
            timeBoardPreview = (ReloadPane) Navigator.timeWithPos("/view/board/preview/TimeBoard.fxml", "", "/view/style.css", true);
        } else {
            timeBoardPreview.shows();
        }
        timeBoardPreview.startTiming(currentStage.getOrderVal() == 1, time);
        timingBtn.setDisable(true);
    }

    @FXML
    public void backLast() {
        PreviewPane p = PreviewStack.removePreview();
        if (p != null) {
            if (previewPane == p) {
                p = PreviewStack.removePreview();
            }
            if (p != null) {
                previewPane = p;
                PreviewStack.addPreview(p);
                Navigator.previewStage.setScene(previewPane.getScene());
            }
        }
        GlobalCache.showAnswer = false;
    }


    // 竞技排行
    @FXML
    public void model7Rank() {
        // 判断是否全部回答完毕
        boolean finished = true;
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        for (List<Model7RuntimeData.ModelData> modelDataList : runtimeData) {
            Model7RuntimeData.ModelData m = modelDataList.get(modelDataList.size() - 1);
            if (m == null || StringUtils.isEmpty(m.getUserSelect())) {
                finished = false;
                break;
            }
        }
        if (!finished) {
            Information.error("竞技尚未结束");
            return;
        }
        //previewPane.getChildren().add(Navigator.load("/view/preview/Model7Rank.fxml"));
    }


    // 计时复位
    @FXML
    public void timeReset() {
        timingBtn.setDisable(false);
        if (timeBoardPreview != null) {
            timeBoardPreview.close();
        }
        timeSecondText.setText(time + "");
        if (currentStage == null) {
            return;
        }

    }


    private void showStageName() {
        stageList.setVisible(false);
        stageName.setVisible(true);
        stageChildList.setVisible(false);
    }


    private void showChildList() {
        stageList.setVisible(false);
        stageName.setVisible(false);

        stageChildList.setVisible(true);
        final List<Stages> stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .eq(Stages::getParentId, currentStage.getId())
                .orderByAsc(Stages::getSortVal)
        );
        showStageData(stages, stageChildList);
    }

    private void showStageData(List<Stages> stages, VBox stageBox) {
        stageBox.getChildren().clear();
        stageBox.setAlignment(Pos.CENTER);
        for (Stages stage : stages) {
            Label label = new Label(stage.getSortVal() + " " + stage.getName());
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(200);
            label.setPrefHeight(40);
            label.setStyle("-fx-border-color: black; -fx-border-width: 1");
            stageBox.getChildren().add(label);
            label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() != MouseButton.PRIMARY) {
                        return;
                    }
                    // 设置时间
                    time = stage.getDescTime();
                    timeSecondText.setText(time + "");

                    // 设置分数
                    scoreInput.setText(stage.getScorePerQ().toString());

                    for (Node child : stageBox.getChildren()) {
                        child.getStyleClass().remove("stage-control-clicked");
                    }
                    label.getStyleClass().add("stage-control-clicked");
                    stageText.setText(stage.getName());
                    currentStage = stage;
                    if (StringUtils.isEmpty(currentStage.getQuestionType()) || currentStage.getQuestionType().equals("0")) {
                        startAnswerBtn.setDisable(true);
                    } else {
                        startAnswerBtn.setDisable(false);
                    }
                    boolean last = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, stage.getId())
                    ).isEmpty();
                    if (last) {
                        // 显示题目
                        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
                        if (!CollectionUtils.isEmpty(parse)) {
                            for (int i = 0; i < parse.size(); i++) {
                                parse.get(i).setSort(i);
                            }
                            // 环节名字索引
                            parse = parse.stream().filter(x -> !StringUtils.isEmpty(x.getStageName()) && x.getStageName().equals(stage.getName())).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(parse)) {
                                for (int i = 0; i < parse.size(); i++) {
                                    parse.get(i).setQuestionNumber(i + 1);
                                }
                                CurrentQuestions.setQuestions(parse, stage.getId());
                                final List<String> questionKeys = parse.stream().map(x -> x.getQuestionNumber().toString()).collect(Collectors.toList());

                                questionCombox.setItems(FXCollections.observableList(questionKeys));
//                                // 扩展屏显示对应的题目列表
//                                StageQuestionPreview.setCurrentStage(currentStage);
//                                ExtendedScreen.show("/view/match/StageQuestionPreview.fxml");
//                                
//                                // 内屏展示对应的题目列表
//                                StageQuestionPreviewPreview.setCurrentStage(currentStage);
//                                previewPane.getChildren().clear();
//                                previewPane.getChildren().add(Navigator.load("/view/preview/StageQuestionPreview.fxml"));
                            } else {
                                questionCombox.setItems(FXCollections.emptyObservableList());
                                questionCombox.setValue(null);
                            }

                        } else {

                        }

                        // ExtendedScreen.show("/view/match/StagePreview.fxml");
                    } else {
                        showStageName();
                    }
                }
            });
        }
    }


    @FXML
    public void secondOpen() {
        if (ExtendedScreen.hasShow()) {
            ExtendedScreen.close();
            secondScreenBtn.setText("第二屏幕开");
        } else {
            secondScreenBtn.setText("第二屏幕关");
            boolean show = ExtendedScreen.extendShow();
            if (!show) {
                return;
            }
            if (previewPane != null) {
                PreviewStack.reload();
            }
        }

    }

    // 显示环节规则
    @FXML
    public void showStageRule() {
        if (currentStage != null) {
            if (!StringUtils.isEmpty(currentStage.getRuleContent())) {
                StageRulePreviewPreview.setCurrentStage(currentStage);
                previewPane = Navigator.preview("/view/preview/StageRulePreview.fxml", "", "/view/style.css", true, true);
                messageHandler.notifyEnterWait("7", currentStage.getRuleContent());
            } else {
                enterStage();
            }
           
            
        }

    }


    // 进入环节
    public void enterStage() {
        if (currentStage.getQuestionType() != null) {
            int size = 0;
            List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
            for (Teams t : teams) {
                String members = t.getMembers();
                if (!StringUtils.isEmpty(members)) {
                    String[] split = members.split(",");
                    size += split.length;
                }
            }
            if (currentStage.getQuestionType().equals(7)) {
                // 团队竞技
                messageHandler.notifyEnterWait("7", currentStage.getRuleContent());
            } else {
                // 个人答题模式 | 团队答题模式
                messageHandler.notifyEnterWait("6" + currentStage.getM6().toString(), currentStage.getRuleContent());
            }

        }
    }


    // 退出环节
    public void exitStage() {
        messageHandler.notifyExit();
    }


    // 显示题目
    @FXML
    public void showQuestion() {
        // 给题目设置分值
        // 扩展屏幕展示并排答题界面（竞技模式）
        for (Questions q : CurrentQuestions.getQuestionList()) {
            q.setScore(currentStage.getScorePerQ());
        }
        if (currentStage.getQuestionType().equals(7)) {
            Model7RuntimeData.setTeams(TeamRuntime.getTeams());
            // 发送试题列表给前端
            // previewPane.getChildren().clear();
            //previewPane.getChildren().add(Navigator.load("/view/preview/Model7.fxml"));
            messageHandler.sendQuestionGroup(CurrentQuestions.getQuestionList());
            // 扩展屏显示题目信息
        } else if (currentStage.getQuestionType().equals(6)) {
            Integer m6 = currentStage.getM6();
            String tihao = questionCombox.getValue();
            Questions questions = CurrentQuestions.getQ(tihao);
            if (questions == null) {
                Information.error("请选择题号");
                return;
            }
            if (m6 == 0) {
                // 答题模式 -- 团队模式
                CurrentQuestions.recordAnswer(tihao);
                // 设置当前需要显示的题目
                Model60.setCurrent(questions);
                // 设置选手信息
                Model60RuntimeData.setTeams(TeamRuntime.getTeams());
                // previewPane.getChildren().clear();
                // previewPane.getChildren().add(Navigator.load("/view/preview/Model60.fxml"));
                messageHandler.sendQuestion(questions);
            } else {
                // 答题模式 -- 个人模式
                CurrentQuestions.recordAnswer(tihao);
                // 设置当前需要显示的题目
                Model61.setCurrent(questions);
                // 设置选手信息
                Model61RuntimeData.setTeams(TeamRuntime.getTeams());
                //  previewPane.getChildren().clear();
                // previewPane.getChildren().add(Navigator.load("/view/preview/Model61.fxml"));
                messageHandler.sendQuestion(questions);
            }
            // 扩展屏显示题目信息
        } else {
            String tihao = questionCombox.getValue();
            Questions questions = CurrentQuestions.getQ(tihao);
            if (questions == null) {
                Information.error("请选择题号!!");
                return;
            }
            CurrentQuestions.recordAnswer(tihao);
            
            // 内屏更新题目
            if (previewPane instanceof QuestionPreviewPreview) {
                QuestionPreviewPreview.setCurrent(questions);
                previewPane.reload();
            } else {
                QuestionPreviewPreview.setCurrent(questions);
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                previewPane.reload();
            }
            
            // previewPane.getChildren().clear();
            // previewPane.getChildren().add(Navigator.load("/view/preview/QuestionPreview.fxml"));
        }

    }

    // 答案显示
    @FXML
    public void showAnswer() {
        if (previewPane != null) {
            previewPane.showAnswer();
        }
    }


    // 上一题
    @FXML
    public void lastQuestion() {
        int selectedIndex = questionCombox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == 0) {
            return;
        }
        GlobalCache.showAnswer = false;
        questionCombox.getSelectionModel().select(selectedIndex - 1);
        showQuestion();
    }

    // 下一题
    @FXML
    public void nextQuestion() {
        int selectedIndex = questionCombox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == questionCombox.getItems().size() - 1) {
            return;
        }
        GlobalCache.showAnswer = false;
        questionCombox.getSelectionModel().select(selectedIndex + 1);
        showQuestion();
    }


    @FXML
    public void toBackend() {
        if (!GlobalCache.backendOpen) {
            GlobalCache.backendOpen = true;
            Navigator.openBackend("/view/Backend.fxml", "后台设置", "/view/style.css", false, false);
        }

    }
    
    
    
    // 显示总规则
    @FXML
    public void showZGZ(){
//       String path =  TextResource.ZGZ;
//       List<String> lines = FileUtil.readLines(path, Charset.defaultCharset());
        Navigator.preview("/view/preview/TotalRulePreview.fxml", "", "/view/style.css", true, false);
    }
    

    @FXML
    public void closeWindow() {
        Navigator.previewStage.hide();
        ExtendedScreen.close();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.close();
        }
        secondScreenBtn.setText("第二屏幕开");
    }

    @FXML
    public void exitApp() {
        System.exit(0);
    }


    @FXML
    public void enterMatch() {
        showStage();
        stageText.setVisible(true);
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();

        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        for (Teams team : teams) {
            team.setCurrentScore(team.getTeamScore());
            team.setMemberScores(new ArrayList<>());
            if (!StringUtils.isEmpty(team.getMembers())) {
                String[] split = team.getMembers().split(",");
                for (String s : split) {
                    team.getMemberScores().add(team.getMemberScore());
                }
            }
        }
        TeamRuntime.setTeams(teams);
        teamComBox.setItems(FXCollections.observableList(teams.stream().map(x -> x.getShortName()).collect(Collectors.toList())));
        if (!StringUtils.isEmpty(config)) {
            String[] split = config.split(ConfigSpliter.SPLITER);
            String boardLayout = split[14];
            if (boardLayout.equals("1")) {
                if (scoreBoardPreview == null) {
                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardH.fxml", "", "/view/style.css", true);
                }
                scoreBoardPreview.shows();
            } else if (boardLayout.equals("2")) {
                if (scoreBoardPreview == null) {
                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardV.fxml", "", "/view/style.css", true);
                }
                scoreBoardPreview.shows();
            }
        }

        // 内屏展示
        previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", false, true);
        previewPane.shows();

    }

    public PreviewPane getPreviewPane() {
        return previewPane;
    }

    public ReloadPane getQuestionPreview() {
        return questionPreview;
    }


    public void setScoreBoard(BoardPane scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public void setTimeBoard(ReloadPane timeBoard) {
        this.timeBoard = timeBoard;
    }


    public ReloadPane getTimeBoard() {
        return timeBoard;
    }

    public BoardPane getScoreBoard() {
        return scoreBoard;
    }

    public void setQuestionPreview(ReloadPane questionPreview) {
        this.questionPreview = questionPreview;
    }
    

    public void setPreviewPane(PreviewPane previewPane) {
        this.previewPane = previewPane;
    }
}
