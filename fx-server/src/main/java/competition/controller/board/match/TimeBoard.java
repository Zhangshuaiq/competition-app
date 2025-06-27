package competition.controller.board.match;

import competition.JavaFxApplication;
import competition.controller.match.ReloadPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;

@Component
public class TimeBoard implements ReloadPane {

    @FXML
    private AnchorPane container;

    private String timeValue = "00秒";


    private int timeVal;

    StackPane stackPane = new StackPane();
    private Text teamScore = new Text();

    @Resource
    private ConfigsMapper configsMapper;

    @FXML
    public void initPage() {
        configsMapper = JavaFxApplication.getApplicationContext().getBean(ConfigsMapper.class);
        stackPane.getChildren().clear();
        container.getChildren().clear();
        stackPane.getChildren().add(teamScore);
        container.getChildren().add(stackPane);
        reload();
    }

    @Override
    public void hideTime() {
        teamScore.setVisible(false);
    }

    @Override
    public void reload(int time, String timeTxt) {
        this.timeVal = time;
        this.timeValue = timeTxt;
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);
        teamScore.setTextAlignment(TextAlignment.CENTER);

        teamScore.setFont(FontMap.load(split[33], Integer.parseInt(split[24])));
        teamScore.setFill(Color.valueOf(split[23]));
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
        showData();
    }

    @Override
    public void reload() {
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);
        teamScore.setTextAlignment(TextAlignment.CENTER);
        teamScore.setFont(FontMap.load(split[33], Integer.parseInt(split[24])));
        teamScore.setFill(Color.valueOf(split[23]));
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
        showData();
    }

    private void showData() {
        teamScore.setText(timeValue);

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
        teamScore.setVisible(true);
        this.timeVal = time;
        this.timeValue = format(timeVal);
        teamScore.setText(timeValue);
    }

    public void stopTiming() {
    }

    public void continueTiming() {
    }

    @Override
    public void shows() {
        teamScore.setVisible(true);
    }

    @Override
    public void close() {
        teamScore.setVisible(false);
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
}
