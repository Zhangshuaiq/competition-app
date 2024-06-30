package competition.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {

   // private static Map<String, MediaPlayer> musicsMap = new HashMap<>();
    private static MediaPlayer mediaPlayer; // 单一实例

    static {
//        load(MusicResource.SECOND_5);
//        load(MusicResource.SECOND_4);
//        load(MusicResource.SECOND_3);
//        load(MusicResource.SECOND_2);
//        load(MusicResource.SECOND_1);
//        load(MusicResource.SECOND_0);
        // 预加载播放，防止播放延迟
    }

    public static void prePlay(String path) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop(); // 如果已有实例在播放，先停止
                mediaPlayer.dispose(); // 释放旧的资源
            }

            Media media = new Media(new File(path).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.seek(Duration.ZERO);
            });
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void play(String path) {
        if (!new File(path).exists()) {
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // 如果已有实例在播放，先停止
            mediaPlayer.dispose(); // 释放旧的资源
        }
        try {
            Media media = new Media(new File(path).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.seek(Duration.ZERO);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
}
