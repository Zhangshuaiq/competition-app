package competition.controller.board.preview;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.match.ReloadPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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

    StackPane stackPane = new StackPane();

    private Text teamScore = new Text();

    private ControlPane controlPane;
    
    private ConfigsMapper configsMapper;
    
    private int endTime;
    
    private WsMessageHandler messageHandler;



    @Override
    public void hideTime() {
        teamScore.setVisible(false);
        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard != null) {
            timeBoard.hideTime();
        }
    }

    @FXML
    public void initPage() {
        stackPane.getChildren().add(teamScore);
        container.getChildren().add(stackPane);
        showData();
    }

    @Override
    public void reload() {
        showData();
    }

    private void showData() {
        controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        configsMapper = JavaFxApplication.getApplicationContext().getBean(ConfigsMapper.class);
        messageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);
        Navigator.timeBoardStage.setX(ScreenAdapter.calculateMainX(Double.parseDouble(split[29])));
        Navigator.timeBoardStage.setY(ScreenAdapter.calculateMainY(Double.parseDouble(split[30])));
        teamScore.setTextAlignment(TextAlignment.CENTER);
        teamScore.setFont(FontMap.load(split[33], ScreenAdapter.calculateMainX(Integer.parseInt(split[24]))));
        
        teamScore.setFill(Color.valueOf(split[23]));
        teamScore.setText(timeValue);
        if (split.length >= 35) {
            stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(split[34]), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        if (split.length >= 36) {
            if (Boolean.parseBoolean(split[35])) {
                teamScore.setStroke(teamScore.getFill());
                teamScore.setStrokeWidth(1);
            } else {
                teamScore.setStroke(null);
            }
        }



        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard == null) {
            timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
            controlPane.setTimeBoard(timeBoard);
        }
        if (timeBoard != null) {
            timeBoard.reload(timeVal, timeValue);
        }
    }

    private String format(int timeVal) {
        int minute = timeVal / 60;
        int seconds = timeVal % 60;
        if (minute > 9) {
            if (seconds >= 10) {
                return minute + ":" + seconds;
            }
            return minute + ":0" + seconds;
        }
        if (minute > 0) {
            if (seconds >= 10) {
                return "0" + minute + ":" + seconds;
            }
            return "0" + minute + ":0" + seconds;
        } else {
            if (seconds >= 10) {
                return "00:" + seconds;
            }
            return "00:0" + seconds;
        }

    }


    @Override
    public void startTiming(boolean order, int time) {
        // 避免二次运行
        if (timeline != null) {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                return;
            }
        }
        if (order) {
            this.timeVal = 0;
            this.timeValue = format(timeVal);
            endTime = time;
            messageHandler.sendQuestionTime(this.timeVal + "", "false");
        } else {
            this.timeVal = time;
            this.timeValue = format(timeVal);
            messageHandler.sendQuestionTime(this.timeVal + "", "false");
        }
        teamScore.setVisible(true);
        teamScore.setText(timeValue);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (order) {
                timeVal++;
                if (timeVal == endTime) {
                    MusicPlayer.play(MusicResource.SECOND_0);
                    close();
                    controlPane.resetTimeBtn();
                }
            } else {
                int alertValue = Integer.parseInt(controlPane.getAlertTime());
                timeVal--;
                if (timeVal == 0) {
                    MusicPlayer.play(MusicResource.SECOND_0);
                    close();
                    controlPane.resetTimeBtn();
                } else {
                    if (timeVal <= alertValue) {
                        MusicPlayer.play(MusicResource.SECOND_PREFIX + timeVal + ".mp3");
                    }
                }
            }
            this.timeValue = format(timeVal);
            messageHandler.sendQuestionTime(timeVal + "", "false");
            messageHandler.sendModel7Time(timeVal + "");
            for (int i = 0; i < Model7RuntimeData.getSeconds().size(); i++) {
                Model7RuntimeData.getTimes().set(i, timeValue);
                if (!Model7RuntimeData.getFinished().get(i)) {
                    Model7RuntimeData.getSeconds().set(i, timeVal);
                    Model7RuntimeData.getFts().set(i, timeValue);
                }
            }

            // 团队模式更新时间数值
            Model60RuntimeData.freshTime(timeVal);
            Model61RuntimeData.freshTime(timeVal);
            
            // 已结束
            if(timeline == null) {
                controlPane.stopOnTime();
            }
            PreviewStack.reload(this.timeVal, this.timeValue);
            showData();
        }));
        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard == null) {
            timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
        }
        if (timeBoard != null) {
            controlPane.setTimeBoard(timeBoard);
            timeBoard.shows();
            timeBoard.reload(timeVal, timeValue);
        }
        timeline.setCycleCount(-1);
        timeline.playFromStart();
    }

    public void stopTiming() {
        if (timeline != null) {
            timeline.pause();
        }
    }

    public void continueTiming() {
        timeline.play();
    }

    @Override
    public void close() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
      
        Stage stage = (Stage) (teamScore.getScene().getWindow());
        stage.hide();
        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard == null) {
            timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
        }
        if (timeBoard != null) {
            controlPane.setTimeBoard(timeBoard);
            timeBoard.close();
        }
    }


    @Override
    public void shows() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.show();
        if (ExtendedScreen.hasShow()) {
            ReloadPane timeBoard = controlPane.getTimeBoard();
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
            }
            if (timeBoard != null) {
                controlPane.setTimeBoard(timeBoard);
                timeBoard.shows();

            }
        }
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
}
