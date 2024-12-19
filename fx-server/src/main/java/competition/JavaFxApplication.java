package competition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.mapper.Model7StatusMapper;
import competition.mapper.RegisterInfoMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private static ConfigurableApplicationContext applicationContext;
    
    public static Stage stage = new Stage();

    @Override
    public void init() throws Exception {
        System.setProperty("java.awt.headless", "false");
        applicationContext = new SpringApplicationBuilder(Main.class).run();
    }



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
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(e -> {
            WsMessageHandler messageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
            // 删除竞技模式缓存
            Model7StatusMapper model7StatusMapper = JavaFxApplication.getApplicationContext().getBean(Model7StatusMapper.class);
            model7StatusMapper.delete(new LambdaQueryWrapper<>());

            messageHandler.notifyPage("wait", null);
            System.exit(0);
        });
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setTitle("答赛答题竞赛系统");
        stage.show();
        MusicPlayer.prePlay(MusicResource.SECOND_0);
    }
    

    @Override
    public void stop() throws Exception {
        applicationContext.close();
        Platform.exit();
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

//    public static void restartJavaFxApplication() {
//        Platform.runLater(() -> {
//            try {
//                FXMLLoader loader = new FXMLLoader(JavaFxApplication.class.getResource("/view/ControlPage.fxml"));
//                loader.setControllerFactory(applicationContext::getBean); // 使用Spring上下文来创建控制器实例
//                Parent load = loader.load();
//                Scene scene = new Scene(load);
//                scene.getStylesheets().add("/view/style.css"); // 应用 CSS
//
//
//                stage.setOnCloseRequest(e -> {
//                    WsMessageHandler messageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
//                    messageHandler.notifyPage("wait", null);
//                    System.exit(0);
//                });
//                stage.setScene(scene);
//                stage.setResizable(false);
//                stage.setAlwaysOnTop(true);
//                stage.setTitle("答赛答题竞赛系统");
//                try {
//                    stage.getIcons().add(new Image(new FileInputStream(BaseDir.IMAGE_DIR + "图标.jpg")));
//                } catch (Exception ex) {
//                    System.out.println("加载不到图片");
//                }
//                stage.show();
//                MusicPlayer.prePlay(MusicResource.SECOND_0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }

   
}
