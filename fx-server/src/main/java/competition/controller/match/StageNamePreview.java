package competition.controller.match;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.preview.*;
import competition.entity.Configs;
import competition.entity.Questions;
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
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StageNamePreview implements ReloadPane<Integer>{

    @FXML
    private AnchorPane container;

    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;


    private Integer innerStageId;

    private Stages stages;

    @Override
    public void setData(Integer data) {
        innerStageId = data;
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }

    @FXML
    public void initPage() {
        stages = stagesMapper.selectById(innerStageId);
        showView();
    }

    private void showView() {
        stages = stagesMapper.selectById(innerStageId);

        try {
            String img = ImageResource.stageImg(stages.getSortVal());
            if (img != null) {
                container.setBackground(new Background(new BackgroundImage(
                        new Image(new FileInputStream(img)), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
                )));
            } else {
                container.setBackground(new Background(new BackgroundImage(
                        new Image(new FileInputStream(ImageResource.环节名称背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
                )));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        container.getChildren().clear();
        StackPane box = new StackPane();
        container.getChildren().add(box);
        final Configs stageConfig = configsMapper.selectById(2);
        final String style = stages.getStyle();
        final String config = stageConfig.getConfig();
        final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
        String[] detailArr = "750%%400%%400%%150%%false%%0x990000ff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0".split(ConfigSpliter.SPLITER);
        if (StringUtils.isEmpty(style)) {
        } else {
            detailArr = style.split(ConfigSpliter.SPLITER);
        }

        box.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
        box.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
        box.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])));
        box.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])));

        if (Boolean.valueOf(detailArr[4])) {
            // 填充背景图片
            if (!StringUtils.isEmpty(detailArr[6])) {
                try {
                    box.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                            new BackgroundSize(
                                    ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])),
                                    ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])), false, false, false, false))));
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
        title.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])));
        title.prefHeight(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])));
        title.setFill(Color.valueOf(huanjieArr[2]));
        if (detailArr.length > 15) {
            title.setFill(Color.valueOf(detailArr[15]));
        }
        Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateExX(Integer.parseInt(detailArr[8])));
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
        
    }

    public Integer getInnerStageId() {
        return innerStageId;
    }

    @Override
    public void reload() {
        showView();
    }
}
