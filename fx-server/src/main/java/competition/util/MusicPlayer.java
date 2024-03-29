package competition.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {

    private static Map<String, MediaPlayer> musicsMap = new HashMap<>();

    static {
        load(MusicResource.SECOND_5);
        load(MusicResource.SECOND_4);
        load(MusicResource.SECOND_3);
        load(MusicResource.SECOND_2);
        load(MusicResource.SECOND_1);
        load(MusicResource.SECOND_0);
        // 预加载播放，防止播放延迟
    }

    public static void prePlay(String path) {
        try {

            Media media = new Media(new File(path).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0);
            mediaPlayer.play();
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.seek(Duration.ZERO);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void load(String path) {
        try {

            Media media = new Media(new File(path).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.seek(Duration.ZERO);
            });
            musicsMap.put(path, mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void play(String path) {
        MediaPlayer player = musicsMap.get(path);
        player.play();
    }
}
