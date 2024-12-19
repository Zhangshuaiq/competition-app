package competition.controller.match;


import competition.controller.ControlPane;
import competition.controller.preview.StageRulePreviewPreview;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Component
@Scope("prototype")
public class StageRulePreview implements ReloadPane{

    private static Stages currentStage;

    private Stages innerStage;

    @FXML
    private AnchorPane container;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private StagesMapper stagesMapper;


    public static void setCurrentStage(Stages currentStage) {
        StageRulePreview.currentStage = currentStage;
    }

    public static final int TITLE_X = 300;

    public static final int TITLE_Y = 100;

    public static final int TITLE_W = 600;

    public static final int TITLE_H = 100;


    @Override
    public void reload() {
        currentStage = stagesMapper.selectById(currentStage.getId());
        showRuleContent();
    }

    @FXML
    public void initialize(){
        // PreviewStack.addPreview(this);
        innerStage = currentStage;
        showRuleContent();
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }

    private void showRuleContent(){

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.环节规则背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        container.getChildren().clear();
        final Configs ruleConfig = configsMapper.selectById(5);

        if (currentStage == null) {
            return;
        }

        String titleRuleStyle = currentStage.getTitleRuleStyle();
        String[] trsArr = {};
        if (!StringUtils.isEmpty(titleRuleStyle)) {
            trsArr = titleRuleStyle.split(ConfigSpliter.SPLITER);
        }

        Text title = new Text(currentStage.getName());
        StackPane titlePane = new StackPane();
        titlePane.getChildren().add(title);
        if (trsArr.length <= 0) {

            titlePane.setLayoutX(ScreenAdapter.calculateExX(TITLE_X));
            titlePane.setLayoutY(ScreenAdapter.calculateExY(TITLE_Y));
            titlePane.setPrefWidth(ScreenAdapter.calculateExX(TITLE_W));
            titlePane.setPrefHeight(ScreenAdapter.calculateExY(TITLE_H));
            titlePane.setBackground(new Background(new BackgroundFill(Color.YELLOW,  new CornerRadii(10), Insets.EMPTY)));

            title.setX(ScreenAdapter.calculateExX(TITLE_X));
            title.setY(ScreenAdapter.calculateExY(TITLE_Y));
            title.prefWidth(ScreenAdapter.calculateExX(TITLE_W));
            title.prefHeight(ScreenAdapter.calculateExY(TITLE_H));
            title.setFill(Color.BLACK);
        } else {
            final String[] cfgArr = trsArr;
            title.setX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[0])));
            title.setY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[1])));
            title.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])));
            title.prefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[3])));
            title.setFill(Color.valueOf(cfgArr[15]));

            titlePane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[0])));
            titlePane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[1])));
            titlePane.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])));
            titlePane.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[3])));


            if (Boolean.valueOf(cfgArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(cfgArr[6])) {
                    try {
                        titlePane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(cfgArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(
                                    ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])),
                                    ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[3])), false, false, false, false))));
                    } catch (FileNotFoundException e) {
                        titlePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    titlePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                // 填充背景颜色
                titlePane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[5]),  new CornerRadii(10), Insets.EMPTY)));
            }

            Font font = FontMap.load(cfgArr[7], ScreenAdapter.calculateExX(Double.parseDouble(cfgArr[8])));
            title.setFont(font);
            if (Boolean.valueOf(cfgArr[10])) {
                title.setStroke(title.getFill());
                title.setStrokeWidth(1);
            } else {
                title.setStroke(null);
            }
            title.getTransforms().clear();
            if (Boolean.valueOf(cfgArr[11])) {
                title.getTransforms().add(new Shear(-0.3, 0));
            }
            title.setUnderline(Boolean.valueOf(cfgArr[12]));
            title.setStrikethrough(Boolean.valueOf(cfgArr[9]));
            title.setTextAlignment(AlignmentUtil.getTextAlign(cfgArr[13]));
            title.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[14])));
            title.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])));
        }
        container.getChildren().add(titlePane);



        final String config = ruleConfig.getConfig();
        String details = ruleConfig.getDetails();
        String ruleType = currentStage.getRuleType();
        final String ruleContent = currentStage.getRuleContent();
        Text text = new Text(ruleContent);
        StackPane textPane = new StackPane();
        textPane.getChildren().add(text);

        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        textPane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[1])));
        textPane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[2])));
        textPane.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[3])));
        textPane.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[4])));
        textPane.setBackground(new Background(new BackgroundFill(Color.YELLOW,  new CornerRadii(10), Insets.EMPTY)));

        text.setX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[1])));
        text.setY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[2])));
        text.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[3])));
        text.prefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[4])));
        text.setFill(Color.valueOf(cfgArr[0]));
        text.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[3])));
        if (StringUtils.isEmpty(details)) {
        } else {
            cfgArr = details.split(ConfigSpliter.SPLITER);
            text.setX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[0])));
            text.setY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[1])));
            text.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])));
            text.prefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[3])));

            textPane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[0])));
            textPane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[1])));
            textPane.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])));
            textPane.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[3])));

            if (Boolean.valueOf(cfgArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(cfgArr[6])) {
                    try {
                        textPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(cfgArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(
                                    ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])),
                                    ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[3])), false, false, false, false))));
                    } catch (FileNotFoundException e) {
                        textPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    textPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                // 填充背景颜色
                textPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[5]),  new CornerRadii(10), Insets.EMPTY)));
            }

            Font font = FontMap.load(cfgArr[7], ScreenAdapter.calculateExX(Double.parseDouble(cfgArr[8])));
            text.setFont(font);
            if (cfgArr.length >= 16) {
                text.setFill(Color.valueOf(cfgArr[15]));
            } else {
                text.setFill(Color.BLACK);
            }

            if (Boolean.valueOf(cfgArr[10])) {
                text.setStroke(text.getFill());
            } else {
                text.setStroke(null);
            }
            text.getTransforms().clear();
            if (Boolean.valueOf(cfgArr[11])) {
                text.getTransforms().add(new Shear(-0.3, 0));
            }
            text.setUnderline(Boolean.valueOf(cfgArr[12]));
            text.setStrikethrough(Boolean.valueOf(cfgArr[9]));
            text.setTextAlignment(AlignmentUtil.getTextAlign(cfgArr[13]));
            text.setLineSpacing(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[14])));
            text.setWrappingWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[2])));
        }
        container.getChildren().add(textPane);

    }
}
