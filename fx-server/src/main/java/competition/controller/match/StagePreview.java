package competition.controller.match;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@Component
@Scope("prototype")
public class StagePreview implements ReloadPane {

    @FXML
    private AnchorPane container;

    @Resource
    private StagesMapper stagesMapper;

    @Resource
    private ConfigsMapper configsMapper;

    private static Integer parentStageId;
    
    private Integer innerStageId;

    public static void setParentStageId(Integer parentStageId) {
        StagePreview.parentStageId = parentStageId;
    }

    @FXML
    public void initialize() {
        innerStageId = parentStageId;
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                   
                }
            }
        });
        showView();
    }

    @Override
    public Node getPane() {
        return container;
    }


    private void showView() {

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.环节背景)), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, true, true)
            )));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
        container.getChildren().clear();
        Configs configs = configsMapper.selectById(2);
        final String configVal = configs.getConfig();
        final String fontDetail = configs.getDetails();
        String[] detailArr = {};
        if (!StringUtils.isEmpty(fontDetail)) {
            detailArr = fontDetail.split(ConfigSpliter.SPLITER);
        }
        if (!StringUtils.isEmpty(configVal)) {
            final String[] split = configVal.split(ConfigSpliter.SPLITER);
            if (Boolean.valueOf(split[9])) {
                // 使用图片展示而不是样式
            } else {
                List<Stages> stages;
                
                if (innerStageId == null) {
                    stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .isNull(Stages::getParentId)
                            .orderByAsc(Stages::getSortVal)
                    );
                } else {
                    stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, innerStageId)
                            .orderByAsc(Stages::getSortVal)
                    );
                }

                int x = Integer.parseInt(split[4]);
                int y = Integer.parseInt(split[5]);
                int width = Integer.parseInt(split[6]);
                int height = Integer.parseInt(split[7]);

                VBox stageContainer = new VBox();
                stageContainer.setLayoutX(x);
                stageContainer.setLayoutY(y);
                stageContainer.prefWidth(width);
                stageContainer.prefHeight(height);

                if (detailArr.length > 0) {
                    stageContainer.setSpacing(Integer.parseInt(detailArr[14]));
                    stageContainer.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                }
                
                for (Stages stage : stages) {
                    HBox box = new HBox();
//                    StackPane stageNumberPane = new StackPane();
//                    Text stageNumber = new Text(stage.getSortVal());
//                    stageNumber.prefWidth(50);
//                    stageNumber.prefHeight(50);
//                    stageNumber.setTextAlignment(TextAlignment.CENTER);
//                    stageNumber.setStyle("-fx-fill: " + split[0].replace("0x", "#"));
//                    if (detailArr.length > 0) {
//                        Font font = Font.font(detailArr[7], FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(detailArr[8]));
//                        stageNumber.setFont(font);
//                        if (Boolean.valueOf(detailArr[10])) {
//                            stageNumber.setStroke(stageNumber.getFill());
//                        } else {
//                            stageNumber.setStroke(null);
//
//                        }
//                        stageNumber.getTransforms().clear();
//                        if (Boolean.valueOf(detailArr[11])) {
//                            stageNumber.getTransforms().add(new Shear(-0.3, 0));
//                        }
//                        stageNumber.setUnderline(Boolean.valueOf(detailArr[12]));
//                        stageNumber.setStrikethrough(Boolean.valueOf(detailArr[9]));
//                        stageNumber.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
//                    }
//
//
//                    stageNumberPane.getChildren().add(stageNumber);
//                    stageNumberPane.setStyle("-fx-background-color: " + split[1].replace("0x", "#"));
//                    stageNumberPane.setPrefSize(50, 50);
//                    
//                    box.getChildren().add(stageNumberPane);
                    // 标题

                    StackPane stageNamePane = new StackPane();
                    Text stageName = new Text(stage.getName());
                    System.out.println(stage.getName());
                    stageName.prefHeight(50);
                    stageName.setTextAlignment(TextAlignment.CENTER);
                    stageName.setStyle("-fx-fill: " + split[2].replace("0x", "#"));
                    stageNamePane.getChildren().add(stageName);
                    stageNamePane.setPrefSize(width - 50, 50);
                    stageNamePane.setStyle("-fx-background-color: " + split[3].replace("0x", "#"));

                    if (detailArr.length > 0) {
                        Font font = Font.font(detailArr[7], FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(detailArr[8]));
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
                        stageName.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
                        stageNamePane.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                    }
                    
                    box.getChildren().add(stageNamePane);

                    stageContainer.getChildren().add(box);
                }

                container.getChildren().add(stageContainer);

            }
        }
    }

    @Override
    public void reload() {
        System.out.println(innerStageId);
        showView();
    }
}
