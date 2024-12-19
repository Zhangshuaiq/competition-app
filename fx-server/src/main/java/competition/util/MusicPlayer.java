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

    public static void playWithCallback(String path, Runnable callback) {
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
                if (callback != null) {
                    callback.run();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static double getAudioDuration(String audioFilePath) {
        // 创建 Media 对象
        Media media = new Media(new File(audioFilePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // 创建一个数组，用于存储音频时长
        final double[] duration = {-1};

        // 使用 CountDownLatch 等待异步加载完成
        final Object lock = new Object();

        // 设置回调，在 MediaPlayer 准备就绪时获取时长
        mediaPlayer.setOnReady(() -> {
            synchronized (lock) {
                // 获取时长（秒）
                duration[0] = mediaPlayer.getMedia().getDuration().toSeconds();
                // 通知等待线程
                lock.notify();
            }
        });

        synchronized (lock) {
            try {
                lock.wait(); // 等待回调完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 释放资源
        mediaPlayer.dispose();

        return duration[0];
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
