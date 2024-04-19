package competition.util;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;

public class VideoPlayer {

    private MediaPlayer mediaPlayer;

    public void start(Stage primaryStage, String path) {
        // 创建 Media 对象
        Media media = new Media(new File(path).toURI().toString());  // 替换为视频文件的路径
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // 自适应窗体大小
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(primaryStage.widthProperty());
        mediaView.fitHeightProperty().bind(primaryStage.heightProperty().subtract(50));  // 留空间给控制面板

        // 创建控制面板
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playButton.setText("Play");
            } else {
                mediaPlayer.play();
                playButton.setText("Pause");
            }
        });

        // 创建滑动条
        Slider timeSlider = new Slider();
        mediaPlayer.setOnReady(() -> timeSlider.setMax(media.getDuration().toSeconds()));

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(newTime.toSeconds());
            }
        });

        timeSlider.valueChangingProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (!timeSlider.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                }
            }
        });

        // 控制面板
        HBox controlBox = new HBox(5, playButton, timeSlider);
        BorderPane root = new BorderPane();
        root.setCenter(mediaView);
        root.setBottom(controlBox);

        // 全屏切换按钮
        Button fullScreenButton = new Button("Full Screen");
        fullScreenButton.setOnAction(e -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
        controlBox.getChildren().add(fullScreenButton);

        // 创建场景

        ObservableList<Screen> screens = Screen.getScreens();
        Screen extendedScreen = screens.get(0); // 获取第二个屏幕
        if (screens.size() > 1) {
            extendedScreen = screens.get(1);
        }
        Rectangle2D bounds = extendedScreen.getBounds();

        // 将窗口定位到扩展屏
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("/view/style.css"); // 应用 CSS
       
        primaryStage.setTitle("视频播放器");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        mediaPlayer.play();
    }
    
    public void stop(){
        mediaPlayer.stop();
        GlobalCache.videoPlay = false;
    }
}


