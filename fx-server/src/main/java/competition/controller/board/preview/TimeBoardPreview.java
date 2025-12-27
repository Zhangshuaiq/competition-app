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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TimeBoardPreview extends PreviewPane {

    @FXML
    private AnchorPane container;

    private String timeValue = "00:00";

    private Timeline timeline;

    private int timeVal;

    private Label timeLabel = new Label();

    private ControlPane controlPane;

    private ConfigsMapper configsMapper;

    private int endTime;

    private WsMessageHandler messageHandler;

    private volatile double cacheTextWidth;

    private volatile double cacheTextHeight;


    @Override
    public void hideTime() {
        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (timeBoard != null) {
            timeBoard.hideTime();
        }
    }

    @FXML
    public void initPage() {
        container.getChildren().clear();
        container.getChildren().add(timeLabel);
        container.setVisible(RuntimeUtil.showTimeBoard);

        // 设置 Label 在容器中居中
        AnchorPane.setTopAnchor(timeLabel, 0.0);
        AnchorPane.setBottomAnchor(timeLabel, 0.0);
        AnchorPane.setLeftAnchor(timeLabel, 0.0);
        AnchorPane.setRightAnchor(timeLabel, 0.0);

        // 允许Label根据内容自动调整大小
        timeLabel.setMaxWidth(Double.MAX_VALUE);
        timeLabel.setMaxHeight(Double.MAX_VALUE);
        timeLabel.setMinWidth(Region.USE_PREF_SIZE);
        timeLabel.setMinHeight(Region.USE_PREF_SIZE);

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

        // 先设置文本内容
        timeLabel.setText(timeValue);

        // 设置字体和样式
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setTextAlignment(TextAlignment.CENTER);
        timeLabel.setFont(FontMap.load(split[33], ScreenAdapter.calculateMainX(Integer.parseInt(split[24]))));
        timeLabel.setTextFill(Color.valueOf(split[23]));

        // 设置自动换行为false，确保文本在一行显示
        timeLabel.setWrapText(false);

        log.info("TimeBoardPreview reload: {}", timeValue);

        if (split.length >= 35) {
            // 设置背景色
            timeLabel.setBackground(new Background(new BackgroundFill(Color.valueOf(split[34]), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        if (split.length >= 36) {
            if (Boolean.parseBoolean(split[35])) {
                String strokeStyle = "-fx-text-stroke: 1px " + split[23] + ";";
                timeLabel.setStyle(timeLabel.getStyle() + strokeStyle);
            } else {
                // 移除描边样式
                String currentStyle = timeLabel.getStyle();
                if (currentStyle != null) {
                    String newStyle = currentStyle.replaceAll("-fx-text-stroke:[^;]*;", "");
                    timeLabel.setStyle(newStyle);
                }
            }
        }

        // 延迟调整大小，确保字体设置生效后再计算
        Platform.runLater(() -> {
            // 计算文本实际需要的大小
            timeLabel.autosize();
            double textWidth = timeLabel.prefWidth(-1);
            double textHeight = timeLabel.prefHeight(-1);

            if (cacheTextWidth <= 0) {
                cacheTextWidth = textWidth;
                cacheTextHeight = textHeight;
            }

            // 打印大小
            // 确保容器足够大来显示文本
            if (cacheTextWidth > 0 && cacheTextHeight > 0) {
                timeLabel.setPrefWidth(cacheTextWidth);
                timeLabel.setPrefHeight(cacheTextHeight);

                // 同时确保窗口大小足够
                Stage stage = (Stage) container.getScene().getWindow();
                if (stage != null) {
                    // 给窗口设置最小大小，确保能显示完整文本
                    stage.setMinWidth(cacheTextWidth + 40);
                    stage.setMinHeight(cacheTextHeight + 40);

                    // 如果当前窗口太小，则调整大小
                    stage.setWidth(cacheTextWidth + 40);
                    stage.setHeight(cacheTextHeight + 40);
                }
            }
        });

        ReloadPane timeBoard = controlPane.getTimeBoard();
        if (ExtendedScreen.hasConnectedExtend()) {
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
                controlPane.setTimeBoard(timeBoard);
            }
            if (timeBoard != null) {
                if (timeline != null) {
                    timeBoard.initPage();
                    timeBoard.reload(timeVal, timeValue);
                    timeBoard.shows();
                }
            }
        } else {
            if (timeBoard != null) {
                timeBoard.close();
            }
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
        timeLabel.setText(timeValue);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            boolean close = false;
            if (order) {
                timeVal++;
                if (timeVal == endTime) {
                    MusicPlayer.play(MusicResource.SECOND_0);
                    close = true;
                    controlPane.resetTimeBtn();
                }
            } else {
                int alertValue = Integer.parseInt(controlPane.getAlertTime());
                timeVal--;
                if (timeVal == 0) {
                    MusicPlayer.play(MusicResource.SECOND_0);
                    close = true;
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
            if (close) {
                close();
            }
        }));

        boolean extendScreen = ExtendedScreen.hasConnectedExtend();
        if (extendScreen) {
            ReloadPane timeBoard = controlPane.getTimeBoard();
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
            } else {
                timeBoard.initPage();
            }
            controlPane.setTimeBoard(timeBoard);
            timeBoard.shows();
            timeBoard.reload(timeVal, timeValue);
        }
        timeline.setCycleCount(-1);
        timeline.playFromStart();
        RuntimeUtil.isTiming = true;
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
            cacheTextWidth = 0;
            cacheTextHeight = 0;
        }

        Stage stage = (Stage) (timeLabel.getScene().getWindow());
        stage.hide();
        ReloadPane timeBoard = controlPane.getTimeBoard();
        boolean extendScreen = ExtendedScreen.hasConnectedExtend();
        if (extendScreen && timeBoard != null) {
            controlPane.setTimeBoard(timeBoard);
            timeBoard.close();
        }
    }


    @Override
    public void shows() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.show();
        if (ExtendedScreen.hasConnectedExtend()) {
            ReloadPane timeBoard = controlPane.getTimeBoard();
            if (timeBoard == null) {
                timeBoard = (ReloadPane) ExtendedScreen.timeWithPos("/view/board/match/TimeBoard.fxml", "", "/view/style.css", true);
            }
            if (timeBoard != null) {
                timeBoard.initPage();
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