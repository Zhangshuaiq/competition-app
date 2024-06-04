package competition;

import competition.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.util.Arrays;

public class JavaFxApplication extends Application {

    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        System.setProperty("java.awt.headless", "false");
        applicationContext = new SpringApplicationBuilder(Main.class).run();
    }

    private double xOffset = 0;
    private double yOffset = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ControlPage.fxml"));
        loader.setControllerFactory(applicationContext::getBean); // 使用Spring上下文来创建控制器实例
        Parent load = loader.load();
        Scene scene = new Scene(load);
        scene.getStylesheets().add("/view/style.css"); // 应用 CSS
//        // 鼠标按下时记录初始坐标
//        load.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });
//
//        // 拖动时更新窗口位置
//        load.setOnMouseDragged(event -> {
//            primaryStage.setX(event.getScreenX() - xOffset);
//            primaryStage.setY(event.getScreenY() - yOffset);
//        });
        primaryStage.setOnCloseRequest(event -> {
            ScoreSaver.saveScore();
            System.exit(0);
        });
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("答题竞赛系统");
        try {
            primaryStage.getIcons().add(new Image(new FileInputStream(BaseDir.IMAGE_DIR + "图标.jpg")));
        }catch (Exception ex) {
            System.out.println("加载不到图片");
        }
        primaryStage.show();
        MusicPlayer.prePlay(MusicResource.SECOND_5);

    }
    

    @Override
    public void stop() throws Exception {
        applicationContext.close();
        Platform.exit();
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
