package competition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.mapper.Model7StatusMapper;
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

        notifyPreloader(new SplashPreloader.SplashNotification(0.05, "初始化字体资源..."));
        FontMap.initLoadFont();

        notifyPreloader(new SplashPreloader.SplashNotification(0.15, "正在启动 Spring 上下文..."));
        applicationContext = new SpringApplicationBuilder(Main.class)
                .listeners(event -> {
                    if (event instanceof org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent) {
                        notifyPreloader(new SplashPreloader.SplashNotification(0.35, "配置应用环境..."));
                    } else if (event instanceof org.springframework.boot.context.event.ApplicationContextInitializedEvent) {
                        notifyPreloader(new SplashPreloader.SplashNotification(0.55, "初始化应用组件..."));
                    } else if (event instanceof org.springframework.boot.context.event.ApplicationPreparedEvent) {
                        notifyPreloader(new SplashPreloader.SplashNotification(0.75, "加载业务数据..."));
                    } else if (event instanceof org.springframework.context.event.ContextRefreshedEvent) {
                        notifyPreloader(new SplashPreloader.SplashNotification(0.92, "完成启动准备..."));
                    }
                })
                .run();

        notifyPreloader(new SplashPreloader.SplashNotification(1.0, "启动完成"));
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ControlPage.fxml"));
        loader.setControllerFactory(applicationContext::getBean); // 使用Spring上下文来创建控制器实例
        Parent load = loader.load();
        Scene scene = new Scene(load);
        scene.getStylesheets().add("/view/style.css"); // 应用 CSS
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(e -> {
            WsMessageHandler messageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
            // 删除竞技模式缓存
            Model7StatusMapper model7StatusMapper = JavaFxApplication.getApplicationContext().getBean(Model7StatusMapper.class);
            model7StatusMapper.delete(new LambdaQueryWrapper<>());

            messageHandler.notifyPage("wait", null);
            Platform.exit();
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
        MusicPlayer.dispose();
        ExtendedScreen.clearCache();
        Navigator.clearCache();
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

   
}
