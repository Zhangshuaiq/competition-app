package competition.controller.match;


import competition.controller.ControlPane;
import competition.controller.preview.CluePreviewPreview;
import competition.controller.preview.QuestionPreviewPreview;
import competition.controller.preview.StageQuestionPreviewPreview;
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
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class StageQuestionPreview implements ReloadPane{


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
        StageQuestionPreview.currentStage = currentStage;
    }


    @Override
    public AnchorPane getPane() {
        return container;
    }

    @FXML
    public void initialize() {
        inner = currentStage;
        showData();
    }


    private void showData() {
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.选题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
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
            title.setBackground(new Background(new BackgroundFill(Color.valueOf(stageConfigAttrSplit[3]), new CornerRadii(10), Insets.EMPTY)));
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
                            title.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        title.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    title.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
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
                                item.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            item.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        item.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
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
