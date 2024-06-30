package competition.controller.board.match;

import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.board.preview.ScoreBoardVPreview;
import competition.entity.Configs;
import competition.entity.ScoreStyle;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreStyleMapper;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScoreBoardV implements BoardPane {


    @FXML
    private HBox container;

    private Set<String> selected = new HashSet<>();


    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ControlPane controlPane;


    @Resource
    private ScoreStyleMapper scoreStyleMapper;

    private VBox left;

    private VBox right;

    @FXML
    public void initialize() {
        show();
    }

    private void show() {
        int count = 2;
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        String[] split = scoreStyle.getStyles().split(ConfigSpliter.SPLITER);
        Font font = Font.font(FontMap.getFont(split[0]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split[1]));

        left = new VBox();
        right = new VBox();
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);


        container.getChildren().clear();
        container.setSpacing(10);

        left.setPrefWidth(100);
        left.setPrefHeight(1000);
        left.setSpacing(10);

        right.setPrefWidth(100);
        right.setPrefHeight(1000);
        right.setSpacing(10);

//        CheckBox selectAll = new CheckBox("全选");
//        selectAll.setPrefWidth(100);
//        selectAll.setFont(Font.font(24));
        container.getChildren().addAll(left, right);
        List<Teams> teams = TeamRuntime.getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Teams team = teams.get(i);
            VBox box = new VBox();
            box.setPrefWidth(Double.parseDouble(cfgArr[19]));
            box.setPrefHeight(Double.parseDouble(cfgArr[20]) * 2 + 20);
            box.setSpacing(1);


            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(Double.parseDouble(cfgArr[19]), Double.parseDouble(cfgArr[20]));
            Text teamName = new Text(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
            teamName.prefWidth(Double.parseDouble(cfgArr[19]));
            left.setPrefWidth(Double.parseDouble(cfgArr[19]));
            right.setPrefWidth(Double.parseDouble(cfgArr[19]));
            teamName.prefHeight(Double.parseDouble(cfgArr[20]));
            teamName.setFill(Color.valueOf(cfgArr[15]));
            stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]),  new CornerRadii(10), Insets.EMPTY)));
            teamName.setFont(font);
            teamName.setUnderline(Boolean.valueOf(split[5]));
            teamName.setStrikethrough(Boolean.valueOf(split[2]));
            if (Boolean.valueOf(split[3])) {
                teamName.setStroke(teamName.getFill());
                teamName.setStrokeWidth(1);
            } else {
                teamName.setStroke(null);

            }
            teamName.getTransforms().clear();
            stackPane.getChildren().add(teamName);
            if (Boolean.valueOf(split[4])) {
                teamName.getTransforms().add(new Shear(-0.3, 0));
            }
            box.getChildren().add(stackPane);


            Text teamScore = new Text(team.getCurrentScore() + "");
            StackPane stackPane1 = new StackPane();
            stackPane1.setPrefSize(Integer.parseInt(cfgArr[19]), Integer.parseInt(cfgArr[20]));
            teamScore.setTextAlignment(TextAlignment.CENTER);
            teamScore.prefWidth(Double.parseDouble(cfgArr[19]));
            teamScore.prefHeight(Double.parseDouble(cfgArr[20]));
            teamScore.setFill(Color.valueOf(cfgArr[21]));
            stackPane1.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));

            teamScore.setFont(font);
            teamScore.setUnderline(Boolean.valueOf(split[5]));
            teamScore.setStrikethrough(Boolean.valueOf(split[2]));
            container.setSpacing(Integer.parseInt(split[6]));
            if (Boolean.valueOf(split[3])) {
                teamScore.setStroke(teamScore.getFill());
                teamScore.setStrokeWidth(1);
            } else {
                teamScore.setStroke(null);

            }
            teamScore.getTransforms().clear();
            stackPane1.getChildren().add(teamScore);
            box.getChildren().add(stackPane1);

            if (i % 2 == 0) {
                left.getChildren().add(box);
            } else {
                right.getChildren().add(box);
            }

            Color statusColor = Color.WHITE;
            int statusSize = (int) new Text().getFont().getSize();

            if (cfgArr.length >= 33) {
                statusSize = Integer.parseInt(cfgArr[31]);
                statusColor = Color.valueOf(cfgArr[32]);
            }

            Font statusFont = Font.font("Microsoft YaHei", statusSize);

            // 面板分数调整（Model60 模式）
            Stages currentStage = controlPane.getCurrentStage();
            if (currentStage != null) {
                Integer m6 = currentStage.getM6();
                Integer questionType = currentStage.getQuestionType();
                if (questionType == 6 && m6 == 0) {
                    List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
                    Map<String, Model60RuntimeData.ModelData> answerMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName(), x -> x));
                    count = 3;
                    StackPane stackPane2 = new StackPane();
                    stackPane2.setPrefSize(Integer.parseInt(cfgArr[19]), Integer.parseInt(cfgArr[20]));
                    Model60RuntimeData.ModelData userData = answerMap.get(team.getFullName());
                    Text userSelect = new Text();
                    // fix bug: 用户未选择答案的情况
                    userSelect.setTextAlignment(TextAlignment.CENTER);
                    userSelect.prefWidth(Integer.parseInt(cfgArr[19]));
                    userSelect.prefHeight(Integer.parseInt(cfgArr[20]));
                    userSelect.setFont(statusFont);
                    userSelect.setFill(statusColor);
                    if (team.getStatus() == 1) {
                        // 回答但是未展示答案
                        stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), new CornerRadii(10), Insets.EMPTY)));
                    } else {
                        if (StringUtils.isEmpty(userData.getUserSelect())) {
                            if (team.getStatus() == 3) {
                                // 答题已结束，展示错误的状态
                                stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                            } else {
                                // 答题未结束，展示未选择状态
                                stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            userSelect.setText(userData.getUserSelect());
                            if (userData.getAnswer().equals(userData.getUserSelect())) {
                                stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), new CornerRadii(10), Insets.EMPTY)));
                            } else {
                                stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                            }
                        }
                    }

                    stackPane2.getChildren().add(userSelect);
                    box.getChildren().add(stackPane2);
                }
                else if (questionType == 6 && m6 == 1) {
                    List<Model61RuntimeData.ModelData> runtimeData = Model61RuntimeData.getRuntimeData();
                    Map<String, Model61RuntimeData.ModelData> dataMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName() + "-" + x.getUserName(), x -> x));
                    count = 3;
                    HBox memberStatus = new HBox();
                    memberStatus.setSpacing(1);
                    memberStatus.setPadding(new Insets(1, 1, 1,1));
                    memberStatus.setPrefWidth(Integer.parseInt(cfgArr[19]));
                    String members = team.getMembers();
                    if (!StringUtils.isEmpty(members)) {
                        String[] ms = members.split(",");
                        double w = Integer.parseInt(cfgArr[19]) * 1.0 / ms.length;
                        for (int x = 0; x < ms.length; x++) {
                            Model61RuntimeData.ModelData modelData = dataMap.get(team.getFullName() + "-" + ms[x]);
                            StackPane stackPane2 = new StackPane();
                            stackPane2.setPrefSize(w, Integer.parseInt(cfgArr[20]));
                            Text l = new Text((x + 1) + "");
                            l.setTextAlignment(TextAlignment.CENTER);
                            stackPane2.getChildren().add(l);
                            l.prefHeight(Integer.parseInt(cfgArr[20]));
                            l.prefWidth(w);
                            l.setFont(statusFont);
                            l.setFill(statusColor);
                            if (team.getStatusList().get(x) == 1) {
                                stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), new CornerRadii(10), Insets.EMPTY)));
                            } else {
                                if (StringUtils.isEmpty(modelData.getUserSelect())) {
                                    if (team.getStatusList().get(x) == 3) {
                                        // 答题已结束，展示错误的状态
                                        stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                                    } else {
                                        // 答题未结束，展示未选择状态
                                        stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                                    }
                                    //stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                                } else {
                                    if (modelData.getUserSelect().equals(modelData.getAnswer())) {
                                        stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), new CornerRadii(10), Insets.EMPTY)));
                                    } else {
                                        stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                                    }
                                }
                            }

                            // 居中
                            memberStatus.getChildren().add(stackPane2);
                        }
                        box.getChildren().add(memberStatus);
                    }
                }
                else if (questionType == 7) {
                    count = 3;
                }

            }

            box.setPrefHeight(Integer.parseInt(cfgArr[20]) * count + count * 10);

        }
//        selectAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {

//
//            }
//        });

        container.setLayoutX(Double.parseDouble(cfgArr[17]));
        container.setLayoutY(Double.parseDouble(cfgArr[18]));
        container.setPrefWidth(1000);
        container.setPrefHeight(1000);
    }

    @Override
    public void close() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.hide();
    }

    @Override
    public void shows() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.show();
    }

    public Set<String> getSelected() {
        return selected;
    }

    @Override
    public void reload() {
        show();
    }

    @Override
    public Node getPane() {
        return container;
    }
}
