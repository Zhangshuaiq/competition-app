package competition.controller.preview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.QuestionPreview;
import competition.controller.match.QuestionPreviewAttr;
import competition.controller.match.ReloadPane;
import competition.entity.*;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Shear;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 试题显示主控屏幕 - 优化版本
@Component
public class QuestionPreviewPreview extends PreviewPane<Questions> {

    // 字体缓存，避免重复加载
    private static final Map<String, Font> FONT_CACHE = new ConcurrentHashMap<>();
    private final  String BLACK_LINE = "=======================================================";

    // UI组件复用
    private Text choiceAText, choiceBText, choiceCText, choiceDText, choiceEText, choiceFText;
    private Image currentQuestionImage, currentAnswerImage, currentBackgroundImage;
    private boolean componentsInitialized = false;

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
    public ScrollPane scrollPane;

    @FXML
    private Text tiganLabel;

    @FXML
    private ImageView imageQ;

    @Resource
    private WsMessageHandler messageHandler;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private StagesMapper stagesMapper;

    private Questions inner;

    private EventHandler<MouseEvent> mouseClickHandler;


    @Override
    public AnchorPane getPane() {
        return container;
    }

    public void setData(Questions inner) {
        this.inner = inner;
    }

    /**
     * 初始化可复用的UI组件
     */
    private void initializeReusableComponents() {
        if (componentsInitialized) {
            return;
        }

        // 初始化选项Text组件
        choiceAText = createChoiceText();
        choiceBText = createChoiceText();
        choiceCText = createChoiceText();
        choiceDText = createChoiceText();
        choiceEText = createChoiceText();
        choiceFText = createChoiceText();

        componentsInitialized = true;
    }

    /**
     * 创建选项Text组件
     */
    private Text createChoiceText() {
        Text text = new Text();
        text.setVisible(false); // 默认隐藏
        return text;
    }

    /**
     * 获取缓存的字体
     */
    private Font getCachedFont(String fontName, double size) {
        String key = fontName + "_" + size;
        return FONT_CACHE.computeIfAbsent(key, k -> FontMap.load(fontName, size));
    }

    /**
     * 安全地设置图片
     */
    private void setImageSafely(ImageView imageView, String imagePath, double width, double height) {
        try {
            // 释放旧图片
            File file = new File(imagePath);
            if (file.exists()) {
                if (imageView == imageQ) {
                    if (currentQuestionImage != null) {
                        currentQuestionImage = null;
                    }
                    currentQuestionImage = new Image(new FileInputStream(file), width, height, false, true);
                    imageView.setImage(currentQuestionImage);
                }
            } else {
               throw new RuntimeException();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            imageView.setImage(null);
        }
    }

    /**
     * 安全地设置背景图片
     */
    private void setBackgroundImageSafely(Region region, String imagePath, double width, double height) {
        try {
            // 释放旧背景图片
            if (currentBackgroundImage != null) {
                currentBackgroundImage = null;
            }
            currentBackgroundImage = new Image(new FileInputStream(imagePath));
            region.setBackground(new Background(new BackgroundImage(
                    currentBackgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT, new BackgroundSize(width, height, false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            region.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                    new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
        }
    }

    @FXML
    public void initPage() {
        container.toBack();
        backPane.setVisible(GlobalCache.showAnswer);
        // 初始化可复用组件
        initializeReusableComponents();

        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }

        // 配置ScrollPane
        configureScrollPane();
        PreviewStack.addPreview(this);

        if (inner != null) {
            String stageNumber = inner.getStageNumber();
            Stages belongStage = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getSortVal, stageNumber)
            );
            if (belongStage != null) {
                inner.setShowNumber(belongStage.getShowNumber());
            }
        }

        // 设置鼠标事件处理器
        setupMouseHandler();

        showData();
    }

    /**
     * 配置ScrollPane
     */
    private void configureScrollPane() {
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() != 0) {
                event.consume();
            }
        });

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: rgba(0, 0, 0, 0); -fx-background-color: transparent; " +
                "-fx-border-color: transparent; -fx-border-radius: 10px; -fx-border-width: 0; " +
                "-fx-background-radius: 10px;");

        scrollPane.toFront();
        tiganBox.toFront();
        backPane.toFront();
    }

    /**
     * 设置鼠标事件处理器
     */
    private void setupMouseHandler() {
        mouseClickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    QuestionPreviewAttr.setQuestions(inner);
                    Navigator.navigatorTo("/view/match/QuestionPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        };
        container.setOnMouseClicked(mouseClickHandler);
    }

    @Override
    public void scrollDown() {
        scrollPane.setVvalue(scrollPane.getVvalue() + 0.1);
        if (ExtendedScreen.hasShow()) {
            ReloadPane questionPreview = controlPane.getQuestionPreview();
            if (questionPreview != null) {
                questionPreview.scrollDown();
            }
        }
    }

    @Override
    public void scrollDownAnswer() {
        // 实现答案向下滚动逻辑
    }

    @Override
    public void scrollUpAnswer() {
        // 实现答案向上滚动逻辑
    }

    @Override
    public void scrollUp() {
        scrollPane.setVvalue(scrollPane.getVvalue() - 0.1);
        if (ExtendedScreen.hasShow()) {
            ReloadPane questionPreview = controlPane.getQuestionPreview();
            if (questionPreview != null) {
                questionPreview.scrollUp();
            }
        }
    }

    /**
     * 优化的showData方法
     */
    private void showData() {
        if (inner == null) {
            return;
        }

        controlPane.setScore(inner);

        // 设置基本布局
        tiganBox.setPadding(new Insets(ScreenAdapter.calculateMainX(20)));
        backPane.setPadding(new Insets(ScreenAdapter.calculateExX(20), 0, ScreenAdapter.calculateExX(20), 0));

        Stages currentStage = controlPane.getCurrentStage();

        // 处理扩展屏幕
        handleExtendedScreen();

        // 处理自动计时
        handleAutoTiming(currentStage);

        // 设置背景
        setContainerBackground();

        // 清理旧的选项组件（但不重新创建）
        clearAndResetChoices();

        // 获取配置
        Configs configs = configsMapper.selectById(1);
        if (configs == null || StringUtils.isEmpty(configs.getConfig())) {
            return;
        }

        String cfgVal = configs.getConfig();
        String[] split = cfgVal.split(ConfigSpliter.SPLITER);

        // 应用基本样式
        applyBasicStyles(split);

        // 处理资源类型
        handleResourceType(split);

        // 处理题型和选项
        handleQuestionTypeAndChoices(split);

        // 设置基础答案信息（位置会被style2覆盖）
        setBasicAnswerInfo(split);

        // 处理阶段显示
        handleStageDisplay(split);

        // 处理样式（优先级最高，会覆盖前面的设置）
        handleStyles();

        // 配置滚动面板
        configureScrollPaneLayout();

        // 强制垃圾回收（谨慎使用）
        suggestGarbageCollection();
    }

    /**
     * 处理扩展屏幕
     */
    private void handleExtendedScreen() {
        if (ExtendedScreen.hasShow()) {
            Object res = ExtendedScreen.showWithControl("/view/match/QuestionPreview.fxml", inner);
            if (res != null) {
                QuestionPreview questionPreview = (QuestionPreview) res;
                controlPane.setQuestionPreview(questionPreview);
            }
        }
    }

    /**
     * 处理自动计时
     */
    private void handleAutoTiming(Stages currentStage) {
        if (currentStage != null && currentStage.getTimeCType() == 2) {
            controlPane.startTiming();
        }
    }

    /**
     * 设置容器背景
     */
    private void setContainerBackground() {
        try {
            setBackgroundImageSafely(container, ImageResource.答题背景(),
                    Navigator.getWidth(), Navigator.getHeight());
        } catch (Exception e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                    new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
        }
    }

    /**
     * 清理并重置选项组件
     */
    private void clearAndResetChoices() {
        // 从父容器中移除所有选项组件
        tiganBox.getChildren().removeIf(node ->
                node == choiceAText || node == choiceBText || node == choiceCText ||
                        node == choiceDText || node == choiceEText || node == choiceFText);

        // 重置选项组件状态
        resetChoiceText(choiceAText);
        resetChoiceText(choiceBText);
        resetChoiceText(choiceCText);
        resetChoiceText(choiceDText);
        resetChoiceText(choiceEText);
        resetChoiceText(choiceFText);

        // 确保题干标签在容器中
        if (!tiganBox.getChildren().contains(tiganLabel)) {
            tiganBox.getChildren().add(tiganLabel);
        }
    }

    /**
     * 重置选项Text组件
     */
    private void resetChoiceText(Text text) {
        text.setVisible(false);
        text.setText("");
        text.getTransforms().clear();
    }

    /**
     * 应用基本样式
     */
    private void applyBasicStyles(String[] split) {
        if (split.length < 8) return;

        // 题干颜色
        tiganLabel.setFill(Color.valueOf(split[0]));
        // 题目行距和宽度
        tiganLabel.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7]) - 20));
        tiganLabel.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
        tiganLabel.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7]) - 20));

        // 题目位置和大小
        tiganBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[5])));
        tiganBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[6])));
        tiganBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[7])));
        tiganBox.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[8])));
        tiganBox.setSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
    }

    /**
     * 处理资源类型
     */
    private void handleResourceType(String[] split) {
        String resourceType = inner.getResourceType();
        if (resourceType == null) {
            resourceType = "文字";
        }
        switch (resourceType) {
            case "音频":
            case "视频":
            case "文字":
                tiganLabel.setVisible(true);
                tiganBox.setVisible(true);
                imageQ.setVisible(false);
                break;
            case "图片":
                tiganLabel.setVisible(false);
                tiganBox.setVisible(false);
                imageQ.setVisible(true);
                imageQ.setLayoutX(0);
                imageQ.setLayoutY(0);
                imageQ.setFitWidth(Navigator.getWidth());
                imageQ.setFitHeight(Navigator.getHeight());
                setImageSafely(imageQ, BaseDir.TIKU_RES_DIR + inner.getQuestion(),
                        Navigator.getWidth(), Navigator.getHeight());
                break;
        }
    }

    /**
     * 处理题型和选项
     */
    private void handleQuestionTypeAndChoices(String[] split) {
        // 处理题型标注
        handleQuestionTypeLabel(split);

        String prefix = ".";
        if (split.length >= 43) {
            prefix = split[42];
        }
        // 处理选项
        String questionType = inner.getQuestionType();
        if (questionType.equals("单选题") || questionType.equals("多选题") || questionType.equals("不定项选择题")) {
            addChoicesWithLabels(prefix);
        } else {
            addChoicesWithoutLabels();
        }
    }

    /**
     * 处理题型标注
     */
    private void handleQuestionTypeLabel(String[] split) {
        if (split.length < 11) return;

        String tx = split[9];
        String txbz = split[10];
        inner.setTxbz(txbz);

        Stages currentStage = controlPane.getCurrentStage();
        if (currentStage != null && currentStage.getQuestionType() == 6) {
            if (currentStage.getM6() == 0) {
                messageHandler.notifyPage("model60", inner);
            } else {
                messageHandler.notifyPage("model61", inner);
            }
        } else {
            String stageNumber = inner.getStageNumber();
            Stages stages = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getSortVal, stageNumber).last(" limit 1"));

            if (stages != null) {
                if (currentStage != null && currentStage.getQuestionType() == 3) {
                    Stages pstage = stagesMapper.selectById(stages.getParentId());
                    if (pstage != null) {
//                        stages.setName(pstage.getName() + " " + stages.getName());
                        messageHandler.notifyPageWithStage("question", inner, stages);
                    } else {
                        messageHandler.notifyPage("question", inner);
                    }
                } else {
                    messageHandler.notifyPage("question", inner);
                }
            }

        }

        // 构建题目文本
        buildQuestionText(tx, txbz);
    }

    /**
     * 构建题目文本
     */
    private void buildQuestionText(String tx, String txbz) {
        String questionText = "";

        switch (txbz) {
            case "4": // 无题型标注
                questionText = inner.getOrder() + "、" + inner.getQuestion();
                break;
            case "3": // 直接标注
                questionText = buildDirectLabel(tx);
                break;
            case "2": // 括号标注
                questionText = buildBracketLabel(tx);
                break;
            case "1": // 方括号标注
                questionText = buildSquareBracketLabel(tx);
                break;
        }

        // 处理题号显示
        if (inner.getShowNumber() == 0) {
            questionText = questionText.replace(inner.getOrder() + "、", "");
        }

        // 处理未注册状态
        if (GlobalCache.MAX_TEAM_NUMBER <= 0) {
            questionText += "(未注册)";
        }

        tiganLabel.setText(questionText.replace("\u00A0", " "));
    }

    private String buildDirectLabel(String tx) {
        switch (tx) {
            case "3":
                return inner.getOrder() + "、" + inner.getQuestion() + " " + inner.getQuestionType();
            case "2":
                return inner.getOrder() + "、" + " " + inner.getQuestionType() + inner.getQuestion();
            case "1":
                return inner.getQuestionType() + "、" + " " + inner.getOrder() + inner.getQuestion();
            default:
                return inner.getOrder() + "、" + inner.getQuestion();
        }
    }

    private String buildBracketLabel(String tx) {
        switch (tx) {
            case "3":
                return inner.getOrder() + "、" + inner.getQuestion() + "(" + inner.getQuestionType() + ")";
            case "2":
                return inner.getOrder() + "、" + "(" + inner.getQuestionType() + ")" + inner.getQuestion();
            case "1":
                return "(" + inner.getQuestionType() + ")" + inner.getOrder() + "、" + inner.getQuestion();
            default:
                return inner.getOrder() + "、" + inner.getQuestion();
        }
    }

    private String buildSquareBracketLabel(String tx) {
        switch (tx) {
            case "3":
                return inner.getOrder() + "、" + inner.getQuestion() + "[" + inner.getQuestionType() + "]";
            case "2":
                return inner.getOrder() + "、" + "[" + inner.getQuestionType() + "]" + inner.getQuestion();
            case "1":
                return "[" + inner.getQuestionType() + "]" + inner.getOrder() + "、" + inner.getQuestion();
            default:
                return inner.getOrder() + "、" + inner.getQuestion();
        }
    }

    /**
     * 添加带标签的选项（A、B、C、D...）
     */
    private void addChoicesWithLabels(String prefix) {
        addChoiceIfNotEmpty(choiceAText, "A" + prefix + inner.getChoiceA(), inner.getChoiceA());
        addChoiceIfNotEmpty(choiceBText, "B" + prefix +inner.getChoiceB(), inner.getChoiceB());
        addChoiceIfNotEmpty(choiceCText, "C" + prefix + inner.getChoiceC(), inner.getChoiceC());
        addChoiceIfNotEmpty(choiceDText, "D" + prefix + inner.getChoiceD(), inner.getChoiceD());
        addChoiceIfNotEmpty(choiceEText, "E" + prefix + inner.getChoiceE(), inner.getChoiceE());
        addChoiceIfNotEmpty(choiceFText, "F" + prefix + inner.getChoiceF(), inner.getChoiceF());
    }

    /**
     * 添加不带标签的选项
     */
    private void addChoicesWithoutLabels() {
        addChoiceIfNotEmpty(choiceAText, inner.getChoiceA(), inner.getChoiceA());
        addChoiceIfNotEmpty(choiceBText, inner.getChoiceB(), inner.getChoiceB());
        addChoiceIfNotEmpty(choiceCText, inner.getChoiceC(), inner.getChoiceC());
        addChoiceIfNotEmpty(choiceDText, inner.getChoiceD(), inner.getChoiceD());
        addChoiceIfNotEmpty(choiceEText, inner.getChoiceE(), inner.getChoiceE());
        addChoiceIfNotEmpty(choiceFText, inner.getChoiceF(), inner.getChoiceF());
    }

    /**
     * 如果选项内容不为空则添加选项
     */
    private void addChoiceIfNotEmpty(Text choiceText, String displayText, String choiceContent) {
        if (!StringUtils.isEmpty(choiceContent)) {
            choiceText.setText(displayText);
            choiceText.setVisible(true);
            if (!tiganBox.getChildren().contains(choiceText)) {
                tiganBox.getChildren().add(choiceText);
            }
        } else {
            choiceText.setVisible(false);
        }
    }

    /**
     * 设置基础答案信息（会被style2覆盖）
     */
    private void setBasicAnswerInfo(String[] split) {
        // 设置基础答案文本
        if (split.length > 4) {
            daanLabel.setText(split[4] + " " + inner.getAnswer());

            // 设置基础位置（会被style2覆盖）
            backPane.setLayoutX(tiganBox.getLayoutX());
            backPane.setLayoutY(tiganBox.getLayoutY() + tiganBox.getPrefHeight() + ScreenAdapter.calculateMainY(10));

            if (split.length > 1) {
                daanLabel.setFill(Color.valueOf(split[1]));
            }
        }
    }

    /**
     * 处理阶段显示
     */
    private void handleStageDisplay(String[] split) {
        if (split.length < 13) return;

        stagePane.setVisible(Boolean.valueOf(split[12]));

        if (stagePane.isVisible()) {
            String stageNumber = inner.getStageNumber();
            Stages stages = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getSortVal, stageNumber).last(" limit 1"));

            if (stages != null) {
                Stages currentStage = controlPane.getCurrentStage();
                if (currentStage != null && currentStage.getQuestionType() == 3) {
                    Stages pstage = stagesMapper.selectById(stages.getParentId());
                    if (pstage != null) {
                        stageLabel.setText(pstage.getName() + " " + stages.getName());
                    } else {
                        stageLabel.setText(stages.getName());
                    }
                } else {
                    stageLabel.setText(stages.getName());
                }

                if (split.length > 11) {
                    stageLabel.setFill(Color.valueOf(split[11]));
                }
            }
        }
    }

    /**
     * 处理样式（优先级最高）
     */
    private void handleStyles() {
        // 获取样式配置
        String style1 = "200%%200%%1550%%200%%false%%0xffffffff%%%%默认.ttf%%30%%false%%false%%false%%false%%左对齐%%10%%0";
        String style2 = "200%%700%%1550%%60%%false%%0xffffffff%%%%默认.ttf%%36%%false%%false%%false%%false%%居中对齐%%0";
        String style3 = "2%%2%%300%%100%%false%%0xffffffff%%%%默认.ttf%%24%%false%%false%%false%%false%%居中对齐%%";

        List<QuestionStyle> parse = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);
        if (!parse.isEmpty() && inner.getSort() < parse.size()) {
            QuestionStyle questionStyle = parse.get(inner.getSort());
            if (questionStyle != null) {
                if (!StringUtils.isEmpty(questionStyle.getStyle1())) {
                    style1 = questionStyle.getStyle1();
                }
                if (!StringUtils.isEmpty(questionStyle.getStyle2())) {
                    style2 = questionStyle.getStyle2();
                }
                if (!StringUtils.isEmpty(questionStyle.getStyle3())) {
                    style3 = questionStyle.getStyle3();
                }
            }
        }

        // 应用样式（覆盖基础设置）
        applyStyle1(style1);
        applyStyle2(style2);
        applyStyle3(style3);
    }

    /**
     * 应用样式1（题干样式）- 优先级最高，覆盖基础设置
     */
    private void applyStyle1(String style1) {
        String[] split1 = style1.split(ConfigSpliter.SPLITER);
        if (split1.length < 15) return;

        // style1的位置和尺寸设置（覆盖基础设置）
        tiganBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split1[0])));
        tiganBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split1[1])));
        tiganBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
        tiganBox.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));

        // 设置背景（覆盖基础背景）
        setRegionBackground(tiganBox, split1);

        // 设置间距和字体（覆盖基础设置）
        tiganBox.setSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));
        Font font = getCachedFont(split1[7], ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
        //Font font = Font.font(ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
        // 应用字体样式到所有文本组件（覆盖基础样式）
        applyFontStyleToAllTexts(font, split1);
        // 处理空行
        if (split1.length > 16) {
            if (Boolean.parseBoolean(split1[16])) {
                addBlankLine(font);
            } else {
                // 如果不需要空行，确保没有多余的空行
                tiganBox.getChildren().removeIf(node -> node instanceof Text && ((Text) node).getText().equals(BLACK_LINE));
            }
        } 
    }


    /**
     * 设置区域背景
     */
    private void setRegionBackground(Region region, String[] split1) {
        if (split1.length < 7) return;

        if (Boolean.valueOf(split1[4])) {
            // 背景图片
            if (!StringUtils.isEmpty(split1[6])) {
                setBackgroundImageSafely(region, split1[6],
                        ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])),
                        ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));
            } else {
                region.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                        new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
            }
        } else {
            // 背景颜色
            region.setBackground(new Background(new BackgroundFill(Color.valueOf(split1[5]),
                    new CornerRadii(ScreenAdapter.calculateMainX(10)), Insets.EMPTY)));
        }
    }

    /**
     * 应用字体样式到所有文本组件
     */
    private void applyFontStyleToAllTexts(Font font, String[] split1) {
        // 应用到题干标签
        applyTextStyle(tiganLabel, font, split1, tiganLabel.getFill());

        // 应用到所有选项
        applyTextStyle(choiceAText, font, split1, tiganLabel.getFill());
        applyTextStyle(choiceBText, font, split1, tiganLabel.getFill());
        applyTextStyle(choiceCText, font, split1, tiganLabel.getFill());
        applyTextStyle(choiceDText, font, split1, tiganLabel.getFill());
        applyTextStyle(choiceEText, font, split1, tiganLabel.getFill());
        applyTextStyle(choiceFText, font, split1, tiganLabel.getFill());

        // 设置包装宽度
        double wrapWidth = ScreenAdapter.calculateMainX(Integer.parseInt(split1[2]));
        setWrappingWidthForAllTexts(wrapWidth);
    }

    /**
     * 应用文本样式
     */
    private void applyTextStyle(Text text, Font font, String[] split1, Paint color) {
        if (split1.length < 15) return;

        text.setFont(font);
        text.setUnderline(Boolean.valueOf(split1[12]));
        text.setStrikethrough(Boolean.valueOf(split1[9]));
        text.setFill(color);
        if (Boolean.valueOf(split1[10])) {
            text.setStroke(text.getFill());
            text.setStrokeWidth(1);
        } else {
            text.setStroke(null);
        }

        text.getTransforms().clear();
        if (Boolean.valueOf(split1[11])) {
            text.getTransforms().add(new Shear(-0.3, 0));
        }

        text.setTextAlignment(AlignmentUtil.getTextAlign(split1[13]));
        text.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));
    }

    /**
     * 设置所有文本的包装宽度
     */
    private void setWrappingWidthForAllTexts(double width) {
        tiganLabel.setWrappingWidth(width);
        choiceAText.setWrappingWidth(width);
        choiceBText.setWrappingWidth(width);
        choiceCText.setWrappingWidth(width);
        choiceDText.setWrappingWidth(width);
        choiceEText.setWrappingWidth(width);
        choiceFText.setWrappingWidth(width);
    }

    /**
     * 添加空行
     */
    private void addBlankLine(Font font) {
        Text blankLine = new Text(BLACK_LINE);
        blankLine.setFont(font);
        blankLine.setVisible(false);
        if (tiganBox.getChildren().size() > 1 && tiganBox.getChildren().get(1) instanceof Text) {
            Text firstChild = (Text) tiganBox.getChildren().get(1);
            if (!firstChild.getText().equals(BLACK_LINE)) {
                tiganBox.getChildren().add(1, blankLine);
            }
        }
    }

    /**
     * 应用样式2（答案样式）- 优先级最高，覆盖基础答案设置
     */
    private void applyStyle2(String style2) {
        String[] split1 = style2.split(ConfigSpliter.SPLITER);
        if (split1.length < 15) return;

        // style2的位置和尺寸设置（覆盖基础答案位置）
        backPane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split1[0])));
        backPane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split1[1])));
        backPane.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
        backPane.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));

        // 设置答案背景（覆盖基础背景）
        setRegionBackground(backPane, split1);

        // 设置答案文本样式（覆盖基础样式）
        daanLabel.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2]) - 40));
        daanLabel.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));

        Font font = getCachedFont(split1[7], ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
        applyTextStyle(daanLabel, font, split1, daanLabel.getFill());
    }

    /**
     * 应用样式3（阶段样式）- 优先级最高，覆盖基础阶段设置
     */
    private void applyStyle3(String style3) {
        if (StringUtils.isEmpty(style3)) return;

        String[] split1 = style3.split(ConfigSpliter.SPLITER);
        if (split1.length < 14) return;

        // style3的位置和尺寸设置（覆盖基础设置）
        stagePane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split1[0])));
        stagePane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split1[1])));
        stagePane.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
        stagePane.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split1[3])));

        // 设置阶段背景（覆盖基础背景）
        setRegionBackground(stagePane, split1);

        // 设置阶段文本样式（覆盖基础样式）
        stageLabel.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split1[2])));
        if (split1.length >= 15) {
            stageLabel.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(split1[14])));
        }

        Font font = getCachedFont(split1[7], ScreenAdapter.calculateMainX(Integer.parseInt(split1[8])));
        applyTextStyle(stageLabel, font, split1, stageLabel.getFill());
    }

    /**
     * 配置滚动面板布局
     */
    private void configureScrollPaneLayout() {
        scrollPane.setLayoutX(tiganBox.getLayoutX());
        scrollPane.setLayoutY(tiganBox.getLayoutY());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // 为所有文本设置包装宽度
        double wrapWidth = tiganBox.getPrefWidth() - ScreenAdapter.calculateMainX(40);
        for (Node child : tiganBox.getChildren()) {
            if (child instanceof Text) {
                ((Text) child).setWrappingWidth(wrapWidth);
            }
        }
    }

    /**
     * 建议进行垃圾回收（谨慎使用）
     */
    private void suggestGarbageCollection() {
        // 只在内存使用率较高时建议GC
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double usage = (double) usedMemory / maxMemory;

        if (usage > 0.7) { // 超过70%时建议GC
            System.gc();
        }
    }

    /**
     * 显示答案
     */
    public void showAnswer() {
        if (inner == null) return;
        if (StringUtils.isEmpty(inner.getResourceType())) {
            inner.setResourceType("图片");
        }

        if (inner.getResourceType().equals("图片")) {
            imageQ.setVisible(true);
            imageQ.setLayoutX(0);
            imageQ.setLayoutY(0);
            imageQ.setFitHeight(Navigator.getHeight());
            imageQ.setFitWidth(Navigator.getWidth());

            try {
                String imagePath = GlobalCache.showAnswer ?
                        BaseDir.TIKU_RES_DIR + inner.getAnswer() :
                        BaseDir.TIKU_RES_DIR + inner.getQuestion();
                setImageSafely(imageQ, imagePath, Navigator.getWidth(), Navigator.getHeight());
                backPane.setVisible(false);
            } catch (Exception e) {
                backPane.setVisible(GlobalCache.showAnswer);
            }
        } else {
            backPane.setVisible(GlobalCache.showAnswer);
        }

        ReloadPane questionPreview = controlPane.getQuestionPreview();
        if (ExtendedScreen.hasShow() && questionPreview != null) {
            questionPreview.showAnswer();
        }
    }

    @Override
    public Questions getData() {
        return inner;
    }

    @Override
    public void reloadStyle() {
        this.reload();
        controlPane.timeReset();
    }

    @Override
    public void reload() {
        showData();

        if (GlobalCache.showAnswer) {
            if (inner != null && inner.getResourceType().equals("图片")) {
                imageQ.setVisible(true);
                imageQ.setLayoutX(0);
                imageQ.setLayoutY(0);
                imageQ.setFitHeight(Navigator.getHeight());
                imageQ.setFitWidth(Navigator.getWidth());

                try {
                    setImageSafely(imageQ, BaseDir.TIKU_RES_DIR + inner.getAnswer(),
                            Navigator.getWidth(), Navigator.getHeight());
                    backPane.setVisible(false);
                } catch (Exception e) {
                    backPane.setVisible(true);
                    imageQ.setVisible(false);
                }
            } else {
                backPane.setVisible(true);
                imageQ.setVisible(false);
            }

            ReloadPane questionPreview = controlPane.getQuestionPreview();
            if (ExtendedScreen.hasShow() && questionPreview != null) {
                questionPreview.showAnswer();
            }
        } else {
            backPane.setVisible(false);
        }
    }

    /**
     * 清理资源的方法 - 在组件销毁时调用
     */
    public void destroy() {
        try {
            // 清理事件监听器
            if (container != null && mouseClickHandler != null) {
                container.setOnMouseClicked(null);
                mouseClickHandler = null;
            }

            // 清理图片资源
            releaseImageResources();

            // 清理UI组件引用
            clearUIComponents();

            // 清理对象引用
            inner = null;

            // 从PreviewStack中移除
            // 建议垃圾回收
            System.gc();

        } catch (Exception e) {
            // 记录清理过程中的异常，但不抛出
            e.printStackTrace();
        }
    }

    /**
     * 释放图片资源
     */
    private void releaseImageResources() {
        if (currentQuestionImage != null) {
            currentQuestionImage = null;
        }
        if (currentAnswerImage != null) {
            currentAnswerImage = null;
        }
        if (currentBackgroundImage != null) {
            currentBackgroundImage = null;
        }
        if (imageQ != null) {
            imageQ.setImage(null);
        }
    }

    /**
     * 清理UI组件
     */
    private void clearUIComponents() {
        if (tiganBox != null) {
            tiganBox.getChildren().clear();
        }

        // 重置文本组件
        if (choiceAText != null) choiceAText.setText("");
        if (choiceBText != null) choiceBText.setText("");
        if (choiceCText != null) choiceCText.setText("");
        if (choiceDText != null) choiceDText.setText("");
        if (choiceEText != null) choiceEText.setText("");
        if (choiceFText != null) choiceFText.setText("");
        if (tiganLabel != null) tiganLabel.setText("");
        if (daanLabel != null) daanLabel.setText("");
        if (stageLabel != null) stageLabel.setText("");
    }
}