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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

// 竞技模式显示控制
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
        reload();
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

    private void showData() {
        try {
            container.setBackground(new Background(new BackgroundImage(
                new Image(new FileInputStream(ImageResource.竞技模式背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        Model3Style model3Style = model3Mapper.selectById(2);
        String titleStyle = model3Style.getTitleStyle();

        double vgap = ScreenAdapter.calculateExY(20);
        double hgap = ScreenAdapter.calculateExX(20);
        double x = ScreenAdapter.calculateExX(10);
        double y = 0;
        double width = ((ExtendedScreen.getWidth() - ScreenAdapter.calculateExX(40)) / 4);
        double height = ScreenAdapter.calculateExY(287);
        int col = 4;

        if (!StringUtils.isEmpty(titleStyle)) {
            String[] split = titleStyle.split(ConfigSpliter.SPLITER);
            col = Integer.parseInt(split[0]);
            x = ScreenAdapter.calculateExX(Integer.parseInt(split[5]));
            y = ScreenAdapter.calculateExY(Integer.parseInt(split[6]));
            width = ScreenAdapter.calculateExX(Integer.parseInt(split[3]));
            height = ScreenAdapter.calculateExY(Integer.parseInt(split[4]));
            vgap = ScreenAdapter.calculateExY(Integer.parseInt(split[1]));
            hgap = ScreenAdapter.calculateExX(Integer.parseInt(split[2]));
        }
        container.setPadding(new Insets(y, 0, 0, x));
        container.getChildren().clear();
        container.setVgap(vgap);
        container.setHgap(hgap);

        // 使用缓存的questions，避免重复数据库查询
        List<Questions> questionsList = getQuestionsWithCache();

        // 添加
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<Teams> teams = TeamRuntime.getTeams();
        for (int i = 0; i < runtimeData.size(); i++) {
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPadding(new Insets(0, 10, 0, 10));
            anchorPane.setPrefWidth(width);
            anchorPane.setMinWidth(0);
            anchorPane.setMaxWidth(Double.MAX_VALUE); // 允许AnchorPane扩展以填充可用空间
            container.add(anchorPane, i % col, i / col);
            VBox upArea = new VBox();
            upArea.setMinWidth(0);
            upArea.setMaxWidth(Double.MAX_VALUE);
            upArea.setLayoutY(ScreenAdapter.calculateExY(10));
            anchorPane.getChildren().add(upArea);
            upArea.setPrefWidth(anchorPane.getPrefWidth());
            // 背景颜色
            upArea.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
            upArea.setPrefHeight(ScreenAdapter.calculateExY(22));

            // 第二行
            GridPane row2 = new GridPane();
            row2.setPadding(new Insets(2));
            row2.setPrefWidth(anchorPane.getPrefWidth());
            List<Model7RuntimeData.ModelData> modelData = runtimeData.get(i);
            double radius = ScreenAdapter.calculateExX(5.0);
            Model7RuntimeData.ModelData last = null;
            int score = 0;
            int finish = 0;
            int correct = 0;

            // 使用缓存的questionsList，而不是在循环中重复查询
            int cols = (int) ((anchorPane.getPrefWidth() - 4) / (2 * radius + 1));
            int finalRows = 0;
            for (int i1 = 0; i1 < questionsList.size(); i1++) {
                Circle c = new Circle();
                c.setStrokeWidth(1);
                c.setRadius(ScreenAdapter.calculateMainX(radius));
                c.setStroke(Color.BLACK);
                c.setFill(Color.WHITE);
                int colIndex = i1 % cols;
                int rowIndex = i1 / cols;
                finalRows = rowIndex;
                row2.add(c, colIndex, rowIndex );
            }
            finalRows ++;
            for (int i1 = 0; i1 < modelData.size(); i1++) {
                Model7RuntimeData.ModelData m = modelData.get(i1);
                if (m.getUserSelect() == null) {
                    ((Circle) (row2.getChildren().get(i1))).setFill(Color.WHITE);
                } else if (m.getUserSelect().equals(m.getAnswer())) {
                    finish++;
                    ((Circle) (row2.getChildren().get(i1))).setFill(Color.GREEN);
                    score += m.getScore();
                    correct ++;
                } else {
                    score -= controlPane.getCurrentStage().getDesValue();
                    finish++;
                    ((Circle) (row2.getChildren().get(i1))).setFill(Color.RED);
                }
                last = m;
            }
            // 补充未答题的分数扣除
            if (Model7RuntimeData.getFinished().get(i)) {
                score -= controlPane.getCurrentStage().getDesValue() * (Model7RuntimeData.totalNumber - modelData.size());
            }
            // 第一行
            HBox row1 = new HBox();
            row1.setPadding(new Insets(0, 3, 0, 3));
            row1.setPrefWidth(anchorPane.getPrefWidth());
            upArea.setSpacing(ScreenAdapter.calculateExY(5));
            Label label = new Label(teams.get(i).getShortName());
            label.setPrefWidth(anchorPane.getPrefWidth() / 4);
            label.setTextFill(Color.WHITE);
            Label scoreL = new Label(score + "分");
            scoreL.setPrefWidth(anchorPane.getPrefWidth() / 4);
            scoreL.setTextFill(Color.WHITE);
            Label timeL = new Label("完成" + finish + "题 答对" + correct +"题|" + timeValue);
            if (Model7RuntimeData.getFinished().get(i)) {
                timeL.setText("完成" + finish + "题 答对" + correct + "题|" + Model7RuntimeData.getFts().get(i));
            }
            timeL.setPrefWidth(anchorPane.getPrefWidth() / 2);
            timeL.setTextFill(Color.WHITE);

            row1.getChildren().addAll(label, scoreL, timeL);

            String[] split = model3Style.getHeadStyle().split(",");

            upArea.getChildren().addAll(row1);
            boolean hideCircles = true;
            if (split.length > 1) {
                if (!Boolean.parseBoolean(split[1])) {
                    upArea.getChildren().add(row2);
                    hideCircles = false;
                }
            }else {
                upArea.getChildren().add(row2);
                hideCircles = true;
            }
            int circleRowHeight = hideCircles ? 0 : finalRows * 15;
            upArea.setPrefHeight(ScreenAdapter.calculateExY(22 + circleRowHeight));

            AnchorPane downArea = new AnchorPane();
            downArea.setPrefWidth(anchorPane.getPrefWidth());
            downArea.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: white");

            downArea.setLayoutY(ScreenAdapter.calculateExY(upArea.getLayoutY() + upArea.getPrefHeight() + 5));
            double downAreaHeight = height - ScreenAdapter.calculateExY(22 + circleRowHeight);
            downArea.setPrefHeight(downAreaHeight);
            if (split.length > 0) {
                if (!Boolean.parseBoolean(split[0])) {
                    anchorPane.getChildren().add(downArea);
                }
            } else {
                anchorPane.getChildren().add(downArea);
            }

            // 回答完毕
            Font font = FontMap.load(FontMap.defaultFontName(), ScreenAdapter.calculateExX(32));
            if (Model7RuntimeData.getFinished().get(i)) {
                Label endLab = new Label("答题结束");
                endLab.setPrefWidth(downArea.getPrefWidth());
                endLab.setPrefHeight(downArea.getPrefHeight() / 5);
                endLab.setLayoutY(downArea.getPrefHeight() / 5);
                endLab.setFont(font);
                endLab.setAlignment(Pos.CENTER);
                endLab.setTextAlignment(TextAlignment.CENTER);

                Label scoreLab = new Label(score + "分");
                scoreLab.setPrefWidth(downArea.getPrefWidth());
                scoreLab.setPrefHeight(downArea.getPrefHeight() / 5);
                scoreLab.setLayoutY(downArea.getPrefHeight() * 0.4);
                scoreLab.setFont(font);
                scoreLab.setTextAlignment(TextAlignment.CENTER);
                scoreLab.setAlignment(Pos.CENTER);
                List<String> fts = Model7RuntimeData.getFts();
                Label timeLab = new Label(fts.get(i));
                timeLab.setPrefWidth(downArea.getPrefWidth());
                timeLab.setPrefHeight(downArea.getPrefHeight() / 5);
                timeLab.setLayoutY(downArea.getPrefHeight() * 0.6);
                timeLab.setFont(font);
                timeLab.setTextAlignment(TextAlignment.CENTER);
                timeLab.setAlignment(Pos.CENTER);
                downArea.getChildren().addAll(endLab, scoreLab, timeLab);
            } else {
                if (last == null) {
                    Label waitLab = new Label("等待答题");
                    waitLab.setPrefWidth(downArea.getPrefWidth());
                    waitLab.setPrefHeight(downArea.getPrefHeight());
                    waitLab.setFont(font);
                    waitLab.setTextAlignment(TextAlignment.CENTER);
                    waitLab.setAlignment(Pos.CENTER);
                    downArea.getChildren().add(waitLab);
                } else {
                    VBox vbx = new VBox();
                    // 设置边距
                    vbx.setPadding(new Insets(1, 5, 1, 5));
                    vbx.setSpacing(ScreenAdapter.calculateExY(2));
                    vbx.setPrefWidth(downArea.getPrefWidth());
                    downArea.getChildren().addAll(vbx);

                    // 使用题型标注格式化题目文本
                    Label q = new Label();
                    String formattedQuestionText = formatQuestionText(last);
                    q.setText(formattedQuestionText);
                    q.setWrapText(true);
                    vbx.getChildren().add(q);

                    if (!StringUtils.isEmpty(last.getChoiceA())) {
                        Label c = new Label("A. " + last.getChoiceA());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceB())) {
                        Label c = new Label("B. " + last.getChoiceB());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceC())) {
                        Label c = new Label("C. " + last.getChoiceC());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceD())) {
                        Label c = new Label("D. " + last.getChoiceD());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceE())) {
                        Label c = new Label("E. " + last.getChoiceE());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceF())) {
                        Label c = new Label("F. " + last.getChoiceF());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }

                    VBox answerArea = new VBox();
                    answerArea.setAlignment(Pos.CENTER);
                    answerArea.setLayoutY(ScreenAdapter.calculateExY(190));
                    answerArea.setPrefWidth(downArea.getPrefWidth());
                    downArea.getChildren().add(answerArea);
                    answerArea.setSpacing(ScreenAdapter.calculateExY(5));
                    if (last.getUserSelect() != null) {
                        Label select = new Label("回答:" + last.getUserSelect());
                        select.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
                        select.setPrefHeight(ScreenAdapter.calculateExY(30));
                        select.setTextAlignment(TextAlignment.CENTER);
                        select.setTextFill(Color.WHITE);
                        select.setPrefWidth(answerArea.getPrefWidth());
                        select.setAlignment(Pos.CENTER);
                        Label status = new Label();
                        if (last.getAnswer().equals(last.getUserSelect())) {
                            status.setText("正确");
                            status.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
                        } else {
                            status.setText("错误");
                            status.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
                        }
                        status.setPrefHeight(ScreenAdapter.calculateExY(30));
                        status.setAlignment(Pos.CENTER);
                        status.setTextAlignment(TextAlignment.CENTER);
                        status.setTextFill(Color.WHITE);
                        status.setPrefWidth(answerArea.getPrefWidth());

                        answerArea.getChildren().addAll(select, status);
                    }
                }
            }
        }
    }

    @Override
    public void reload() {
        // 简单的防抖检查
        if (shouldUpdate()) {
            showData();
        }
    }

    @Override
    public void reload(int time, String timeTxt) {
        this.timeValue = timeTxt;
        // 简单的防抖检查
        if (shouldUpdate()) {
            showData();
        }
    }

    /**
     * 清理缓存的方法
     */
    public void clearCache() {
        cachedQuestions = null;
        cachedStageNumber = null;
    }

    /**
     * 重新加载样式时同时重新加载题型标注配置
     */
    public void reloadStyle() {
        // 重新加载题型标注配置
        loadQuestionTypeConfig();
        reload();
    }
}