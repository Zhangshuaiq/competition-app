package competition.controller.preview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.*;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.QuestionsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StageNamePreviewPreview extends PreviewPane<Integer> {

    @Resource
    private ControlPane controlPane;

    @FXML
    private AnchorPane container;

    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;
    
    @Resource
    private QuestionsMapper questionsMapper;
    
    @Resource
    private WsMessageHandler messageHandler;

    private Integer innerStageId;
    
    private Stages stages;


    @Override
    public void setData(Integer data) {
        this.innerStageId = data;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
    
    public void initPage() {
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }

        PreviewStack.addPreview(this);

        stages = stagesMapper.selectById(innerStageId);
        messageHandler.notifyPage("stageName", stages);
        
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    StageNamePreviewAttr.setConfigId(innerStageId);
                    Navigator.navigatorTo("/view/match/StageNamePreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        showView();
    }

    @Override
    public Integer getData() {
        return innerStageId;
    }

    @Override
    public void reloadStyle() {
        this.reload();
    }

    private void showView() {
        stages = stagesMapper.selectById(innerStageId);
        controlPane.updateScoreBoard();
        if (ExtendedScreen.hasShow()) {
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/StageNamePreview.fxml", innerStageId);
            if (res != null) {
                controlPane.setQuestionPreview((StageNamePreview) res);
            }
        }

        try {
            String img = ImageResource.stageImg(stages.getSortVal());
            if (img != null) {
                container.setBackground(new Background(new BackgroundImage(
                        new Image(new FileInputStream(img)), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
                )));
            } else {
                container.setBackground(new Background(new BackgroundImage(
                        new Image(new FileInputStream(ImageResource.环节名称背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
                )));
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        container.getChildren().clear();
        StackPane box = new StackPane();
        container.getChildren().add(box);
        //   hjbh.setValue(Color.valueOf(huanjieArr[0]));
        //            bhbj.setValue(Color.valueOf(huanjieArr[1]));
        //            hjmc.setValue(Color.valueOf(huanjieArr[2]));
        //            mcbj.setValue(Color.valueOf(huanjieArr[3]));
        //            hjx.setText(huanjieArr[4]);
        //            hjy.setText(huanjieArr[5]);
        //            hjk.setText(huanjieArr[6]);
        //            hjg.setText(huanjieArr[7]);
        //            xscjan.setSelected(Boolean.valueOf(huanjieArr[8]));
        //            xshjlb.setSelected(Boolean.valueOf(huanjieArr[9]));
        final Configs stageConfig = configsMapper.selectById(2);
        final String style = stages.getStyle();
        final String config = stageConfig.getConfig();
        final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
        String[] detailArr = "750%%400%%400%%150%%false%%0x990000ff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0".split(ConfigSpliter.SPLITER);
        if (StringUtils.isEmpty(style)) {
//            box.setLayoutX(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_X));
//            box.setLayoutY(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_Y));
//            box.setPrefWidth(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_W));
//            box.setPrefHeight(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_H));
//            box.setBackground(new Background(new BackgroundFill(Color.valueOf(huanjieArr[3]),  new CornerRadii(10), Insets.EMPTY)));
//
//            Text title = new Text(stages.getName());
//            title.prefWidth(ScreenAdapter.calculateMainX(ConfigSpliter.DEFAULT_W));
//            title.prefHeight(ScreenAdapter.calculateMainY(ConfigSpliter.DEFAULT_H));
//            title.setTextAlignment(TextAlignment.CENTER);
//            title.setFill(Color.valueOf(huanjieArr[2]));
//            box.getChildren().add(title);
        } else {
            detailArr = style.split(ConfigSpliter.SPLITER);
        }

        box.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[0])));
        box.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[1])));
        box.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])));
        box.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])));

        if (Boolean.valueOf(detailArr[4])) {
            // 填充背景图片
            if (!StringUtils.isEmpty(detailArr[6])) {
                try {
                    box.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                            new BackgroundSize(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])), ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])), false, false, false, false))));
                } catch (FileNotFoundException e) {
                    box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
            }
        } else {
            // 填充背景颜色
            box.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]),  new CornerRadii(10), Insets.EMPTY)));
        }
        Text title = new Text(stages.getName());
        title.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
        box.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
        title.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])));
        title.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])));
        title.setFill(Color.valueOf(huanjieArr[2]));
        if (detailArr.length > 15) {
            title.setFill(Color.valueOf(detailArr[15]));
        }

        Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[8])));
        title.setFont(font);
        if (Boolean.valueOf(detailArr[10])) {
            title.setStroke(title.getFill());
            title.setStrokeWidth(1);
        } else {
            title.setStroke(null);

        }
        title.getTransforms().clear();
        if (Boolean.valueOf(detailArr[11])) {
            title.getTransforms().add(new Shear(-0.3, 0));
        }
        title.setUnderline(Boolean.valueOf(detailArr[12]));
        title.setStrikethrough(Boolean.valueOf(detailArr[9]));
        box.getChildren().add(title);
        
        box.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                List<Stages> childrenStage = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                        .eq(Stages::getParentId, stages.getId())
                );
                
                if (stages.getQuestionType() == 3){
                    // 风险题 优先级高
                    controlPane.setPreviewPane(
                            Navigator.preview("/view/preview/Model3Preview.fxml", "", "/view/style.css", true, stages.getId())
                    );
                    return;
                }

                if (CollectionUtils.isEmpty(childrenStage)) {
                    List<Questions> parse ;
                    if (stages.getRandomSort() == 1) {
                        parse = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, stages.getSortVal())
                                .orderByAsc(Questions::getViewSort)
                        );

                        for (Questions q : parse) {
                            q.setOrder(q.getViewSort());
                        }
                    } else {
                        parse = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, stages.getSortVal())
                                .orderByAsc(Questions::getQuestionNumber)
                        );

                        for (Questions q : parse) {
                            q.setOrder(q.getQuestionNumber());
                        }
                    }
                    if (!CollectionUtils.isEmpty(parse)) {
                        for (int i = 0; i < parse.size(); i++) {
                            parse.get(i).setDesValue(stages.getDesValue());
                        }
                    }

                    //CurrentQuestions.setQuestions(parse, stages.getId());
                    controlPane.showQuestions();
                    // 判断环节出题的类型
                    if (stages.getQuestionType() == 1) {
                        // 连续答题
                        List<Questions> collect = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                                .eq(Questions::getStageNumber, stages.getSortVal())
                                .eq(Questions::isHasAnswer, false));

                        if (!collect.isEmpty()) {

                            if (stages.getRandomSort() == 1) {
                                for (Questions questions : collect) {
                                    questions.setOrder(questions.getViewSort());
                                }
                            } else {
                                for (Questions questions : collect) {
                                    questions.setOrder(questions.getQuestionNumber());
                                }
                            }

                            collect.sort(Comparator.comparingInt(Questions::getOrder));

                            controlPane.selectQuestion(collect.get(0).getOrder().toString());
                            collect.get(0).setHasAnswer(true);
                            questionsMapper.updateById(collect.get(0));
                            controlPane.setPreviewPane(
                                    Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, collect.get(0))
                            );
                        } else {
                            Information.info("此环节题目已完成作答");
                        }
                        

                    } else if (stages.getQuestionType() == 2) {
                        // 自选题目
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, stages)
                        );
                    } else if (stages.getQuestionType() == 4){
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/RollPreview.fxml", "", "/view/style.css", true, stages)
                        );
                    } else if (stages.getQuestionType() == 5){
                        // 线索题
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, stages)
                        );
                    } else if (stages.getQuestionType() == 6){
                        // 答题模式
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, stages)
                        );
//                        List<Questions> questionList = CurrentQuestions.getQuestionList();
//                        QuestionPreviewPreview.setCurrent( questionList.get(0));
//                        controlPane.selectQuestion(questionList.get(0).getQuestionNumber().toString());
                        if (stages.getM6() == 0) {
//                            Model60Preview.setCurrent(questionList.get(0));
//                            controlPane.setPreviewPane(
//                                    Navigator.preview("/view/preview/Model60.fxml", "", "/view/style.css", true, true)
//                            );
                            messageHandler.notifyEnterWait("model60");
                        } else {
//                            Model61Preview.setCurrent(questionList.get(0));
//                            controlPane.setPreviewPane(
//                                    Navigator.preview("/view/preview/Model61.fxml", "", "/view/style.css", true, true)
//                            );
                            messageHandler.notifyEnterWait("model61");
                        }
                    } else if (stages.getQuestionType() == 7){
                        // 竞技模式
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/Model7Preview.fxml", "", "/view/style.css", true, stages)
                        );
                        //messageHandler.notifyEnterWait("model7");
                    }
                } else {
                    // 展示子环节
                    ExtendedScreen.showWithControl("/view/match/StagePreview.fxml", innerStageId);

                    controlPane.setPreviewPane(
                            Navigator.preview("/view/preview/StagePreview.fxml", "", "/view/style.css", true, innerStageId)
                    );
                }

            }
        });
    }

    @Override
    public void reload() {
        showView();
    }
}
