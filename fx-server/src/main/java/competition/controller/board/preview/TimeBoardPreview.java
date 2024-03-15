package competition.controller.board.preview;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.match.ReloadPane;
import competition.controller.preview.PreviewPane;
import competition.util.ExtendedScreen;
import competition.util.PreviewStack;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class TimeBoardPreview extends PreviewPane {
    
    @FXML
    private AnchorPane container;

    private String timeValue = "00秒";

    private Timeline timeline;

    private int timeVal;

    private Label teamScore = new Label();
    
    private ControlPane controlPane;
    
    
    
    @FXML
    public void initialize(){
        controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        container.getChildren().add(teamScore);
        teamScore.setTextAlignment(TextAlignment.CENTER);
        teamScore.setAlignment(Pos.CENTER);
        teamScore.setFont(Font.font(64));
        teamScore.setTextFill(Color.RED);
       
    }
    
    @Override
    public void reload() {
        showData();
    }
    
    private void showData(){
        teamScore.setText(timeValue);
        if (ExtendedScreen.hasShow()) {
            ReloadPane timeBoard = controlPane.getTimeBoard();
            if (timeBoard == null) {
                timeBoard =  (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
                if (timeBoard != null) {
                    controlPane.setTimeBoard(timeBoard);
                }
            }
            timeBoard.reload(timeVal, timeValue);
        }
    }

    private String format(int timeVal) {
        int minute = timeVal / 60;
        int seconds = timeVal % 60;
        if (minute > 0) {
            if (seconds >= 10) {
                return minute + ":" + seconds;
            }
            return minute + ":0" + seconds;
        } else {
            if (seconds >= 10) {
                return "00:" + seconds ;
            }
            return "0" + seconds;
        }

    }

    @Override
    public void startTiming(boolean order,int time) {
        this.timeVal = time;
        this.timeValue = format(timeVal);
        teamScore.setText(timeValue);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeVal++;
            this.timeValue = format(timeVal);
            PreviewStack.reload(this.timeVal, this.timeValue);
            showData();
        }));
        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard == null) {
            timeBoard =  (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
            if (timeBoard != null) {
                controlPane.setTimeBoard(timeBoard);
                timeBoard.reload(timeVal, timeValue);
            }
        }
        timeline.setCycleCount(-1);
        timeline.playFromStart();
    }

    public void stopTiming() {
        if (timeline != null) {
            timeline.pause();
        }
    }
    
    public void continueTiming(){
        timeline.play();
    }

    @Override
    public void close() {
        timeline.stop();
        timeline = null;
        Stage stage = (Stage) (teamScore.getScene().getWindow());
        stage.hide();
        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard == null) {
            timeBoard =  (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
            if (timeBoard != null) {
                controlPane.setTimeBoard(timeBoard);
            }
        }
        timeBoard.close();
    }


    @Override
    public void shows() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.show();
        if (ExtendedScreen.hasShow()) {
            ReloadPane timeBoard = controlPane.getTimeBoard();
            if (timeBoard == null) {
                timeBoard =  (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
                if (timeBoard != null) {
                    controlPane.setTimeBoard(timeBoard);
                }
            }
            timeBoard.shows();
        }

    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
}
