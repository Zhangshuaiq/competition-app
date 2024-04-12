package competition.controller.board.match;

import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.preview.Model60Preview;
import competition.entity.Configs;
import competition.entity.ScoreStyle;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreStyleMapper;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import competition.util.TeamRuntime;
import competition.ws.Model60RuntimeData;
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
public class ScoreBoardH implements BoardPane {
    
    @FXML
    private HBox container;
    
    @Resource
    private ConfigsMapper configsMapper;
    @Resource
    private ScoreStyleMapper scoreStyleMapper;
    
    @Resource
    private ControlPane controlPane;
    
    
    @FXML
    public void initialize(){
        show();
    }
    
    private void show(){
        Configs configs = configsMapper.selectById(1);
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        String[] split = scoreStyle.getStyles().split(ConfigSpliter.SPLITER);
        Font font = Font.font(FontMap.getFont(split[0]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split[1]));
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);
        container.getChildren().clear();
        container.setSpacing(20);


        List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
        Map<String, Model60RuntimeData.ModelData> answerMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName(), x -> x));
        
        List<Teams> teams = TeamRuntime.getTeams();
        for (Teams team : teams) {
            VBox box = new VBox();
            box.setPrefWidth(Integer.parseInt(cfgArr[19]));
            box.setPrefHeight(Integer.parseInt(cfgArr[20]) * 2 + 20);
            box.setSpacing(10);
            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(Integer.parseInt(cfgArr[19]), Integer.parseInt(cfgArr[20]));
            Text teamName = new Text(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
            teamName.prefWidth(Integer.parseInt(cfgArr[19]));
            teamName.prefHeight(Integer.parseInt(cfgArr[20]));
            teamName.setFill(Color.valueOf(cfgArr[15]));
            stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]), CornerRadii.EMPTY, Insets.EMPTY)));
            teamName.setFont(font);
            teamName.setUnderline(Boolean.valueOf(split[5]));
            teamName.setStrikethrough(Boolean.valueOf(split[2]));
            if (Boolean.valueOf(split[3])) {
                teamName.setStroke(teamName.getFill());
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
            teamScore.prefWidth(Integer.parseInt(cfgArr[19]));
            teamScore.prefHeight(Integer.parseInt(cfgArr[20]));
            teamScore.setFill(Color.valueOf(cfgArr[21]));
            stackPane1.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));
            teamScore.setFont(font);
            teamScore.setUnderline(Boolean.valueOf(split[5]));
            teamScore.setStrikethrough(Boolean.valueOf(split[2]));
            container.setSpacing(Integer.parseInt(split[6]));
            if (Boolean.valueOf(split[3])) {
                teamScore.setStroke(teamScore.getFill());
            } else {
                teamScore.setStroke(null);
            }
            teamScore.getTransforms().clear();
            stackPane1.getChildren().add(teamScore);
            box.getChildren().add(stackPane1);

            // 面板分数调整（Model60 模式）
            if (controlPane.getQuestionPreview() instanceof Model60Preview) {
                StackPane stackPane2 = new StackPane();
                stackPane2.setPrefSize(Integer.parseInt(cfgArr[19]), Integer.parseInt(cfgArr[20]));
                Model60RuntimeData.ModelData userData = answerMap.get(team.getFullName());
                Text userSelect = new Text(userData.getUserSelect());
                userSelect.setTextAlignment(TextAlignment.CENTER);
                userSelect.prefWidth(Integer.parseInt(cfgArr[19]));
                userSelect.prefHeight(Integer.parseInt(cfgArr[20]));
                userSelect.setFill(Color.valueOf(cfgArr[21]));
                if (StringUtils.isEmpty(userData.getUserSelect())) {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
                } else {
                    if (userData.getAnswer().equals(userData.getUserSelect())) {
                        stackPane2.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
                    } else {
                        stackPane2.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
                    }
                }
                stackPane2.getChildren().add(userSelect);
                box.getChildren().add(stackPane2);
            }
            
            
            container.getChildren().add(box);
        }
        
        container.setLayoutX(Double.parseDouble(cfgArr[17]));
        container.setLayoutY(Double.parseDouble(cfgArr[18]));
        container.setPrefWidth((Double.parseDouble(cfgArr[19]) + 20) * teams.size());
        container.setPrefHeight(Double.parseDouble(cfgArr[20]) * 2 + 30);
    }

    private void showScoreBoard(){
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        List<Teams> teams = TeamRuntime.getTeams();
        List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
        Map<String, Model60RuntimeData.ModelData> answerMap = runtimeData.stream().collect(Collectors.toMap(x -> x.getTeamName(), x -> x));
        for (Teams team : teams) {
            VBox box = new VBox();
            box.setPrefWidth(Integer.parseInt(cfgArr[19]));
            box.setPrefHeight(Integer.parseInt(cfgArr[20]) * 2 + 20);
            box.setSpacing(10);
            Label teamName = new Label(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
            teamName.setAlignment(Pos.CENTER);
            teamName.setPrefWidth(Integer.parseInt(cfgArr[19]));
            teamName.setPrefHeight(Integer.parseInt(cfgArr[20]));
            teamName.setTextFill(Color.valueOf(cfgArr[15]));
            teamName.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]), CornerRadii.EMPTY, Insets.EMPTY)));
            box.getChildren().add(teamName);

            Label teamScore = new Label(team.getCurrentScore() + "");
            teamScore.setTextAlignment(TextAlignment.CENTER);
            teamScore.setAlignment(Pos.CENTER);
            teamScore.setPrefWidth(Integer.parseInt(cfgArr[19]));
            teamScore.setPrefHeight(Integer.parseInt(cfgArr[20]));
            teamScore.setTextFill(Color.valueOf(cfgArr[21]));
            teamScore.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));
            box.getChildren().add(teamScore);

            Model60RuntimeData.ModelData userData = answerMap.get(team.getFullName());
            Label userSelect = new Label(userData.getUserSelect());
            userSelect.setTextAlignment(TextAlignment.CENTER);
            userSelect.setAlignment(Pos.CENTER);
            userSelect.setPrefWidth(Integer.parseInt(cfgArr[19]));
            userSelect.setPrefHeight(Integer.parseInt(cfgArr[20]));
            userSelect.setTextFill(Color.valueOf(cfgArr[21]));
            if (StringUtils.isEmpty(userData.getUserSelect())) {
                userSelect.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), Insets.EMPTY)));
            } else {
                if (userData.getAnswer().equals(userData.getUserSelect())) {
                    userSelect.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
                } else {
                    userSelect.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
                }
            }
            box.getChildren().add(userSelect);
        }


//        scoreBoard.setLayoutX(Integer.parseInt(cfgArr[17]));
//        scoreBoard.setLayoutY(Integer.parseInt(cfgArr[18]));
//        scoreBoard.setPrefWidth((Integer.parseInt(cfgArr[19]) + 20) * teams.size());
//        scoreBoard.setPrefHeight(Integer.parseInt(cfgArr[20]) * 2 + 30);
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

    @Override
    public void reload() {
        show();
    }

    @Override
    public Node getPane() {
        return container;
    }

    @Override
    public Set<String> getSelected() {
        return new HashSet<>();
    }
}
