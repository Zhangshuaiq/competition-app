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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class StageNamePreview implements ReloadPane{

    @Resource
    private ControlPane controlPane;

    @FXML
    private AnchorPane container;

    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;

    private static Integer stageId;

    private Integer innerStageId;

    private Stages stages;

    public static void setStageId(Integer stageId) {
        StageNamePreview.stageId = stageId;
    }


    @Override
    public AnchorPane getPane() {
        return container;
    }

    @FXML
    public void initialize() {
        innerStageId= stageId;

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
        if (StringUtils.isEmpty(style)) {
            box.setLayoutX(ConfigSpliter.DEFAULT_X);
            box.setLayoutY(ConfigSpliter.DEFAULT_Y);
            box.setPrefWidth(ConfigSpliter.DEFAULT_W);
            box.setPrefHeight(ConfigSpliter.DEFAULT_H);
            box.setBackground(new Background(new BackgroundFill(Color.valueOf(huanjieArr[3]), CornerRadii.EMPTY, Insets.EMPTY)));

            Text title = new Text(stages.getName());
            title.prefWidth(ConfigSpliter.DEFAULT_W);
            title.prefHeight(ConfigSpliter.DEFAULT_H);
            title.setTextAlignment(TextAlignment.CENTER);
            title.setStyle("-fx-fill: " + huanjieArr[2].replace("0x", "#"));
            box.getChildren().add(title);
        } else {
            String[] detailArr = style.split(ConfigSpliter.SPLITER);
            box.setLayoutX(Integer.parseInt(detailArr[0]));
            box.setLayoutY(Integer.parseInt(detailArr[1]));
            box.setPrefWidth(Integer.parseInt(detailArr[2]));
            box.setPrefHeight(Integer.parseInt(detailArr[3]));

            if (Boolean.valueOf(detailArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(detailArr[6])) {
                    try {
                        box.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(Integer.parseInt(detailArr[2]), Integer.parseInt(detailArr[3]), false, false, false, false))));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // 填充背景颜色
                box.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), CornerRadii.EMPTY, Insets.EMPTY)));
            }
            Text title = new Text(stages.getName());
            title.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
            box.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
            title.prefWidth(Integer.parseInt(detailArr[2]));
            title.prefHeight(Integer.parseInt(detailArr[3]));
            title.setStyle("-fx-fill: " + huanjieArr[2].replace("0x", "#"));


            Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(detailArr[8]));
            title.setFont(font);
            if (Boolean.valueOf(detailArr[10])) {
                title.setStroke(title.getFill());
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
    }

    public Integer getInnerStageId() {
        return innerStageId;
    }

    @Override
    public void reload() {
        showView();
    }
}
