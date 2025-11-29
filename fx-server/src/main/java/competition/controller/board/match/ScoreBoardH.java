package competition.controller.board.match;

import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.preview.QuestionPreviewPreview;
import competition.controller.preview.RankPane;
import competition.controller.preview.ShowAnswerPreview;
import competition.entity.Configs;
import competition.entity.ScoreStyle;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreStyleMapper;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ScoreBoardH implements BoardPane {

    @FXML
    private HBox container;

    @Resource
    private ConfigsMapper configsMapper;

    private final Set<String> selected = new HashSet<>();

    @Resource
    private ControlPane controlPane;

    @Resource
    private ScoreStyleMapper scoreStyleMapper;

    @FXML
    public void initPage() {
        show();
    }

    @Override
    public void reloadColor() {

    }

    private void show() {
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        String[] split = scoreStyle.getStyles().split(ConfigSpliter.SPLITER);

        ScoreStyle scoreStyle1 = scoreStyleMapper.selectById(3);
        String[] ssplit = scoreStyle1.getStyles().split(ConfigSpliter.SPLITER);

        Font font = FontMap.load(split[0], ScreenAdapter.calculateExX(Integer.parseInt(split[1])));
        Font sfont = FontMap.load(ssplit[0], ScreenAdapter.calculateExX(Integer.parseInt(ssplit[1])));


        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);



        container.getChildren().clear();
        container.setSpacing(ScreenAdapter.calculateExX(20));

        Color statusColor = Color.WHITE;
        double statusSize =  new Text().getFont().getSize();

        if (cfgArr.length >= 33) {
            statusSize = ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[31]));
            statusColor = Color.valueOf(cfgArr[32]);
        }

        String sf = cfgArr.length >= 41 ? cfgArr[40] : FontMap.defaultFontName();
        boolean sb = cfgArr.length >= 42 && Boolean.parseBoolean(cfgArr[41]);

        Font statusFont = FontMap.load(sf, statusSize);
        List<Teams> teams = TeamRuntime.getTeams();
        for (Teams team : teams) {
            int count = 1;
            VBox box = new VBox();
            box.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])));
            box.setSpacing(1);
            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])),
                ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));

            Text teamName = new Text(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
//            teamName.prefWidth(Integer.parseInt(cfgArr[19]));
//            teamName.prefHeight(Integer.parseInt(cfgArr[20]));
            teamName.setFill(Color.valueOf(cfgArr[15]));
            stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]), new CornerRadii(10), Insets.EMPTY)));
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


            Text teamScore = new Text(team.getTeamScore() + "");
            StackPane stackPane1 = new StackPane();
            stackPane1.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])), ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
            teamScore.setTextAlignment(TextAlignment.CENTER);

            teamScore.setFill(Color.valueOf(cfgArr[21]));
            stackPane1.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));
            teamScore.setFont(sfont);
            teamScore.setUnderline(Boolean.valueOf(ssplit[5]));
            teamScore.setStrikethrough(Boolean.valueOf(ssplit[2]));
            container.setSpacing(ScreenAdapter.calculateExX(Integer.parseInt(split[6])));
            if (Boolean.valueOf(ssplit[3])) {
                teamScore.setStroke(teamScore.getFill());
                teamScore.setStrokeWidth(1);
            } else {
                teamScore.setStroke(null);

            }
            teamScore.getTransforms().clear();
            stackPane1.getChildren().add(teamScore);
            if (Boolean.valueOf(ssplit[4])) {
                teamScore.getTransforms().add(new Shear(-0.3, 0));
            }
            if (RuntimeUtil.showScoreBar) {
                count ++;
                box.getChildren().add(stackPane1);
            }


            // 抢答分时面板
            if (RuntimeUtil.showBuzzBar) {
                count ++;
                Long timeVal = RuntimeUtil.buzzTimeMap.get(team.getShortName());
                Text buzzTime = new Text("0");
                if (timeVal != null) {
                    buzzTime.setText(NumberUtil.fix2Point(timeVal / 1000000000.0));
                }
                StackPane buzzPane = new StackPane();
                buzzPane.setMaxHeight(Double.MAX_VALUE);
                VBox.setVgrow(buzzPane, Priority.NEVER);

                buzzPane.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])), ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20])));
                buzzTime.setTextAlignment(TextAlignment.CENTER);
                buzzTime.setFill(Color.valueOf(cfgArr[21]));

                Boolean b = RuntimeUtil.buzzResult.get(team.getShortName());
                Color color = null;
                if (b == null) {
                    // 未抢答
                    color =  Color.valueOf(cfgArr[25]);
                } else if (b) {
                    // 已抢答成功
                    color = Color.valueOf(cfgArr[27]);
                } else {
                    // 抢答违规
                    color = Color.valueOf(cfgArr[28]);
                }
                buzzPane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), Insets.EMPTY)));
                buzzTime.setFont(Font.font(ScreenAdapter.calculateExX(12)));
                buzzTime.setUnderline(Boolean.valueOf(split[5]));
                buzzTime.setStrikethrough(Boolean.valueOf(split[2]));

                if (cfgArr.length >= 37) {
                    buzzTime.setFont( Font.font(ScreenAdapter.calculateExX(Double.parseDouble(cfgArr[36]))));
                } else {
                    buzzTime.setFont(Font.font(ScreenAdapter.calculateExX(12)));
                }
                if (cfgArr.length >= 38) {
                    buzzTime.setFill(Color.valueOf(cfgArr[37]));
                }

                if (cfgArr.length >= 39) {
                    Font f = FontMap.load(cfgArr[38], buzzTime.getFont().getSize());
                    buzzTime.setFont(f);
                }

                if (cfgArr.length >= 40) {
                    if (Boolean.parseBoolean(cfgArr[39])) {
                        buzzTime.setStroke(buzzTime.getFill());
                        buzzTime.setStrokeWidth(1);
                    } else {
                        buzzTime.setStroke(null);
                    }
                } else {
                    buzzTime.setStroke(null);
                }

                buzzTime.getTransforms().clear();

                buzzPane.getChildren().add(buzzTime);
                box.getChildren().add(buzzPane);
            }


            // 面板分数调整（Model60 模式）
            Stages currentStage = controlPane.getCurrentStage();
            if (currentStage != null) {
                Integer m6 = currentStage.getM6();
                Integer questionType = currentStage.getQuestionType();
                if (questionType == 6 && controlPane.getPreviewPane() instanceof QuestionPreviewPreview){
                    if (m6 == 0) {
                        List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
                        Map<String, Model60RuntimeData.ModelData> answerMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName(), x -> x));
                        count ++;
                        StackPane stackPane2 = new StackPane();
                        stackPane2.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])), ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
                        Model60RuntimeData.ModelData userData = answerMap.get(team.getFullName());
                        Text userSelect = new Text();
                        // fix bug: 用户未选择答案的情况
                        userSelect.setTextAlignment(TextAlignment.CENTER);
//                    userSelect.prefWidth(Integer.parseInt(cfgArr[19]));
//                    userSelect.prefHeight(Integer.parseInt(cfgArr[20]));
                        userSelect.setFont(statusFont);
                        userSelect.setFill(statusColor);
                        if (sb) {
                            userSelect.setStroke(userSelect.getFill());
                            userSelect.setStrokeWidth(1);
                        } else {
                            userSelect.setStroke(null);
                        }
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
                                userSelect.setText(userData.getUserSelect().replace("\u00A0", " "));
                                if (userData.getAnswer().equals(userData.getUserSelect())) {
                                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), new CornerRadii(10), Insets.EMPTY)));
                                } else {
                                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                                }
                            }
                        }


                        if (Objects.equals(1, currentStage.getAnswerSingle()) && controlPane.getPreviewPane() instanceof ShowAnswerPreview) {

                        } else {
                            if (Objects.equals(1, currentStage.getAnswerSingle())) {
                                userSelect.setText("");
                            }
                            stackPane2.getChildren().add(userSelect);
                            box.getChildren().add(stackPane2);
                        }

                    }
                    else if (m6 == 1) {
                        List<Model61RuntimeData.ModelData> runtimeData = Model61RuntimeData.getRuntimeData();
                        Map<String, Model61RuntimeData.ModelData> dataMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName() + "-" + x.getUserName(), x -> x));
                        count ++;
                        HBox memberStatus = new HBox();
                        memberStatus.setSpacing(1);
                        memberStatus.setPadding(new Insets(1, 1, 1,1));
                        memberStatus.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])));
                        String members = team.getMembers();
                        if (!StringUtils.isEmpty(members)) {
                            String[] ms = members.split(",");
                            double w = Integer.parseInt(cfgArr[19]) * 1.0 / ms.length;
                            for (int i = 0; i < ms.length; i++) {
                                Model61RuntimeData.ModelData modelData = dataMap.get(team.getFullName() + "-" + ms[i]);
                                StackPane stackPane2 = new StackPane();
                                stackPane2.setPrefSize(ScreenAdapter.calculateExX(w), ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
                                Text l = new Text((i + 1) + "");
                                l.setTextAlignment(TextAlignment.CENTER);
                                stackPane2.getChildren().add(l);
                                l.setFont(statusFont);
                                l.setFill(statusColor);
                                if (sb) {
                                    l.setStroke(l.getFill());
                                    l.setStrokeWidth(1);
                                } else {
                                    l.setStroke(null);
                                }
                                if (team.getStatusList().get(i) == 1) {
                                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), new CornerRadii(10), Insets.EMPTY)));
                                } else {
                                    if (StringUtils.isEmpty(modelData.getUserSelect())) {
                                        if (team.getStatusList().get(i) == 3) {
                                            // 答题已结束，展示错误的状态
                                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                                        } else {
                                            // 答题未结束，展示未选择状态
                                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                                        }
                                        //stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                                    } else {
                                        if (modelData.getUserSelect().replace("\u00A0", " ").equals(modelData.getAnswer().replace("\u00A0", " "))) {
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
                }
                else if (questionType == 7) {
                    count ++;
                }

            }
            box.setPrefHeight(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[20]) * count + count - 1));
            container.getChildren().add(box);
        }
//        CheckBox selectAll = new CheckBox("全选");
//        selectAll.setPrefWidth(100);
//        selectAll.setFont(Font.font(24));
//        container.getChildren().add(selectAll);
//        selectAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {

//            }
//        });

        double prefWidth = ScreenAdapter.calculateExX(Double.parseDouble(cfgArr[19])) * teams.size() + container.getSpacing() * teams.size();
        container.setPrefWidth(prefWidth);
        if (!container.getChildren().isEmpty()) {
            int size = ((VBox)container.getChildren().get(0)).getChildren().size();
            container.setPrefHeight(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20]) * size + size - 1));
        }
        ExtendedScreen.scoreBoardStage.setWidth(container.getPrefWidth());
        ExtendedScreen.scoreBoardStage.setHeight(container.getPrefHeight());
    }


    public Set<String> getSelected() {
        return selected;
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
        reload();
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
