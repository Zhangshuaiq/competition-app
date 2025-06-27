package competition.controller.board.preview;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.match.ReloadPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Buzz;
import competition.entity.Configs;
import competition.mapper.BuzzMapper;
import competition.mapper.ConfigsMapper;
import competition.util.ConfigSpliter;
import competition.util.ExtendedScreen;
import competition.util.FontMap;
import competition.util.MusicPlayer;
import competition.util.MusicResource;
import competition.util.Navigator;
import competition.util.PreviewStack;
import competition.util.RuntimeUtil;
import competition.util.ScreenAdapter;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class BuzzTimeBoardPreview extends PreviewPane {

    private static MediaPlayer mediaPlayer;

    @FXML
    private AnchorPane container;

    private String timeValue = "0";

    private volatile boolean stop = false;

    private Timeline timeline;

    private int timeVal;

    StackPane stackPane = new StackPane();

    private Text teamScore = new Text();

    private ControlPane controlPane;

    private void playInternal(double speed, int limitTime, int alertValue){
        if (stop) {
            return;
        }
        // 不显示 0
        if (timeVal == 0) {
            MusicPlayer.playWithCallback(MusicResource.BUZZ_START, () -> {
                RuntimeUtil.buzzCalculate = System.nanoTime();
                buzzTiming(limitTime);
            });
            return;
        }
        this.timeValue = format(timeVal);
        if (this.timeValue.equals("0")) {
            this.timeValue = "";
        }
        PreviewStack.reload(this.timeVal, this.timeValue);
        teamScore.setText(timeValue);
        updateExtendScreen();
        //showData();
        MusicPlayer.playWithCallback(MusicResource.BUZZ_TIME(timeVal), () -> {
            if (timeVal <= alertValue) {
                timeline = new Timeline(new KeyFrame(Duration.seconds(speed), e -> {
                    timeVal--;
                    playInternal(speed, limitTime, alertValue);
                }));
                timeline.setCycleCount(1);
                timeline.play();
            }

        });
    }


    @Override
    public void startBuzz(int time, int limitTime) {

        showData();

        BuzzMapper buzzMapper = JavaFxApplication.getApplicationContext().getBean(BuzzMapper.class);
        Buzz buzz = buzzMapper.selectById(1);
        double speed = 1.0;
        try {
            speed =  Double.parseDouble(buzz.getSpeed());
        } catch (Exception ex) {
            System.out.println("抢答速率设置错误");
        }
        // 避免二次运行
        if (timeline != null) {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                return;
            }
        }
        // 抢答开始等待时间
        this.timeVal = time;
        this.timeValue = format(timeVal);
        teamScore.setVisible(true);
        teamScore.setText(timeValue);
        stop = false;
        playInternal(speed, limitTime, Integer.parseInt(controlPane.getAlertTime()));
        updateExtendScreen();
    }

    private void updateExtendScreen(){
        if (ExtendedScreen.hasShow()) {
            ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.buzzTimeWithPos("/view/board/match/BuzzTimeBoard.fxml", "", "/view/style.css", true);
            }
            if (timeBoard != null) {
                controlPane.setBuzzTimeBoard(timeBoard);
                timeBoard.shows();
                timeBoard.reload(timeVal, timeValue);
            }
        }
    }



    @Override
    public void hideTime() {
        teamScore.setVisible(false);
        ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
        if (timeBoard != null) {
            timeBoard.hideTime();
        }
    }

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
        BuzzMapper buzzMapper = JavaFxApplication.getApplicationContext().getBean(BuzzMapper.class);
        Buzz buzz = buzzMapper.selectById(1);
        teamScore.setTextAlignment(TextAlignment.CENTER);
        teamScore.setFont(FontMap.load(buzz.getFont(), ScreenAdapter.calculateMainX(buzz.getFontSize())));

        teamScore.setFill(Color.valueOf(buzz.getColor()));
        teamScore.setText(timeValue);
        // stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(b), CornerRadii.EMPTY, Insets.EMPTY)));

        if (1 == (buzz.getBold())) {
            teamScore.setStroke(teamScore.getFill());
            teamScore.setStrokeWidth(1);
        } else {
            teamScore.setStroke(null);
        }


        ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
        if (ExtendedScreen.hasShow()) {
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.buzzTimeWithPos("/view/board/match/BuzzTimeBoard.fxml", "", "/view/style.css", true);
                controlPane.setBuzzTimeBoard(timeBoard);
            }
            if (timeBoard != null) {
                timeBoard.reload(timeVal, timeValue);
            }
        }


        Navigator.timeBoardStage.setX(ScreenAdapter.calculateMainX(Double.parseDouble(buzz.getX())));
        Navigator.timeBoardStage.setY(ScreenAdapter.calculateMainY(Double.parseDouble(buzz.getY())));
    }

    private String format(int timeVal) {
        return timeVal + "";
    }

    public void buzzTiming(int time) {
        // 避免二次运行
        if (timeline != null) {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                return;
            }
        }
        this.timeVal = time;
        this.timeValue = format(timeVal);
        double speed = 1.0;
        teamScore.setVisible(true);
        teamScore.setText(timeValue);
        timeline = new Timeline(new KeyFrame(Duration.seconds(speed), e -> {
            int alertValue = Integer.parseInt(controlPane.getAlertTime());
            timeVal--;
            if (timeVal == 0) {
                controlPane.reloadScoreBoard();
                close();
                controlPane.resetBuzzTime();
            }
            this.timeValue = format(timeVal);

            PreviewStack.reload(this.timeVal, this.timeValue);
            showData();
        }));
        if (ExtendedScreen.hasShow()) {
            ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.buzzTimeWithPos("/view/board/match/BuzzTimeBoard.fxml", "", "/view/style.css", true);
            }
            if (timeBoard != null) {
                controlPane.setBuzzTimeBoard(timeBoard);
                timeBoard.shows();
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

    public void continueTiming() {
        timeline.play();
    }

    @Override
    public void close() {
        if (timeline != null) {
            stop = true;
            timeline.stop();
            timeline = null;
        }

        Stage stage = (Stage) (teamScore.getScene().getWindow());
        stage.hide();
        ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
        if (timeBoard != null) {
            controlPane.setBuzzTimeBoard(timeBoard);
            timeBoard.close();
        }
    }


    @Override
    public void shows() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.show();
        if (ExtendedScreen.hasShow()) {
            ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.buzzTimeWithPos("/view/board/match/BuzzTimeBoard.fxml", "", "/view/style.css", true);
            }
            if (timeBoard != null) {
                controlPane.setBuzzTimeBoard(timeBoard);
                timeBoard.shows();

            }
        }
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
}
