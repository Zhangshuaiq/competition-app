package competition.controller.preview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.Model7;
import competition.controller.match.ReloadPane;
import competition.entity.*;
import competition.mapper.ConfigsMapper;
import competition.mapper.Model3Mapper;
import competition.mapper.QuestionsMapper;
import competition.modal.Model7RankData;
import competition.util.*;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// 竞技模式显示控制 - 优化版本，避免重复创建UI组件
@Component
@Slf4j
public class Model7Preview extends RankPane<Stages> {

    @FXML
    private GridPane container;

    @Resource
    private WsMessageHandler messageHandler;

    private String timeValue = "00:00";

    @Resource
    private ControlPane controlPane;

    @Resource
    private Model3Mapper model3Mapper;

    @Resource
    private QuestionsMapper questionsMapper;

    @Resource
    private ConfigsMapper configsMapper;

    private Stages inner;

    // 优化相关的缓存字段
    private volatile boolean rankDataNeedsUpdate = true;
    private List<Model7RankData> cachedRankData = new ArrayList<>();
    private final Object rankCalculationLock = new Object();

    // Questions查询缓存
    private List<Questions> cachedQuestions;
    private String cachedStageNumber;

    // 题型标注配置缓存
    private String cachedTx = "3";
    private String cachedTxbz = "4";

    // UI组件缓存 - 核心优化点
    private List<TeamDisplayPanel> teamPanels = new ArrayList<>();
    private boolean uiInitialized = false;
    private Model3Style currentModel3Style;

    // 布局参数缓存
    private double cachedVgap, cachedHgap, cachedX, cachedY, cachedWidth, cachedHeight;
    private double cachedWF, cachedTH, cachedQF;
    private Color cachedWFColor, cachedQFColor, cachedBkColor;
    private boolean cachedSortScore;
    private int cachedCol;

    private final Map<String, Object> questionCache = new HashMap<>();

    // 内部类：封装每个队伍的显示面板
    private static class TeamDisplayPanel {
        VBox mainPane;
        VBox upArea;
        HBox row1;
        GridPane row2;
        AnchorPane downArea;

        // 上方区域的标签
        Label teamNameLabel;
        Label scoreLabel;
        Label timeLabel;

        // 下方区域的组件
        VBox questionArea;
        Label questionLabel;
        List<Label> choiceLabels;
        VBox answerArea;
        Label answerLabel;
        Label statusLabel;
        Label waitLabel;
        Label endLabel;
        Label finalScoreLabel;
        Label finalTimeLabel;

        // 圆圈组件
        List<Circle> circles;

        TeamDisplayPanel() {
            choiceLabels = new ArrayList<>();
            circles = new ArrayList<>();
        }

        void initializeComponents(double width, double height, int totalQuestions, int cols,
                double radius,
                double wfSize, Color wfColor,
                double qfSize, Color qfColor,
                Color bkColor) {
            // 创建主面板
            mainPane = new VBox();
            mainPane.setPadding(new Insets(0, 0, 0, 0));
            mainPane.setPrefWidth(width);
            mainPane.setMinWidth(0);
            mainPane.setMaxWidth(Double.MAX_VALUE);

            // 创建上方区域
            upArea = new VBox();
            upArea.setPrefWidth(width - 20);
            upArea.setMinWidth(width - 20);
            upArea.setMaxWidth(width - 20);
            upArea.setMinWidth(0);
            upArea.setMaxWidth(Double.MAX_VALUE);
            upArea.setBackground(new Background(new BackgroundFill(bkColor, new CornerRadii(10), Insets.EMPTY)));
            upArea.setSpacing(5);

            // 创建第一行（队伍信息）
            row1 = new HBox();
            row1.setPadding(new Insets(0, 3, 0, 3));
            row1.setPrefWidth(width); // 与upArea保持一致
            row1.setMinWidth(width);
            row1.setMaxWidth(width);
            teamNameLabel = new Label();
            teamNameLabel.setPrefWidth(width * 0.2);
            teamNameLabel.setTextFill(Color.WHITE);

            scoreLabel = new Label();
            scoreLabel.setPrefWidth(width * 0.2);
            scoreLabel.setTextFill(Color.WHITE);

            timeLabel = new Label();
            timeLabel.setPrefWidth(width * 0.6);
            timeLabel.setTextFill(Color.WHITE);

            ObservableList<Node> children = row1.getChildren();
            children.addAll(teamNameLabel, scoreLabel, timeLabel);
            for (Node child : children) {
                if (child instanceof Label) {
                    Label label = (Label) child;
                    label.setFont(Font.font(ScreenAdapter.calculateMainX(wfSize)));
                    label.setTextFill(wfColor);
                    label.setAlignment(Pos.CENTER);
                    label.setTextAlignment(TextAlignment.CENTER);
                }
            }
            teamNameLabel.setTextAlignment(TextAlignment.LEFT);
            teamNameLabel.setAlignment(Pos.CENTER_LEFT);
            teamNameLabel.setPadding(new Insets(0, 0, 0, 10));

            // 创建第二行（圆圈显示）
            row2 = new GridPane();
            row2.setPadding(new Insets(2));
            row2.setPrefWidth(width);

            // 创建圆圈
            circles.clear();
            for (int i = 0; i < totalQuestions; i++) {
                Circle c = new Circle();
                c.setStrokeWidth(1);
                c.setRadius(ScreenAdapter.calculateMainX(radius));
                c.setStroke(Color.BLACK);
                c.setFill(Color.WHITE);
                int colIndex = i % cols;
                int rowIndex = i / cols;
                row2.add(c, colIndex, rowIndex);
                circles.add(c);
            }

            upArea.getChildren().addAll(row1, row2);
            mainPane.getChildren().add(upArea);

            // 创建下方区域
            downArea = new AnchorPane();
            downArea.setPrefWidth(width);
            downArea.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white");

            // 题目区域
            questionArea = new VBox();
            questionArea.setPadding(new Insets(1, 5, 1, 5));
            questionArea.setSpacing(ScreenAdapter.calculateMainY(2));
            questionArea.setPrefWidth(width);

            questionLabel = new Label();
            questionLabel.setWrapText(true);
            questionLabel.setFont(Font.font(ScreenAdapter.calculateMainX(qfSize)));
            questionLabel.setTextFill(qfColor);
            questionArea.getChildren().add(questionLabel);

            // 选择题选项
            String[] choices = { "A", "B", "C", "D", "E", "F" };
            for (String choice : choices) {
                Label choiceLabel = new Label();
                choiceLabel.setWrapText(false);
                choiceLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
                choiceLabel.setVisible(false);
                choiceLabel.setFont(Font.font(ScreenAdapter.calculateMainX(qfSize)));
                choiceLabel.setTextFill(qfColor);
                choiceLabels.add(choiceLabel);
                questionArea.getChildren().add(choiceLabel);
            }

            // 答案区域
            answerArea = new VBox();
            answerArea.setAlignment(Pos.CENTER);
            answerArea.setLayoutY(ScreenAdapter.calculateMainY(190));
            answerArea.setPrefWidth(width);
            answerArea.setSpacing(ScreenAdapter.calculateMainY(5));

            answerLabel = new Label();
            answerLabel.setPrefHeight(ScreenAdapter.calculateMainY(30));
            answerLabel.setMinHeight(ScreenAdapter.calculateMainY(30));
            answerLabel.setMaxHeight(ScreenAdapter.calculateMainY(30));
            answerLabel.setTextAlignment(TextAlignment.CENTER);
            answerLabel.setTextFill(Color.WHITE);
            answerLabel.setPrefWidth(width);
            answerLabel.setAlignment(Pos.CENTER);
            answerLabel.setVisible(false);

            statusLabel = new Label();
            statusLabel.setPrefHeight(ScreenAdapter.calculateMainY(30));
            statusLabel.setMinHeight(ScreenAdapter.calculateMainY(30));
            statusLabel.setMaxHeight(ScreenAdapter.calculateMainY(30));

            statusLabel.setAlignment(Pos.CENTER);
            statusLabel.setTextAlignment(TextAlignment.CENTER);
            statusLabel.setTextFill(Color.WHITE);
            statusLabel.setPrefWidth(width);
            statusLabel.setVisible(false);

            answerArea.getChildren().addAll(answerLabel, statusLabel);
            answerArea.setPrefHeight(ScreenAdapter.calculateMainY(60));
            answerArea.setMaxHeight(ScreenAdapter.calculateMainY(60));
            // 等待标签
            waitLabel = new Label("等待答题");
            waitLabel.setPrefWidth(width);
            waitLabel.setPrefHeight(height); // 初始化时设置为整个面板高度，后续会在updateLayout中调整
            waitLabel.setMaxHeight(height);
            waitLabel.setMinHeight(height);
            waitLabel.setLayoutY(0);
            waitLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateMainX(35)));
            waitLabel.setTextFill(Color.BLACK);
            waitLabel.setTextAlignment(TextAlignment.CENTER);
            waitLabel.setAlignment(Pos.CENTER);
            waitLabel.setVisible(false);

            // 结束标签
            endLabel = new Label("答题结束");
            endLabel.setPrefWidth(width);
            endLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateMainX(32)));
            endLabel.setAlignment(Pos.CENTER);
            endLabel.setTextAlignment(TextAlignment.CENTER);
            endLabel.setVisible(false);

            finalScoreLabel = new Label();
            finalScoreLabel.setPrefWidth(width);
            finalScoreLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateMainX(32)));
            finalScoreLabel.setTextAlignment(TextAlignment.CENTER);
            finalScoreLabel.setAlignment(Pos.CENTER);
            finalScoreLabel.setVisible(false);

            finalTimeLabel = new Label();
            finalTimeLabel.setPrefWidth(width);
            finalTimeLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateMainX(32)));
            finalTimeLabel.setTextAlignment(TextAlignment.CENTER);
            finalTimeLabel.setAlignment(Pos.CENTER);
            finalTimeLabel.setVisible(false);

            downArea.getChildren().addAll(questionArea, answerArea, waitLabel, endLabel, finalScoreLabel,
                    finalTimeLabel);
            mainPane.getChildren().add(downArea);
        }

        void updateLayout(double width, double height, boolean hideCircles, boolean hideDownArea) {
            mainPane.setPrefWidth(width);
            upArea.setPrefWidth(width);
            upArea.setMinWidth(width);
            row1.setPrefWidth(width);
            row1.setMinWidth(width);
            row2.setPrefWidth(width);
            row2.setMinWidth(width);
            teamNameLabel.setPrefWidth(width * 0.2);
            scoreLabel.setPrefWidth(width * 0.2);
            timeLabel.setPrefWidth(width * 0.6);

            // 更新圆圈行的显示状态
            row2.setVisible(!hideCircles);
            row2.setManaged(!hideCircles);

            // 精确计算圆圈区域高度
            int circleRowHeight = 0;
            if (!hideCircles && circles.size() > 0) {
                // 计算每行能放多少个圆圈
                double circleWidth = 2 * ScreenAdapter.calculateMainX(5) + 2; // 圆圈直径 + 间距
                int colsPerRow = Math.max(1, (int) ((width - 4) / circleWidth));
                // 计算需要多少行
                int rows = circles.size() / colsPerRow; // 向上取整
                // 每行的实际高度（包括圆圈直径和间距）
                double rowHeight = 2 * ScreenAdapter.calculateMainX(5) + 2;
                circleRowHeight = (int) (rows * rowHeight);
            }
            // 上方蓝色区域高度 = 第一行高度(22) + 圆圈区域高度 + 内边距
            double upAreaHeight = ScreenAdapter.calculateMainY(22 + circleRowHeight + (hideCircles ? 0 : 5));
            upArea.setPrefHeight(upAreaHeight);
            upArea.setMaxHeight(upAreaHeight);

            // 下方区域的位置和高度
            downArea.setPrefWidth(width);
            double downAreaY = ScreenAdapter.calculateMainY(10) + upAreaHeight;
            downArea.setLayoutY(downAreaY);
            double downAreaHeight = height - (downAreaY - ScreenAdapter.calculateMainY(10));
            downArea.setPrefHeight(downAreaHeight);
            downArea.setMinHeight(downAreaHeight);
            downArea.setMaxHeight(downAreaHeight);
            downArea.setVisible(!hideDownArea);
            downArea.setManaged(!hideDownArea);

            // 题目区域保持不变
            questionArea.setPrefWidth(width);

            // 答案区域 - 根据下方区域高度动态调整位置
            answerArea.setPrefWidth(width);
            double answerAreaHeight = ScreenAdapter.calculateMainY(70);
            double answerAreaY = downAreaHeight - answerAreaHeight;
            answerArea.setLayoutY(answerAreaY);

            // 等待标签 - 填满整个下方区域，保持原字体大小
            waitLabel.setPrefWidth(width);
            waitLabel.setPrefHeight(downAreaHeight);
            waitLabel.setMinHeight(downAreaHeight);
            waitLabel.setMaxHeight(downAreaHeight);
            waitLabel.setLayoutY(0);

            // 结束相关标签 - 根据下方区域高度重新分布位置
            double labelHeight = downAreaHeight / 3;

            endLabel.setPrefWidth(width);
            endLabel.setPrefHeight(labelHeight);
            endLabel.setMinHeight(labelHeight);
            endLabel.setMaxHeight(labelHeight);
            endLabel.setLayoutY(0);

            finalScoreLabel.setPrefWidth(width);
            finalScoreLabel.setPrefHeight(labelHeight);
            finalScoreLabel.setMinHeight(labelHeight);
            finalScoreLabel.setMaxHeight(labelHeight);
            finalScoreLabel.setLayoutY(labelHeight);

            finalTimeLabel.setPrefWidth(width);
            finalTimeLabel.setPrefHeight(labelHeight);
            finalTimeLabel.setMinHeight(labelHeight);
            finalTimeLabel.setMaxHeight(labelHeight);
            finalTimeLabel.setLayoutY(labelHeight * 2);
        }
    }

    @Override
    public void setData(Stages data) {
        inner = data;
    }

    public void stopTiming() {
    }

    @Override
    public Node getPane() {
        return container;
    }

    @FXML
    public void initPage() {
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }

        Stages currentStage = controlPane.getCurrentStage();

        Configs configs = configsMapper.selectById(1);
        String cfgVal = configs.getConfig();
        String txbz = "4";
        if (!StringUtils.isEmpty(cfgVal)) {
            String[] split = cfgVal.split(ConfigSpliter.SPLITER);
            txbz = split[10];
        }
        // 获取题目数据并缓存
        List<Questions> questions = getQuestionsWithCache();
        for (Questions question : questions) {

            if (StringUtils.isEmpty(question.getAddScore()) || !NumberUtil.isInt(question.getAddScore())) {
                question.setScore(currentStage.getScorePerQ());
            } else {
                question.setScore(Integer.parseInt(question.getAddScore()));
            }

            if (StringUtils.isEmpty(question.getDescScore()) || !NumberUtil.isInt(question.getDescScore())) {
                question.setDesValue(currentStage.getDesValue());
            } else {
                question.setDesValue(Integer.parseInt(question.getDescScore()));
            }

            question.setTxbz(txbz);
            question.setShowNumber(inner.getShowNumber());
        }
        boolean random = currentStage.getrRandomSort() == 1;

        questionCache.put("random", random);
        questionCache.put("questions", questions);

        messageHandler.notifyPage("model7", questions);

        Model7RuntimeData.totalNumber = questions.size();
        PreviewStack.addPreview(this);

        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    Navigator.navigatorTo("/view/match/Model7Attr.fxml", "", "/view/style.css", false, false);
                }
            }
        });

        // 初始化时加载题型标注配置
        loadQuestionTypeConfig();

        // 初始化时异步计算排名
        calculatePWAsync();

        // 初始化UI组件
        initializeUI();
        reload();
    }

    /**
     * 初始化UI组件结构（只执行一次）
     */
    private void initializeUI() {
        if (uiInitialized) {
            return;
        }

        // 设置背景
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.竞技模式背景())), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false))));
        } catch (FileNotFoundException e) {
            container.setBackground(
                    new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        // 获取样式配置
        currentModel3Style = model3Mapper.selectById(2);
        cacheLayoutParameters();

        container.setPrefWidth(Navigator.getWidth());
        container.setVgap(cachedVgap);
        container.setHgap(cachedHgap);
        container.setPadding(new Insets(cachedY, 0, 0, cachedX));

        // 创建队伍面板
        createTeamPanels();

        uiInitialized = true;
    }

    /**
     * 缓存布局参数
     */
    private void cacheLayoutParameters() {
        String titleStyle = currentModel3Style.getTitleStyle();

        // 默认值
        cachedSortScore = false;
        cachedVgap = ScreenAdapter.calculateMainY(20);
        cachedHgap = ScreenAdapter.calculateMainX(20);
        cachedX = ScreenAdapter.calculateMainX(10);
        cachedY = 0;
        cachedWidth = ScreenAdapter.calculateMainX((int) ((Navigator.getWidth() - 40) / 4));
        cachedHeight = ScreenAdapter.calculateMainY(287);
        cachedCol = 4;
        cachedWF = ScreenAdapter.calculateMainX(12);
        cachedTH = ScreenAdapter.calculateMainX(5);
        cachedQF = ScreenAdapter.calculateMainX(12);
        cachedWFColor = Color.BLACK;
        cachedQFColor = Color.BLACK;
        cachedBkColor = Color.BLACK;
        if (!StringUtils.isEmpty(titleStyle)) {
            String[] split = titleStyle.split(ConfigSpliter.SPLITER);
            cachedCol = Integer.parseInt(split[0]);
            cachedX = ScreenAdapter.calculateMainX(Integer.parseInt(split[5]));
            cachedY = ScreenAdapter.calculateMainY(Integer.parseInt(split[6]));
            cachedWidth = ScreenAdapter.calculateMainX(Integer.parseInt(split[3]));
            cachedHeight = ScreenAdapter.calculateMainY(Integer.parseInt(split[4]));
            cachedVgap = ScreenAdapter.calculateMainY(Integer.parseInt(split[1]));
            cachedHgap = ScreenAdapter.calculateMainX(Integer.parseInt(split[2]));
            if (split.length > 7) {
                cachedWF = ScreenAdapter.calculateMainX(Integer.parseInt(split[7].trim()));
            }
            if (split.length > 8) {
                cachedTH = ScreenAdapter.calculateMainX(Integer.parseInt(split[8]));
            }
            if (split.length > 9) {
                cachedQF = ScreenAdapter.calculateMainX(Integer.parseInt(split[9]));
            }
            if (split.length > 10) {
                cachedWFColor = Color.web(split[10]);
            }
            if (split.length > 11) {
                cachedQFColor = Color.web(split[11]);
            }
            if (split.length > 12) {
                cachedBkColor = Color.web(split[12]);
            }
            if (split.length > 13) {
                cachedSortScore = Boolean.parseBoolean(split[13]);
            }
        }
    }

    /**
     * 创建队伍面板（只执行一次）
     */
    private void createTeamPanels() {
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<Questions> questions = getQuestionsWithCache();
        int cols = (int) ((cachedWidth - 4) / (2 * ScreenAdapter.calculateMainX(cachedTH) + 1));

        teamPanels.clear();
        container.getChildren().clear();

        for (int i = 0; i < runtimeData.size(); i++) {
            TeamDisplayPanel panel = new TeamDisplayPanel();
            panel.initializeComponents(cachedWidth, cachedHeight, questions.size(), cols, cachedTH,
                    cachedWF, cachedWFColor, cachedQF, cachedQFColor, cachedBkColor);

            container.add(panel.mainPane, i % cachedCol, i / cachedCol);
            teamPanels.add(panel);
        }
    }

    /**
     * 加载题型标注配置
     */
    private void loadQuestionTypeConfig() {
        try {
            Configs configs = configsMapper.selectById(1);
            String cfgVal = configs.getConfig();
            if (!StringUtils.isEmpty(cfgVal)) {
                String[] split = cfgVal.split(ConfigSpliter.SPLITER);
                if (split.length > 10) {
                    cachedTx = split[9]; // 题型
                    cachedTxbz = split[10]; // 题型标注
                }
            }
        } catch (Exception e) {
            log.error("加载题型标注配置失败: {}", e.getMessage());
            // 使用默认值
            cachedTx = "3";
            cachedTxbz = "4";
        }
    }

    /**
     * 根据题型标注配置格式化题目文本
     */
    private String formatQuestionText(Model7RuntimeData.ModelData questionData) {
        String questionText = "";
        String questionType = questionData.getQuestionType();
        Integer questionNumber = questionData.getQuestionNumber();
        String question = questionData.getQuestion();

        // 如果不显示题号，则不添加题号
        if (Objects.equals(inner.getShowNumber(), 0)) {
            // 无题型标注
            if (cachedTxbz.equals("4")) {
                questionText = question;
            } else if (cachedTxbz.equals("3")) {
                if (cachedTx.equals("3")) {
                    questionText = question + " " + questionType;
                } else if (cachedTx.equals("2")) {
                    questionText = questionType + question;
                } else if (cachedTx.equals("1")) {
                    questionText = questionType + " " + question;
                }
            } else if (cachedTxbz.equals("2")) {
                if (cachedTx.equals("3")) {
                    questionText = question + "(" + questionType + ")";
                } else if (cachedTx.equals("2")) {
                    questionText = "(" + questionType + ")" + question;
                } else if (cachedTx.equals("1")) {
                    questionText = "(" + questionType + ")" + question;
                }
            } else if (cachedTxbz.equals("1")) {
                if (cachedTx.equals("3")) {
                    questionText = question + "[" + questionType + "]";
                } else if (cachedTx.equals("2")) {
                    questionText = "[" + questionType + "]" + question;
                } else if (cachedTx.equals("1")) {
                    questionText = "[" + questionType + "]" + question;
                }
            }
        } else {
            // 显示题号
            // 无题型标注
            if (cachedTxbz.equals("4")) {
                questionText = questionNumber + ". " + question;
            } else if (cachedTxbz.equals("3")) {
                if (cachedTx.equals("3")) {
                    questionText = questionNumber + ". " + question + " " + questionType;
                } else if (cachedTx.equals("2")) {
                    questionText = questionNumber + ". " + questionType + question;
                } else if (cachedTx.equals("1")) {
                    questionText = questionType + " " + questionNumber + ". " + question;
                }
            } else if (cachedTxbz.equals("2")) {
                if (cachedTx.equals("3")) {
                    questionText = questionNumber + ". " + question + "(" + questionType + ")";
                } else if (cachedTx.equals("2")) {
                    questionText = questionNumber + ". " + "(" + questionType + ")" + question;
                } else if (cachedTx.equals("1")) {
                    questionText = "(" + questionType + ")" + questionNumber + ". " + question;
                }
            } else if (cachedTxbz.equals("1")) {
                if (cachedTx.equals("3")) {
                    questionText = questionNumber + ". " + question + "[" + questionType + "]";
                } else if (cachedTx.equals("2")) {
                    questionText = questionNumber + ". " + "[" + questionType + "]" + question;
                } else if (cachedTx.equals("1")) {
                    questionText = "[" + questionType + "]" + questionNumber + ". " + question;
                }
            }
        }

        // 添加未注册标记
        if (GlobalCache.MAX_TEAM_NUMBER <= 0) {
            questionText = questionText + "(未注册)";
        }

        return questionText;
    }

    public void clearCache() {
        cachedQuestions = new ArrayList<>();
        Model7 bean = JavaFxApplication.getApplicationContext().getBean(Model7.class);
        if (bean != null) {
            bean.clearCache();
        }
    }

    /**
     * 获取题目数据（带缓存）
     */
    public List<Questions> getQuestionsWithCache() {
        if (inner == null) {
            return new ArrayList<>();
        }
        if (cachedQuestions == null || cachedQuestions.isEmpty()
                || !Objects.equals(cachedStageNumber, inner.getSortVal())) {
            cachedQuestions = questionsMapper.selectList(
                    new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, inner.getSortVal()));
            cachedStageNumber = inner.getSortVal();
        }
        return cachedQuestions;
    }

    /**
     * 异步计算排名数据
     */
    private void calculatePWAsync() {
        // 标记需要更新
        rankDataNeedsUpdate = true;

        // 在后台线程计算
        CompletableFuture.supplyAsync(() -> {
            synchronized (rankCalculationLock) {
                if (!rankDataNeedsUpdate) {
                    return cachedRankData; // 如果不需要更新，返回缓存数据
                }

                return calculateRankData();
            }
        }).thenAcceptAsync(rankData -> {
            // 更新缓存
            synchronized (rankCalculationLock) {
                cachedRankData = rankData;
                rankDataNeedsUpdate = false;
            }

            // 通知前端
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("rank", rankData);
            messageHandler.notifyPage("hmodel7", mapData);
        }, Platform::runLater);
    }

    /**
     * 实际的排名计算逻辑（从原calculatePW方法提取）
     */
    private List<Model7RankData> calculateRankData() {
        List<Model7RankData> rankList = new ArrayList<>();
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<String> timeData = Model7RuntimeData.getFts();
        List<Integer> seconds = Model7RuntimeData.getSeconds();

        boolean finish = true;
        List<Boolean> finished = Model7RuntimeData.getFinished();
        for (Boolean b : finished) {
            finish = finish & b;
        }

        List<Questions> questions = (List<Questions>) questionCache.get("questions");
        Map<Integer, String> desMap = questions.stream().collect(
                Collectors.toMap(Questions::getQuestionNumber, q -> q.getDescScore() != null ? q.getDescScore() : ""));

        for (int i = 0; i < runtimeData.size(); i++) {
            List<Model7RuntimeData.ModelData> questionList = runtimeData.get(i);
            int score = 0;

            if (finish) {
                for (Model7RuntimeData.ModelData data : questionList) {
                    if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                        score += data.getScore();
                    } else {
                        score -= data.getDesValue();
                    }
                }
                List<Integer> answeredQuestion = questionList.stream()
                        .map(Model7RuntimeData.ModelData::getQuestionNumber).collect(Collectors.toList());
                Integer unAnsweredValue = 0;
                for (Entry<Integer, String> entrySet : desMap.entrySet()) {
                    int num = entrySet.getKey();
                    String val = entrySet.getValue();
                    if (!answeredQuestion.contains(num) && NumberUtil.isInt(val)) {
                        unAnsweredValue += Integer.parseInt(val);
                    } else if (!answeredQuestion.contains(num)) {
                        unAnsweredValue += controlPane.getCurrentStage().getDesValue();
                    }
                }
                score = score - unAnsweredValue;
            } else {
                for (Model7RuntimeData.ModelData data : questionList) {
                    if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                        score += data.getScore();
                    } else {
                        score -= data.getDesValue();
                    }
                }
            }

            String team = Model7RuntimeData.getTeams().get(i).getFullName();
            String time = timeData.get(i);

            Model7RankData rankData = new Model7RankData();
            rankData.setScore(score);
            rankData.setSecond(seconds.get(i));
            rankData.setTeam(team);
            rankData.setTime(time);
            rankList.add(rankData);
        }

        // 排序
        rankList.sort((o1, o2) -> {
            if (o1.getScore() < o2.getScore()) {
                return 1;
            } else if (o1.getScore() > o2.getScore()) {
                return -1;
            } else {
                return o1.getSecond().compareTo(o2.getSecond());
            }
        });

        // 设置排名
        int rank = 1;
        Model7RankData first = rankList.get(0);
        for (Model7RankData rankData : rankList) {
            if (first.compareTo(rankData) < 0) {
                rank++;
            }
            rankData.setRank(rank);
            first = rankData;
        }

        return rankList;
    }

    /**
     * 更新数据显示（只更新内容，不重新创建组件）
     */
    private void updateData() {
        // 如果排名数据需要更新，异步计算
        if (rankDataNeedsUpdate) {
            calculatePWAsync();
        }

        // 处理扩展屏幕显示
        if (ExtendedScreen.hasShow()) {
            // 进入比赛
            ReloadPane questionPreview = controlPane.getQuestionPreview();
            if (questionPreview != null) {
                if (questionPreview instanceof Model7) {
                    questionPreview.reload(1, this.timeValue);
                } else {
                    Object res = ExtendedScreen.showWithControl("/view/match/Model7.fxml", inner);
                    if (res != null) {
                        controlPane.setQuestionPreview((Model7) res);
                    }
                }
            } else {
                Object res = ExtendedScreen.showWithControl("/view/match/Model7.fxml", inner);
                if (res != null) {
                    controlPane.setQuestionPreview((Model7) res);
                }
            }
        }

        // 获取样式配置
        String[] headStyleSplit = currentModel3Style.getHeadStyle().split(",");
        boolean hideCircles = headStyleSplit.length > 1 ? Boolean.parseBoolean(headStyleSplit[1]) : false;
        boolean hideDownArea = headStyleSplit.length > 0 ? Boolean.parseBoolean(headStyleSplit[0]) : false;

        // 获取原始数据
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<Teams> teams = TeamRuntime.getTeams();

        // 创建包含索引和分数的排序数据结构
        List<TeamSortData> sortedTeamData = new ArrayList<>();

        // 计算每个队伍的分数并创建排序数据
        for (int i = 0; i < runtimeData.size(); i++) {
            List<Model7RuntimeData.ModelData> modelData = runtimeData.get(i);

            // 计算分数
            int score = 0;
            int finish = 0;
            int correct = 0;

            for (Model7RuntimeData.ModelData m : modelData) {
                if (m.getUserSelect() != null) {
                    if (m.getUserSelect().equals(m.getAnswer())) {
                        score += m.getScore();
                        correct++;
                    } else {
                        score -= m.getDesValue();
                    }
                    finish++;
                }
            }

            // 补充未答题的分数扣除
            List<Questions> questions = (List<Questions>) questionCache.get("questions");
            Map<Integer, String> desMap = questions.stream().collect(Collectors.toMap(Questions::getQuestionNumber,
                    q -> q.getDescScore() != null ? q.getDescScore() : ""));

            List<Integer> answeredQuestion = modelData.stream().map(Model7RuntimeData.ModelData::getQuestionNumber)
                    .collect(Collectors.toList());
            Integer unAnsweredValue = 0;
            Stages currentStageForDes = controlPane.getCurrentStage();
            for (Entry<Integer, String> entrySet : desMap.entrySet()) {
                int num = entrySet.getKey();
                String val = entrySet.getValue();
                if (!answeredQuestion.contains(num) && NumberUtil.isInt(val)) {
                    unAnsweredValue += Integer.parseInt(val);
                } else if (!answeredQuestion.contains(num) && currentStageForDes != null) {
                    unAnsweredValue += currentStageForDes.getDesValue();
                }
            }
            if (Model7RuntimeData.getFinished().get(i)) {
                 score = score - unAnsweredValue;
            }

            // 获取用时（秒）
            int timeInSeconds = Model7RuntimeData.getSeconds().get(i);

            // 创建排序数据
            TeamSortData teamSortData = new TeamSortData();
            teamSortData.originalIndex = i;
            teamSortData.score = score;
            teamSortData.timeInSeconds = timeInSeconds;
            teamSortData.finishCount = finish;
            teamSortData.correctCount = correct;
            teamSortData.modelData = modelData;
            teamSortData.team = teams.get(i);
            teamSortData.isFinished = Model7RuntimeData.getFinished().get(i);

            sortedTeamData.add(teamSortData);
        }

        // 按分数排序（分数高的在前，分数相同时用时短的在前）
        // 使用分数排序
        if (cachedSortScore) {
            sortedTeamData.sort((o1, o2) -> {
                if (o1.score != o2.score) {
                    return Integer.compare(o2.score, o1.score); // 分数降序
                } else {
                    return Integer.compare(o1.timeInSeconds, o2.timeInSeconds); // 用时升序
                }
            });
        }

        // 清空当前显示
        container.getChildren().clear();

        // 按排序后的顺序更新面板数据和位置
        for (int displayIndex = 0; displayIndex < sortedTeamData.size()
                && displayIndex < teamPanels.size(); displayIndex++) {
            TeamSortData teamData = sortedTeamData.get(displayIndex);
            TeamDisplayPanel panel = teamPanels.get(teamData.originalIndex); // 使用原始索引获取面板

            // 重新添加到容器中（按新的排序位置）
            container.add(panel.mainPane, displayIndex % cachedCol, displayIndex / cachedCol);

            // 更新布局
            panel.updateLayout(cachedWidth, cachedHeight, hideCircles, hideDownArea);

            // 更新圆圈状态
            Model7RuntimeData.ModelData last = null;
            for (int j = 0; j < teamData.modelData.size(); j++) {
                Model7RuntimeData.ModelData m = teamData.modelData.get(j);
                if (j < panel.circles.size()) {
                    Circle circle = panel.circles.get(j);
                    if (m.getUserSelect() == null) {
                        circle.setFill(Color.WHITE);
                    } else if (m.getUserSelect().equals(m.getAnswer())) {
                        circle.setFill(Color.GREEN);
                    } else {
                        circle.setFill(Color.RED);
                    }
                }
                last = m;
            }

            // 重置剩余圆圈为白色
            for (int j = teamData.modelData.size(); j < panel.circles.size(); j++) {
                panel.circles.get(j).setFill(Color.WHITE);
            }

            // 更新队伍信息
            panel.teamNameLabel.setText(teamData.team.getShortName());
            panel.scoreLabel.setText(teamData.score + "分");

            String timeText = "完成" + teamData.finishCount + "题 答对" + teamData.correctCount + "题|" + timeValue;
            if (teamData.isFinished) {
                timeText = "完成" + teamData.finishCount + "题 答对" + teamData.correctCount + "题|" +
                        Model7RuntimeData.getFts().get(teamData.originalIndex);
            }
            panel.timeLabel.setText(timeText);

            // 更新下方区域内容
            // 隐藏所有下方区域组件
            panel.questionArea.setVisible(false);
            panel.answerArea.setVisible(false);
            panel.waitLabel.setVisible(false);
            panel.endLabel.setVisible(false);
            panel.finalScoreLabel.setVisible(false);
            panel.finalTimeLabel.setVisible(false);

            if (teamData.isFinished) {
                // 显示完成状态
                panel.endLabel.setVisible(true);
                panel.finalScoreLabel.setText(teamData.score + "分");
                panel.finalScoreLabel.setVisible(true);
                panel.finalTimeLabel.setText(Model7RuntimeData.getFts().get(teamData.originalIndex));
                panel.finalTimeLabel.setVisible(true);
            } else if (last == null) {
                // 显示等待状态
                panel.waitLabel.setVisible(true);
            } else {
                // 显示当前题目
                panel.questionArea.setVisible(true);

                // 更新题目文本
                String formattedQuestionText = formatQuestionText(last);
                panel.questionLabel.setText(formattedQuestionText);

                // 更新选择题选项
                String[] choices = { last.getChoiceA(), last.getChoiceB(), last.getChoiceC(),
                        last.getChoiceD(), last.getChoiceE(), last.getChoiceF() };
                String[] choiceLabels = { "A", "B", "C", "D", "E", "F" };

                for (int j = 0; j < panel.choiceLabels.size(); j++) {
                    Label choiceLabel = panel.choiceLabels.get(j);
                    if (j < choices.length && !StringUtils.isEmpty(choices[j])) {
                        choiceLabel.setText(choiceLabels[j] + ". " + choices[j]);
                        choiceLabel.setVisible(true);
                    } else {
                        choiceLabel.setVisible(false);
                    }
                }

                // 更新答案显示
                if (last.getUserSelect() != null) {
                    panel.answerArea.setVisible(true);
                    panel.answerLabel.setText("回答:" + last.getUserSelect());
                    panel.answerLabel.setBackground(
                            new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
                    panel.answerLabel.setVisible(true);

                    if (last.getAnswer().equals(last.getUserSelect())) {
                        panel.statusLabel.setText("正确");
                        panel.statusLabel.setBackground(
                                new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
                    } else {
                        panel.statusLabel.setText("错误");
                        panel.statusLabel.setBackground(
                                new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
                    }
                    panel.statusLabel.setVisible(true);
                }
            }
        }
    }

    // 添加辅助类用于排序
    private static class TeamSortData {
        int originalIndex; // 原始索引
        int score; // 分数
        int timeInSeconds; // 用时（秒）
        int finishCount; // 完成题数
        int correctCount; // 正确题数
        List<Model7RuntimeData.ModelData> modelData; // 题目数据
        Teams team; // 队伍信息
        boolean isFinished; // 是否完成
    }

    @Override
    public void destroy() {
        // 清理缓存的组件
        teamPanels.clear();
        container.getChildren().clear();
        uiInitialized = false;
        ReloadPane questionPreview = controlPane.getQuestionPreview();
        if (questionPreview != null) {
            questionPreview.destroy();
        }
    }

    @Override
    public void reloadStyle() {
        // 重新加载题型标注配置
        loadQuestionTypeConfig();

        // 重新加载样式配置
        currentModel3Style = model3Mapper.selectById(2);
        cacheLayoutParameters();

        // 如果UI已初始化，需要重新创建（因为布局可能改变）
        if (uiInitialized) {
            destroy();
            initializeUI();
        }

        this.reload();
    }

    @Override
    public void reload() {
        // 标记排名数据需要更新
        rankDataNeedsUpdate = true;

        if (Model7RuntimeData.executeStart) {
            this.timeValue = "00:00";
            messageHandler.sendModel7Time("0");
            Model7RuntimeData.executeStart = false;
        }

        // 确保UI已初始化
        if (!uiInitialized) {
            initializeUI();
        }

        updateData();
    }

    @Override
    public void reload(int time, String timeTxt) {
        this.timeValue = timeTxt;
        messageHandler.sendModel7Time(time + "");

        // 标记排名数据需要更新
        rankDataNeedsUpdate = true;

        // 确保UI已初始化
        if (!uiInitialized) {
            initializeUI();
        }

        updateData();
    }

    @Override
    public Stages getData() {
        return inner;
    }

    public Map<String, Object> getQuestionCache() {
        return questionCache;
    }
}