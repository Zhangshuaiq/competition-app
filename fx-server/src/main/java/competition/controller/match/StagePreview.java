package competition.controller.match;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.preview.StageNamePreviewPreview;
import competition.controller.preview.StagePreviewPreview;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.entity.StagesStyle;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.mapper.StagesStyleMapper;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    @Resource
    private StagesStyleMapper stagesStyleMapper;

    private static Integer parentStageId;

    private Integer innerStageID;


    public static void setParentStageId(Integer parentStageId) {
        StagePreview.parentStageId = parentStageId;
    }


    @Override
    public AnchorPane getPane() {
        return container;
    }

    @FXML
    public void initialize() {
        innerStageID = parentStageId;
        showView();
    }


    private void showView() {
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.环节背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        container.getChildren().clear();
        Configs configs = configsMapper.selectById(2);
        final String configVal = configs.getConfig();

        if (!StringUtils.isEmpty(configVal)) {
            final String[] split = configVal.split(ConfigSpliter.SPLITER);
            if (Boolean.valueOf(split[9])) {
                // 使用图片展示而不是样式
            } else {
                List<Stages> stages;
                StagesStyle stagesStyle;
                if (innerStageID == null) {
                    stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .isNull(Stages::getParentId)
                            .orderByAsc(Stages::getSortVal)
                    );
                    stagesStyle = stagesStyleMapper.selectOne(new LambdaQueryWrapper<StagesStyle>()
                            .isNull(StagesStyle::getParentId)
                    );
                } else {
                    stages = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, innerStageID)
                            .orderByAsc(Stages::getSortVal)
                    );
                    stagesStyle = stagesStyleMapper.selectOne(new LambdaQueryWrapper<StagesStyle>()
                            .eq(StagesStyle::getParentId, innerStageID)
                    );
                }

                String defaultStyle = "550%%300%%350%%80%%false%%0xb31a1aff%%%%默认.ttf%%28%%false%%false%%false%%false%%居中对齐%%60%%0%%2";
                String[] detailArr = defaultStyle.split(ConfigSpliter.SPLITER);
                if (stagesStyle != null) {
                    detailArr = stagesStyle.getStyle().split(ConfigSpliter.SPLITER);
                }
                double x = ScreenAdapter.calculateExX(Integer.parseInt(split[4]));
                double y = ScreenAdapter.calculateExY(Integer.parseInt(split[5]));
                double width = ScreenAdapter.calculateExX(Integer.parseInt(split[6]));
                double height = ScreenAdapter.calculateExY(Integer.parseInt(split[7]));

                GridPane stageContainer = new GridPane();
                stageContainer.setLayoutX(x);
                stageContainer.setLayoutY(y);
                stageContainer.prefWidth(width);
                // stageContainer.height

                if (detailArr.length > 0) {
                    stageContainer.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                    stageContainer.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
                    stageContainer.prefWidth(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])));


                    width = ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2]));
                    height =  ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3]));
                    x = ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0]));
                    y = ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1]));
                    if (detailArr.length >= 18) {
                        stageContainer.setHgap(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[17])));
                    }
                    stageContainer.setVgap(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[14])));
                    stageContainer.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                }

                int count = 0;
                for (Stages stage : stages) {
                    HBox box = new HBox();
                    StackPane stageNamePane = new StackPane();
                    Text stageName = new Text(stage.getName());
                    stageName.setTextAlignment(TextAlignment.CENTER);
                    stageName.setFill(Color.valueOf(split[2]));
                    stageNamePane.getChildren().add(stageName);
                    stageNamePane.setPrefSize(width, height);

                    if (stage.getShow() == 1) {
                        if (stage.getSelectGrey() == 1) {
                            if (GlobalCache.isRecord(stage.getId())) {
                                stageNamePane.setVisible(false);
                            } else {
                                stageNamePane.setVisible(true);
                            }
                        }
                    } else {
                        continue;
                    }

                    if (detailArr.length > 0) {
                        if (Boolean.valueOf(detailArr[4])) {
                            // 填充背景图片
                            if (!StringUtils.isEmpty(detailArr[6])) {
                                try {
                                    stageNamePane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                            new BackgroundSize(
                                                ScreenAdapter.calculateExX(Integer.parseInt(detailArr[2])),
                                                ScreenAdapter.calculateExY(Integer.parseInt(detailArr[3])), false, false, false, false))));
                                } catch (FileNotFoundException e) {
                                    stageNamePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                                }
                            } else {
                                stageNamePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            // 填充背景颜色
                            stageNamePane.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
                        }
                    }
                    if (detailArr.length > 0) {
                        Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateExX(Integer.parseInt(detailArr[8])));
                        stageName.setFont(font);
                        if (Boolean.valueOf(detailArr[10])) {
                            stageName.setStroke(stageName.getFill());
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
                        stageName.setTextAlignment(AlignmentUtil.getTextAlign(detailArr[13]));
                        stageNamePane.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                        if (detailArr.length >= 19) {
                            stageName.setFill(Color.valueOf(detailArr[18]));
                            if (Boolean.valueOf(detailArr[10])) {
                                stageName.setStroke(stageName.getFill());
                                stageName.setStrokeWidth(1);
                            } else {
                                stageName.setStroke(null);
                            }
                        }
                        if (detailArr.length >= 16) {
                            String text = stageName.getText();
                            char[] chars = text.toCharArray();
                            String sli = "";
                            int i = Integer.parseInt(detailArr[15]);
                            if (i > 0) {
                                for (int m = 0; m < i; m++) {
                                    sli = sli + " ";
                                }
                            }
                            List<String> cs = new ArrayList<>();
                            for (char aChar : chars) {
                                cs.add(aChar + "");
                            }
                            stageName.setText(String.join(sli, cs));
                        }
                    }

                    box.getChildren().add(stageNamePane);
                    int cols = 1;
                    if (detailArr.length >= 17) {
                        cols = Integer.parseInt(detailArr[16]);
                    }
                    int row = count / cols;
                    int col = count % cols;
                    // 是否展示环节
//                    if (stage.getShow() == 1) {
//                        if (stage.getSelectGrey() == 1) {
//                            if (GlobalCache.isRecord(stage.getId())) {
//                                box.setVisible(false);
//                            } else {
//                                box.setVisible(true);
//                            }
//                        } else {
//                            box.setVisible(true);
//                        }
//                    } else {
//                        box.setVisible(false);
//                    }
                    stageContainer.add(box, col, row);
                    count ++;

                    // stageContainer.getChildren().add(box);
                }
                container.getChildren().add(stageContainer);
            }
        }
    }

    @Override
    public void reload() {
        showView();
    }

    public Integer getInnerStageID() {
        return innerStageID;
    }
}
