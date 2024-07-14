package competition.controller.preview;

import competition.controller.ControlPane;
import competition.controller.match.RollPreview;
import competition.controller.match.ShowAnswer;
import competition.entity.AnswerStyle;
import competition.mapper.AnswerStyleMapper;
import competition.modal.TeamResult;
import competition.util.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
import java.util.List;

@Component
@Scope("prototype")
public class ShowAnswerPreview extends PreviewPane {

    @FXML
    private AnchorPane container;

    @FXML
    private StackPane answerBack;

    @FXML
    private VBox vbox;

    @FXML
    private Text correctText;

    @FXML
    private GridPane gridPane;
    
    @Resource
    private ControlPane controlPane;

    @Resource
    private AnswerStyleMapper answerStyleMapper;

    private static TeamResult teamResult;

    public static void setTeamResult(TeamResult teamResult) {
        ShowAnswerPreview.teamResult = teamResult;
    }

    @FXML
    public void initialize() {
        controlPane.getScoreBoardPreview().getSelected().clear();
        //PreviewStack.addPreview(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 右键弹出属性框设置
                if (event.getButton() == MouseButton.SECONDARY) {
                    Navigator.navigatorTo("/view/match/AnswerAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        this.showData();
    }


    private void showData() {

        if (ExtendedScreen.hasShow()) {
            ShowAnswer.setTeamResult(teamResult);
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/ShowAnswer.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((ShowAnswer) res);
            }
        }

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.答案背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }
        
        
        AnswerStyle answerStyle = answerStyleMapper.selectById(1);

        int col = 1;
        double rowSpace = 0;
        double colSpace = 0;
        double x = ScreenAdapter.calculateMainX(50);
        double y = ScreenAdapter.calculateMainY(50);
        double width = ScreenAdapter.calculateMainW(500);
        double height = ScreenAdapter.calculateMainH(50);
        if (!StringUtils.isEmpty(answerStyle.getStyle1())) {
            String[] split = answerStyle.getStyle1().split(ConfigSpliter.SPLITER);
            col = Integer.parseInt(split[0]);
            rowSpace = ScreenAdapter.calculateMainH(Integer.parseInt(split[2]));
            colSpace = ScreenAdapter.calculateMainW(Integer.parseInt(split[1]));
            x = ScreenAdapter.calculateMainX(Integer.parseInt(split[3]));
            y = ScreenAdapter.calculateMainY(Integer.parseInt(split[4]));
            width = ScreenAdapter.calculateMainW(Integer.parseInt(split[5]));
            height = ScreenAdapter.calculateMainH(Integer.parseInt(split[6]));
        }

        Font defaultFont = Font.getDefault();

        String f1Font = defaultFont.getName();
        double f1Size = ScreenAdapter.calculateMainW(defaultFont.getSize());
        String f1l = "居中对齐";
        Color f1Color = Color.BLACK;
        boolean f1DL= false, f1IT= false, f1BD= false, f1UD = false, f1Fill = false;
        Color f1BackColor = Color.WHITE;
        String f1Path = "";

        if (!StringUtils.isEmpty(answerStyle.getStyle2())) {
            String[] split = answerStyle.getStyle2().split(ConfigSpliter.SPLITER);
            f1Font = (split[0]);
            f1Color = (Color.valueOf(split[1]));
            f1l = (split[2]);
            f1Size = ScreenAdapter.calculateMainW(Integer.parseInt(split[3]));
            f1DL = (Boolean.valueOf(split[4]));
            f1IT = (Boolean.valueOf(split[5]));
            f1BD = (Boolean.valueOf(split[6]));
            f1UD = (Boolean.valueOf(split[7]));
            f1Fill = (Boolean.valueOf(split[8]));
            f1BackColor = (Color.valueOf(split[9]));
            if (split.length >= 11) {
                f1Path = (split[10]);
            }
        }

        String f2Font = defaultFont.getName();
        double f2Size = ScreenAdapter.calculateMainW(defaultFont.getSize());
        String f2l = "居中对齐";
        Color f2Color = Color.BLACK;
        boolean f2DL = false, f2IT = false, f2BD = false, f2UD = false, f2Fill = false;
        Color f2BackColor = Color.WHITE;
        String f2Path = "";

        if (!StringUtils.isEmpty(answerStyle.getStyle3())) {
            String[] split = answerStyle.getStyle3().split(ConfigSpliter.SPLITER);
            f2Font = (split[0]);
            f2Color = (Color.valueOf(split[1]));
            f2l = (split[2]);
            f2Size = ScreenAdapter.calculateMainW(Integer.parseInt(split[3]));
            f2DL = (Boolean.valueOf(split[4]));
            f2IT = (Boolean.valueOf(split[5]));
            f2BD = (Boolean.valueOf(split[6]));
            f2UD = (Boolean.valueOf(split[7]));
            f2Fill = (Boolean.valueOf(split[8]));
            f2BackColor = (Color.valueOf(split[9]));
            if (split.length >= 11) {
                f2Path = (split[10]);
            }
        }

        String f3Font = defaultFont.getName();
        double f3Size = ScreenAdapter.calculateMainW(defaultFont.getSize());
        String f3l = "居中对齐";
        Color f3Color = Color.BLACK;
        boolean f3DL = false, f3IT = false, f3BD = false, f3UD = false, f3Fill = false;
        Color f3BackColor = Color.WHITE;
        String f3Path = "";

        if (!StringUtils.isEmpty(answerStyle.getStyle4())) {
            String[] split = answerStyle.getStyle4().split(ConfigSpliter.SPLITER);
            f3Font = (split[0]);
            f3Color = (Color.valueOf(split[1]));
            f3l = (split[2]);
            f3Size = ScreenAdapter.calculateMainW(Integer.parseInt(split[3]));
            f3DL = (Boolean.valueOf(split[4]));
            f3IT = (Boolean.valueOf(split[5]));
            f3BD = (Boolean.valueOf(split[6]));
            f3UD = (Boolean.valueOf(split[7]));
            f3Fill = (Boolean.valueOf(split[8]));
            f3BackColor = (Color.valueOf(split[9]));
            if (split.length >= 11) {
                f3Path = (split[10]);
            }
        }


        String f4Font = defaultFont.getName();
        double f4Size = ScreenAdapter.calculateMainW(defaultFont.getSize());
        String f4l = "居中对齐";
        Color f4Color = Color.BLACK;
        boolean f4DL = false, f4IT = false, f4BD = false, f4UD = false, f4Fill = false;
        Color f4BackColor = Color.WHITE;
        String f4Path = "";


        if (!StringUtils.isEmpty(answerStyle.getStyle5())) {
            String[] split = answerStyle.getStyle5().split(ConfigSpliter.SPLITER);
            f4Font = (split[0]);
            f4Color = (Color.valueOf(split[1]));
            f4l = (split[2]);
            f4Size = ScreenAdapter.calculateMainW(Integer.parseInt(split[3]));
            f4DL = (Boolean.valueOf(split[4]));
            f4IT = (Boolean.valueOf(split[5]));
            f4BD = (Boolean.valueOf(split[6]));
            f4UD = (Boolean.valueOf(split[7]));
            f4Fill = (Boolean.valueOf(split[8]));
            f4BackColor = (Color.valueOf(split[9]));
            if (split.length >= 11) {
                f4Path = (split[10]);
            }
        }

        String f5Font = defaultFont.getName();
        double f5Size = ScreenAdapter.calculateMainW(defaultFont.getSize());
        String f5l = "居中对齐";
        Color f5Color = Color.BLACK;
        boolean f5DL = false, f5IT = false, f5BD = false, f5UD = false, f5Fill = false;
        Color f5BackColor = Color.WHITE;
        String f5Path = "";

        if (!StringUtils.isEmpty(answerStyle.getStyle6())) {
            String[] split = answerStyle.getStyle6().split(ConfigSpliter.SPLITER);
            f5Font = (split[0]);
            f5Color = (Color.valueOf(split[1]));
            f5l = (split[2]);
            f5Size = ScreenAdapter.calculateMainW(Integer.parseInt(split[3]));
            f5DL = (Boolean.valueOf(split[4]));
            f5IT = (Boolean.valueOf(split[5]));
            f5BD = (Boolean.valueOf(split[6]));
            f5UD = (Boolean.valueOf(split[7]));
            f5Fill = (Boolean.valueOf(split[8]));
            f5BackColor = (Color.valueOf(split[9]));
            if (split.length >= 11) {
                f5Path = (split[10]);
            }
        }


        vbox.setLayoutX(x);
        vbox.setLayoutY(y);
        vbox.setSpacing(colSpace);

        // 答案样式
        answerBack.setPrefHeight(height);
        answerBack.setPrefWidth(width * col + (col - 1) * colSpace);
        answerBack.setAlignment(AlignmentUtil.getAlign(f1l));

        Font font = Font.font(FontMap.getFont(f1Font), FontWeight.NORMAL, FontPosture.REGULAR, f1Size);
        correctText.setFont(font);
        
        if (f1BD) {
            correctText.setStroke(f1Color);
            correctText.setStrokeWidth(1);
        } else {
            correctText.setStroke(null);

        }
        correctText.getTransforms().clear();
        if (f1IT) {
            correctText.getTransforms().add(new Shear(-0.3, 0));
        }
        correctText.setUnderline(f1UD);
        correctText.setStrikethrough(f1DL);
        correctText.setFill(f1Color);

        if (f1Fill) {
            if (!StringUtils.isEmpty(f1Path)) {
                try {
                    answerBack.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(f1Path)),
                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                            new BackgroundSize(width * col + (col - 1) * colSpace, height, false, false, false, false))));
                } catch (FileNotFoundException e) {
                    answerBack.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));

                }
            }
        } else {
            // 填充背景颜色
            answerBack.setBackground(new Background(new BackgroundFill(f1BackColor, new CornerRadii(10), Insets.EMPTY)));
        }

        if (teamResult != null) {
            // 显示正确答案
            correctText.setText("正确答案：" + teamResult.getCorrectAnswer());
        }

        
        // 每一个选手回答的样式
        gridPane.setVgap(colSpace);
        gridPane.setHgap(rowSpace);
        if (teamResult != null) {
            // 显示正确答案
            List<TeamResult.DataItem> dataItemList = teamResult.getDataItemList();
            for (int i = 0; i < dataItemList.size(); i++) {
                int r = i / col;
                int c = i % col;
                
                HBox itemBox = new HBox();
                StackPane stackPane = new StackPane();
                stackPane.setPrefSize(width - height, height);
                itemBox.setPrefWidth(width);
                itemBox.setPrefHeight(height);
                TeamResult.DataItem dataItem = dataItemList.get(i);

                String teamName = dataItem.getTeamName().length() > 10? dataItem.getTeamName().substring(0, 10) + " ...... ":dataItem.getTeamName();
                
                String text = " 团队: " + teamName + "  " + dataItem.getMember() + "  回答:" + dataItem.getAnswer();
                if (StringUtils.isEmpty(dataItem.getMember())) {
                    text = " 团队: " + teamName + "  " + "  回答:" + dataItem.getAnswer();
                }
                Text memberResult = new Text(text);
                stackPane.getChildren().add(memberResult);
                stackPane.setAlignment(AlignmentUtil.getAlign(f2l));


                memberResult.setFill(f2Color);
                Font font2 = Font.font(FontMap.getFont(f2Font), FontWeight.NORMAL, FontPosture.REGULAR, f2Size);
                memberResult.setFont(font2);

                if (f2BD) {
                    memberResult.setStroke(f2Color);
                    memberResult.setStrokeWidth(1);
                } else {
                    memberResult.setStroke(null);

                }
                memberResult.getTransforms().clear();
                if (f2IT) {
                    memberResult.getTransforms().add(new Shear(-0.3, 0));
                }
                memberResult.setUnderline(f2UD);
                memberResult.setStrikethrough(f2DL);
                memberResult.setFill(f2Color);

                if (f2Fill) {
                    if (!StringUtils.isEmpty(f2Path)) {
                        try {
                            itemBox.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(f2Path)),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(width, height, false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            itemBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        itemBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    itemBox.setBackground(new Background(new BackgroundFill(f2BackColor, new CornerRadii(10), Insets.EMPTY)));
                }
                
                StackPane rPane = new StackPane();
                rPane.setPrefSize(height, height);
                Text ut = new Text(dataItem.getScore() + "");
                ut.prefWidth(height);
                ut.prefHeight(height);
                rPane.getChildren().add(ut);
                if (dataItem.getScore() > 0) {
                    ut.setText("+" + dataItem.getScore());
                    rPane.setAlignment(AlignmentUtil.getAlign(f3l));
                    ut.setFill(f3Color);
                    Font font3 = Font.font(FontMap.getFont(f3Font), FontWeight.NORMAL, FontPosture.REGULAR, f3Size);
                    ut.setFont(font3);

                    if (f3BD) {
                        ut.setStroke(f3Color);
                        ut.setStrokeWidth(1);
                    } else {
                        ut.setStroke(null);

                    }
                    ut.getTransforms().clear();
                    if (f3IT) {
                        ut.getTransforms().add(new Shear(-0.3, 0));
                    }
                    ut.setUnderline(f3UD);
                    ut.setStrikethrough(f3DL);
                    ut.setFill(f3Color);

                    if (f3Fill) {
                        if (!StringUtils.isEmpty(f3Path)) {
                            try {
                                rPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(f3Path)),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(ut.getStrokeWidth(), height, false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                rPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            rPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        rPane.setBackground(new Background(new BackgroundFill(f3BackColor,  new CornerRadii(10), Insets.EMPTY)));
                    }
                } else if (dataItem.getScore() < 0) {
                    rPane.setAlignment(AlignmentUtil.getAlign(f4l));
                    ut.setFill(f4Color);
                    Font font4 = Font.font(FontMap.getFont(f4Font), FontWeight.NORMAL, FontPosture.REGULAR, f4Size);
                    ut.setFont(font4);

                    if (f4BD) {
                        ut.setStroke(f4Color);
                        ut.setStrokeWidth(1);
                    } else {
                        ut.setStroke(null);

                    }
                    ut.getTransforms().clear();
                    if (f4IT) {
                        ut.getTransforms().add(new Shear(-0.3, 0));
                    }
                    ut.setUnderline(f4UD);
                    ut.setStrikethrough(f4DL);
                    ut.setFill(f4Color);

                    if (f4Fill) {
                        if (!StringUtils.isEmpty(f4Path)) {
                            try {
                                rPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(f4Path)),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(ut.getStrokeWidth(), height, false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                rPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            rPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        rPane.setBackground(new Background(new BackgroundFill(f4BackColor,  new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    rPane.setAlignment(AlignmentUtil.getAlign(f5l));

                    ut.setFill(f5Color);
                    Font font5 = Font.font(FontMap.getFont(f5Font), FontWeight.NORMAL, FontPosture.REGULAR, f5Size);
                    ut.setFont(font5);

                    if (f5BD) {
                        ut.setStroke(f5Color);
                        ut.setStrokeWidth(1);
                    } else {
                        ut.setStroke(null);

                    }
                    ut.getTransforms().clear();
                    if (f5IT) {
                        ut.getTransforms().add(new Shear(-0.3, 0));
                    }
                    ut.setUnderline(f5UD);
                    ut.setStrikethrough(f5DL);
                    ut.setFill(f5Color);

                    if (f5Fill) {
                        if (!StringUtils.isEmpty(f5Path)) {
                            try {
                                rPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(f5Path)),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(ut.getStrokeWidth(), height, false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                rPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            rPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        rPane.setBackground(new Background(new BackgroundFill(f5BackColor,  new CornerRadii(10), Insets.EMPTY)));
                    }
                }
                itemBox.getChildren().add(stackPane);
                itemBox.getChildren().add(rPane);
                gridPane.add(itemBox, c, r);
            }
        }
        
    }


    @Override
    public void reload() {
        this.showData();
    }
}
