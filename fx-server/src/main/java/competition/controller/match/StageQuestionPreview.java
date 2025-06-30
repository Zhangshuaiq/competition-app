package competition.controller.match;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.QuestionsMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
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
import java.util.*;

@Component
public class StageQuestionPreview implements ReloadPane<Stages> {


    @FXML
    private AnchorPane container;

    @Resource
    private ConfigsMapper configsMapper;

    
    @Resource
    private QuestionsMapper questionsMapper;
    
    @Resource
    private WsMessageHandler messageHandler;



    private Stages inner;

    @Override
    public void setData(Stages data) {
        inner = data;
    }


    @Override
    public AnchorPane getPane() {
        return container;
    }

    @FXML
    public void initPage() {
        showData();
    }


    private void showData() {
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.选题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
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
        List<Questions> questionList;

        if (inner.getRandomSort() == 1) {
            questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getStageNumber, inner.getSortVal())
                    .orderByAsc(Questions::getViewSort)
            );

            for (Questions q : questionList) {
                q.setOrder(q.getViewSort());
            }

        } else {
            questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getStageNumber, inner.getSortVal())
                    .orderByAsc(Questions::getQuestionNumber)
            );

            for (Questions q : questionList) {
                q.setOrder(q.getQuestionNumber());
            }
        }
//        questionList.sort(new Comparator<Questions>() {
//            @Override
//            public int compare(Questions o1, Questions o2) {
//                return o1.getQuestionNumber().compareTo(o2.getQuestionNumber());
//            }
//        });

        String questionStyle = "760%%240%%400%%80%%false%%0x990000ff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%10###415%%370%%100%%100%%false%%0x990000ff%%%%默认.ttf%%30%%false%%false%%false%%false%%居中对齐%%10%%10";
        String[] cfgArr = {};

        if (!StringUtils.isEmpty(config)) {

            cfgArr = config.split(ConfigSpliter.SPLITER);

            // 标题名称

        }

        String[] styleArr = questionStyle.split(ConfigSpliter.DIFFER);
        if (!StringUtils.isEmpty(inner.getQuestionStyle()) && !inner.getQuestionStyle().equals(ConfigSpliter.EMPTY)) {
            String[] arrs = inner.getQuestionStyle().split(ConfigSpliter.DIFFER);
            if (!StringUtils.isEmpty(arrs[0]) && !arrs[0].equals(ConfigSpliter.EMPTY)) {
                styleArr[0] = arrs[0];
            }
            if (!StringUtils.isEmpty(arrs[1]) && !arrs[1].equals(ConfigSpliter.EMPTY)) {
                styleArr[1] = arrs[1];
            }
        }
        

        StackPane title = new StackPane();
        Text stageName = new Text(inner.getName());
        title.getChildren().add(stageName);
        // 环节标题的字体颜色和背景色
        stageName.setFill(Color.valueOf(stageConfigAttrSplit[2]));
        title.setBackground(new Background(new BackgroundFill(Color.valueOf(stageConfigAttrSplit[3]), new CornerRadii(10), Insets.EMPTY)));
        title.setLayoutX(ScreenAdapter.calculateExX(ConfigSpliter.DEFAULT_X));
        title.setLayoutY(ScreenAdapter.calculateExY(ConfigSpliter.DEFAULT_Y));
        title.setPrefWidth(ScreenAdapter.calculateExX(ConfigSpliter.DEFAULT_W));
        title.setPrefHeight(ScreenAdapter.calculateExY(ConfigSpliter.DEFAULT_H));
        stageName.setLayoutX(ScreenAdapter.calculateExX(ConfigSpliter.DEFAULT_X));
        stageName.setLayoutY(ScreenAdapter.calculateExY(ConfigSpliter.DEFAULT_Y));
        stageName.prefWidth(ScreenAdapter.calculateExX(ConfigSpliter.DEFAULT_W));
        stageName.prefHeight(ScreenAdapter.calculateExY(ConfigSpliter.DEFAULT_H));


        if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[0])) {
            String[] detailArr = styleArr[0].split(ConfigSpliter.SPLITER);
            title.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
            title.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
            title.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])));
            title.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])));

            if (Boolean.valueOf(detailArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(detailArr[6])) {
                    try {
                        title.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(
                                        ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])),
                                        ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])), false, false, false, false))));
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

            stageName.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
            stageName.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
            stageName.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])));
            stageName.prefHeight(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])));
            stageName.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
            if (detailArr.length >= 16) {
                stageName.setFill(Color.valueOf(detailArr[15]));
            }
            title.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
            Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateExX(Integer.parseInt(detailArr[8])));;
            
            stageName.setFont(font);
            if (Boolean.valueOf(detailArr[10])) {
                stageName.setStroke(stageName.getFill());
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

        int col = 1;
        if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[1])) {
            String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);
            if (detailArr.length >= 16) {
                col = Integer.parseInt(detailArr[15]);
            }
        }


        layout.setHgap(ScreenAdapter.calculateExX(20));
        layout.setVgap(ScreenAdapter.calculateExY(20));
        if (cfgArr.length > 4) {
            layout.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[3])));
            layout.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[4])));
        }



        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("stage", inner);
        dataMap.put("questions", questionList);
        messageHandler.notifyPage("stageQuestion", dataMap);

        for (int i = 0; i < questionList.size(); i++) {
            StackPane item = new StackPane();
            if (cfgArr.length > 6) {
                item.setPrefSize(
                        ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[5])),
                        ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[6])));
            }

            Questions questions = questionList.get(i);
            Text text = new Text(Objects.toString(i + 1));
            if (questions.isHasAnswer()) {
                item.setVisible(false);
            } else {
                item.setVisible(true);
            }
            if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[1])) {
                String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);

                layout.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                layout.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));

                item.setPrefSize(
                        ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])),
                        ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])));
                item.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                item.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                item.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
                layout.setVgap(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[14])));
                layout.setHgap(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[14])));
                if (Boolean.valueOf(detailArr[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(detailArr[6])) {
                        try {
                            item.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                            ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])),
                                            ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])), false, false, false, false))));
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


                Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateExX(Integer.parseInt(detailArr[8])));
                text.setFont(font);


                if (cfgArr.length > 0) {
                    text.setFill(Color.valueOf(cfgArr[0]));
                }
                if (Boolean.valueOf(detailArr[10])) {
                    text.setStroke(text.getFill());
                    text.setStrokeWidth(1);
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
            item.getChildren().add(text);
            layout.add(item, i % col, i / col);
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
