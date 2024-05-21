package competition.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.board.BoardPane;
import competition.controller.match.ReloadPane;
import competition.controller.preview.*;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.mapper.TeamsMapper;
import competition.modal.TeamResult;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ControlPane {

    private VideoPlayer videoPlayer;

    // 倒计时提示音控制, 默认 5 秒
    @FXML
    private TextField alertTime;

    // 内衬预览屏
    private PreviewPane previewPane;

    @FXML
    private VBox stageList, stageChildList;

    @FXML
    private CheckBox teamSelector;

    @FXML
    private Button startAnswerBtn;


    @FXML
    private AnchorPane stageName;


    @FXML
    private Label stageText;

    @FXML
    private ImageView playBtn, backendBtn, frontBtn;


    @FXML
    private ComboBox<String> questionCombox;

    @FXML
    private Button secondScreenBtn;

    @FXML
    private Button timingBtn, controlBtn;


    // 时间输入
    @FXML
    public TextField timeSecondText;

    // 分数输入
    @FXML
    private TextField scoreInput;

    // 资源显示按钮
    @FXML
    private Button showResourceBtn;


    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private TeamsMapper teamsMapper;

    private Stages currentStage, controlStage;


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
            timeVisibleBtn.setText("消失");
            timeVisibleBtn.setTextFill(Color.RED);
        }

        // 初始化图片资源
        try {
            playBtn.setImage(new Image(new FileInputStream(new File(ImageResource.播放()))));
            playBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @SneakyThrows
                @Override
                public void handle(MouseEvent event) {
                    if (videoPlayer != null) {
                        if (GlobalCache.videoPlay) {
                            try {
                                playBtn.setImage(new Image(new FileInputStream(new File(ImageResource.播放()))));
                                videoPlayer.pause();
                                GlobalCache.videoPlay = false;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            playBtn.setImage(new Image(new FileInputStream(new File(ImageResource.暂停()))));
                            videoPlayer.play();
                            GlobalCache.videoPlay = true;
                        }
                    }
                }
            });
            Tooltip frontTip = new Tooltip("进 10 秒");
            Tooltip.install(frontBtn, frontTip);
            Tooltip backTip = new Tooltip("退 10 秒");
            Tooltip.install(backendBtn, backTip);
            frontBtn.setImage(new Image(new FileInputStream(new File(ImageResource.快进()))));
            frontBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (videoPlayer != null) {
                        videoPlayer.front();
                    }
                }
            });
            backendBtn.setImage(new Image(new FileInputStream(new File(ImageResource.快退()))));
            backendBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (videoPlayer != null) {
                        videoPlayer.back();
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                if (selectedIndex == -1) {
                    return;
                }
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

        showScoreBoard();
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

        MusicPlayer.play(MusicResource.ADD_SCORE);

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

        teamSelector.setSelected(false);
        // 刷新面板分数
        messageHandler.notifyPage("", null);

    }

    public String getAlertTime() {
        return alertTime.getText();
    }

    // 减分
    @FXML
    public void descScore() {

        String text = scoreInput.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请输入分数");
            return;
        }
        MusicPlayer.play(MusicResource.DES_SCORE);
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
        if (questionPreview != null) {
            questionPreview.reload();
        }
        teamSelector.setSelected(false);
        // 刷新分数
        messageHandler.notifyPage("", null);
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
//        if (currentStage.getTimeCType() == 2) {
//            // 自动计时
//            startTiming();
//        }
        if (currentStage.getQuestionType() == null) {

        } else {
            if (startAnswerBtn.getText().equals("开始答题")) {
                if (currentStage.getQuestionType() == 7) {

                    Model7RuntimeData.reset();
                    // 发送试题列表给前端
                    boolean random = currentStage.getrRandomSort() == 1;
                    messageHandler.sendQuestionGroup(CurrentQuestions.getQuestionList(), random);
                    startTiming();
                    timeBoardPreview.hideTime();
                    //messageHandler.sendModel7Start();
                    // 扩展屏显示题目信息
                } else if (currentStage.getQuestionType() == 6) {
                    if (currentStage.getM6() == 1) {
//                        if (Model61RuntimeData.getRuntimeData().isEmpty() || questions == null) {
//                            Information.error("请先显示试题");
//                            return;
//                        }
                        //messageHandler.sendModel61Start();
                    } else if (currentStage.getM6() == 0) {
//                        if (Model60RuntimeData.getRuntimeData().isEmpty() || questions == null) {
//                            Information.error("请先显示试题");
//                            return;
//                        }
                        //messageHandler.sendModel60Start();
                    }
                    startTiming();
                }
                startAnswerBtn.setText("停止答题");
                startAnswerBtn.setTextFill(Color.RED);
            } else {
                timeReset();
                if (currentStage.getQuestionType() == 7) {
                    //messageHandler.sendModel7Start();
                    Model7RuntimeData.forceFinish();
//                    for (int i = 0; i < Model7RuntimeData.getFinished().size(); i++) {
//                        Model7RuntimeData.getFinished().set(i, true);
//                    }

                    previewPane.reload();
                    messageHandler.model7Finish();
                } else if (currentStage.getQuestionType() == 6) {
                    if (currentStage.getM6() == 1) {
                        Model61RuntimeData.forceFinish();
                        //  showAnswer();
                    } else {
                        Model60RuntimeData.forceFinish();
                    }
                }
                startAnswerBtn.setText("开始答题");
                startAnswerBtn.setTextFill(Color.BLACK);
            }

        }
    }

    // 由于时间结束导致答题结束
    public void stopOnTime() {
        timeReset();
        if (currentStage.getQuestionType() == 7) {
            Model7RuntimeData.forceFinish();
            previewPane.reload();
            messageHandler.model7Finish();
        } else if (currentStage.getQuestionType() == 6) {
//            if (currentStage.getM6() == 1){
//                showAnswer();
//            }
        }
        startAnswerBtn.setText("开始答题");
        startAnswerBtn.setTextFill(Color.BLACK);
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
        if (timeBoardPreview != null) {
            if (controlStage == null) {
                // 未进入环节的情况下默认倒计时
                timeBoardPreview.startTiming(false, time);
            } else {
                timeBoardPreview.startTiming(currentStage.getOrderVal() == 1, time);
            }
            timingBtn.setDisable(true);
        }

    }


    @FXML
    public void showResource() {
        String tihao = questionCombox.getValue();
        Questions questions = CurrentQuestions.getQ(tihao);
        if (questions.getResourceType().equals("视频")) {
            String path = BaseDir.TIKU_RES_DIR + questions.getResource();
            if (!GlobalCache.videoPlay) {
                videoPlayer = new VideoPlayer();
                videoPlayer.start(ExtendedScreen.videoStage, path);
                GlobalCache.videoPlay = true;
                try {
                    playBtn.setImage(new Image(new FileInputStream(new File(ImageResource.暂停()))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (videoPlayer != null) {
                    videoPlayer.stop();
                    ExtendedScreen.videoStage.close();
                    GlobalCache.videoPlay = false;
                    videoPlayer = null;
                }
                try {
                    playBtn.setImage(new Image(new FileInputStream(new File(ImageResource.播放()))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (questions.getResourceType().equals("音频")) {
            String path = BaseDir.TIKU_RES_DIR + questions.getResource();
            MusicPlayer.play(path);
        }
    }

    @FXML
    public void backLast() {
        PreviewPane last = PreviewStack.getLast();

        // 环节规则页无法返回，只能通过点击环节规则返回到前一个页面
        if (previewPane instanceof StageRulePreviewPreview) {
            return;
        }

        if (last instanceof QuestionPreviewPreview) {
            // 答题 返回 选题界面
            QuestionPreviewPreview x = (QuestionPreviewPreview) previewPane;
            Questions q = x.getInner();
            Stages stages = stagesMapper.selectById(CurrentQuestions.getStageId());
            // 判断当前环节的出题类型
            if (stages.getQuestionType() == 2 || stages.getQuestionType() == 1) {
                // 自选题目
                StageQuestionPreviewPreview.setCurrentStage(stages);
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
            } else if (stages.getQuestionType() == 4) {
                // 滚动选题
                RollPreviewPreview.setCurrentStage(stages);
                previewPane = Navigator.preview("/view/preview/RollPreview.fxml", "", "/view/style.css", true, true);
            } else if (stages.getQuestionType() == 3) {
                // 风险题
                List<Stages> queryList = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                        .eq(Stages::getParentId, controlStage.getId())
                );
                if (queryList.isEmpty()) {
                    Model3Preview.setPassedId(controlStage.getParentId());
                    previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, true);
                } else {
                    Model3Preview.setPassedId(controlStage.getId());
                    previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, true);
                }
            } else if (stages.getQuestionType() == 6) {
                // 答题模式
                StageQuestionPreviewPreview.setCurrentStage(stages);
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
            } else if (stages.getQuestionType() == 7) {
                // 竞技模式
                StageQuestionPreviewPreview.setCurrentStage(stages);
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
            }

        } else if (last instanceof Model7Preview) {
            Stages stages = stagesMapper.selectById(CurrentQuestions.getStageId());
            StageQuestionPreviewPreview.setCurrentStage(stages);
            previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
        } else if (last instanceof Model3Preview) {
            // 风险题返回名称
            Model3Preview x = (Model3Preview) previewPane;
            StageNamePreviewPreview.setStageId(x.getParentStageId());
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
        } else if (last instanceof CluePreviewPreview) {
            // 答题 返回 选题界面
            CluePreviewPreview x = (CluePreviewPreview) previewPane;
            Questions q = x.getInner();
            Stages stages = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getSortVal, q.getStageNumber())
            );
            // 默认为 5 
            StageQuestionPreviewPreview.setCurrentStage(stages);
            previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);

        } else if (last instanceof StageQuestionPreviewPreview) {
            // 选题界面 返回 环节名称
            StageQuestionPreviewPreview x = (StageQuestionPreviewPreview) previewPane;
            StageNamePreviewPreview.setStageId(x.getInner().getId());
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
        } else if (last instanceof RollPreviewPreview) {
            // 选题界面 返回 环节名称
            RollPreviewPreview x = (RollPreviewPreview) previewPane;
            StageNamePreviewPreview.setStageId(x.getInner().getId());
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
        } else if (last instanceof StageNamePreviewPreview) {
            // 环节名称 返回 环节列表
            StageNamePreviewPreview x = (StageNamePreviewPreview) previewPane;
            Integer stageId = x.getInnerStageId();
            Stages stages = stagesMapper.selectById(stageId);
            if (stages.getParentId() == null) {
                StagePreviewPreview.setParentStageId(null);
            } else {
                StagePreviewPreview.setParentStageId(stages.getParentId());
            }
            previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, true);
        } else if (last instanceof StagePreviewPreview) {
            // 环节列表 返回 环节名称
            StagePreviewPreview x = (StagePreviewPreview) previewPane;
            if (x.getInnerStageID() == null) {
                // 已经到了最上一层
                return;
            } else {
                StageNamePreviewPreview.setStageId(x.getInnerStageID());
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
            }
        }
//        PreviewPane p = PreviewStack.removePreview();
//        if (p != null) {
//            if (previewPane == p) {
//                p = PreviewStack.removePreview();
//            }
//            if (p != null) {
//                previewPane = p;
//                PreviewStack.addPreview(p);
//                Navigator.previewStage.setScene(previewPane.getScene());
//            }
//        }
        GlobalCache.showAnswer = false;

        updateScoreBoard();
    }


    // 选择面板所有队伍
    @FXML
    public void selectAllTeam() {
        scoreBoardPreview.selectAll(teamSelector.isSelected());
    }


    // 竞技排行
    @FXML
    public void model7Rank() {
        // 判断当前环节编号
        if (currentStage.getQuestionType() == 7 || currentStage.getQuestionType() == 6) {
            // 竞技答题排名(整体排名)
            if (!GlobalCache.showModel7Rank) {
                previewPane = Navigator.preview("/view/preview/Model7RankPreview.fxml", "", "/view/style.css", true, true);
                GlobalCache.showModel7Rank = true;
            } else {
                returnLast();
                GlobalCache.showModel7Rank = false;
            }
        }


    }

    // 选手排行
    @FXML
    public void memberRank() {
        if (!GlobalCache.showMemberRank) {
            previewPane = Navigator.preview("/view/preview/MemberRank.fxml", "", "/view/style.css", true, true);
            GlobalCache.showMemberRank = true;
        } else {
            returnLast();
            GlobalCache.showMemberRank = false;
        }
    }


    /**
     * 团队排名
     */
    @FXML
    public void teamRank() {
        if (!GlobalCache.showTeamRank) {
            previewPane = Navigator.preview("/view/preview/TeamRank.fxml", "", "/view/style.css", true, true);
            GlobalCache.showTeamRank = true;
        } else {
            returnLast();
            GlobalCache.showTeamRank = false;
        }
    }


    public void resetTimeBtn() {
        timingBtn.setDisable(false);
    }


    // 计时复位
    @FXML
    public void timeReset() {
        timingBtn.setDisable(false);
        // 时间隐藏
        messageHandler.sendQuestionTime("-1");
        if (timeBoardPreview != null) {
            timeBoardPreview.close();
        }
        timeSecondText.setText(time + "");
//        if (currentStage == null) {
//            return;
//        }

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
                .eq(Stages::getParentId, controlStage.getId())
                .orderByAsc(Stages::getSortVal)
        );
        showStageData(stages, stageChildList);
    }


    public void reloadStage() {
        currentStage = stagesMapper.selectById(currentStage.getId());
        controlStage = currentStage;
    }

    public void setCurrentStage(Stages stage) {
        currentStage = stage;
        controlStage = currentStage;
        timeSecondText.setText(currentStage.getDescTime().toString());
        scoreInput.setText(currentStage.getScorePerQ() + "");
    }

    public void selectQuestion(String value) {
        questionCombox.setValue(value);
        controlResourceBtn(value);
    }

    private void controlResourceBtn(String value) {
        Questions questions = CurrentQuestions.getQ(value);
        if (!StringUtils.isEmpty(questions.getResource())) {
            showResourceBtn.setDisable(false);
        } else {
            showResourceBtn.setDisable(true);
        }
    }

    public void showQuestions() {
        // 显示题目
        List<Questions> questionList = CurrentQuestions.getQuestionList();
        questionList.sort(Comparator.comparing(Questions::getQuestionNumber));
        final List<String> questionKeys = questionList.stream().map(x -> x.getQuestionNumber().toString()).collect(Collectors.toList());

        questionCombox.setItems(FXCollections.observableList(questionKeys));
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
                    for (Node child : stageBox.getChildren()) {
                        child.getStyleClass().remove("stage-control-clicked");
                    }
                    label.getStyleClass().add("stage-control-clicked");
                    stageText.setText(stage.getName());
                    controlStage = stage;
                    boolean last = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, stage.getId())
                    ).isEmpty();
                    if (last) {
//                        // 显示题目
//                        List<Questions> parse = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
//                        if (!CollectionUtils.isEmpty(parse)) {
//                            for (int i = 0; i < parse.size(); i++) {
//                                parse.get(i).setSort(i);
//                            }
//                            // 环节名字索引
//                            parse = parse.stream().filter(x -> !StringUtils.isEmpty(x.getStageName()) && x.getStageName().equals(stage.getName())).collect(Collectors.toList());
//                            if (!CollectionUtils.isEmpty(parse)) {
//                                for (int i = 0; i < parse.size(); i++) {
//                                    parse.get(i).setQuestionNumber(i + 1);
//                                }
//                                CurrentQuestions.setQuestions(parse, stage.getId());
//                                final List<String> questionKeys = parse.stream().map(x -> x.getQuestionNumber().toString()).collect(Collectors.toList());
//
//                                questionCombox.setItems(FXCollections.observableList(questionKeys));
////                                // 扩展屏显示对应的题目列表
////                                StageQuestionPreview.setCurrentStage(currentStage);
////                                ExtendedScreen.show("/view/match/StageQuestionPreview.fxml");
////                                
////                                // 内屏展示对应的题目列表
////                                StageQuestionPreviewPreview.setCurrentStage(currentStage);
////                                previewPane.getChildren().clear();
////                                previewPane.getChildren().add(Navigator.load("/view/preview/StageQuestionPreview.fxml"));
//                            } else {
//                                questionCombox.setItems(FXCollections.emptyObservableList());
//                                questionCombox.setValue(null);
//                            }
//
//                        }
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
            if (scoreBoardPreview != null) {
                scoreBoardPreview.reload();
            }
        }

    }


    // 显示环节规则
    @FXML
    public void showStageRule() {
        if (controlStage != null) {

            if (!GlobalCache.showRule) {

                // 风险题目
                if (controlStage.getQuestionType() == 3) {
                    List<Stages> childrenStage = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, controlStage.getId())
                    );

                    if (childrenStage.isEmpty()) {
                        Stages stages = stagesMapper.selectById(controlStage.getParentId());
                        StageRulePreviewPreview.setCurrentStage(stages);
                    } else {
                        StageRulePreviewPreview.setCurrentStage(controlStage);
                        currentStage = controlStage;
                    }
                } else {
                    StageRulePreviewPreview.setCurrentStage(controlStage);
                    currentStage = controlStage;
                }
                previewPane = Navigator.preview("/view/preview/StageRulePreview.fxml", "", "/view/style.css", true, true);
                GlobalCache.showRule = true;
                // 判断环节类型， 给前端同步规则

//                    if (controlStage.getQuestionType() == 6) {
//                        messageHandler.notifyRule(controlStage.getRuleContent());
//                    } else if (controlStage.getQuestionType() == 7) {
//                        messageHandler.notifyRule(controlStage.getRuleContent());
//                    } 

            } else {
                returnLast();
            }

        }
    }

    private void returnLast() {
        if (controlStage == null) {
            StagePreviewPreview.setParentStageId(null);
            previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, true);
            return;
        }

//        if (controlStage.getQuestionType() == 6) {
//            messageHandler.notifyRule("");
//        } else if (controlStage.getQuestionType() == 7) {
//            messageHandler.notifyRule("");
//        }
        PreviewPane last = PreviewStack.getLast();
        if (last != null) {
            if (last instanceof QuestionPreviewPreview) {
                QuestionPreviewPreview x = (QuestionPreviewPreview) last;
                Questions q = x.getInner();
                QuestionPreviewPreview.setCurrent(q);
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
            } else if (last instanceof StageQuestionPreviewPreview) {
                StageQuestionPreviewPreview x = (StageQuestionPreviewPreview) last;
                StageQuestionPreviewPreview.setCurrentStage(x.getInner());
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
            } else if (last instanceof StageNamePreviewPreview) {
                StageNamePreviewPreview x = (StageNamePreviewPreview) last;
                Integer stageId = x.getInnerStageId();
                StageNamePreviewPreview.setStageId(stageId);
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
            } else if (last instanceof StagePreviewPreview) {
                StagePreviewPreview x = (StagePreviewPreview) last;
                StagePreviewPreview.setParentStageId(x.getInnerStageID());
                previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, true);
            } else if (last instanceof Model7Preview) {
                previewPane = Navigator.preview("/view/preview/Model7Preview.fxml", "", "/view/style.css", true, true);
            } else if (last instanceof Model3Preview) {
                List<Stages> childrenStages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                        .eq(Stages::getParentId, controlStage.getId())
                );
                if (childrenStages.isEmpty()) {
                    Model3Preview.setPassedId(controlStage.getParentId());
                } else {
                    Model3Preview.setPassedId(controlStage.getId());
                }
                previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, true);
            }
            GlobalCache.showRule = false;
        }
    }


    public void emptyTeam() {
        teamComBox.getSelectionModel().select(-1);
        memberComBox.getSelectionModel().select(-1);
        teamComBox.setValue("");
        memberComBox.setValue("");
    }

    @FXML
    // 进入环节
    public void enterStage() {
        // 外屏和内屏进入对应的环节
        if (controlStage != null) {
            boolean last = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getParentId, controlStage.getId())
            ).isEmpty();

            if (controlStage.getQuestionType() == 3) {
                // 风险题 优先级高
                if (last) {
                    List<Questions> parse = RuntimeQuestionStock.getQuestions();
                    if (!CollectionUtils.isEmpty(parse)) {
                        for (int i = 0; i < parse.size(); i++) {
                            parse.get(i).setSort(i);
                        }
                        parse = parse.stream().filter(x -> !StringUtils.isEmpty(x.getStageNumber()) && x.getStageNumber().equals(controlStage.getSortVal())).collect(Collectors.toList());
                        if (controlStage.getRandomSort() == 1) {
                            Collections.shuffle(parse);
                        }
                        if (!CollectionUtils.isEmpty(parse)) {
                            for (int i = 0; i < parse.size(); i++) {
                                parse.get(i).setQuestionNumber(i + 1);
                            }
                        }
                        CurrentQuestions.setQuestions(parse, controlStage.getId());
                        currentStage = controlStage;
                    }
                    controlStage = stagesMapper.selectById(controlStage.getId());
                    showQuestions();
                    // 风险题目是最后一级，则只是展示对应的题号，不做任何界面跳转
                    Stages stages = stagesMapper.selectById(controlStage.getParentId());
                    Model3Preview.setPassedId(stages.getId());
                    setPreviewPane(
                            Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, true)
                    );
                    return;
                } else {
                    Model3Preview.setPassedId(controlStage.getId());
                    setPreviewPane(
                            Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, true)
                    );
                    return;
                }


            }
            // 底层环节，展示题目
            if (last) {
                List<Questions> parse = RuntimeQuestionStock.getQuestions();
                if (!CollectionUtils.isEmpty(parse)) {
                    for (int i = 0; i < parse.size(); i++) {
                        parse.get(i).setSort(i);
                    }
                    parse = parse.stream().filter(x -> !StringUtils.isEmpty(x.getStageNumber()) && x.getStageNumber().equals(controlStage.getSortVal())).collect(Collectors.toList());
                    if (controlStage.getRandomSort() == 1) {
                        Collections.shuffle(parse);
                    }
                    if (!CollectionUtils.isEmpty(parse)) {
                        for (int i = 0; i < parse.size(); i++) {
                            parse.get(i).setQuestionNumber(i + 1);
                        }
                    }
                    CurrentQuestions.setQuestions(parse, controlStage.getId());
                    currentStage = controlStage;
                }
                controlStage = stagesMapper.selectById(controlStage.getId());
                showQuestions();
                if (controlStage.getQuestionType() == 1) {
                    // 连续答题
                    List<Questions> collect = CurrentQuestions.getQuestionList().stream().filter(x -> !x.isHasAnswer()).collect(Collectors.toList());
                    if (!collect.isEmpty()) {
                        QuestionPreviewPreview.setCurrent(collect.get(0));
                        CurrentQuestions.recordAnswer(collect.get(0).getQuestionNumber().toString());
                    }
                    previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                } else if (controlStage.getQuestionType() == 2) {
                    // 自选题目
                    StageQuestionPreviewPreview.setCurrentStage(controlStage);
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
                } else if (controlStage.getQuestionType() == 3) {
                    // System.out.println("333333333333333333333333");
                    // 风险题
                } else if (controlStage.getQuestionType() == 4) {
                    // 滚动选题
                    RollPreviewPreview.setCurrentStage(controlStage);
                    previewPane = Navigator.preview("/view/preview/RollPreview.fxml", "", "/view/style.css", true, true);
                } else if (controlStage.getQuestionType() == 5) {
                    // 线索题
                    StageQuestionPreviewPreview.setCurrentStage(controlStage);
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
                } else if (controlStage.getQuestionType() == 6) {
                    // 答题模式
                    StageQuestionPreviewPreview.setCurrentStage(controlStage);
                    setPreviewPane(
                            Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true)
                    );
//                    List<Questions> questionList = CurrentQuestions.getQuestionList();
//                    QuestionPreviewPreview.setCurrent(questionList.get(0));
//                    selectQuestion(questionList.get(0).getQuestionNumber().toString());
                    if (controlStage.getM6() == 0) {
//                        Model60Preview.setCurrent(questionList.get(0));
//                        setPreviewPane(
//                                Navigator.preview("/view/preview/Model60.fxml", "", "/view/style.css", true, true)
//                        );
                        //messageHandler.notifyEnterWait("model60");
                    } else {
//                        Model61Preview.setCurrent(questionList.get(0));
//                        setPreviewPane(
//                                Navigator.preview("/view/preview/Model61.fxml", "", "/view/style.css", true, true)
//                        );
                        // messageHandler.notifyEnterWait("model61");
                    }
                } else if (controlStage.getQuestionType() == 7) {
                    // 竞技模式
                    StageQuestionPreviewPreview.setCurrentStage(controlStage);
                    setPreviewPane(
                            Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true)
                    );
                    // messageHandler.notifyEnterWait("model7");
                }
                previewPane.reload();
            } else {
                StageNamePreviewPreview.setStageId(controlStage.getId());
                // 展示环节名称
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
                previewPane.reload();
            }
        }
    }


    // 显示题目
    @FXML
    public void showQuestion() {
        // 重置答题状态
        // 给题目设置分值
        // 扩展屏幕展示并排答题界面（竞技模式）
        for (Questions q : CurrentQuestions.getQuestionList()) {
            q.setScore(currentStage.getScorePerQ());
        }
        if (currentStage.getQuestionType() == (7)) {
            Model7RuntimeData.setTeams(TeamRuntime.getTeams());
            previewPane = Navigator.preview("/view/preview/Model7Preview.fxml", "", "/view/style.css", true, true);
            updateScoreBoard();
        } else if (currentStage.getQuestionType() == (6)) {
            Integer m6 = currentStage.getM6();
            String tihao = questionCombox.getValue();
            Questions questions = CurrentQuestions.getQ(tihao);
            if (questions == null) {
                Information.error("请选择题号");
                return;
            }
            controlResourceBtn(tihao);
            if (m6 == 0) {
                CurrentQuestions.recordAnswer(tihao);
                // 设置当前需要显示的题目
                QuestionPreviewPreview.setCurrent(questions);
                // 设置选手信息
                Model60RuntimeData.setTeams(TeamRuntime.getTeams());
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                // messageHandler.sendQuestion(questions);
                Model60RuntimeData.setRuntimeData(questions);
                updateScoreBoard();
            } else {
                // 答题模式 -- 个人模式
                CurrentQuestions.recordAnswer(tihao);
                // 设置当前需要显示的题目
                QuestionPreviewPreview.setCurrent(questions);
                // 设置选手信息
                Model61RuntimeData.setTeams(TeamRuntime.getTeams());
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                //messageHandler.sendQuestion(questions);
                Model61RuntimeData.setRuntimeData(questions);
                updateScoreBoard();
            }
        } else {
            if (currentStage.getQuestionType() == 3) {
                String tihao = currentStage.getSortVal() + "-" + questionCombox.getValue();
                Questions questions = CurrentQuestions.getQ(tihao);
                if (questions == null) {
                    Information.error("请选择题号!!");
                    return;
                }
                controlResourceBtn(tihao);
                CurrentQuestions.recordAnswer(tihao);
                QuestionPreviewPreview.setCurrent(questions);
                GlobalCache.showAnswer = false;
                if (previewPane instanceof QuestionPreviewPreview) {
                    previewPane.reload();
                } else {
                    QuestionPreviewPreview.setCurrent(questions);
                    previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                    GlobalCache.showAnswer = false;
                    previewPane.reload();
                }

            } else {
                String tihao = questionCombox.getValue();
                Questions questions = CurrentQuestions.getQ(tihao);
                if (questions == null) {
                    Information.error("请选择题号!!");
                    return;
                }
                controlResourceBtn(tihao);
                CurrentQuestions.recordAnswer(tihao);
                // 内屏更新题目
                if (currentStage.getQuestionType() == 2 || currentStage.getQuestionType() == 1 || currentStage.getQuestionType() == 4) {
                    if (previewPane instanceof QuestionPreviewPreview) {
                        QuestionPreviewPreview.setCurrent(questions);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    } else {
                        QuestionPreviewPreview.setCurrent(questions);
                        previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    }
                } else if (currentStage.getQuestionType() == 5) {
                    if (previewPane instanceof CluePreviewPreview) {
                        CluePreviewPreview.setCurrent(questions);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    } else {
                        CluePreviewPreview.setCurrent(questions);
                        previewPane = Navigator.preview("/view/preview/CluePreview.fxml", "", "/view/style.css", true, true);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    }
                }
            }


            // previewPane.getChildren().clear();
            // previewPane.getChildren().add(Navigator.load("/view/preview/QuestionPreview.fxml"));
        }

    }

    // 答案显示
    @FXML
    public void showAnswer() {
        if (controlStage == null) {
            return;
        }
        // 更新分数
        TeamRuntime.updateScore();
        Integer questionType = controlStage.getQuestionType();
        if (questionType == 6 && controlStage.getM6() == 1) {
            if (!GlobalCache.showAnswer) {
                // 更新状态
                TeamRuntime.updateMS();
                //TeamRuntime.resetStatus();
                GlobalCache.showAnswer = true;
                List<Model61RuntimeData.ModelData> runtimeData = Model61RuntimeData.getRuntimeData();
                TeamResult result = new TeamResult();
                String answer = runtimeData.get(0).getAnswer();
                result.setCorrectAnswer(answer);
                List<TeamResult.DataItem> items = new ArrayList<>();
                for (int i = 0; i < runtimeData.size(); i++) {
                    TeamResult.DataItem item = new TeamResult.DataItem();
                    item.setAnswer(runtimeData.get(i).getUserSelect());
                    if (runtimeData.get(i).getAnswer().equals(runtimeData.get(i).getUserSelect())) {
                        item.setScore(runtimeData.get(i).getScore());
                    } else {
                        item.setScore(0);
                    }
                    item.setTeamName(runtimeData.get(i).getTeamName());
                    item.setMember(runtimeData.get(i).getUserName());
                    items.add(item);
                }

                result.setDataItemList(items);
                ShowAnswerPreview.setTeamResult(result);
                previewPane = Navigator.preview("/view/preview/ShowAnswer.fxml", "", "/view/style.css", true, true);
                messageHandler.notifyPage("model61R", result);
            } else {
                GlobalCache.showAnswer = false;
                returnLast();
            }
        }
        else {
            if (questionType == 6 && controlStage.getM6() == 0) {
                TeamRuntime.updateS();
                messageHandler.sendShowAnswer();
                // 更新状态
                List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
                List<String> selectList = runtimeData.stream().map(x -> x.getUserSelect()).collect(Collectors.toList());

                // 设置团队的选择的答案，用于展示当前前端的答题结果
                for (int i = 0; i < selectList.size(); i++) {
                    TeamRuntime.getTeams().get(i).setTeamSelect(selectList.get(i));
                }

                messageHandler.notifyPage("", null);
                if (previewPane != null) {
                    previewPane.showAnswer();
                }
//              
            } 
            else {
                TeamRuntime.updateS();
                messageHandler.sendShowAnswer();
                // 更新状态
                messageHandler.notifyPage("", null);
                if (previewPane != null) {
                    previewPane.showAnswer();
                }
            }
        }
        if (timeVisibleBtn.getText().equals("消失")) {
            if (timeBoardPreview != null) {
                timeBoardPreview.close();
            }
            resetTimeBtn();
            timeReset();
        }

        scoreBoardPreview.reload();
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
        TeamRuntime.resetStatus();
        if (timeVisibleBtn.getText().equals("消失")) {
            timeReset();
        }
        showQuestion();

    }

    // 下一题
    @FXML
    public void nextQuestion() {
        int selectedIndex = questionCombox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == questionCombox.getItems().size() - 1) {
            Information.info("已经是最后一题");
            return;
        }
        GlobalCache.showAnswer = false;
        questionCombox.getSelectionModel().select(selectedIndex + 1);
        TeamRuntime.resetStatus();
        if (timeVisibleBtn.getText().equals("消失")) {
            timeReset();
        }
        showQuestion();
    }


    @FXML
    public void toBackend() {
        if (!GlobalCache.backendOpen) {
            GlobalCache.backendOpen = true;
            Navigator.openBackend("/view/Backend.fxml", "后台设置", "/view/style.css", false, false);
        } else {
            Information.info("您已打开后台设置");
        }

    }


    // 显示总规则
    @FXML
    public void showZGZ() {
        if (!GlobalCache.showTotalRule) {
            previewPane = Navigator.preview("/view/preview/TotalRulePreview.fxml", "", "/view/style.css", true, false);

            GlobalCache.showTotalRule = true;
//            if (!StringUtils.isEmpty(controlStage.getRuleContent())) {
//                StageRulePreviewPreview.setCurrentStage(controlStage);
//                previewPane = Navigator.preview("/view/preview/StageRulePreview.fxml", "", "/view/style.css", true, true);
//               
//                // messageHandler.notifyEnterWait("7", controlStage.getRuleContent());
//            }
        } else {
            PreviewPane last = PreviewStack.getLast();
            if (last != null) {
                if (last instanceof QuestionPreviewPreview) {
                    QuestionPreviewPreview x = (QuestionPreviewPreview) last;
                    Questions q = x.getInner();
                    QuestionPreviewPreview.setCurrent(q);
                    previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true);
                } else if (last instanceof StageQuestionPreviewPreview) {
                    StageQuestionPreviewPreview x = (StageQuestionPreviewPreview) last;
                    StageQuestionPreviewPreview.setCurrentStage(x.getInner());
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true);
                } else if (last instanceof StageNamePreviewPreview) {
                    StageNamePreviewPreview x = (StageNamePreviewPreview) last;
                    Integer stageId = x.getInnerStageId();
                    StageNamePreviewPreview.setStageId(stageId);
                    previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, true);
                } else if (last instanceof StagePreviewPreview) {
                    StagePreviewPreview x = (StagePreviewPreview) last;
                    StagePreviewPreview.setParentStageId(x.getInnerStageID());
                    previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, true);
                }
            } else {
                enterMatch();
            }
            GlobalCache.showTotalRule = false;
        }
    }


    @FXML
    public void closeWindow() {
        Navigator.previewStage.hide();
        ExtendedScreen.close();
        secondScreenBtn.setText("第二屏幕开");
        messageHandler.notifyPage("wait", null);
    }

    @FXML
    public void exitApp() {
        ScoreSaver.saveScore();
        System.exit(0);
    }


    public void showScoreBoard() {
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();

        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        // 排序队伍，按照编码排序
        teams.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));

        for (Teams team : teams) {
            team.setCurrentScore(team.getTeamScore());
            team.setMemberScores(new ArrayList<>());
            team.setQScores(new ArrayList<>());
            team.setStatusList(new ArrayList<>());
            team.setStatus(0);
            if (!StringUtils.isEmpty(team.getMembers())) {
                String[] split = team.getMembers().split(",");
                for (String s : split) {
                    team.getMemberScores().add(team.getMemberScore());
                    team.getStatusList().add(0);
                    team.getQScores().add(0);
                }
            }
        }
        // 若已有分数，则加载旧的分数
        List<Teams> existTeams = ScoreSaver.loadScore();
        if (existTeams.isEmpty()) {
            existTeams.addAll(teams);
        } else {
            for (int i = 0; i < teams.size(); i++) {
                if (i < existTeams.size()) {
                    teams.get(i).setCurrentScore(existTeams.get(i).getCurrentScore());
                    teams.get(i).setTeamScore(existTeams.get(i).getTeamScore());
                    teams.get(i).setMemberScore(existTeams.get(i).getMemberScore());
                    teams.get(i).setMemberScores(existTeams.get(i).getMemberScores());
                }
            }
            existTeams = teams;
        }
        TeamRuntime.setTeams(existTeams);
        Model7RuntimeData.setTeams(TeamRuntime.getTeams());
        Model60RuntimeData.setTeams(TeamRuntime.getTeams());
        Model61RuntimeData.setTeams(TeamRuntime.getTeams());
        teamComBox.setItems(FXCollections.observableList(existTeams.stream().map(x -> x.getShortName()).collect(Collectors.toList())));
        if (!StringUtils.isEmpty(config)) {
            String[] split = config.split(ConfigSpliter.SPLITER);
            String boardLayout = split[14];
            if (boardLayout.equals("1")) {
                if (scoreBoardPreview == null) {
                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardH.fxml", "", "/view/style.css", true);
                }
                if (scoreBoardPreview != null) {
                    scoreBoardPreview.shows();
                }

            } else if (boardLayout.equals("2")) {
                if (scoreBoardPreview == null) {
                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardV.fxml", "", "/view/style.css", true);
                }
                if (scoreBoardPreview != null) {
                    scoreBoardPreview.shows();
                }
            }
        }
    }


    public void updateScoreBoard() {
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            String[] split = config.split(ConfigSpliter.SPLITER);
            String boardLayout = split[14];
            if (boardLayout.equals("1")) {
                if (scoreBoardPreview != null) {
                    scoreBoardPreview.reload();
                } else {
                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardH.fxml", "", "/view/style.css", true);
                }
            } else if (boardLayout.equals("2")) {
                if (scoreBoardPreview != null) {
                    scoreBoardPreview.reload();
                } else {
                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardV.fxml", "", "/view/style.css", true);
                }
            }
        }
    }


    @FXML
    public void enterMatch() {
//        Configs configs = configsMapper.selectById(1);
//        String config = configs.getConfig();
//
//        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
//        for (Teams team : teams) {
//            team.setCurrentScore(team.getTeamScore());
//            team.setMemberScores(new ArrayList<>());
//            if (!StringUtils.isEmpty(team.getMembers())) {
//                String[] split = team.getMembers().split(",");
//                for (String s : split) {
//                    team.getMemberScores().add(team.getMemberScore());
//                }
//            }
//        }
//        TeamRuntime.setTeams(teams);
//        teamComBox.setItems(FXCollections.observableList(teams.stream().map(x -> x.getShortName()).collect(Collectors.toList())));
//        if (!StringUtils.isEmpty(config)) {
//            String[] split = config.split(ConfigSpliter.SPLITER);
//            String boardLayout = split[14];
//            if (boardLayout.equals("1")) {
//                if (scoreBoardPreview == null) {
//                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardH.fxml", "", "/view/style.css", true);
//                }
//                scoreBoardPreview.shows();
//            } else if (boardLayout.equals("2")) {
//                if (scoreBoardPreview == null) {
//                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardV.fxml", "", "/view/style.css", true);
//                }
//                scoreBoardPreview.shows();
//            }
//        }

        // 内屏展示
        showStage();
        stageText.setVisible(true);
        StagePreviewPreview.setParentStageId(null);
        previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", false, true);
        previewPane.shows();

    }

    public ReloadPane getTimeBoardPreview() {
        return timeBoardPreview;
    }

    public BoardPane getScoreBoardPreview() {
        return scoreBoardPreview;
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

    public Stages getCurrentStage() {
        return currentStage;
    }

    public Button getStartAnswerBtn() {
        return startAnswerBtn;
    }
}
