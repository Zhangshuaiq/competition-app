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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class BuzzTimeBoardPreview extends PreviewPane {

    @FXML
    private AnchorPane container;

    private String timeValue = "0";

    private Timeline timeline;

    private int timeVal;

    StackPane stackPane = new StackPane();

    private Text teamScore = new Text();

    private ControlPane controlPane;


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
        timeline = new Timeline(new KeyFrame(Duration.seconds(speed), e -> {
            int alertValue = Integer.parseInt(controlPane.getAlertTime());
            timeVal--;
            if (timeVal == 0) {
                timeline.stop();
                timeline = null;
                MusicPlayer.playWithCallback(MusicResource.BUZZ_START, () -> {
                    RuntimeUtil.buzzCalculate = System.nanoTime();
                    buzzTiming(limitTime);
                });


            } else {
                if (timeVal <= alertValue) {
                    MusicPlayer.play(MusicResource.BUZZ_TIME(timeVal));
                }
            }
            this.timeValue = format(timeVal);
            // 不显示 0
            if (this.timeValue.equals("0")) {
                this.timeValue = "";
            }
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
        MusicPlayer.play(MusicResource.BUZZ_TIME(timeVal));
    }

    @Override
    public void hideTime() {
        teamScore.setVisible(false);
        ReloadPane timeBoard = controlPane.getBuzzTimeBoard();
        if (timeBoard != null) {
            timeBoard.hideTime();
        }
    }

    @FXML
    public void initialize() {
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
