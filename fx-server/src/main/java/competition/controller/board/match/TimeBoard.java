package competition.controller.board.match;

import competition.JavaFxApplication;
import competition.controller.match.ReloadPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import competition.util.RuntimeUtil;
import competition.util.ScreenAdapter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;

@Component
@Slf4j
public class TimeBoard implements ReloadPane {

    @FXML
    private AnchorPane container;

    private String timeValue = "00:00";

    private int timeVal;

    private Label timeLabel = new Label();

    @Resource
    private ConfigsMapper configsMapper;

    @FXML
    public void initPage() {
        container.getChildren().clear();
        timeLabel = new Label();
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

        reload();
    }

    @Override
    public void hideTime() {
    }

    @Override
    public void reload(int time, String timeTxt) {
        this.timeVal = time;
        this.timeValue = timeTxt;
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);

        // 先设置文本内容
        timeLabel.setText(timeValue);

        // 设置字体和样式
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setTextAlignment(TextAlignment.CENTER);
        timeLabel.setFont(FontMap.load(split[33], ScreenAdapter.calculateExX(Integer.parseInt(split[24]))));
        timeLabel.setTextFill(Color.valueOf(split[23]));

        // 设置自动换行为false，确保文本在一行显示
        timeLabel.setWrapText(false);

        if (split.length >= 35) {
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
            log.error("width: {}, height: {}", textWidth, textHeight);

            // 确保容器足够大来显示文本
            if (textWidth > 0 && textHeight > 0) {
                timeLabel.setPrefWidth(textWidth);
                timeLabel.setPrefHeight(textHeight);

                // 同时确保窗口大小足够
                Stage stage = (Stage) container.getScene().getWindow();
                if (stage != null) {
                    // 给窗口设置最小大小，确保能显示完整文本
                    stage.setMinWidth(textWidth + 40);
                    stage.setMinHeight(textHeight + 40);


                    // 如果当前窗口太小，则调整大小
                    stage.setWidth(textWidth + 40);
                    stage.setHeight(textHeight + 40);
                }
            }
        });

        showData();
    }

    @Override
    public void reload() {
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);

        // 先设置文本内容
        timeLabel.setText(timeValue);

        // 设置字体和样式
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setTextAlignment(TextAlignment.CENTER);
        timeLabel.setFont(FontMap.load(split[33], Integer.parseInt(split[24])));
        timeLabel.setTextFill(Color.valueOf(split[23]));

        // 设置自动换行为false，确保文本在一行显示
        timeLabel.setWrapText(false);

        if (split.length >= 35) {
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

            // 确保容器足够大来显示文本
            if (textWidth > 0 && textHeight > 0) {
                timeLabel.setPrefWidth(textWidth);
                timeLabel.setPrefHeight(textHeight);

                // 同时确保窗口大小足够
                Stage stage = (Stage) container.getScene().getWindow();
                if (stage != null) {
                    // 给窗口设置最小大小，确保能显示完整文本
                    stage.setMinWidth(textWidth + 40);
                    stage.setMinHeight(textHeight + 40);
                    stage.setWidth(textWidth + 40);
                    stage.setHeight(textHeight + 40);
                }
            }
        });

        showData();
    }

    private void showData() {
        timeLabel.setText(timeValue);
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
        this.timeVal = time;
        this.timeValue = format(timeVal);
        timeLabel.setText(timeValue);
    }

    public void stopTiming() {
    }

    public void continueTiming() {
    }

    @Override
    public void shows() {
        Stage stage = (Stage) (timeLabel.getScene().getWindow());
        stage.show();
    }

    @Override
    public void close() {
        Stage stage = (Stage) (timeLabel.getScene().getWindow());
        this.timeVal = 0;
        this.timeValue = format(timeVal);
        stage.hide();
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
}