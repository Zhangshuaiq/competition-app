package competition.controller.board.match;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.match.ReloadPane;
import competition.entity.Buzz;
import competition.entity.Configs;
import competition.mapper.BuzzMapper;
import competition.mapper.ConfigsMapper;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import competition.util.ScreenAdapter;
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
import org.springframework.stereotype.Component;

@Component
public class BuzzTimeBoard implements ReloadPane {

    @FXML
    private AnchorPane container;

    private String timeValue = "0";


    private int timeVal;

    StackPane stackPane = new StackPane();
    private Text teamScore = new Text();

    private ConfigsMapper configsMapper;

    @FXML
    public void initPage() {
        configsMapper = JavaFxApplication.getApplicationContext().getBean(ConfigsMapper.class);
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
        showData();
    }

    @Override
    public void reload() {

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

        showData();
    }

    private void showData() {
        teamScore.setText(timeValue);

    }

    private String format(int timeVal) {
        return timeVal + "";
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
