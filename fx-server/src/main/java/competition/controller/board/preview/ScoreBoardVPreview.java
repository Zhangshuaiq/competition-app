package competition.controller.board.preview;

import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.board.match.ScoreBoardH;
import competition.controller.board.match.ScoreBoardV;
import competition.controller.match.Model61;
import competition.controller.match.ReloadPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ScoreBoardVPreview extends PreviewPane implements BoardPane {
    
    
    @FXML
    private HBox container;

    private Set<String> selected = new HashSet<>();
    
    
    @Resource
    private ConfigsMapper configsMapper;
    
    @Resource
    private ControlPane controlPane;


    @FXML
    public void initialize(){
        show();
    }

    private void show(){
        if (ExtendedScreen.hasShow()) {
            BoardPane scoreBoard = controlPane.getScoreBoard();
            if (scoreBoard == null) {
                scoreBoard = (ScoreBoardV) ExtendedScreen.scoreWithPos("/view/board/match/ScoreBoardV.fxml", "", "/view/style.css", false);
                if (scoreBoard != null) {
                    controlPane.setScoreBoard(scoreBoard);
                    scoreBoard.reload();
                }
            }
        }
        
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

        container.getChildren().addAll(left, right);
        List<Teams> teams = TeamRuntime.getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Teams team = teams.get(i);
            VBox box = new VBox();
            box.setPrefWidth(Double.parseDouble(cfgArr[19]));
            box.setPrefHeight(Double.parseDouble(cfgArr[20]) * 2 + 20);
            box.setSpacing(10);


            Label teamName = new Label(team.getShortName());
            teamName.setTextAlignment(TextAlignment.CENTER);
            teamName.setAlignment(Pos.CENTER);
            teamName.setPrefWidth(Double.parseDouble(cfgArr[19]));
            teamName.setPrefHeight(Double.parseDouble(cfgArr[20]));
            teamName.setTextFill(Color.valueOf(cfgArr[15]));
            teamName.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]), CornerRadii.EMPTY, Insets.EMPTY)));
            box.getChildren().add(teamName);

            Label teamScore = new Label(team.getCurrentScore() + "");
            teamScore.setTextAlignment(TextAlignment.CENTER);
            teamScore.setAlignment(Pos.CENTER);
            teamScore.setPrefWidth(Double.parseDouble(cfgArr[19]));
            teamScore.setPrefHeight(Double.parseDouble(cfgArr[20]));
            teamScore.setTextFill(Color.valueOf(cfgArr[21]));
            teamScore.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), new CornerRadii(10), Insets.EMPTY)));
            box.getChildren().add(teamScore);

            if (i <= teams.size() / 2) {
                left.getChildren().add(box);
            } else {
                right.getChildren().add(box);
            }

            box.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Border border = box.getBorder();
                    if (border == null) {
                        box.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
                        selected.add(teamName.getText());
                    } else {
                        box.setBorder(null);
                        selected.remove(teamName.getText());
                    }
                }
            });
        }
        container.setLayoutX(Double.parseDouble(cfgArr[17]));
        container.setLayoutY(Double.parseDouble(cfgArr[18]));
        container.setPrefWidth((Double.parseDouble(cfgArr[19]) + 20) * teams.size());
        container.setPrefHeight(Double.parseDouble(cfgArr[20]) * 2 + 30);
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
        if (ExtendedScreen.hasShow()) {
            BoardPane scoreBoard = controlPane.getScoreBoard();
            if (scoreBoard == null) {
                scoreBoard = (ScoreBoardV) ExtendedScreen.scoreWithPos("/view/board/match/ScoreBoardV.fxml", "", "/view/style.css", false);
                if (scoreBoard != null) {
                    controlPane.setScoreBoard(scoreBoard);
                }
            }
            scoreBoard.reload();
        }

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
