package competition.controller.preview;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.RollPreview;
import competition.controller.match.StageQuestionPreviewAttr;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.QuestionsMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// 滚动选题
@Component
public class RollPreviewPreview extends PreviewPane<Stages> {

    @FXML
    private AnchorPane container;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ControlPane controlPane;


    @FXML
    private StackPane backPane, title;

    @FXML
    private Text questionText, stageName;

    @Resource
    private WsMessageHandler messageHandler;

    private AnimationTimer timer;

    private Stages inner;
    
    @Resource
    private QuestionsMapper questionsMapper;


    @Override
    public void setData(Stages data) {
        this.inner = data;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }

    @FXML
    public void initPage() {
        GlobalCache.isRolling = false;
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        PreviewStack.addPreview(this);

        backPane.setOnMouseClicked(event -> {
            event.consume();
            if (timer != null) {
                return;
            }
            String text = questionText.getText();
            if (!StringUtils.isEmpty(text)) {
                // 进入题目界面
                controlPane.showQuestion();
            }
        });

        container.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                StageQuestionPreviewAttr.setCurrentStage(inner);
                Navigator.navigatorTo("/view/match/StageQuestionPreviewAttr.fxml", "", "/view/style.css", false, false);
            } else if (event.getButton() == MouseButton.PRIMARY) {
                if (!GlobalCache.isRolling) {
                    GlobalCache.isRolling = true;
                    // 选题
                    startSelector();
                } else {
                    stopSelector();
                    GlobalCache.isRolling = false;
                }
            }
        });
        questionText.setText("");
        showData();
    }

    private void showData() {
        messageHandler.notifyPage("roll", null);
        if (ExtendedScreen.hasShow()) {
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/RollPreview.fxml", inner);
            if (res != null) {
                controlPane.setQuestionPreview((RollPreview) res);
            }
        }
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.滚动抽题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }


        // 未选择阶段，不展示
        if (inner == null) {
            return;
        }

        // 选题字色的配置
        Configs configs = configsMapper.selectById(4);
        // 环节字体颜色的配置
        Configs stageConfig = configsMapper.selectById(2);
        String config = configs.getConfig();
        String stageConfigAttr = stageConfig.getConfig();
        String[] stageConfigAttrSplit = stageConfigAttr.split(ConfigSpliter.SPLITER);
        List<Questions> questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                .eq(Questions::getStageNumber, inner.getSortVal())
                .orderByAsc(Questions::getQuestionNumber)
        );
//        questionList.sort(new Comparator<Questions>() {
//            @Override
//            public int compare(Questions o1, Questions o2) {
//                return o1.getQuestionNumber().compareTo(o2.getQuestionNumber());
//            }
//        });
        String questionStyle = "560%%240%%800%%100%%false%%0x990000ff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0###920%%400%%80%%80%%false%%0x990000ff%%%%默认.ttf%%36%%false%%true%%false%%false%%居中对齐%%0%%1";
        if (!StringUtils.isEmpty(inner.getQuestionStyle())) {
            questionStyle = inner.getQuestionStyle();
        }

        String[] styleArr = questionStyle.split(ConfigSpliter.DIFFER);

        // 标题名称
        stageName.setText(inner.getName());
        // 环节标题的字体颜色和背景色
        stageName.setFill(Color.valueOf(stageConfigAttrSplit[2]));
        title.setBackground(new Background(new BackgroundFill(Color.valueOf(stageConfigAttrSplit[3]), new CornerRadii(10), Insets.EMPTY)));
        title.setLayoutX(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_X));
        title.setLayoutY(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_Y));
        title.setPrefWidth(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_W));
        title.setPrefHeight(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_H));
        stageName.setLayoutX(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_X));
        stageName.setLayoutY(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_Y));
        stageName.prefWidth(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_W));
        stageName.prefHeight(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_H));


        if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[0])) {
            String[] detailArr = styleArr[0].split(ConfigSpliter.SPLITER);
            title.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[0])));
            title.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[1])));
            title.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])));
            title.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])));

            if (Boolean.valueOf(detailArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(detailArr[6])) {
                    try {
                        title.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])), ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])), false, false, false, false))));
                    } catch (FileNotFoundException e) {
                        title.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    title.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                // 填充背景颜色
                title.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
            }

            stageName.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[0])));
            stageName.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[1])));
            stageName.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])));
            stageName.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])));
            stageName.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
            if (detailArr.length >= 16) {
                stageName.setFill(Color.valueOf(detailArr[15]));
            }
            title.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
            Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[8])));
            stageName.setFont(font);
            if (Boolean.valueOf(detailArr[10])) {
                stageName.setStroke(Color.valueOf(stageConfigAttrSplit[2]));
                stageName.setStrokeWidth(1);
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

        // 以后台配置为准

        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        int col = 1;
        if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[1])) {
            String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);
            if (detailArr.length >= 16) {
                col = Integer.parseInt(detailArr[15]);
            }
        }


        backPane.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[5])), ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[6])));

        if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[1])) {
            String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);


            backPane.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])), ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])));
            backPane.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
            backPane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[0])));
            backPane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[1])));
            if (Boolean.valueOf(detailArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(detailArr[6])) {
                    try {
                        backPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])), ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])), false, false, false, false))));
                    } catch (FileNotFoundException e) {
                        backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                                new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                            new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                // 填充背景颜色
                backPane.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
            }


            Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[8])));;
            
            questionText.setFont(font);
            questionText.setFill(Color.valueOf(cfgArr[0]));
            if (Boolean.valueOf(detailArr[10])) {
                questionText.setStroke(Color.valueOf(cfgArr[0]));
                questionText.setStrokeWidth(1);
            } else {
                questionText.setStroke(null);

            }
            questionText.getTransforms().clear();
            if (Boolean.valueOf(detailArr[11])) {
                questionText.getTransforms().add(new Shear(-0.3, 0));
            }
            questionText.setUnderline(Boolean.valueOf(detailArr[12]));
            questionText.setStrikethrough(Boolean.valueOf(detailArr[9]));
        }

//                backPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                        @Override
//                        public void handle(MouseEvent event) {
//                            if (event.getButton() != MouseButton.PRIMARY) {
//                                return;
//                            }
//                            // 给题目设置分值
//                            // 扩展屏幕展示并排答题界面（竞技模式）
//                            for (Questions q : CurrentQuestions.getQuestionList()) {
//                                q.setScore(currentStage.getScorePerQ());
//                            }
//                            String tihao = questions.getQuestionNumber().toString();
//                            Questions questions = CurrentQuestions.getQ(tihao);
//                            CurrentQuestions.recordAnswer(tihao);
//                            // 设置当前需要显示的题目
//                            // 内屏更新
//                            QuestionPreviewPreview.setCurrent(questions);
//                            controlPane.setPreviewPane(
//                                    Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true)
//                            );
//                            // 控制面板选择题号
//                            controlPane.selectQuestion(tihao);
//    
//                        }
//                    });


    }

    public void stopSelector() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        String selectedVal = questionText.getText();
        if (StringUtils.isEmpty(selectedVal)) {
            return;
        }
        questionText.setText(selectedVal);
        controlPane.selectQuestion(selectedVal);
    }

    private void startSelector() {
        List<Questions> questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                .eq(Questions::getStageNumber, inner.getSortVal())
                .eq(Questions::isHasAnswer, false)
        );
        // 无可选择的题目
        if (questionList.size() <= 0) {
            return;
        }
        RollPreview rollPreview = (RollPreview) controlPane.getQuestionPreview();
        backPane.setVisible(true);
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                int randomNumber = (int) (Math.random() * questionList.size());
                String selectedNo = questionList.get(randomNumber).getQuestionNumber().toString();
                questionText.setText(selectedNo);
                messageHandler.notifyPage("roll", selectedNo);
                if (rollPreview != null) {
                    rollPreview.reload(0, selectedNo);
                }
            }
        };

        // 每秒更新数字
        timer.start();
    }


    @Override
    public Stages getData() {
        return inner;
    }

    @Override
    public void reload() {
        showData();
    }

    @Override
    public void reloadStyle() {
        this.reload();
    }
}
