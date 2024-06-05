package competition.controller.match;

import competition.controller.ControlPane;
import competition.entity.Model3Style;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.Model3Mapper;
import competition.util.*;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

// 竞技模式显示控制
@Component
@Scope("prototype")
public class Model7 implements ReloadPane {

    @FXML
    private GridPane container;

    private String timeValue = "00秒";

    @Resource
    private ControlPane controlPane;

    @Resource
    private Model3Mapper model3Mapper;


    public void stopTiming() {
    }

    @Override
    public Node getPane() {
        return container;
    }

    @FXML
    public void initialize() {
        reload();
    }


    private void showData() {

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.竞技模式背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        Model3Style model3Style = model3Mapper.selectById(2);
        String titleStyle = model3Style.getTitleStyle();

        double vgap = ScreenAdapter.calculateExH(20);
        double hgap = ScreenAdapter.calculateExW(20);
        double x = ScreenAdapter.calculateExX(10);
        double y = 0;
        double width = ((ExtendedScreen.getWidth() - ScreenAdapter.calculateExW(40)) / 4);
        double height = ScreenAdapter.calculateExH(287);
        int col = 4;

        if (!StringUtils.isEmpty(titleStyle)) {
            String[] split = titleStyle.split(ConfigSpliter.SPLITER);
            col = Integer.parseInt(split[0]);
            x = ScreenAdapter.calculateExX(Integer.parseInt(split[5]));
            y = ScreenAdapter.calculateExY(Integer.parseInt(split[6]));
            width = ScreenAdapter.calculateExW(Integer.parseInt(split[3]));
            height = ScreenAdapter.calculateExH(Integer.parseInt(split[4]));
            vgap = ScreenAdapter.calculateExH(Integer.parseInt(split[1]));
            hgap = ScreenAdapter.calculateExW(Integer.parseInt(split[2]));
        }
        container.setPadding(new Insets(y, 0, 0, x));
        container.getChildren().clear();
        container.setVgap(vgap);
        container.setHgap(hgap);
        // 添加
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<Teams> teams = TeamRuntime.getTeams();
        for (int i = 0; i < runtimeData.size(); i++) {
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPadding(new Insets(0, 10, 0, 10));
            anchorPane.setPrefWidth(width);
            anchorPane.setMinWidth(0);
            anchorPane.setMaxWidth(Double.MAX_VALUE); // 允许AnchorPane扩展以填充可用空间
            container.add(anchorPane, i % col, i / col);
            VBox upArea = new VBox();
            upArea.setMinWidth(0);
            upArea.setMaxWidth(Double.MAX_VALUE);
            upArea.setLayoutY(ScreenAdapter.calculateExY(10));
            anchorPane.getChildren().add(upArea);
            upArea.setPrefWidth(anchorPane.getPrefWidth());
            // 背景颜色
            upArea.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
            upArea.setPrefHeight(ScreenAdapter.calculateExH(37));

            // 第二行
            HBox row2 = new HBox();
            row2.setPadding(new Insets(0, 3, 0, 3));
            row2.setPrefWidth(anchorPane.getPrefWidth());
            List<Model7RuntimeData.ModelData> modelData = runtimeData.get(i);
            double radius = ScreenAdapter.calculateExW(5.0);
            Model7RuntimeData.ModelData last = null;
            int score = 0;
            int finish = 0;
            for (Questions questions : CurrentQuestions.getQuestionList()) {
                Circle c = new Circle();
                c.setRadius(radius);
                c.setStroke(Color.BLACK);
                c.setFill(Color.WHITE);
                row2.getChildren().add(c);
            }
            for (int i1 = 0; i1 < modelData.size(); i1++) {
                Model7RuntimeData.ModelData m = modelData.get(i1);
                if (m.getUserSelect() == null) {
                    ((Circle) (row2.getChildren().get(i1))).setFill(Color.WHITE);
                } else if (m.getUserSelect().equals(m.getAnswer())) {
                    finish++;
                    ((Circle) (row2.getChildren().get(i1))).setFill(Color.GREEN);
                    score += m.getScore();
                } else {
                    finish++;
                    ((Circle) (row2.getChildren().get(i1))).setFill(Color.RED);
                }
                last = m;
            }
            // 第一行
            HBox row1 = new HBox();
            row1.setPadding(new Insets(0, 3, 0, 3));
            row1.setPrefWidth(anchorPane.getPrefWidth());
            upArea.setSpacing(ScreenAdapter.calculateExH(5));
            Label label = new Label(teams.get(i).getFullName());
            label.setPrefWidth(anchorPane.getPrefWidth() / 4);
            label.setTextFill(Color.WHITE);
            Label scoreL = new Label(score + "分");
            scoreL.setPrefWidth(anchorPane.getPrefWidth() / 4);
            scoreL.setTextFill(Color.WHITE);
            Label timeL = new Label("完成" + finish + "题|用时" + timeValue);
            if (Model7RuntimeData.getFinished().get(i)) {
                timeL.setText("完成" + finish + "题|用时" + Model7RuntimeData.getFts().get(i));
            }
            timeL.setPrefWidth(anchorPane.getPrefWidth() / 2);
            timeL.setTextFill(Color.WHITE);

            row1.getChildren().addAll(label, scoreL, timeL);
            upArea.getChildren().addAll(row1, row2);

            AnchorPane downArea = new AnchorPane();
            downArea.setPrefWidth(anchorPane.getPrefWidth());
            downArea.setStyle("-fx-border-color: black; -fx-border-width: 1");
            if (!GlobalCache.model7Hide) {
                anchorPane.getChildren().add(downArea);
            }
            downArea.setLayoutY(ScreenAdapter.calculateExY(52));
            downArea.setPrefHeight(height - ScreenAdapter.calculateExH(37));

            // 回答完毕
            if (Model7RuntimeData.getFinished().get(i)) {
                Label endLab = new Label("答题结束");
                endLab.setPrefWidth(downArea.getPrefWidth());
                endLab.setPrefHeight(downArea.getPrefHeight() / 5);
                endLab.setLayoutY(downArea.getPrefHeight() / 5);
                endLab.setFont(Font.font(ScreenAdapter.calculateExW(32)));
                endLab.setAlignment(Pos.CENTER);
                endLab.setTextAlignment(TextAlignment.CENTER);

                Label scoreLab = new Label(score + "分");
                scoreLab.setPrefWidth(downArea.getPrefWidth());
                scoreLab.setPrefHeight(downArea.getPrefHeight() / 5);
                scoreLab.setLayoutY(downArea.getPrefHeight() * 0.4);
                scoreLab.setFont(Font.font(ScreenAdapter.calculateExW(32)));
                scoreLab.setTextAlignment(TextAlignment.CENTER);
                scoreLab.setAlignment(Pos.CENTER);
                List<String> fts = Model7RuntimeData.getFts();
                Label timeLab = new Label(fts.get(i));
                timeLab.setPrefWidth(downArea.getPrefWidth());
                timeLab.setPrefHeight(downArea.getPrefHeight() / 5);
                timeLab.setLayoutY(downArea.getPrefHeight() * 0.6);
                timeLab.setFont(Font.font(ScreenAdapter.calculateExW(32)));
                timeLab.setTextAlignment(TextAlignment.CENTER);
                timeLab.setAlignment(Pos.CENTER);
                downArea.getChildren().addAll(endLab, scoreLab, timeLab);
            }
            else {
                if (last == null) {
                    Label waitLab = new Label("等待答题");
                    waitLab.setPrefWidth(downArea.getPrefWidth());
                    waitLab.setPrefHeight(downArea.getPrefHeight());
                    waitLab.setFont(Font.font(ScreenAdapter.calculateExW(32)));
                    waitLab.setTextAlignment(TextAlignment.CENTER);
                    waitLab.setAlignment(Pos.CENTER);
                    downArea.getChildren().add(waitLab);
                } else {
                    VBox vbx = new VBox();
                    // 设置边距
                    vbx.setPadding(new Insets(1, 5, 1, 5));
                    vbx.setSpacing(ScreenAdapter.calculateExH(2));
                    vbx.setPrefWidth(downArea.getPrefWidth());
                    downArea.getChildren().addAll(vbx);
                    Label q = new Label(last.getQuestionNumber() + ". " + last.getQuestion());
                    q.setWrapText(true);
                    vbx.getChildren().add(q);
                    if (!StringUtils.isEmpty(last.getChoiceA())) {
                        Label c = new Label("A. " + last.getChoiceA());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceB())) {
                        Label c = new Label("B. " + last.getChoiceB());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceC())) {
                        Label c = new Label("C. " + last.getChoiceC());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceD())) {
                        Label c = new Label("D. " + last.getChoiceD());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceE())) {
                        Label c = new Label("E. " + last.getChoiceE());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }
                    if (!StringUtils.isEmpty(last.getChoiceF())) {
                        Label c = new Label("F. " + last.getChoiceF());
                        c.setWrapText(true);
                        vbx.getChildren().add(c);
                    }

                    VBox answerArea = new VBox();
                    answerArea.setAlignment(Pos.CENTER);
                    answerArea.setLayoutY(ScreenAdapter.calculateExY(190));
                    answerArea.setPrefWidth(downArea.getPrefWidth());
                    downArea.getChildren().add(answerArea);
                    answerArea.setSpacing(ScreenAdapter.calculateExH(5));
                    if (last.getUserSelect() != null) {
                        Label select = new Label("回答:" + last.getUserSelect());
                        select.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
                        select.setPrefHeight(ScreenAdapter.calculateExH(30));
                        select.setTextAlignment(TextAlignment.CENTER);
                        select.setTextFill(Color.WHITE);
                        select.setPrefWidth(answerArea.getPrefWidth());

                        Label status = new Label();
                        if (last.getAnswer().equals(last.getUserSelect())) {
                            status.setText("正确");
                            status.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
                        } else {
                            status.setText("错误");
                            status.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
                        }
                        status.setPrefHeight(ScreenAdapter.calculateExH(30));
                        status.setTextAlignment(TextAlignment.CENTER);
                        status.setTextFill(Color.WHITE);
                        status.setPrefWidth(answerArea.getPrefWidth());

                        answerArea.getChildren().addAll(select, status);
                    }
                }
            }


        }
    }


    @Override
    public void reload() {
        showData();
    }


    @Override
    public void reload(int time, String timeTxt) {
        this.timeValue = timeTxt;
        showData();
    }

}
