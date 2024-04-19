package competition.controller.board.match;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.match.ReloadPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.ConfigSpliter;
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
public class TimeBoard implements ReloadPane {
    
    @FXML
    private AnchorPane container;

    private String timeValue = "00秒";


    private int timeVal;

    private Label teamScore = new Label();

    private ConfigsMapper configsMapper;


    @FXML
    public void initialize(){
        configsMapper = JavaFxApplication.getApplicationContext().getBean(ConfigsMapper.class);
        container.getChildren().add(teamScore);
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
        showData();
    }

    @Override
    public void reload() {
       
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);
        teamScore.setTextAlignment(TextAlignment.CENTER);
        teamScore.setAlignment(Pos.CENTER);
        teamScore.setFont(Font.font(Integer.parseInt(split[24])));
        teamScore.setTextFill(Color.valueOf(split[23]));
        showData();
    }
    
    private void showData(){
        teamScore.setText(timeValue);
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
        teamScore.setVisible(true);
        this.timeVal = time;
        this.timeValue = format(timeVal);
        teamScore.setText(timeValue);
    }

    public void stopTiming() {
    }
    
    public void continueTiming(){
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
