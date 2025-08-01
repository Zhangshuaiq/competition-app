package competition.controller.match;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.entity.Configs;
import competition.entity.Model3Style;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.Model3Mapper;
import competition.mapper.QuestionsMapper;
import competition.util.*;
import competition.ws.Model7RuntimeData;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

// 竞技模式显示控制 - 优化版本，避免重复创建UI组件
@Component
public class Model7 implements ReloadPane<Stages> {

    @FXML
    private GridPane container;

    private String timeValue = "00:00";

    @Resource
    private ControlPane controlPane;

    @Resource
    private Model3Mapper model3Mapper;

    private Stages currentStage;

    @Resource
    private QuestionsMapper questionsMapper;

    @Resource
    private ConfigsMapper configsMapper;

    // 优化相关的缓存字段
    private List<Questions> cachedQuestions;
    private String cachedStageNumber;
    private final AtomicLong lastUpdateTime = new AtomicLong(0);
    private static final long UPDATE_INTERVAL = 100; // 100ms防抖间隔

    // 题型标注配置缓存
    private String cachedTx = "3";      // 题型位置
    private String cachedTxbz = "4";    // 题型标注样式

    // UI组件缓存 - 核心优化点
    private List<TeamDisplayPanel> teamPanels = new ArrayList<>();
    private boolean uiInitialized = false;
    private Model3Style currentModel3Style;

    // 布局参数缓存 - 与Preview保持一致
    private double cachedVgap, cachedHgap, cachedX, cachedY, cachedWidth, cachedHeight;
    private double cachedWF, cachedTH, cachedQF;
    private Color cachedWFColor, cachedQFColor, cachedBkColor;
    private int cachedCol;

    // 内部类：封装每个队伍的显示面板 - 与Preview保持一致的结构
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
            // 创建主面板 - 与Preview一致使用VBox
            mainPane = new VBox();
            mainPane.setPadding(new Insets(0, 0, 0, 0));
            mainPane.setPrefWidth(width);
            mainPane.setMinWidth(0);
            mainPane.setMaxWidth(Double.MAX_VALUE);

            // 创建上方区域 - 与Preview保持一致
            upArea = new VBox();
            upArea.setPrefWidth(width - 20);
            upArea.setMinWidth(width - 20);
            upArea.setMaxWidth(width - 20);
            upArea.setMinWidth(0);
            upArea.setMaxWidth(Double.MAX_VALUE);
            upArea.setBackground(new Background(new BackgroundFill(bkColor, new CornerRadii(10), Insets.EMPTY)));
            upArea.setSpacing(5);

            // 创建第一行（队伍信息） - 与Preview保持一致
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

            // 创建第二行（圆圈显示） - 与Preview保持一致
            row2 = new GridPane();
            row2.setPadding(new Insets(2));
            row2.setPrefWidth(width);

            // 创建圆圈 - 使用ExtendedScreen的适配方法
            circles.clear();
            for (int i = 0; i < totalQuestions; i++) {
                Circle c = new Circle();
                c.setStrokeWidth(1);
                c.setRadius(ScreenAdapter.calculateExX(radius));
                c.setStroke(Color.BLACK);
                c.setFill(Color.WHITE);
                int colIndex = i % cols;
                int rowIndex = i / cols;
                row2.add(c, colIndex, rowIndex);
                circles.add(c);
            }

            upArea.getChildren().addAll(row1, row2);
            mainPane.getChildren().add(upArea);

            // 创建下方区域 - 与Preview保持一致
            downArea = new AnchorPane();
            downArea.setPrefWidth(width);
            downArea.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white");

            // 题目区域 - 与Preview保持一致
            questionArea = new VBox();
            questionArea.setPadding(new Insets(1, 5, 1, 5));
            questionArea.setSpacing(ScreenAdapter.calculateExY(2));
            questionArea.setPrefWidth(width);

            questionLabel = new Label();
            questionLabel.setWrapText(true);
            questionLabel.setFont(Font.font(ScreenAdapter.calculateExX(qfSize)));
            questionLabel.setTextFill(qfColor);
            questionArea.getChildren().add(questionLabel);

            // 选择题选项 - 与Preview保持一致
            String[] choices = {"A", "B", "C", "D", "E", "F"};
            for (String choice : choices) {
                Label choiceLabel = new Label();
                choiceLabel.setWrapText(true);
                choiceLabel.setVisible(false);
                choiceLabel.setFont(Font.font(ScreenAdapter.calculateExX(qfSize)));
                choiceLabel.setTextFill(qfColor);
                choiceLabels.add(choiceLabel);
                questionArea.getChildren().add(choiceLabel);
            }

            // 答案区域 - 与Preview保持一致
            answerArea = new VBox();
            answerArea.setAlignment(Pos.CENTER);
            answerArea.setLayoutY(ScreenAdapter.calculateExY(190));
            answerArea.setPrefWidth(width);
            answerArea.setSpacing(ScreenAdapter.calculateExY(5));

            answerLabel = new Label();
            answerLabel.setPrefHeight(ScreenAdapter.calculateExY(30));
            answerLabel.setTextAlignment(TextAlignment.CENTER);
            answerLabel.setTextFill(Color.WHITE);
            answerLabel.setPrefWidth(width);
            answerLabel.setAlignment(Pos.CENTER);
            answerLabel.setVisible(false);

            statusLabel = new Label();
            statusLabel.setPrefHeight(ScreenAdapter.calculateExY(30));
            statusLabel.setAlignment(Pos.CENTER);
            statusLabel.setTextAlignment(TextAlignment.CENTER);
            statusLabel.setTextFill(Color.WHITE);
            statusLabel.setPrefWidth(width);
            statusLabel.setVisible(false);

            answerArea.getChildren().addAll(answerLabel, statusLabel);

            // 等待标签 - 与Preview保持一致
            waitLabel = new Label("等待答题");
            waitLabel.setPrefWidth(width);
            waitLabel.setPrefHeight(height); // 初始化时设置为整个面板高度，后续会在updateLayout中调整
            waitLabel.setLayoutY(0);
            waitLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateExX(35)));
            waitLabel.setTextFill(Color.BLACK);
            waitLabel.setTextAlignment(TextAlignment.CENTER);
            waitLabel.setAlignment(Pos.CENTER);
            waitLabel.setVisible(false);

            // 结束标签 - 与Preview保持一致
            endLabel = new Label("答题结束");
            endLabel.setPrefWidth(width);
            endLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateExX(32)));
            endLabel.setAlignment(Pos.CENTER);
            endLabel.setTextAlignment(TextAlignment.CENTER);
            endLabel.setVisible(false);

            finalScoreLabel = new Label();
            finalScoreLabel.setPrefWidth(width);
            finalScoreLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateExX(32)));
            finalScoreLabel.setTextAlignment(TextAlignment.CENTER);
            finalScoreLabel.setAlignment(Pos.CENTER);
            finalScoreLabel.setVisible(false);

            finalTimeLabel = new Label();
            finalTimeLabel.setPrefWidth(width);
            finalTimeLabel.setFont(FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateExX(32)));
            finalTimeLabel.setTextAlignment(TextAlignment.CENTER);
            finalTimeLabel.setAlignment(Pos.CENTER);
            finalTimeLabel.setVisible(false);

            downArea.getChildren().addAll(questionArea, answerArea, waitLabel, endLabel, finalScoreLabel, finalTimeLabel);
            mainPane.getChildren().add(downArea);
        }

        void updateLayout(double width, double height, boolean hideCircles, boolean hideDownArea) {
            mainPane.setPrefWidth(width);
            upArea.setPrefWidth(width);
            row1.setPrefWidth(width);
            row2.setPrefWidth(width);
            teamNameLabel.setPrefWidth(width * 0.2);
            scoreLabel.setPrefWidth(width * 0.2);
            timeLabel.setPrefWidth(width * 0.6);

            // 更新圆圈行的显示状态 - 与Preview保持一致
            row2.setVisible(!hideCircles);
            row2.setManaged(!hideCircles);

            // 精确计算圆圈区域高度 - 与Preview保持一致
            int circleRowHeight = 0;
            if (!hideCircles && circles.size() > 0) {
                // 计算每行能放多少个圆圈
                double circleWidth = 2 * ScreenAdapter.calculateExX(5) + 2; // 圆圈直径 + 间距
                int colsPerRow = Math.max(1, (int) ((width - 4) / circleWidth));
                // 计算需要多少行
                int rows = circles.size() / colsPerRow; // 向上取整
                // 每行的实际高度（包括圆圈直径和间距）
                double rowHeight = 2 * ScreenAdapter.calculateExX(5) + 2;
                circleRowHeight = (int) (rows * rowHeight);
            }
            // 上方蓝色区域高度 = 第一行高度(22) + 圆圈区域高度 + 内边距
            double upAreaHeight = ScreenAdapter.calculateExY(22 + circleRowHeight + (hideCircles ? 0 : 5));
            upArea.setPrefHeight(upAreaHeight);

            // 下方区域的位置和高度 - 与Preview保持一致
            downArea.setPrefWidth(width);
            double downAreaY = ScreenAdapter.calculateExY(10) + upAreaHeight;
            downArea.setLayoutY(downAreaY);
            double downAreaHeight = height - (downAreaY - ScreenAdapter.calculateExY(10));
            downArea.setPrefHeight(downAreaHeight);
            downArea.setVisible(!hideDownArea);
            downArea.setManaged(!hideDownArea);

            // 题目区域保持不变
            questionArea.setPrefWidth(width);

            // 答案区域 - 根据下方区域高度动态调整位置，与Preview保持一致
            answerArea.setPrefWidth(width);
            double answerAreaY = Math.min(ScreenAdapter.calculateExY(190), downAreaHeight * 0.75);
            answerArea.setLayoutY(answerAreaY);

            // 等待标签 - 填满整个下方区域，保持原字体大小，与Preview保持一致
            waitLabel.setPrefWidth(width);
            waitLabel.setPrefHeight(downAreaHeight);
            waitLabel.setLayoutY(0);

            // 结束相关标签 - 根据下方区域高度重新分布位置，与Preview保持一致
            double labelHeight = downAreaHeight / 3;

            endLabel.setPrefWidth(width);
            endLabel.setPrefHeight(labelHeight);
            endLabel.setLayoutY(0);

            finalScoreLabel.setPrefWidth(width);
            finalScoreLabel.setPrefHeight(labelHeight);
            finalScoreLabel.setLayoutY(labelHeight);

            finalTimeLabel.setPrefWidth(width);
            finalTimeLabel.setPrefHeight(labelHeight);
            finalTimeLabel.setLayoutY(labelHeight * 2);
        }
    }

    @Override
    public void setData(Stages data) {
        currentStage = data;
    }

    public void stopTiming() {
    }

    @Override
    public Node getPane() {
        return container;
    }

    @FXML
    public void initPage() {
        // 初始化时加载题型标注配置
        loadQuestionTypeConfig();

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
                    new Image(new FileInputStream(ImageResource.竞技模式背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        // 获取样式配置
        currentModel3Style = model3Mapper.selectById(2);
        cacheLayoutParameters();

        container.setPrefWidth(ExtendedScreen.getWidth());
        container.setVgap(cachedVgap);
        container.setHgap(cachedHgap);
        container.setPadding(new Insets(cachedY, 0, 0, cachedX));

        // 创建队伍面板
        createTeamPanels();

        uiInitialized = true;
    }

    /**
     * 缓存布局参数 - 与Preview保持一致
     */
    private void cacheLayoutParameters() {
        String titleStyle = currentModel3Style.getTitleStyle();

        // 默认值 - 与Preview保持一致
        cachedVgap = ScreenAdapter.calculateExY(20);
        cachedHgap = ScreenAdapter.calculateExX(20);
        cachedX = ScreenAdapter.calculateExX(10);
        cachedY = 0;
        cachedWidth = ScreenAdapter.calculateExX((int) ((ExtendedScreen.getWidth() - 40) / 4));
        cachedHeight = ScreenAdapter.calculateExY(287);
        cachedCol = 4;
        cachedWF = ScreenAdapter.calculateExX(12);
        cachedTH = ScreenAdapter.calculateExX(5);
        cachedQF = ScreenAdapter.calculateExX(12);
        cachedWFColor = Color.BLACK;
        cachedQFColor = Color.BLACK;
        cachedBkColor = Color.BLACK;

        if (!StringUtils.isEmpty(titleStyle)) {
            String[] split = titleStyle.split(ConfigSpliter.SPLITER);
            cachedCol = Integer.parseInt(split[0]);
            cachedX = ScreenAdapter.calculateExX(Integer.parseInt(split[5]));
            cachedY = ScreenAdapter.calculateExY(Integer.parseInt(split[6]));
            cachedWidth = ScreenAdapter.calculateExX(Integer.parseInt(split[3]));
            cachedHeight = ScreenAdapter.calculateExY(Integer.parseInt(split[4]));
            cachedVgap = ScreenAdapter.calculateExY(Integer.parseInt(split[1]));
            cachedHgap = ScreenAdapter.calculateExX(Integer.parseInt(split[2]));
            if (split.length > 7) {
                cachedWF = ScreenAdapter.calculateExX(Integer.parseInt(split[7]));
            }
            if (split.length > 8) {
                cachedTH = ScreenAdapter.calculateExX(Integer.parseInt(split[8]));
            }
            if (split.length > 9) {
                cachedQF = ScreenAdapter.calculateExX(Integer.parseInt(split[9]));
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
        }
    }

    /**
     * 创建队伍面板（只执行一次） - 与Preview保持一致
     */
    private void createTeamPanels() {
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<Questions> questions = getQuestionsWithCache();
        int cols = (int) ((cachedWidth - 4) / (2 * ScreenAdapter.calculateExX(cachedTH) + 1));

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
                    cachedTx = split[9];     // 题型
                    cachedTxbz = split[10];  // 题型标注
                }
            }
        } catch (Exception e) {
            System.err.println("加载题型标注配置失败: " + e.getMessage());
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
        if (Objects.equals(currentStage.getShowNumber(), 0)) {
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

    /**
     * 获取题目数据（带缓存）
     */
    private List<Questions> getQuestionsWithCache() {
        if (cachedQuestions == null || !Objects.equals(cachedStageNumber, currentStage.getSortVal())) {
            cachedQuestions = questionsMapper.selectList(
                    new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getStageNumber, currentStage.getSortVal())
            );
            cachedStageNumber = currentStage.getSortVal();
        }
        return cachedQuestions;
    }

    /**
     * 简单的防抖检查
     */
    private boolean shouldUpdate() {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastUpdateTime.get();
        if (currentTime - lastTime >= UPDATE_INTERVAL) {
            lastUpdateTime.set(currentTime);
            return true;
        }
        return false;
    }

    /**
     * 更新数据显示（只更新内容，不重新创建组件）
     */
    private void updateData() {
        // 获取样式配置
        String[] headStyleSplit = currentModel3Style.getHeadStyle().split(",");
        boolean hideCircles = headStyleSplit.length > 1 ? Boolean.parseBoolean(headStyleSplit[1]) : false;
        boolean hideDownArea = headStyleSplit.length > 0 ? Boolean.parseBoolean(headStyleSplit[0]) : false;

        // 更新每个队伍面板的数据
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<Teams> teams = TeamRuntime.getTeams();

        for (int i = 0; i < runtimeData.size() && i < teamPanels.size(); i++) {
            TeamDisplayPanel panel = teamPanels.get(i);
            List<Model7RuntimeData.ModelData> modelData = runtimeData.get(i);

            // 更新布局（如果样式改变）
            panel.updateLayout(cachedWidth, cachedHeight, hideCircles, hideDownArea);

            // 计算分数和状态
            int score = 0;
            int finish = 0;
            int correct = 0;
            Model7RuntimeData.ModelData last = null;

            for (int j = 0; j < modelData.size(); j++) {
                Model7RuntimeData.ModelData m = modelData.get(j);
                if (j < panel.circles.size()) {
                    Circle circle = panel.circles.get(j);
                    if (m.getUserSelect() == null) {
                        circle.setFill(Color.WHITE);
                    } else if (m.getUserSelect().equals(m.getAnswer())) {
                        finish++;
                        circle.setFill(Color.GREEN);
                        score += m.getScore();
                        correct++;
                    } else {
                        score -= controlPane.getCurrentStage().getDesValue();
                        finish++;
                        circle.setFill(Color.RED);
                    }
                }
                last = m;
            }

            // 重置剩余圆圈为白色
            for (int j = modelData.size(); j < panel.circles.size(); j++) {
                panel.circles.get(j).setFill(Color.WHITE);
            }

            // 补充未答题的分数扣除
            if (Model7RuntimeData.getFinished().get(i)) {
                score -= controlPane.getCurrentStage().getDesValue() * (Model7RuntimeData.totalNumber - modelData.size());
            }

            // 更新队伍信息
            panel.teamNameLabel.setText(teams.get(i).getShortName());
            panel.scoreLabel.setText(score + "分");

            String timeText = "完成" + finish + "题 答对" + correct + "题|" + timeValue;
            if (Model7RuntimeData.getFinished().get(i)) {
                timeText = "完成" + finish + "题 答对" + correct + "题|" + Model7RuntimeData.getFts().get(i);
            }
            panel.timeLabel.setText(timeText);

            // 更新下方区域内容
            boolean isFinished = Model7RuntimeData.getFinished().get(i);

            // 隐藏所有下方区域组件
            panel.questionArea.setVisible(false);
            panel.answerArea.setVisible(false);
            panel.waitLabel.setVisible(false);
            panel.endLabel.setVisible(false);
            panel.finalScoreLabel.setVisible(false);
            panel.finalTimeLabel.setVisible(false);

            if (isFinished) {
                // 显示完成状态
                panel.endLabel.setVisible(true);
                panel.finalScoreLabel.setText(score + "分");
                panel.finalScoreLabel.setVisible(true);
                panel.finalTimeLabel.setText(Model7RuntimeData.getFts().get(i));
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
                String[] choices = {last.getChoiceA(), last.getChoiceB(), last.getChoiceC(),
                        last.getChoiceD(), last.getChoiceE(), last.getChoiceF()};
                String[] choiceLabels = {"A", "B", "C", "D", "E", "F"};

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
                    panel.answerLabel.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
                    panel.answerLabel.setVisible(true);

                    if (last.getAnswer().equals(last.getUserSelect())) {
                        panel.statusLabel.setText("正确");
                        panel.statusLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
                    } else {
                        panel.statusLabel.setText("错误");
                        panel.statusLabel.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
                    }
                    panel.statusLabel.setVisible(true);
                }
            }
        }
    }

    @Override
    public void reload() {
        // 简单的防抖检查
        if (shouldUpdate()) {
            // 确保UI已初始化
            if (!uiInitialized) {
                initializeUI();
            }
            updateData();
        }
    }

    @Override
    public void reload(int time, String timeTxt) {
        this.timeValue = timeTxt;
        // 简单的防抖检查
        if (shouldUpdate()) {
            // 确保UI已初始化
            if (!uiInitialized) {
                initializeUI();
            }
            updateData();
        }
    }

    /**
     * 清理缓存的方法
     */
    public void clearCache() {
        cachedStageNumber = null;
        cachedQuestions = new ArrayList<>();
        // 清理UI组件缓存
        teamPanels.clear();
        if (container != null) {
            container.getChildren().clear();
            uiInitialized = false;

        }
    }

    @Override
    public void destroy() {
        teamPanels.clear();
        container.getChildren().clear();
        uiInitialized = false;
    }

    /**
     * 重新加载样式时同时重新加载题型标注配置
     */
    public void reloadStyle() {
        // 重新加载题型标注配置
        loadQuestionTypeConfig();

        // 重新加载样式配置
        currentModel3Style = model3Mapper.selectById(2);
        cacheLayoutParameters();

        // 如果UI已初始化，需要重新创建（因为布局可能改变）
        if (uiInitialized) {
            clearCache();
            initializeUI();
        }

        reload();
    }
}