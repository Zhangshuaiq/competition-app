package competition.controller.board.match;

import competition.controller.board.BoardPane;
import competition.entity.Configs;
import competition.entity.ScoreStyle;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreStyleMapper;
import competition.util.ConfigSpliter;
import competition.util.ExtendedScreen;
import competition.util.TeamRuntime;
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

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ScoreBoardV implements BoardPane {
    
    
    @FXML
    private HBox container;

    private Set<String> selected = new HashSet<>();
    
    
    @Resource
    private ConfigsMapper configsMapper;


    @FXML
    public void initialize(){
        show();
    }

    @Resource
    private ScoreStyleMapper scoreStyleMapper;
    
    
    private void show(){
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);
        
        
        container.getChildren().clear();
        container.setSpacing(10);
        VBox left = new VBox();
        left.setPrefWidth(100);
        left.setPrefHeight(1000);
        left.setSpacing(10);
        
        VBox right = new VBox();
        right.setPrefWidth(100);
        right.setPrefHeight(1000);
        right.setSpacing(10);


        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        String[] split = scoreStyle.getStyles().split(ConfigSpliter.SPLITER);
        Font font = Font.font(split[0], FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(split[1]));
        
        container.getChildren().addAll(left, right);
        List<Teams> teams = TeamRuntime.getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Teams team = teams.get(i);
            VBox box = new VBox();
            box.setPrefWidth(Double.parseDouble(cfgArr[19]));
            box.setPrefHeight(Double.parseDouble(cfgArr[20]) * 2 + 20);
            box.setSpacing(10);


            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(Double.parseDouble(cfgArr[19]), Double.parseDouble(cfgArr[20]));
            Text teamName = new Text(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
            teamName.prefWidth(Double.parseDouble(cfgArr[19]));
            teamName.prefHeight(Double.parseDouble(cfgArr[20]));
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
            teamScore.prefWidth(Double.parseDouble(cfgArr[19]));
            teamScore.prefHeight(Double.parseDouble(cfgArr[20]));
            teamScore.setFill(Color.valueOf(cfgArr[21]));
            stackPane1.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));

            teamScore.setFont(font);
            teamScore.setUnderline(Boolean.valueOf(split[5]));
            teamScore.setStrikethrough(Boolean.valueOf(split[2]));
            if (Boolean.valueOf(split[3])) {
                teamScore.setStroke(teamScore.getFill());
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
        }
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

    public Set<String> getSelected(){
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
