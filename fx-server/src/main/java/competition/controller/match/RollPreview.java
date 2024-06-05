package competition.controller.match;


import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

// 滚动选题
@Component
@Scope("prototype")
public class RollPreview implements ReloadPane {

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


    private static Stages currentStage;


    private Stages inner;

    public static void setCurrentStage(Stages currentStage) {
        RollPreview.currentStage = currentStage;
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
                    new Image(new FileInputStream(ImageResource.滚动抽题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
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
        List<Questions> questionList = CurrentQuestions.getQuestionList();
        questionList.sort(new Comparator<Questions>() {
            @Override
            public int compare(Questions o1, Questions o2) {
                return o1.getQuestionNumber().compareTo(o2.getQuestionNumber());
            }
        });
        if (!StringUtils.isEmpty(config)) {
            String questionStyle = currentStage.getQuestionStyle();
            String[] styleArr = {};
            if (!StringUtils.isEmpty(questionStyle)) {
                styleArr = questionStyle.split(ConfigSpliter.DIFFER);
            }


            // 标题名称
            stageName.setText(currentStage.getName());
            // 环节标题的字体颜色和背景色
            stageName.setFill(Color.valueOf(stageConfigAttrSplit[2]));
            title.setBackground(new Background(new BackgroundFill(Color.valueOf(stageConfigAttrSplit[3]), new CornerRadii(10), Insets.EMPTY)));
            title.setLayoutX(ScreenAdapter.calculateExX(ConfigSpliter.DEFAULT_X));
            title.setLayoutY(ScreenAdapter.calculateExY(ConfigSpliter.DEFAULT_Y));
            title.setPrefWidth(ScreenAdapter.calculateExW(ConfigSpliter.DEFAULT_W));
            title.setPrefHeight(ScreenAdapter.calculateExH(ConfigSpliter.DEFAULT_H));
            stageName.setLayoutX(ScreenAdapter.calculateExX(ConfigSpliter.DEFAULT_X));
            stageName.setLayoutY(ScreenAdapter.calculateExY(ConfigSpliter.DEFAULT_Y));
            stageName.prefWidth(ScreenAdapter.calculateExW(ConfigSpliter.DEFAULT_W));
            stageName.prefHeight(ScreenAdapter.calculateExH(ConfigSpliter.DEFAULT_H));


            if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[0])) {
                String[] detailArr = styleArr[0].split(ConfigSpliter.SPLITER);
                title.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                title.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
                title.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])));
                title.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])));

                if (Boolean.valueOf(detailArr[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(detailArr[6])) {
                        try {
                            title.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                        ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])),
                                        ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])), false, false, false, false))));
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
                stageName.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])));
                stageName.prefHeight(ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])));
                stageName.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
                title.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                    ScreenAdapter.calculateExW(Integer.parseInt(detailArr[8])));
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

            // 以后台配置为准

            String[] cfgArr = config.split(ConfigSpliter.SPLITER);

            int col = 1;
            if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[1])) {
                String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);
                if (detailArr.length >= 16) {
                    col = Integer.parseInt(detailArr[15]);
                }
            }


                backPane.setPrefSize(ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[5])),
                    ScreenAdapter.calculateExH(Integer.parseInt(cfgArr[6])));
                
                if (styleArr.length > 1 && !ConfigSpliter.EMPTY.equals(styleArr[1])) {
                    String[] detailArr = styleArr[1].split(ConfigSpliter.SPLITER);


                    backPane.setPrefSize(ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])),
                        ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])));
                    backPane.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                    backPane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                    backPane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
                    if (Boolean.valueOf(detailArr[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(detailArr[6])) {
                            try {
                                backPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(
                                            ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])),
                                            ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])), false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            backPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        backPane.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
                    }


                    Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                        ScreenAdapter.calculateExW(Integer.parseInt(detailArr[8])));
                    questionText.setFont(font);
                    if (Boolean.valueOf(detailArr[10])) {
                        questionText.setStroke(questionText.getFill());
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
                questionText.setFill(Color.valueOf(cfgArr[0]));
        }
    }

    @Override
    public void reload(int time, String timeTxt) {
        backPane.setVisible(true);
        questionText.setText(timeTxt);
    }

    public Stages getInner() {
        return inner;
    }

    @Override
    public void reload() {
        showData();
    }
}
