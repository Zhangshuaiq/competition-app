package competition.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.JavaFxApplication;
import competition.controller.backend.TerminalConfig;
import competition.controller.board.BoardPane;
import competition.controller.match.ReloadPane;
import competition.controller.preview.*;
import competition.dto.BuzzDevice;
import competition.dto.ScreenDevice;
import competition.entity.*;
import competition.mapper.*;
import competition.modal.TeamResult;
import competition.serial.Command;
import competition.serial.SerialPortConfig;
import competition.serial.SerialPortConnector;
import competition.service.ScoreService;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ControlPane {
    private double xOffset = 0;
    private double yOffset = 0;

    private VideoPlayer videoPlayer;

    // 倒计时提示音控制, 默认 5 秒


    @FXML
    private Circle hardwareStatus;

    @FXML
    private ImageView iconImg;

    @FXML
    private Label closeLabel;

    @FXML
    private AnchorPane parentPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField alertTime;

    @FXML
    private Button lastQBtn, nextQBtn;

    // 内衬预览屏
    private PreviewPane previewPane;

    @FXML
    private VBox stageList, stageChildList;

    @FXML
    private CheckBox teamSelector;

    @Resource
    private BuzzMapper buzzMapper;

    @Resource
    private TimeMapper timeMapper;

    @Resource
    private QuestionsMapper questionsMapper;

    @FXML
    private Button startAnswerBtn;


    @FXML
    private AnchorPane stageName;


    @FXML
    private Label stageText;

    @FXML
    private ImageView playBtn, backendBtn1, frontBtn1;

    @FXML
    private Button button, backendBtn, frontBtn, backendCfgBtn;

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
    private TextField scoreInput, scoreDescInput;

    // 资源显示按钮
    @FXML
    private Button showResourceBtn;


    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ScoreService scoreService;

    @Resource
    private TeamsMapper teamsMapper;

    private Stages currentStage, controlStage;


    // 外接显示屏展示题目
    private ReloadPane questionPreview;

    // 主屏幕端计分板
    private BoardPane scoreBoardPreview;

    // 计时器
    private ReloadPane timeBoardPreview;

    // 抢答计时器
    private ReloadPane buzzTimeBoardPreview;

    // 外屏幕端抢答计时器
    private ReloadPane buzzTimeBoard;


    // 抢答结果面板
    private ReloadPane buzzResultPreview;

    // 外屏幕抢答结果面板

    private ReloadPane buzzResult;

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
    private Button buzzBtn, backBtn;

    // 计时显示控制
    @FXML
    private Button timeVisibleBtn;

    @FXML
    private HBox titleBar, dragBox;

    // 更新客户端展示的数据（与客户端实时交互）
    @Resource
    private WsMessageHandler messageHandler;

    @Resource
    private Model7StatusMapper model7StatusMapper;


    private void loadConnectionStatsBar(){
        Configs configs = configsMapper.selectById(12);
        String config = configs.getConfig();
        RuntimeUtil.showConnectionStatus = (Objects.equals("1", config));
        if (RuntimeUtil.showConnectionStatus) {
            Navigator.connectionBar("/view/board/preview/TeamConnectionBar.fxml", "", "/view/style.css", true);
        }
    }


    @FXML
    public void lastTeam() {
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex <= 0) {
            teamComBox.getSelectionModel().selectLast();
            selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
            String members = TeamRuntime.getTeams().get(selectedIndex).getMembers();
            if (!StringUtils.isEmpty(members)) {
                memberComBox.setItems(FXCollections.observableList(Arrays.asList(members.split(","))));
            } else {
                memberComBox.setItems(FXCollections.emptyObservableList());
            }
            memberComBox.setValue("");
            teamScore.setText(TeamRuntime.getTeams().get(selectedIndex).getTeamScore() + "");
        } else {
            teamComBox.getSelectionModel().selectPrevious();
            String members = TeamRuntime.getTeams().get(selectedIndex - 1).getMembers();
            if (!StringUtils.isEmpty(members)) {
                memberComBox.setItems(FXCollections.observableList(Arrays.asList(members.split(","))));
            } else {
                memberComBox.setItems(FXCollections.emptyObservableList());
            }
            memberComBox.setValue("");
            teamScore.setText(TeamRuntime.getTeams().get(selectedIndex - 1).getTeamScore() + "");
        }

    }

    @FXML
    public void nextTeam() {
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= teamComBox.getItems().size() - 1) {
            teamComBox.getSelectionModel().selectFirst();
            selectedIndex = 0;
            String members = TeamRuntime.getTeams().get(selectedIndex).getMembers();
            if (!StringUtils.isEmpty(members)) {
                memberComBox.setItems(FXCollections.observableList(Arrays.asList(members.split(","))));
            } else {
                memberComBox.setItems(FXCollections.emptyObservableList());
            }
            memberComBox.setValue("");
            teamScore.setText(TeamRuntime.getTeams().get(selectedIndex).getTeamScore() + "");
        } else {
            teamComBox.getSelectionModel().selectNext();
            String members = TeamRuntime.getTeams().get(selectedIndex + 1).getMembers();
            if (!StringUtils.isEmpty(members)) {
                memberComBox.setItems(FXCollections.observableList(Arrays.asList(members.split(","))));
            } else {
                memberComBox.setItems(FXCollections.emptyObservableList());
            }
            memberComBox.setValue("");
            teamScore.setText(TeamRuntime.getTeams().get(selectedIndex + 1).getTeamScore() + "");
        }

    }


    @FXML
    public void lastMember() {
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= teamComBox.getItems().size() - 1) {
            return;
        }
        int index1 = memberComBox.getSelectionModel().getSelectedIndex();
        String selectedMember;
        if (index1 <= 0) {
            memberComBox.getSelectionModel().selectLast();
        } else {
            memberComBox.getSelectionModel().selectPrevious();
        }
        selectedMember = memberComBox.getSelectionModel().getSelectedItem();
        if (selectedMember != null && selectedMember.length() > 0) {
            String memberScore = TeamRuntime.getTeams().get(selectedIndex).getMemberScore();
            JSONObject jsonObject = JSON.parseObject(memberScore);
            ControlPane.this.memberScore.setText(jsonObject.getIntValue(selectedMember) + "");
        } else {
            memberScore.setText("0");
        }
    }


    @FXML
    public void nextMember() {
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= teamComBox.getItems().size() - 1) {
            return;
        }
        int index1 = memberComBox.getSelectionModel().getSelectedIndex();
        String selectedMember;
        if (index1 >= memberComBox.getItems().size() - 1) {
            memberComBox.getSelectionModel().selectFirst();
        } else {
            memberComBox.getSelectionModel().selectNext();
        }
        selectedMember = memberComBox.getSelectionModel().getSelectedItem();
        if (selectedMember != null && selectedMember.length() > 0) {
            String memberScore = TeamRuntime.getTeams().get(selectedIndex).getMemberScore();
            JSONObject jsonObject = JSON.parseObject(memberScore);
            ControlPane.this.memberScore.setText(jsonObject.getIntValue(selectedMember) + "");
        } else {
            memberScore.setText("0");
        }
    }


    @FXML
    public void clickVis() {
        if (timeVisibleBtn.getText().equals("不消失")) {
            timeVisibleBtn.setText("消失");
            timeVisibleBtn.setTextFill(Color.BLACK);
            timeVisibleBtn.setStyle("");
        } else {
            timeVisibleBtn.setText("不消失");
            timeVisibleBtn.setStyle("-fx-background-color: #01a25a");
            timeVisibleBtn.setTextFill(Color.WHITE);
        }
    }

    @Resource
    private RegisterInfoMapper registerInfoMapper;

    private void setBackendStatus() {
        if (GlobalCache.MAX_TEAM_NUMBER > 0) {
            backendCfgBtn.setDisable(false);
        } else {
            backendCfgBtn.setDisable(true);
        }
    }

    private boolean enterModelQuestionPage(){
        return previewPane != null &&
                ((previewPane instanceof QuestionPreviewPreview && (currentStage.getQuestionType() == 7 || currentStage.getQuestionType() == 6))
                        || (previewPane instanceof CluePreviewPreview)
                        || (previewPane instanceof Model7Preview)
                );
    }

    private boolean enterQuestionPage(){
        return previewPane != null &&
                ((previewPane instanceof QuestionPreviewPreview)
                        || (previewPane instanceof CluePreviewPreview)
                        || (previewPane instanceof Model7Preview)
                );
    }

    @FXML
    public void initialize() {
        // 检测硬件状态
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(100), event -> {

            if (scoreBoard != null) {
                if (previewPane instanceof RankPane) {
                    scoreBoard.close();
                }   else {
                    scoreBoard.shows();
                }
            }

            // 检测开始答题状态
            if (enterModelQuestionPage()) {
                startAnswerBtn.setDisable(false);
            } else {
                startAnswerBtn.setDisable(true);
            }

            // 检测返回按钮状态
            if (!Objects.equals("开始答题", startAnswerBtn.getText())) {
                backBtn.setDisable(true);
            } else {
                backBtn.setDisable(false);
            }

            SerialPortConnector instance = SerialPortConnector.getInstance();
            if (instance == null) {
                hardwareStatus.setFill(Color.RED);
            } else if (instance.isConnected()) {
                hardwareStatus.setFill(Color.GREEN);
            } else {
                hardwareStatus.setFill(Color.RED);
            }

//            double random = Math.random();
//            if (random < 0.33) {
//                hardwareStatus.setFill(Color.RED);
//            } else if (random < 0.66) {
//                hardwareStatus.setFill(Color.YELLOW);
//            } else {
//                hardwareStatus.setFill(Color.GREEN);
//            }

        }));
        timeline.setCycleCount(-1);
        timeline.play();

        closeLabel.getStyleClass().add("label-hover");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        try {
            iconImg.setImage(new Image(new FileInputStream(BaseDir.SUCAI_DIR + "图标.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        dragBox.setOnMousePressed((MouseEvent event) -> {
            // 记录鼠标按下时的坐标
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        dragBox.setOnMouseDragged((MouseEvent event) -> {
            // 根据鼠标拖动的距离移动窗口
            Stage primaryStage = (Stage) (dragBox.getScene().getWindow());
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // 根据是否注册来判断其按钮状态
        setBackendStatus();
        parentPane.setFocusTraversable(true);

        TimeEntity entity = timeMapper.selectById(1);
        alertTime.setText(entity.getTime().toString());
        alertTime.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    entity.setTime(Integer.parseInt(newValue));
                    timeMapper.updateById(entity);
                } catch (Exception ex) {
                    log.error("设置失败");
                }
            }
        });

        Navigator.timeBoardStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.S) {
                    lastQuestion();
                } else if (event.getCode() == KeyCode.X) {
                    nextQuestion();
                } else if (event.getCode() == KeyCode.T) {
                    startTiming();
                } else if (event.getCode() == KeyCode.R) {
                    timeReset();
                } else if (event.getCode() == KeyCode.F) {
                    backLast();
                } else if (event.getCode() == KeyCode.D) {
                    if (enterQuestionPage()) {
                        showAnswer();
                    }
                } else if (event.getCode() == KeyCode.F1) {
                    if (previewPane != null) {
                        previewPane.scrollUp();
                    }
                } else if (event.getCode() == KeyCode.F2) {
                    if (previewPane != null) {
                        previewPane.scrollDown();
                    }
                } else if (event.getCode() == KeyCode.F3) {
                    if (previewPane != null) {
                        previewPane.scrollUpAnswer();
                    }
                } else if (event.getCode() == KeyCode.F4) {
                    if (previewPane != null) {
                        previewPane.scrollDownAnswer();
                    }
                } else if (event.getCode() == KeyCode.EQUALS) {
                    addScore();
                } else if (event.getCode() == KeyCode.MINUS) {
                    descScore();
                }
            }
        });

        Navigator.previewStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.S) {
                    lastQuestion();
                } else if (event.getCode() == KeyCode.X) {
                    nextQuestion();
                } else if (event.getCode() == KeyCode.T) {
                    startTiming();
                } else if (event.getCode() == KeyCode.R) {
                    timeReset();
                } else if (event.getCode() == KeyCode.F) {
                    backLast();
                } else if (event.getCode() == KeyCode.D) {
                    if (enterQuestionPage()) {
                        showAnswer();
                    }
                } else if (event.getCode() == KeyCode.F1) {
                    if (previewPane != null) {
                        previewPane.scrollUp();
                    }
                } else if (event.getCode() == KeyCode.F2) {
                    if (previewPane != null) {
                        previewPane.scrollDown();
                    }
                } else if (event.getCode() == KeyCode.F3) {
                    if (previewPane != null) {
                        previewPane.scrollUpAnswer();
                    }
                } else if (event.getCode() == KeyCode.F4) {
                    if (previewPane != null) {
                        previewPane.scrollDownAnswer();
                    }
                } else if (event.getCode() == KeyCode.EQUALS) {
                    addScore();
                } else if (event.getCode() == KeyCode.MINUS) {
                    descScore();
                }
            }
        });

        ExtendedScreen.extendScreen.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.S) {
                    lastQuestion();
                } else if (event.getCode() == KeyCode.X) {
                    nextQuestion();
                } else if (event.getCode() == KeyCode.T) {
                    startTiming();
                } else if (event.getCode() == KeyCode.R) {
                    timeReset();
                } else if (event.getCode() == KeyCode.F) {
                    backLast();
                } else if (event.getCode() == KeyCode.D) {
                    if (enterQuestionPage()) {
                        showAnswer();
                    }
                } else if (event.getCode() == KeyCode.F1) {
                    if (previewPane != null) {
                        previewPane.scrollUp();
                    }
                } else if (event.getCode() == KeyCode.F2) {
                    if (previewPane != null) {
                        previewPane.scrollDown();
                    }
                } else if (event.getCode() == KeyCode.F3) {
                    if (previewPane != null) {
                        previewPane.scrollUpAnswer();
                    }
                } else if (event.getCode() == KeyCode.F4) {
                    if (previewPane != null) {
                        previewPane.scrollDownAnswer();
                    }
                } else if (event.getCode() == KeyCode.EQUALS) {
                    addScore();
                } else if (event.getCode() == KeyCode.MINUS) {
                    descScore();
                }
            }
        });

        Navigator.scoreBoardStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.S) {
                    lastQuestion();
                } else if (event.getCode() == KeyCode.X) {
                    nextQuestion();
                } else if (event.getCode() == KeyCode.T) {
                    startTiming();
                } else if (event.getCode() == KeyCode.R) {
                    timeReset();
                } else if (event.getCode() == KeyCode.F) {
                    backLast();
                } else if (event.getCode() == KeyCode.D) {
                    if (enterQuestionPage()) {
                        showAnswer();
                    }
                } else if (event.getCode() == KeyCode.F1) {
                    if (previewPane != null) {
                        previewPane.scrollUp();
                    }
                } else if (event.getCode() == KeyCode.F2) {
                    if (previewPane != null) {
                        previewPane.scrollDown();
                    }
                } else if (event.getCode() == KeyCode.F3) {
                    if (previewPane != null) {
                        previewPane.scrollUpAnswer();
                    }
                } else if (event.getCode() == KeyCode.F4) {
                    if (previewPane != null) {
                        previewPane.scrollDownAnswer();
                    }
                } else if (event.getCode() == KeyCode.EQUALS) {
                    addScore();
                } else if (event.getCode() == KeyCode.MINUS) {
                    descScore();
                }
            }
        });

        parentPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.S) {
                    lastQuestion();
                } else if (event.getCode() == KeyCode.X) {
                    nextQuestion();
                } else if (event.getCode() == KeyCode.T) {
                    startTiming();
                } else if (event.getCode() == KeyCode.R) {
                    timeReset();
                } else if (event.getCode() == KeyCode.F) {
                    backLast();
                } else if (event.getCode() == KeyCode.D) {
                    if (enterQuestionPage()) {
                        showAnswer();
                    }
                } else if (event.getCode() == KeyCode.F1) {
                    if (previewPane != null) {
                        previewPane.scrollUp();
                    }
                } else if (event.getCode() == KeyCode.F2) {
                    if (previewPane != null) {
                        previewPane.scrollDown();
                    }
                } else if (event.getCode() == KeyCode.F3) {
                    if (previewPane != null) {
                        previewPane.scrollUpAnswer();
                    }
                } else if (event.getCode() == KeyCode.F4) {
                    if (previewPane != null) {
                        previewPane.scrollDownAnswer();
                    }
                } else if (event.getCode() == KeyCode.EQUALS) {
                    addScore();
                } else if (event.getCode() == KeyCode.MINUS) {
                    descScore();
                }
            }
        });


        if (timeVisibleBtn.getText().equals("不消失")) {
            timeVisibleBtn.setTextFill(Color.WHITE);
            timeVisibleBtn.setStyle("-fx-background-color: #01a25a");
        } else {
            timeVisibleBtn.setText("消失");
            timeVisibleBtn.setTextFill(Color.BLACK);
            timeVisibleBtn.setStyle("");
        }

        // 初始化图片资源
        try {
            playBtn.setImage(new Image(new FileInputStream(new File(ImageResource.播放()))));
            button.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
            frontBtn1.setImage(new Image(new FileInputStream(new File(ImageResource.快进()))));
            frontBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (videoPlayer != null) {
                        videoPlayer.front();
                    }
                }
            });
            backendBtn1.setImage(new Image(new FileInputStream(new File(ImageResource.快退()))));
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
            secondScreenBtn.setText("扩展  开");
            secondScreenBtn.setTextFill(Color.WHITE);
            secondScreenBtn.setStyle("-fx-background-color: #01a25a");
        } else {
            secondScreenBtn.setText("扩展  关");
            secondScreenBtn.setTextFill(Color.BLACK);
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
                teamScore.setText(TeamRuntime.getTeams().get(selectedIndex).getTeamScore() + "");
            }
        });

        memberComBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
                String selectedMember = memberComBox.getSelectionModel().getSelectedItem();
                if (selectedMember != null && selectedMember.length() > 0) {
                    String memberScore = TeamRuntime.getTeams().get(selectedIndex).getMemberScore();
                    JSONObject jsonObject = JSON.parseObject(memberScore);
                    ControlPane.this.memberScore.setText(jsonObject.getIntValue(selectedMember) + "");
                } else {
                    memberScore.setText("0");
                }

            }
        });

        showScoreBoard();
        loadConnectionStatsBar();


    }

    private void showStage() {
        final List<Stages> stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .isNull(Stages::getParentId)
                .orderByAsc(Stages::getSortVal)
        );
        showStageData(stages, stageList);
        showTopStage();
    }


    // 抢答复位
    @FXML
    public void buzzReset(){
        RuntimeUtil.showBuzzBar = false;
        if (buzzTimeBoardPreview != null) {
            buzzTimeBoardPreview.close();
        }
        buzzBtn.setText("开始抢答");
        buzzBtn.setTextFill(Color.BLACK);
        buzzBtn.setStyle("");
        TeamRuntime.resetBuzzStatus();
        Navigator.buzzBoardStage.close();
        ExtendedScreen.buzzBoardStage.close();
        reloadScoreBoard();
    }

    // 抢答按钮点击
    @FXML
    public void buzzClicked() {
        Buzz buzz = buzzMapper.selectById(1);
        Platform.runLater(() -> {
            String text = buzzBtn.getText();
            if (text.equals("开始抢答")) {
                TeamRuntime.resetBuzzStatus();
                closeBuzzResultWindow();
                RuntimeUtil.buzzCalculate = 0;
                RuntimeUtil.buzzStart = 0;
                RuntimeUtil.showBuzzBar = false;
                RuntimeUtil.buzzTimeMap.clear();
                RuntimeUtil.buzzResult.clear();
                // 清空已有但未播放完毕的旧音乐文件
                MusicPlayer.clearQueue();
                if (scoreBoardPreview != null) {
                    reloadScoreBoard();
                }

                String mode = "直接抢答";
                int timeLimit = 10;
                if (buzz != null) {
                    mode = buzz.getMode();
                    timeLimit = buzz.getTimeLimit();
                }
                if (currentStage != null && !StringUtils.isEmpty(currentStage.getpAType())) {
                    mode = currentStage.getpAType();
                }
                messageHandler.notifyPage("", null);
                buzzBtn.setText("停止抢答");
                buzzBtn.setTextFill(Color.WHITE);
                RuntimeUtil.buzzStart = System.nanoTime();

                buzzBtn.setStyle("-fx-background-color: #01a25a");
                if (!mode.equals("直接抢答")) {
                    // 倒计时
                    startBuzz(timeLimit, true,3);
                } else {
                    RuntimeUtil.buzzCalculate = System.nanoTime();
                    startBuzz(timeLimit, true, 0);
                }
                TerminalConfig.sendStartBuzz();
            } else {
                TerminalConfig.sendStopBuzz();
                buzzReset();
            }
        });

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
        Set<String> selected;
        if (scoreBoardPreview == null) {
            selected = new HashSet<>();
        } else {
            selected = scoreBoardPreview.getSelected();
        }

        for (Teams teamList : teamLists) {
            for (String s : selected) {
                if (teamList.getShortName().equals(s)) {
                    teamList.setTeamScore(teamList.getTeamScore() + Integer.parseInt(text));
                    teamsMapper.updateById(teamList);
                    if (currentStage != null && !StringUtils.isEmpty(questionCombox.getValue())) {
                        Questions questions;
                        if (controlStage.getRandomSort() == 1) {
                            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                    .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()))
                                    .eq(Questions::getStageNumber, currentStage.getSortVal())
                            );

                        } else {
                            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                    .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                                    .eq(Questions::getStageNumber, currentStage.getSortVal())
                            );

                        }


                        // currentStage
                        // Questions questions = CurrentQuestions.getQuestionList().get(Integer.parseInt(questionCombox.getValue()) - 1);
                        if (questions == null) {
                            scoreService.addScore(null, teamList, currentStage.getName(), null, null, Integer.parseInt(text),
                                    teamList.getTeamScore().toString(), "");
                        } else {
                            if (controlStage.getRandomSort() == 1) {
                                questions.setOrder(questions.getViewSort());
                            } else {
                                questions.setOrder(questions.getQuestionNumber());
                            }
                            scoreService.addScore(null, teamList, currentStage.getName(), questions.getQuestionNumber().toString(),
                                    null, Integer.parseInt(text), teamList.getTeamScore().toString(), "");
                        }
                    } else {
                        scoreService.addScore(null, teamList, null, null, null, Integer.parseInt(text),
                                teamList.getTeamScore().toString(), "");
                    }
                }
            }
        }

        MusicPlayer.play(MusicResource.ADD_SCORE);

        // 添加团队分数
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Teams teams = TeamRuntime.getTeams().get(selectedIndex);


            // 添加个人分数
            int selectedIndex1 = memberComBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex1 > -1) {
                String member = memberComBox.getSelectionModel().getSelectedItem();
                String memberScore = teams.getMemberScore();
                JSONObject jsonObject = JSON.parseObject(memberScore);
                jsonObject.put(member, jsonObject.getIntValue(member) + Integer.parseInt(text));
                teams.setMemberScore(JSON.toJSONString(jsonObject));
                teamsMapper.updateById(teams);
                this.memberScore.setText(jsonObject.getIntValue(member) + "");

                if (currentStage != null) {

                    Questions questions;
                    if (controlStage.getRandomSort() == 1) {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    } else {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    }


                    if (questions == null) {
                        scoreService.addScore(memberComBox.getValue(), teams, currentStage.getName(), null, null, Integer.parseInt(text),
                                jsonObject.getIntValue(member) + "", "");
                    } else {
                        if (controlStage.getRandomSort() == 1) {
                            questions.setOrder(questions.getViewSort());
                        } else {
                            questions.setOrder(questions.getQuestionNumber());
                        }
                        scoreService.addScore(memberComBox.getValue(), teams, currentStage.getName(), questions.getQuestionNumber().toString(),
                                null, Integer.parseInt(text), jsonObject.getIntValue(member) + "", "");
                    }
                } else {
                    scoreService.addScore(memberComBox.getValue(), teams, null, null,
                            null, Integer.parseInt(text), jsonObject.getIntValue(member) + "", "");
                }
            } else {
                memberScore.setText("0");
            }

            // 给控制面板内加分，如果已选择则忽略，避免二次加分
            if (!selected.contains(teams.getShortName())) {
                teams.setTeamScore(teams.getTeamScore() + Integer.parseInt(text));
                teamsMapper.updateById(teams);
                if (currentStage != null) {

                    Questions questions;
                    if (controlStage.getRandomSort() == 1) {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    } else {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    }
                    if (questions == null) {
                        scoreService.addScore(null, teams, currentStage.getName(), null,
                                null, Integer.parseInt(text), teams.getTeamScore().toString(), "");
                    } else {
                        if (controlStage.getRandomSort() == 1) {
                            questions.setOrder(questions.getViewSort());
                        } else {
                            questions.setOrder(questions.getQuestionNumber());
                        }
                        scoreService.addScore(null, teams, currentStage.getName(), questions.getQuestionNumber().toString(),
                                null, Integer.parseInt(text), teams.getTeamScore().toString(), "");
                    }
                } else {
                    scoreService.addScore(null, teams, null, null,
                            null, Integer.parseInt(text), teams.getTeamScore().toString(), "");
                }
            }
            teamScore.setText(teams.getTeamScore().toString());
        }
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
            scoreBoardPreview.reload();
        }
        if (previewPane != null
                && (previewPane instanceof MemberRankPreview || previewPane instanceof TeamRankPreview || previewPane instanceof BuzzRankPreview)) {
            previewPane.reload();
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

        String text = scoreDescInput.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请输入分数");
            return;
        }
        MusicPlayer.play(MusicResource.DES_SCORE);
        // 面板快速减少
        List<Teams> teamLists = TeamRuntime.getTeams();
        Set<String> selected = new HashSet<>();
        if (scoreBoardPreview != null) {
            selected = scoreBoardPreview.getSelected();
        }
        for (Teams teamList : teamLists) {
            for (String s : selected) {
                if (teamList.getShortName().equals(s)) {
                    int currScore = teamList.getTeamScore() - Integer.parseInt(text);
                    teamList.setTeamScore(currScore);
                    teamsMapper.updateById(teamList);
                    if (currentStage != null && !StringUtils.isEmpty(questionCombox.getValue())) {


                        Questions questions;
                        if (controlStage.getRandomSort() == 1) {
                            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                    .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()))
                                    .eq(Questions::getStageNumber, currentStage.getSortVal())
                            );
                        } else {
                            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                    .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                                    .eq(Questions::getStageNumber, currentStage.getSortVal())
                            );
                        }
                        if (questions == null) {
                            scoreService.addScore(null, teamList, currentStage.getName(), null,
                                    null, -Integer.parseInt(text), teamList.getTeamScore().toString(), "");
                        } else {
                            if (controlStage.getRandomSort() == 1) {
                                questions.setOrder(questions.getViewSort());
                            } else {
                                questions.setOrder(questions.getQuestionNumber());
                            }
                            scoreService.addScore(null, teamList, currentStage.getName(), questions.getQuestionNumber().toString(),
                                    null, -Integer.parseInt(text), teamList.getTeamScore().toString(), "");
                        }
                    } else {
                        scoreService.addScore(null, teamList, null, null,
                                null, -Integer.parseInt(text), teamList.getTeamScore().toString(), "");
                    }
                }
            }
        }


        // 减少团队分数
        int selectedIndex = teamComBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Teams teams = TeamRuntime.getTeams().get(selectedIndex);


            // 减少个人分数
            int selectedIndex1 = memberComBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex1 > -1) {
                String member = memberComBox.getSelectionModel().getSelectedItem();
                String memberScore = teams.getMemberScore();
                JSONObject jsonObject = JSON.parseObject(memberScore);
                int memItemScore = jsonObject.getIntValue(member) - Integer.parseInt(text);
                jsonObject.put(member, memItemScore);
                teams.setMemberScore(JSON.toJSONString(jsonObject));
                teamsMapper.updateById(teams);
                if (currentStage != null) {

                    Questions questions;
                    if (controlStage.getRandomSort() == 1) {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()) )
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    } else {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    }

                    if (questions == null) {
                        scoreService.addScore(memberComBox.getValue(), teams, currentStage.getName(), null,
                                null, -Integer.parseInt(text), memItemScore + "", "");
                    } else {
                        questions.setOrder(Integer.parseInt(questionCombox.getValue()) );
                        scoreService.addScore(memberComBox.getValue(), teams, currentStage.getName(),
                                questions.getQuestionNumber().toString(), null, -Integer.parseInt(text), memItemScore + "", "");
                    }
                } else {
                    scoreService.addScore(memberComBox.getValue(), teams, null, null, null,
                            -Integer.parseInt(text), memItemScore + "", "");
                }
                this.memberScore.setText(memItemScore + "");
            } else {
                memberScore.setText("0");
            }

            if (!selected.contains(teams.getShortName())) {
                int currScore = teams.getTeamScore() - Integer.parseInt(text);
                teams.setTeamScore(currScore);
                teamsMapper.updateById(teams);
                if (currentStage != null) {

                    Questions questions;
                    if (controlStage.getRandomSort() == 1) {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    } else {
                        questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                                .eq(Questions::getStageNumber, currentStage.getSortVal())
                        );
                    }
                    if (questions == null) {
                        scoreService.addScore(null, teams, currentStage.getName(), null,
                                null, -Integer.parseInt(text), teams.getTeamScore().toString(), "");
                    } else {
                        questions.setOrder(Integer.parseInt(questionCombox.getValue()) );
                        scoreService.addScore(null, teams, currentStage.getName(), questions.getQuestionNumber().toString(),
                                null, -Integer.parseInt(text), teams.getTeamScore().toString(), "");
                    }
                } else {
                    scoreService.addScore(null, teams, null, null,
                            null, -Integer.parseInt(text), teams.getTeamScore().toString(), "");
                }
            }

            teamScore.setText(teams.getTeamScore().toString());


        }
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
            scoreBoardPreview.reload();
        }

        if (previewPane != null
                && (previewPane instanceof MemberRankPreview || previewPane instanceof TeamRankPreview || previewPane instanceof BuzzRankPreview)) {
            previewPane.reload();
        }

        teamSelector.setSelected(false);
        // 刷新分数
        messageHandler.notifyPage("", null);
    }

    @FXML
    public void showTopStage() {
        scrollPane.setVvalue(0);
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
        if (currentStage == null || currentStage.getQuestionType() == null) {

        } else {
            if (startAnswerBtn.getText().equals("开始答题")) {
                if (currentStage.getQuestionType() == 7) {
                    Model7RuntimeData.executeStart = true;
                    startAnswerBtn.setDisable(false);
                    Model7RuntimeData.reset();
                    // 发送试题列表给前端
                    boolean random = currentStage.getrRandomSort() == 1;
                    List<Questions> questions = questionsMapper.selectList(
                            new LambdaQueryWrapper<Questions>().eq(Questions::getStageNumber, currentStage.getSortVal())
                    );

                    Configs configs = configsMapper.selectById(1);
                    String cfgVal = configs.getConfig();
                    String txbz = "4";
                    if (!StringUtils.isEmpty(cfgVal)) {
                        String[] split = cfgVal.split(ConfigSpliter.SPLITER);
                        txbz = split[10];
                    }

                    for (Questions question : questions) {
                        question.setScore(currentStage.getScorePerQ());
                        question.setDesValue(controlStage.getDesValue());
                        question.setTxbz(txbz);
                        question.setShowNumber(currentStage.getShowNumber());
                    }
                    messageHandler.sendQuestionGroup(
                            questions,
                            random
                    );

                    // sleep 1 second
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    startTiming();
                    timeBoardPreview.hideTime();
                    // 开始启动每秒一次的 UI 更新
                    messageHandler.startPeriodicUIUpdate();
                    // 扩展屏显示题目信息
                } else if (currentStage.getQuestionType() == 6) {
                    startAnswerBtn.setDisable(false);
                    startTiming();
                } else {
                    startAnswerBtn.setDisable(true);
                }
                startAnswerBtn.setText("停止答题");
                lastQBtn.setDisable(true);
                nextQBtn.setDisable(true);
                startAnswerBtn.setTextFill(Color.WHITE);
                startAnswerBtn.setStyle("-fx-background-color: #01a25a");
                timingBtn.setDisable(true);
            } else {
                // 清除旧的竞技模式数据
                model7StatusMapper.delete(new LambdaQueryWrapper<>());
                timeReset();
                startAnswerBtn.setDisable(false);
                if (currentStage.getQuestionType() == 7) {
                    Model7RuntimeData.forceFinish();
                    previewPane.reload();
                    messageHandler.model7Finish();
                    updateScoreBoard();
                } else if (currentStage.getQuestionType() == 6) {
                    if (currentStage.getM6() == 1) {
                        Model61RuntimeData.forceFinish();
                        //  showAnswer();
                    } else {
                        Model60RuntimeData.forceFinish();
                    }
                }
                startAnswerBtn.setText("开始答题");
                lastQBtn.setDisable(false);
                nextQBtn.setDisable(false);
                startAnswerBtn.setTextFill(Color.BLACK);
                startAnswerBtn.setStyle("");
            }

        }
    }

    // 由于时间结束导致答题结束
    public void stopOnTime() {
        timeReset();
        if (currentStage == null) {
            return;
        }
        if (currentStage.getQuestionType() == 7) {
            // 自动计时清除已回答的缓存
            model7StatusMapper.delete(new LambdaQueryWrapper<>());
            messageHandler.model7Finish();
            Model7RuntimeData.forceFinish();
            previewPane.reload();
        } else if (currentStage.getQuestionType() == 6) {
            if (currentStage.getM6() == 1) {
                Model61RuntimeData.forceFinish();
                //showAnswer();
            } else {
                Model60RuntimeData.forceFinish();
            }
        }
        startAnswerBtn.setText("开始答题");
        startAnswerBtn.setTextFill(Color.BLACK);
        startAnswerBtn.setStyle("");
        lastQBtn.setDisable(false);
        nextQBtn.setDisable(false);
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
    public void startTiming(){
        buzzReset();
        parentPane.requestFocus();
        String text = timeSecondText.getText();
        if (!StringUtils.isEmpty(text)) {
            time = Integer.parseInt(text);
        }
        if (timeBoardPreview != null) {
            timeReset();
            timeBoardPreview = null;
        }
        if (timeBoardPreview == null) {
            timeBoardPreview = (ReloadPane) Navigator.timeWithPos("/view/board/preview/TimeBoard.fxml", "", "/view/style.css", true);
        }

        if (timeBoardPreview != null) {
            if (controlStage == null) {
                // 未进入环节的情况下默认倒计时
                timeBoardPreview.startTiming(false, time);
            } else {
                if (currentStage != null) {
                    if (currentStage.getQuestionType() == 7) {
                        // 竞技答题强制正计时
                        timeBoardPreview.startTiming(true, time);
                    } else {
                        timeBoardPreview.startTiming(currentStage.getOrderVal() == 1, time);
                    }
                } else {
                    timeBoardPreview.startTiming(false, time);
                }
            }
            timingBtn.setDisable(true);
            startAnswerBtn.setDisable(true);
        }
    }

    public void startBuzz(int passTime, boolean buzzBefore, int buzzTime){
        parentPane.requestFocus();
        buzzTimeBoardPreview = (ReloadPane) Navigator.buzzTimeWithPos("/view/board/preview/BuzzTimeBoard.fxml", "", "/view/style.css", true);
        if (buzzBefore) {
            if (buzzTime > 0) {
                buzzTimeBoardPreview.startBuzz(buzzTime, passTime);
            } else {
                buzzTimeBoardPreview.buzzTiming(passTime);
            }

        }
    }





    @FXML
    public void showResource() {
        String tihao = questionCombox.getValue();

        Questions questions;
        if (controlStage.getRandomSort() == 1) {
            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getViewSort, Integer.parseInt(tihao))
                    .eq(Questions::getStageNumber, currentStage.getSortVal())
            );
        } else {
            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getQuestionNumber, Integer.parseInt(tihao))
                    .eq(Questions::getStageNumber, currentStage.getSortVal())
            );
        }
        questions.setOrder(Integer.parseInt(tihao) );
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

        buzzReset();

        if (previewPane instanceof StageRulePreviewPreview) {
            showStageRule();
            return;
        }

        // 环节规则页无法返回，只能通过点击环节规则返回到前一个页面
        if (previewPane instanceof StageRulePreviewPreview) {
            return;
        }


        if (previewPane instanceof ShowAnswerPreview) {
            GlobalCache.showAnswer = false;
            returnLast();
            return;
        }

        PreviewPane last = PreviewStack.getLast();

        if (last instanceof QuestionPreviewPreview) {
            // 答题 返回 选题界面
            GlobalCache.showAnswer = false;
            QuestionPreviewPreview x = (QuestionPreviewPreview) previewPane;
            Questions q = x.getData();
            Stages stages = stagesMapper.selectById(currentStage.getId());
            // 返回关闭计时
            if (timeBoardPreview != null) {
                timeReset();
            }
            // 判断当前环节的出题类型
            if (stages.getQuestionType() == 2) {
                // 自选题目
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, stages);
            } else if (stages.getQuestionType() == 4) {
                // 滚动选题
                previewPane = Navigator.preview("/view/preview/RollPreview.fxml", "", "/view/style.css", true, stages);
            } else if (stages.getQuestionType() == 3) {
                // 风险题
                List<Stages> queryList = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                        .eq(Stages::getParentId, controlStage.getId())
                );
                if (queryList.isEmpty()) {
                    previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, controlStage.getParentId());
                } else {
                    previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, controlStage.getId());
                }
            } else if (stages.getQuestionType() == 6) {
                // 答题模式
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, stages);
            } else if (stages.getQuestionType() == 1) {
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, stages.getId());
            }

        } else if (last instanceof Model7Preview) {
            GlobalCache.showAnswer = false;
            Model7Preview model7Preview = (Model7Preview) last;
            // 结束当前答题状态,如果在答题中的话
            if (!startAnswerBtn.getText().equals("开始答题")) {
                startAnswerQuestion();
            }
            Stages stages = stagesMapper.selectById(model7Preview.getData().getId());
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, stages.getId());
        } else if (last instanceof Model3Preview) {
            GlobalCache.showAnswer = false;
            // 风险题返回名称
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, previewPane.getData());
        } else if (last instanceof CluePreviewPreview) {
            GlobalCache.showAnswer = false;
            // 答题 返回 选题界面
            CluePreviewPreview x = (CluePreviewPreview) previewPane;
            Questions q = x.getData();
            Stages stages = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getSortVal, q.getStageNumber())
            );
            // 默认为 5 
            previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, stages);

        } else if (last instanceof StageQuestionPreviewPreview) {
            GlobalCache.showAnswer = false;
            // 选题界面 返回 环节名称
            StageQuestionPreviewPreview x = (StageQuestionPreviewPreview) previewPane;
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, x.getData().getId());
        } else if (last instanceof RollPreviewPreview) {
            GlobalCache.showAnswer = false;
            // 选题界面 返回 环节名称
            RollPreviewPreview x = (RollPreviewPreview) previewPane;
            x.stopSelector();
            previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, x.getData().getId());
        } else if (last instanceof StageNamePreviewPreview) {
            GlobalCache.showAnswer = false;
            if (previewPane instanceof StagePreviewPreview) {
                return;
            }
            // 环节名称 返回 环节列表
            StageNamePreviewPreview x = (StageNamePreviewPreview) previewPane;
            Integer stageId = x.getData();
            Stages stages = stagesMapper.selectById(stageId);
            if (stages.getParentId() == null) {
                currentStage = null;
            } else {
                // do nothing
            }
            previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, null);
        } else if (last instanceof StagePreviewPreview) {
            GlobalCache.showAnswer = false;
            // 环节列表 返回 环节名称
            StagePreviewPreview x = (StagePreviewPreview) previewPane;
            if (x.getData() == null) {
                // 已经到了最上一层
                currentStage = null;
            } else {
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, x.getData());
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
        if (scoreBoardPreview != null) {
            scoreBoardPreview.selectAll(teamSelector.isSelected());
        }
    }


    // 竞技排行
    @FXML
    public void model7Rank() {
        if (controlStage == null) {
            return;
        }

        if (currentStage == null) {
            return;
        }
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

    @FXML
    public void buzzRank(){
        if (!GlobalCache.showBuzzRank) {
            previewPane = Navigator.preview("/view/preview/BuzzRank.fxml", "", "/view/style.css", true, true);
            GlobalCache.showBuzzRank = true;
        } else {
            returnLast();
            GlobalCache.showBuzzRank = false;
        }
    }


    // 由于时间到了导致没有抢答完成的分情况
    public void resetBuzzTime(){
        Buzz buzz = buzzMapper.selectById(1);

        String mode = buzz.getMode();
        if (currentStage != null && !StringUtils.isEmpty(currentStage.getpAType())) {
            mode = currentStage.getpAType();
        }
        if (mode.equals("淘汰模式")) {
            Integer lastSuccess = buzz.getLastSuccess();
            LinkedList<String> linkedList = new LinkedList<>(RuntimeUtil.buzzTimeMap.keySet());
            List<Teams> teams = new ArrayList<>();

            // 记录已回答的人，这里都是回答犯规的
            for (String s : linkedList) {
                for (Teams team : TeamRuntime.getTeams()) {
                    if (s.equals(team.getShortName())) {
                        teams.add(team);
                    }
                }
            }

            // 无人抢答
            if (teams.isEmpty()) {
                stopBuzz(null, new ArrayList<>(), false, false, false);
                MusicPlayer.playWithCallback(MusicResource.BUZZ_STOP, () -> {});
                return;
            }

            // 未配置最后一条记录为抢答成功
            if (lastSuccess == null || lastSuccess.equals(0)) {
                stopBuzz(null, teams, false, true, false);
            } else {
                Teams lastTeam = teams.remove(teams.size() - 1);
                // 配置最后一名抢答犯规为成功
                stopBuzz(lastTeam, teams, true, false, false);
            }
        } else {
            stopBuzz(null, new ArrayList<>(), false, false, false);
            MusicPlayer.playWithCallback(MusicResource.BUZZ_STOP, () -> {});
        }

    }

    public void resetTimeBtn() {
        timingBtn.setDisable(false);
    }


    // 计时复位
    @FXML
    public void timeReset() {
        parentPane.requestFocus();
        timingBtn.setDisable(false);
        // 时间隐藏
        messageHandler.sendQuestionTime("-1", "false");
        if (timeBoardPreview != null) {
            timeBoardPreview.close();
        }
        timeSecondText.setText(time + "");
        startAnswerBtn.setDisable(false);
    }


    private void showStageName() {
        stageList.setVisible(false);
        stageName.setVisible(true);
        stageChildList.setVisible(false);
    }


    private void showChildList() {
        scrollPane.setVvalue(0);
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
        int desValue = 0;
        if (currentStage.getDesValue() != null) {
            desValue = currentStage.getDesValue();
        }
        scoreDescInput.setText(desValue + "");
    }

    public void selectQuestion(String value) {
        questionCombox.setValue(value);
        controlResourceBtn(value);
    }

    private void controlResourceBtn(String value) {
        Questions questions ;
        if (currentStage.getRandomSort() == 1) {
            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getStageNumber, currentStage.getSortVal())
                    .eq(Questions::getViewSort, Integer.parseInt(value))
            );
        } else {
            questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getStageNumber, currentStage.getSortVal())
                    .eq(Questions::getQuestionNumber, Integer.parseInt(value))
            );
        }

        if (!StringUtils.isEmpty(questions.getResource())) {
            showResourceBtn.setDisable(false);
        } else {
            showResourceBtn.setDisable(true);
        }
    }

    private void controlResourceBtn(Questions questions) {
        if (!StringUtils.isEmpty(questions.getResource())) {
            showResourceBtn.setDisable(false);
        } else {
            showResourceBtn.setDisable(true);
        }
    }

    public void showQuestions() {
        // 显示题目
        List<Questions> questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>().
                eq(Questions::getStageNumber, controlStage.getSortVal())
        );
        if (controlStage.getRandomSort() == 1) {
            questionList.sort(Comparator.comparing(Questions::getViewSort));
            final List<String> questionKeys = questionList.stream().map(x -> x.getViewSort().toString()).collect(Collectors.toList());
            questionCombox.setItems(FXCollections.observableList(questionKeys));
        } else {
            questionList.sort(Comparator.comparing(Questions::getQuestionNumber));
            final List<String> questionKeys = questionList.stream().map(x -> x.getQuestionNumber().toString()).collect(Collectors.toList());
            questionCombox.setItems(FXCollections.observableList(questionKeys));
        }

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
            secondScreenBtn.setText("扩展  关");
            secondScreenBtn.setTextFill(Color.BLACK);
            secondScreenBtn.setStyle("");
        } else {
            secondScreenBtn.setText("扩展  开");
            secondScreenBtn.setTextFill(Color.WHITE);
            secondScreenBtn.setStyle("-fx-background-color: #01a25a");
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
                Stages data;
                if (controlStage.getQuestionType() == 3) {
                    List<Stages> childrenStage = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, controlStage.getId())
                    );

                    if (childrenStage.isEmpty()) {
                        Stages stages = stagesMapper.selectById(controlStage.getParentId());
                        data = stages;
                    } else {
                        data = controlStage;
                        currentStage = controlStage;
                    }
                } else {
                    data = controlStage;
                    currentStage = controlStage;
                }
                previewPane = Navigator.preview("/view/preview/StageRulePreview.fxml", "", "/view/style.css", true, data);
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
            previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, null);
            return;
        }
        PreviewPane last = PreviewStack.getLast();
        if (last != null) {
            if (last instanceof QuestionPreviewPreview) {
                QuestionPreviewPreview x = (QuestionPreviewPreview) last;
                Questions q = x.getData();
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, q);
            } else if (last instanceof StageQuestionPreviewPreview) {
                previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, last.getData());
            } else if (last instanceof RollPreviewPreview) {
                previewPane = Navigator.preview("/view/preview/RollPreview.fxml", "", "/view/style.css", true, last.getData());
            } else if (last instanceof StageNamePreviewPreview) {
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, last.getData());
            } else if (last instanceof StagePreviewPreview) {
                previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, last.getData());
            } else if (last instanceof Model7Preview) {
                previewPane = Navigator.preview("/view/preview/Model7Preview.fxml", "", "/view/style.css", true,  last.getData());
            } else if (last instanceof Model3Preview) {
                List<Stages> childrenStages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                        .eq(Stages::getParentId, controlStage.getId())
                );
                Integer data;
                if (childrenStages.isEmpty()) {
                    data = (controlStage.getParentId());
                } else {
                    data = (controlStage.getId());
                }
                previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, data);
            } else if (last instanceof Model7RankPreview) {
                returnLast();
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
        // 强制关闭抢答结果框
        buzzReset();
        // 强制性关闭答案显示
        GlobalCache.showAnswer = false;
        if (previewPane instanceof RollPreviewPreview) {
            // 选题界面 返回 环节名称
            RollPreviewPreview x = (RollPreviewPreview) previewPane;
            x.stopSelector();
        }
        // 外屏和内屏进入对应的环节
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        timeReset();
        if (controlStage != null) {
            controlStage = stagesMapper.selectById(controlStage.getId());
            boolean last = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getParentId, controlStage.getId())
            ).isEmpty();

            if (controlStage.getQuestionType() == 3) {
                // 风险题 优先级高
                if (last) {
                    List<Questions> parse;
                    if (controlStage.getRandomSort() == 1) {
                        parse = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, controlStage.getSortVal())
                                .orderByAsc(Questions::getViewSort)
                        );
                        for (Questions q : parse) {
                            q.setOrder(q.getViewSort());
                        }


                    } else {
                        parse = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, controlStage.getSortVal())
                                .orderByAsc(Questions::getQuestionNumber)
                        );

                        for (Questions q : parse) {
                            q.setOrder(q.getQuestionNumber());
                        }
                    }
                    if (!CollectionUtils.isEmpty(parse)) {
                        for (int i = 0; i < parse.size(); i++) {
                            parse.get(i).setDesValue(controlStage.getDesValue());
                        }
                    }
                    controlStage = stagesMapper.selectById(controlStage.getId());

                    showQuestions();
                    // 风险题目是最后一级，则只是展示对应的题号，不做任何界面跳转
                    Stages stages = stagesMapper.selectById(controlStage.getParentId());
                    currentStage = stages;
                    setPreviewPane(
                            Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, stages.getId())
                    );
                } else {
                    currentStage = controlStage;
                    setPreviewPane(
                            Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, controlStage.getId())
                    );

                }
                scoreInput.setText(controlStage.getScorePerQ() + "");
                int desValue = 0;
                if (controlStage.getDesValue() != null) {
                    desValue = controlStage.getDesValue();
                }
                scoreDescInput.setText(desValue + "");
                timeSecondText.setText(controlStage.getDescTime() + "");
                updateScoreBoard();
                return;

            }
            // 底层环节，展示题目
            if (last) {
                List<Questions> parse;

                if (controlStage.getRandomSort() == 1) {
                    parse = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, controlStage.getSortVal())
                            .orderByAsc(Questions::getViewSort)
                    );
                    for (Questions q : parse) {
                        q.setOrder(q.getViewSort());
                    }
                } else {
                    parse = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, controlStage.getSortVal())
                            .orderByAsc(Questions::getQuestionNumber)
                    );
                    for (Questions q : parse) {
                        q.setOrder(q.getQuestionNumber());
                    }
                }
                if (!CollectionUtils.isEmpty(parse)) {
                    for (int i = 0; i < parse.size(); i++) {
                        parse.get(i).setDesValue(controlStage.getDesValue());
                    }
                }
                currentStage = controlStage;
                controlStage = stagesMapper.selectById(controlStage.getId());
                showQuestions();
                if (controlStage.getQuestionType() == 1) {
                    // 连续答题
                    List<Questions> collect;
                    if (controlStage.getRandomSort() == 1) {
                         collect = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, controlStage.getSortVal())
                                .eq(Questions::isHasAnswer, false)
                                 .orderByAsc(Questions::getViewSort)
                        );

                        for (Questions questions : collect) {
                            questions.setOrder(questions.getViewSort());
                        }
                    } else {
                        collect = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, controlStage.getSortVal())
                                .eq(Questions::isHasAnswer, false)
                                .orderByAsc(Questions::getQuestionNumber)
                        );
                        for (Questions questions : collect) {
                            questions.setOrder(questions.getQuestionNumber());
                        }
                    }


                    if (!collect.isEmpty()) {
                        questionCombox.setValue(collect.get(0).getOrder().toString());
                        // CurrentQuestions.recordAnswer(collect.get(0).getQuestionNumber().toString());
                        collect.get(0).setHasAnswer(true);
                        questionsMapper.updateById(collect.get(0));
                    }
                    previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, collect.get(0));
                } else if (controlStage.getQuestionType() == 2) {
                    // 自选题目
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, controlStage);
                } else if (controlStage.getQuestionType() == 3) {
                    // 风险题
                } else if (controlStage.getQuestionType() == 4) {
                    // 滚动选题
                    previewPane = Navigator.preview("/view/preview/RollPreview.fxml", "", "/view/style.css", true, controlStage);
                } else if (controlStage.getQuestionType() == 5) {
                    // 线索题
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, controlStage);
                } else if (controlStage.getQuestionType() == 6) {
                    // 答题模式

                    previewPane =  Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, controlStage);
                } else if (controlStage.getQuestionType() == 7) {
                    // 竞技模式
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, controlStage);

                }
                // previewPane.reload();
            } else {
                currentStage = controlStage;
                // 展示环节名称
                previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, controlStage.getId());
                previewPane.reload();
            }
            scoreInput.setText(controlStage.getScorePerQ() + "");
            int desValue = 0;
            if (controlStage.getDesValue() != null) {
                desValue = controlStage.getDesValue();
            }
            scoreDescInput.setText(desValue + "");
            timeSecondText.setText(controlStage.getDescTime() + "");
            updateScoreBoard();
        }
    }


    // 显示题目
    @FXML
    public void showQuestion() {
        if (StringUtils.isEmpty(questionCombox.getValue())){
            Information.error("请选择题号");
            return;
        }
        GlobalCache.showModel7Rank = false;
        parentPane.requestFocus();
        // 重置答题状态
        // 给题目设置分值
        // 扩展屏幕展示并排答题界面（竞技模式）
        MusicPlayer.play(MusicResource.SHOW_QUESTION);
        if (currentStage.getQuestionType() == (7)) {
            Model7RuntimeData.setTeams(TeamRuntime.getTeams());
            previewPane = Navigator.preview("/view/preview/Model7Preview.fxml", "", "/view/style.css", true, currentStage);
            updateScoreBoard();
        } else if (currentStage.getQuestionType() == (6)) {
            Integer m6 = currentStage.getM6();
            String tihao = questionCombox.getValue();
            Questions questions;
            boolean showAnswer = GlobalCache.showAnswer;
            if (controlStage.getRandomSort() == 1) {
                questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                        .eq(Questions::getStageNumber, currentStage.getSortVal())
                        .eq(Questions::getViewSort, Integer.parseInt(tihao))
                );

                questions.setOrder(questions.getViewSort());
            } else {
                questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                        .eq(Questions::getStageNumber, currentStage.getSortVal())
                        .eq(Questions::getQuestionNumber, Integer.parseInt(tihao))
                );
                questions.setOrder(questions.getQuestionNumber());
            }

            questions.setScore(currentStage.getScorePerQ());
//                    CurrentQuestions.getQ(tihao);
            if (questions == null) {
                Information.error("请选择题号");
                return;
            }
            controlResourceBtn(tihao);
            if (m6 == 0) {
                questions.setHasAnswer(true);

                questionsMapper.updateById(questions);
                //CurrentQuestions.recordAnswer(tihao);
                // 设置当前需要显示的题目
                // 设置选手信息
                Model60RuntimeData.setTeams(TeamRuntime.getTeams());
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, questions);
                // messageHandler.sendQuestion(questions);
                Model60RuntimeData.setRuntimeData(questions);
                updateScoreBoard();
            } else {
                // 答题模式 -- 个人模式
                questions.setHasAnswer(true);

                questionsMapper.updateById(questions);
                // 设置当前需要显示的题目
                // 设置选手信息
                Model61RuntimeData.setTeams(TeamRuntime.getTeams());
                previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, questions);
                //messageHandler.sendQuestion(questions);
                Model61RuntimeData.setRuntimeData(questions);
                updateScoreBoard();
            }
        } else {
            if (currentStage.getQuestionType() == 3) {

                Questions questions;
                if (controlStage.getRandomSort() == 1) {
                    questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, controlStage.getSortVal())
                            .eq(Questions::getViewSort, Integer.parseInt(questionCombox.getValue()))
                    );
                    questions.setOrder(questions.getViewSort());
                } else {
                    questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, controlStage.getSortVal())
                            .eq(Questions::getQuestionNumber, Integer.parseInt(questionCombox.getValue()))
                    );
                    questions.setOrder(questions.getQuestionNumber());
                }

                if (questions == null) {
                    Information.error("请选择题号!!");
                    return;
                }
                controlResourceBtn(questions);
                questions.setScore(controlStage.getScorePerQ());
                questions.setHasAnswer(true);
                questionsMapper.updateById(questions);
                GlobalCache.showAnswer = false;
                if (previewPane instanceof QuestionPreviewPreview) {
                    previewPane.setData(questions);
                    previewPane.reload();
                } else {
                    previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, questions);
                    GlobalCache.showAnswer = false;
                    previewPane.reload();
                }

            } else {
                String tihao = questionCombox.getValue();
                Questions questions;
                if (controlStage.getRandomSort() == 1) {
                    questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, currentStage.getSortVal())
                            .eq(Questions::getViewSort, Integer.parseInt(tihao)));
                    questions.setOrder(questions.getViewSort());
                } else {
                    questions = questionsMapper.selectOne(new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, currentStage.getSortVal())
                            .eq(Questions::getQuestionNumber, Integer.parseInt(tihao))
                            );
                    questions.setOrder(questions.getQuestionNumber());
                }
                if (questions == null) {
                    Information.error("请选择题号!!");
                    return;
                }
                controlResourceBtn(tihao);
                //CurrentQuestions.recordAnswer(tihao);
                questions.setHasAnswer(true);
                questions.setScore(currentStage.getScorePerQ());
                questions.setShowNumber(currentStage.getShowNumber());
                questionsMapper.updateById(questions);
                // 内屏更新题目
                if (currentStage.getQuestionType() == 2 || currentStage.getQuestionType() == 1 || currentStage.getQuestionType() == 4) {
                    if (previewPane instanceof QuestionPreviewPreview) {
                        previewPane.setData(questions);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    } else {
                        previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, questions);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    }
                } else if (currentStage.getQuestionType() == 5) {
                    if (previewPane instanceof CluePreviewPreview) {
                        previewPane.setData(questions);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    } else {
                        previewPane = Navigator.preview("/view/preview/CluePreview.fxml", "", "/view/style.css", true, questions);
                        GlobalCache.showAnswer = false;
                        previewPane.reload();
                    }
                }
            }
        }
        parentPane.requestFocus();

    }


    // 答案显示
    @FXML
    public void showAnswer() {
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        parentPane.requestFocus();
        if (controlStage == null) {
            return;
        }

        // 关闭抢答结果的弹窗
        closeBuzzResultWindow();

        MusicPlayer.play(MusicResource.SHOW_ANSWER);
        // 首先强行关闭
        if (!startAnswerBtn.getText().equals("开始答题")) {
            startAnswerQuestion();
        }
        // 更新分数
        Integer questionType = controlStage.getQuestionType();
        if (questionType == 6 && controlStage.getM6() == 1) {
            TeamRuntime.updateScore();

            if (!GlobalCache.showAnswer) {
                // 更新状态
                TeamRuntime.updateMS();
                //TeamRuntime.resetStatus();
                GlobalCache.showAnswer = true;
                MusicPlayer.play(MusicResource.SHOW_ANSWER);
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
                        item.setScore(-currentStage.getDesValue());
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
        } else {
            if (questionType == 6 && controlStage.getM6() == 0) {
                TeamRuntime.updateScore();
                TeamRuntime.updateS();

                GlobalCache.showAnswer = !GlobalCache.showAnswer;
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
                if (currentStage.getAnswerSingle() == 1) {
                    // 单独展示
                    if (GlobalCache.showAnswer) {
                        // 更新状态
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
                                item.setScore(-currentStage.getDesValue());
                            }
                            item.setTeamName(runtimeData.get(i).getTeamName());
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
//              
            } else {
                TeamRuntime.updateS();
                GlobalCache.showAnswer = !GlobalCache.showAnswer;
                if (GlobalCache.showAnswer) {
                    lastQBtn.setDisable(false);
                    nextQBtn.setDisable(false);
                }
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

            timingBtn.setDisable(false);
            timeReset();
        }

        if (scoreBoardPreview != null) {
            scoreBoardPreview.reload();
        }

    }


    private void closeBuzzResultWindow(){
        Navigator.buzzBoardStage.close();
        ExtendedScreen.buzzBoardStage.close();
    }

    // 上一题
    @FXML
    public void lastQuestion() {
        if (!(previewPane instanceof QuestionPreviewPreview || previewPane instanceof CluePreviewPreview)) {
            return;
        }
        int selectedIndex = questionCombox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == 0) {
            return;
        }
        RuntimeUtil.showBuzzBar = false;
        // 关闭抢答结果的弹窗
        buzzReset();
        closeBuzzResultWindow();
        messageHandler.notifyPage("", null);
        reloadScoreBoard();
        GlobalCache.showAnswer = false;
        questionCombox.getSelectionModel().select(selectedIndex - 1);
        TeamRuntime.resetStatus();
        if (timeVisibleBtn.getText().equals("消失")) {
            timeReset();
        }
        TeamRuntime.resetScore();
        showQuestion();
    }

    // 下一题
    @FXML
    public void nextQuestion() {
        parentPane.requestFocus();
        if (!(previewPane instanceof QuestionPreviewPreview
                || previewPane instanceof CluePreviewPreview
                || previewPane instanceof ShowAnswerPreview
        )) {
            return;
        }
        try {
            int selectedIndex = questionCombox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == questionCombox.getItems().size() - 1) {
                Information.info("已经是最后一题");
                return;
            }
            RuntimeUtil.showBuzzBar = false;
            buzzReset();
            closeBuzzResultWindow();
            messageHandler.notifyPage("", null);
            reloadScoreBoard();
            GlobalCache.showAnswer = false;
            questionCombox.getSelectionModel().select(selectedIndex + 1);
            TeamRuntime.resetStatus();
            if (timeVisibleBtn.getText().equals("消失")) {
                timeReset();
            }
            TeamRuntime.resetScore();
            showQuestion();
        } finally {
            parentPane.requestFocus();
        }

    }


    @FXML
    public void toBackend() {
        //Navigator.openBackend("/view/Backend.fxml", "后台设置", "/view/style.css", false, false);
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
        } else {
            PreviewPane last = PreviewStack.getLast();
            if (last != null) {
                if (last instanceof QuestionPreviewPreview) {
                    QuestionPreviewPreview x = (QuestionPreviewPreview) last;
                    Questions q = x.getData();
                    previewPane = Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, q);
                } else if (last instanceof StageQuestionPreviewPreview) {
                    previewPane = Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, last.getData());
                } else if (last instanceof StageNamePreviewPreview) {
                    StageNamePreviewPreview x = (StageNamePreviewPreview) last;
                    previewPane = Navigator.preview("/view/preview/StageNamePreview.fxml", "", "/view/style.css", true, last.getData());
                } else if (last instanceof StagePreviewPreview) {
                    previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, last.getData());
                } else if (last instanceof Model3Preview) {
                    previewPane = Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, last.getData());
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
        secondScreenBtn.setText("扩展  关");
        secondScreenBtn.setTextFill(Color.BLACK);
        secondScreenBtn.setStyle("");
        messageHandler.notifyPage("wait", null);
    }


    @FXML
    public void miniApp() {
        // 最小化
        JavaFxApplication.stage.setIconified(true);
    }

    @FXML
    public void exitApp() {
        // 删除竞技模式缓存
        model7StatusMapper.delete(new LambdaQueryWrapper<>());
        messageHandler.notifyPage("contestant", null);
        System.exit(0);
    }


    public void showScoreBoard() {
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();

        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        // 排序队伍，按照编码排序
        teams.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));

        for (Teams team : teams) {
            team.setQScores(new ArrayList<>());
            team.setStatusList(new ArrayList<>());
            team.setStatus(0);
            if (!StringUtils.isEmpty(team.getMembers())) {
                String[] split = team.getMembers().split(",");
                for (String s : split) {
                    team.getStatusList().add(0);
                    team.getQScores().add(0);
                }
            }
        }
        // 若已有分数，则加载旧的分数
        TeamRuntime.setTeams(teams);
        Model7RuntimeData.setTeams(TeamRuntime.getTeams());
        Model60RuntimeData.setTeams(TeamRuntime.getTeams());
        Model61RuntimeData.setTeams(TeamRuntime.getTeams());
        teamComBox.setItems(FXCollections.observableList(teams.stream().map(x -> x.getShortName()).collect(Collectors.toList())));
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
                scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardH.fxml", "", "/view/style.css", true);
//                if (scoreBoardPreview != null) {
//                    scoreBoardPreview.reload();
//                } else {
//                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardH.fxml", "", "/view/style.css", true);
//                }
            } else if (boardLayout.equals("2")) {
                scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardV.fxml", "", "/view/style.css", true);

//                if (scoreBoardPreview != null) {
//                    scoreBoardPreview.reload();
//                } else {
//                    scoreBoardPreview = (BoardPane) Navigator.scoreWithPos("/view/board/preview/ScoreBoardV.fxml", "", "/view/style.css", true);
//                }
            }
        }
    }


    @FXML
    public void enterMatch() {
        // 强制关闭抢答框
        buzzReset();
        // 内屏展示
        GlobalCache.showAnswer = false;
        showStage();
        stageText.setVisible(true);
        showScoreBoard();
        previewPane = Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", false, null);
        previewPane.shows();

    }

    public ReloadPane getTimeBoardPreview() {
        return timeBoardPreview;
    }

    public ReloadPane getBuzzTimeBoardPreview() {
        return buzzTimeBoardPreview;
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


    public void setBuzzTimeBoard(ReloadPane buzzTimeBoard) {
        this.buzzTimeBoard = buzzTimeBoard;
    }

    public void setBuzzTimeBoardPreview(ReloadPane buzzTimeBoardPreview) {
        this.buzzTimeBoardPreview = buzzTimeBoardPreview;
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

    public void configTimeAndScore(Stages belongStage, String value) {
        List<Questions> questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                .eq(Questions::getStageNumber, belongStage.getSortVal())
        );

        if (controlStage.getRandomSort() == 1) {
            questionList.sort(Comparator.comparing(Questions::getViewSort));
            final List<String> questionKeys = questionList.stream().map(x -> x.getViewSort().toString()).collect(Collectors.toList());
            questionCombox.setItems(FXCollections.observableList(questionKeys));
        } else {
            questionList.sort(Comparator.comparing(Questions::getQuestionNumber));
            final List<String> questionKeys = questionList.stream().map(x -> x.getQuestionNumber().toString()).collect(Collectors.toList());
            questionCombox.setItems(FXCollections.observableList(questionKeys));
        }
        questionCombox.setValue(value);
        timeSecondText.setText(belongStage.getDescTime().toString());
        scoreInput.setText(belongStage.getScorePerQ().toString());
        int desValue = 0;
        if (belongStage.getDesValue() != null) {
            desValue = belongStage.getDesValue();
        }
        scoreDescInput.setText(desValue + "");
        controlStage = belongStage;
    }


    public void notifyBuzzByHardware(String teamAddress){
        BuzzDevice buzzDevice = SerialPortConfig.getBuzzDevice(teamAddress);
        Teams teams = TeamRuntime.getByCode(teamAddress);
        if (buzzDevice != null) {
            notifyBuzz(teams.getShortName(), "");
        }
    }


    // 通知抢答结果
    public void notifyBuzz(String team, String member){
        Buzz buzz = buzzMapper.selectById(1);
        // 抢答违规的时间间隔
        long timeGap = (long)((
                3 * Double.parseDouble(buzz.getSpeed()) +
                        MusicPlayer.getAudioDuration(MusicResource.BUZZ_TIME(3)) +
                        MusicPlayer.getAudioDuration(MusicResource.BUZZ_TIME(2)) +
                        MusicPlayer.getAudioDuration(MusicResource.BUZZ_TIME(1)) +
                                MusicPlayer.getAudioDuration(MusicResource.BUZZ_START)
        ) * 1000000000L);
        long slipTime =  System.nanoTime() - (RuntimeUtil.buzzStart);

        Teams teams = TeamRuntime.getTeams().stream().filter(x -> x.getShortName().equals(team)).collect(Collectors.toList()).get(0);
        if (RuntimeUtil.buzzStart > 0) {
            String mode = buzz.getMode();
            if (currentStage != null && !StringUtils.isEmpty(currentStage.getpAType())) {
                mode = currentStage.getpAType();
            }
            if (mode.equals("直接抢答")) {
                timeGap = 0;
            }

            // 判断是否是淘汰模式
            if (mode.equals("淘汰模式")) {
                // 防止重复抢，多次点击只会计算第一次的
                RuntimeUtil.showBuzzBar = true;
                if (RuntimeUtil.buzzTimeMap.containsKey(team)) {
                    return;
                }
                // 提前抢答记录淘汰
                if (slipTime < timeGap || RuntimeUtil.buzzCalculate == 0) {
                    RuntimeUtil.buzzTimeMap.put(team,  -Math.abs(slipTime - timeGap));
                    RuntimeUtil.buzzResult.put(team, false);
                    // 每次抢答都更新状态面板
                    if (RuntimeUtil.buzzTimeMap.size() == TeamRuntime.getTeams().size()) {
                        // 防止其他人抢到
                        RuntimeUtil.buzzStart = 0;
                        boolean lastSuccess = buzz.getLastSuccess() == 1;

                        // 最后一名违规的视为抢答成功是否勾选
                        if (lastSuccess) {
                            // 抢答失败但是由于勾选最后一名为成功，因此直接播放成功音效
                            if (Objects.equals(1, buzz.getSuccessMusic())) {
                                MusicPlayer.playWithCallback(MusicResource.BUZZ_SUCCESS_BEFORE, () -> {
                                    RuntimeUtil.buzzResult.put(team, true);
                                    BuzzResultPreview.setTeams(teams, true);
                                    stopBuzz(teams,
                                            TeamRuntime.getTeams().stream().filter(x -> !x.getShortName().equals(team)).collect(Collectors.toList()),
                                            true, false, false);
                                });
                            } else {
                                RuntimeUtil.buzzResult.put(team, true);
                                BuzzResultPreview.setTeams(teams, true);
                                stopBuzz(teams,
                                        TeamRuntime.getTeams().stream().filter(x -> !x.getShortName().equals(team)).collect(Collectors.toList()),
                                        true, false, false);
                            }


                        } else {
                            // 抢答失败直接播放音效
                            if (Objects.equals(1, buzz.getErrorMusic())) {
                                MusicPlayer.playWithCallback(MusicResource.BUZZ_FAIL_BEFORE, () -> {
                                    RuntimeUtil.buzzResult.put(team, false);
                                    stopBuzz(null, TeamRuntime.getTeams(), false, false, false);
                                });
                            } else {
                                RuntimeUtil.buzzResult.put(team, false);
                                stopBuzz(null, TeamRuntime.getTeams(), false, false, false);
                            }
                        }
                        Platform.runLater(() -> {
                            // 顺便更新抢答计分
                            updateBuzzTimely();
                            Navigator.buzzResult("/view/preview/BuzzResult.fxml", "", "/view/style.css", true);
                        });
                    } else {
                        // 抢答失败直接播放音效
                        if (Objects.equals(1, buzz.getErrorMusic())) {
                            MusicPlayer.playOther(MusicResource.BUZZ_FAIL_BEFORE);
                        }
                        // 抢答失败扣分
                        if (currentStage != null && currentStage.getQdDesc() != null && currentStage.getQdDesc() == 1) {
                            teams.setTeamScore(teams.getTeamScore() - currentStage.getDesValue());
                            teamsMapper.updateById(teams);
                        }
                        for (Teams t : TeamRuntime.getTeams()) {
                            Boolean b = RuntimeUtil.buzzResult.get(t.getShortName());
                            if (b == null) {
                                t.setBuzzStatus(0);
                            }
                            else if (b) {
                                t.setBuzzStatus(1);
                            } else {
                                t.setBuzzStatus(2);
                            }
                            t.setBuzzTime(NumberUtil.fix2Point(RuntimeUtil.buzzTimeMap.getOrDefault(t.getShortName(), 0L) / 1000000000.0));
                        }
                        Platform.runLater(this::reloadScoreBoard);
                        return;
                    }
                } else {
                    teams.setBuzzStatus(1);
                    // 抢答成功，直接结束抢答
                    RuntimeUtil.buzzStart = 0;
                    RuntimeUtil.buzzTimeMap.put(team, System.nanoTime() - RuntimeUtil.buzzCalculate);
                    RuntimeUtil.buzzResult.put(team, true);
                    RuntimeUtil.buzzCalculate = 0;
                    List<Teams> taotaiTeams = TeamRuntime.getTeams().stream().filter(x ->
                                    RuntimeUtil.buzzResult.containsKey(x.getShortName()) && !RuntimeUtil.buzzResult.get(x.getShortName())
                            )
                            .collect(Collectors.toList());
                    // 顺便更新抢答计分
                    updateBuzzTimely();
                    if (Objects.equals(1, buzz.getSuccessMusic())) {
                        MusicPlayer.playWithCallback(MusicResource.BUZZ_SUCCESS_BEFORE, () -> {
                            stopBuzz(teams, taotaiTeams, true, false, false);
                            BuzzResultPreview.setTeams(teams, true);
                            Platform.runLater(() -> {
                                Navigator.buzzResult("/view/preview/BuzzResult.fxml", "", "/view/style.css", true);
                            });
                        });
                    } else {
                        stopBuzz(teams, taotaiTeams, true, false, false);
                        BuzzResultPreview.setTeams(teams, true);
                        Platform.runLater(() -> {
                            Navigator.buzzResult("/view/preview/BuzzResult.fxml", "", "/view/style.css", true);
                        });
                    }
                }
            }
            // 提前抢答
            else if (slipTime < timeGap || RuntimeUtil.buzzCalculate == 0) {
                // 防止其他人抢到
                RuntimeUtil.buzzStart = 0;
                RuntimeUtil.buzzResult.put(team, false);
                RuntimeUtil.buzzTimeMap.put(team, -Math.abs(slipTime - timeGap));
                if (currentStage != null && currentStage.getQdDesc() != null && currentStage.getQdDesc() == 1) {
                    teams.setTeamScore(teams.getTeamScore() - currentStage.getDesValue());
                    teamsMapper.updateById(teams);
                }
                updateBuzzTimely();
                stopBuzz(null, Collections.singletonList(teams), false, true, true);
                Platform.runLater(() -> {
                    BuzzResultPreview.setTeams(teams, false);
                    Navigator.buzzResult("/view/preview/BuzzResult.fxml", "", "/view/style.css", true);
                });
            } else
            {
                // 防止其他人抢到
                RuntimeUtil.buzzStart = 0;
                // 抢答成功
                RuntimeUtil.buzzTimeMap.put(team, System.nanoTime() - RuntimeUtil.buzzCalculate);
                RuntimeUtil.buzzResult.put(team, true);
                RuntimeUtil.buzzCalculate = 0;
                stopBuzz(teams, new ArrayList<>(), true, false, true);
                updateBuzzTimely();
                Platform.runLater(() -> {
                    BuzzResultPreview.setTeams(teams, true);
                    Navigator.buzzResult("/view/preview/BuzzResult.fxml", "", "/view/style.css", true);
                });

            }
        }

        for (Teams t : TeamRuntime.getTeams()) {
            Boolean b = RuntimeUtil.buzzResult.get(t.getShortName());
            if (b == null) {
                t.setBuzzStatus(0);
            }
            else
            if (b) {
                t.setBuzzStatus(1);
            } else {
                t.setBuzzStatus(2);
            }
            t.setBuzzTime(NumberUtil.fix2Point(RuntimeUtil.buzzTimeMap.getOrDefault(t.getShortName(), 0L) / 1000000000.0));
        }
        Platform.runLater(() -> {
            reloadScoreBoard();
        });

    }

    public void updateBuzzTimely(){
        Platform.runLater(() -> {
            if (previewPane != null && previewPane instanceof BuzzRankPreview) {
                BuzzRankPreview brp = (BuzzRankPreview) previewPane;
                brp.reload();
            }
        });
    }

    public void reloadScoreBoard(){
        if (scoreBoardPreview != null) {
            scoreBoardPreview.reload();
        }
        updateBuzzTimely();
        messageHandler.notifyPage("", null);
    }

    // 停止抢答
    private void stopBuzz(Teams successTeam, List<Teams> failTeams, boolean success, boolean fail, boolean standard){
        TerminalConfig.sendStopBuzz();
        Buzz buzz = buzzMapper.selectById(1);
        Platform.runLater(() -> {
            RuntimeUtil.showBuzzBar = true;
            buzzBtn.setText("开始抢答");
            buzzBtn.setTextFill(Color.BLACK);
            buzzBtn.setStyle("");
            buzzTimeBoardPreview.close();
            reloadScoreBoard();

            if (success) {
                // 闪烁计分屏
                lightScreen(Objects.equals(1, buzz.getLight()), successTeam);
                if (buzz.getSuccessMusic() == 1) {
                    if (standard) {
                        // 标准模式下判定是否需要提前响应音乐
                        MusicPlayer.playWithCallback(MusicResource.BUZZ_SUCCESS_BEFORE, () -> {
                            MusicPlayer.play(MusicResource.BUZZ_SUCCESS(successTeam.getCode()));
                        });
                    } else {
                        MusicPlayer.playWithCallback(MusicResource.BUZZ_SUCCESS(successTeam.getCode()), () -> {
                            // 播放违规
                            if (buzz.getPlayError() == 1) {
                                if (!failTeams.isEmpty()) {
                                    for (Teams failTeam : failTeams) {
                                        MusicPlayer.playQueued(MusicResource.BUZZ_FAIL(failTeam.getCode()));
                                    }
                                    MusicPlayer.playNext();
                                }
                            }
                        });
                    }

                } else {
                    if (standard) {
                        // 标准模式下判定是否需要提前响应音乐
                        MusicPlayer.play(MusicResource.BUZZ_SUCCESS(successTeam.getCode()));
                    } else {
                        MusicPlayer.playWithCallback(MusicResource.BUZZ_SUCCESS(successTeam.getCode()), () -> {
                            // 播放违规
                            if (buzz.getPlayError() == 1) {
                                if (!failTeams.isEmpty()) {
                                    for (Teams failTeam : failTeams) {
                                        MusicPlayer.playQueued(MusicResource.BUZZ_FAIL(failTeam.getCode()));
                                    }
                                    MusicPlayer.playNext();
                                }

                            }
                        });
                    }
                }
                selectTeamOnSuccess(successTeam);
                scoreBoardPreview.reload();
                return;
            }

            if (fail) {
                // 不播放前置的违规音效
//                if (buzz.getPlayError() != 1) {
//                    return;
//                }
                // 播放违规音效
                if (buzz.getErrorMusic() == 1 && buzz.getPlayError() == 1) {
                    if (standard) {
                        MusicPlayer.playWithCallback(MusicResource.BUZZ_FAIL_BEFORE, () -> {
                            MusicPlayer.play(MusicResource.BUZZ_FAIL(failTeams.get(0).getCode()));
                        });
                    } else {
                        for (Teams failTeam : failTeams) {
                            MusicPlayer.playQueued(MusicResource.BUZZ_FAIL(failTeam.getCode()));
                        }
                        MusicPlayer.playNext();
                    }
                } else if (buzz.getErrorMusic() == 0 && buzz.getPlayError() == 1){
                    MusicPlayer.playWithCallback(MusicResource.BUZZ_FAIL_BEFORE, () -> {});
                } else if (buzz.getErrorMusic() == 1 && buzz.getPlayError() == 0){
                    if (standard) {
                        MusicPlayer.play(MusicResource.BUZZ_FAIL(failTeams.get(0).getCode()));
                    } else {
                        for (Teams failTeam : failTeams) {
                            MusicPlayer.playQueued(MusicResource.BUZZ_FAIL(failTeam.getCode()));
                        }
                        MusicPlayer.playNext();
                    }
                }
                scoreBoardPreview.reload();
                return;
            }

            // 超时
            //MusicPlayer.play(MusicResource.BUZZ_STOP);
            scoreBoardPreview.reload();
        });
    }

    private void selectTeamOnSuccess(Teams teams){
        teamComBox.setValue(teams.getShortName());
    }


    @FXML
    public void registerSoftware() {
        try {
            FXMLLoader loader = null;
            if (GlobalCache.MAX_TEAM_NUMBER > 0) {
                loader = new FXMLLoader(Navigator.class.getResource("/view/Unregister.fxml"));
            } else {
                loader = new FXMLLoader(Navigator.class.getResource("/view/Register.fxml"));
            }

            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            Parent load = loader.load();
            Navigator.activeStage.setScene(new Scene(load));
            Navigator.activeStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReloadPane getBuzzTimeBoard() {
        return buzzTimeBoard;
    }

    public void setBuzzResult(ReloadPane buzzResult) {
        this.buzzResult = buzzResult;
    }

    public void setBuzzResultPreview(ReloadPane buzzResultPreview) {
        this.buzzResultPreview = buzzResultPreview;
    }

    public ReloadPane getBuzzResultPreview() {
        return buzzResultPreview;
    }

    public ReloadPane getBuzzResult() {
        return buzzResult;
    }

    private void lightScreen(boolean light, Teams team) {
        if (light) {
            ScreenDevice screenDevice = SerialPortConfig.getScreenDevice(team.getCode());
            SerialPortConnector instance = SerialPortConnector.getInstance();
            if (instance != null && instance.isConnected()) {
                instance.send(Command.SHOW, SerialPortConfig.buildScreenData(screenDevice, team.getTeamScore(), false));
            }

        }
    }
}
