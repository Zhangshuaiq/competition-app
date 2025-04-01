package competition.controller.board.preview;

import competition.ws.WsTeamMemberWaiter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TeamConnectionBarPreview {

    @FXML
    private VBox container;

    private Timeline timeline;

    @FXML
    public void initialize(){

        WsTeamMemberWaiter.initData();


        this.container.setSpacing(10);

        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            refresh();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refresh(){
        this.container.getChildren().clear();
        for (Map.Entry<String, String> entry : WsTeamMemberWaiter.getConnectTeams().entrySet()) {
            String teamId = entry.getKey();
            String clientId = entry.getValue();
            Circle circle = new Circle(10);
            Text text = new Text(teamId);
            if (clientId != null && !clientId.isEmpty()) {
                circle.setFill(Color.GREEN);
            } else {
                circle.setFill(Color.RED);
            }
            HBox item = new HBox(circle, text);
            item.setSpacing(10);
            this.container.getChildren().add(item);
        }
        this.container.autosize();
    }
}
