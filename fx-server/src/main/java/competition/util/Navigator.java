package competition.util;

import competition.JavaFxApplication;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class Navigator {
    
    
    public static final Stage previewStage = new Stage();
    
    public static final Stage backendStage = new Stage();
    
    
    public static final Stage timeBoardStage = new Stage();
    
    public static final Stage scoreBoardStage = new Stage();
    

    static {
       // previewStage.initStyle(StageStyle.TRANSPARENT);
        previewStage.initStyle(StageStyle.TRANSPARENT);
        timeBoardStage.setAlwaysOnTop(true);
        timeBoardStage.initStyle(StageStyle.TRANSPARENT);
        scoreBoardStage.setAlwaysOnTop(true);
        scoreBoardStage.initStyle(StageStyle.TRANSPARENT);
        backendStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                GlobalCache.backendOpen = false;
            }
        });
    }


    public static double getWidth(){
        ObservableList<Screen> screens = Screen.getScreens();
        Screen extendedScreen = screens.get(0); // 获取第1个屏幕
        Rectangle2D bounds = extendedScreen.getBounds();
        return bounds.getWidth();
    }

    public static double getHeight(){
        ObservableList<Screen> screens = Screen.getScreens();
        Screen extendedScreen = screens.get(0); // 获取第1个屏幕
        Rectangle2D bounds = extendedScreen.getBounds();
        return bounds.getHeight();
    }

    public static PreviewPane preview(String fxml, String title, String cssFile, boolean hiddenControl, boolean fullScreen){
        Parent load = null;
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            load = loader.load();
            previewStage.setTitle(title);
            Scene previewScene;
            final Rectangle2D bounds = Screen.getPrimary().getBounds();
            previewScene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            if (hiddenControl) {
                //previewStage.initStyle(StageStyle.TRANSPARENT);
            }
            
            previewScene.getStylesheets().add(cssFile); // 应用 CSS

            previewStage.setScene(previewScene);
            previewStage.show();
            PreviewPane previewPane =  loader.getController();
            previewPane.setScene(previewScene);
            return previewPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void openBackend(String fxml, String title, String cssFile, boolean hiddenControl, boolean fullScreen){
        Parent load = null;
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            load = loader.load();
            Stage stage = backendStage;
            stage.setTitle(title);
            Scene scene;
            if (fullScreen) {
                final Rectangle2D bounds = Screen.getPrimary().getBounds();
                scene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            } else {
                scene = new Scene(load);
            }

            if (hiddenControl) {
                stage.initStyle(StageStyle.TRANSPARENT);
            }
            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void navigatorTo(String fxml, String title, String cssFile, boolean hiddenControl, boolean fullScreen){
        Parent load = null;
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            load = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            Scene scene;
            if (fullScreen) {
                final Rectangle2D bounds = Screen.getPrimary().getBounds();
                scene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            } else {
                scene = new Scene(load);
            }
            
            if (hiddenControl) {
                stage.initStyle(StageStyle.TRANSPARENT);
            }
            scene.getStylesheets().add(cssFile); // 应用 CSS
           
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Object timeWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        try {

            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
            Configs configs = configsMapper.selectById(1);
            load = loader.load();
            Stage stage = timeBoardStage;
            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                String[] split = config.split(ConfigSpliter.SPLITER);
                stage.setX(Double.parseDouble(split[29]));
                stage.setY(Double.parseDouble(split[30]));
            }
            stage.setTitle(title);
            Scene  scene = new Scene(load, Color.TRANSPARENT);
            if (hiddenControl) {
                //stage.initStyle(StageStyle.TRANSPARENT);
            }
            load.setOnMousePressed(event -> {
                xOffset.set(event.getSceneX());
                yOffset.set(event.getSceneY());
            });

            // 拖动时更新窗口位置
            load.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset.get());
                stage.setY(event.getScreenY() - yOffset.get());
                ObservableList<Screen> screens = Screen.getScreens();
                if (screens.size() < 2) {
                    return;
                }
                Screen extendedScreen = screens.get(1); // 获取第二个屏幕
                Rectangle2D bounds = extendedScreen.getBounds();
                ExtendedScreen.timeBoardStage.setX(bounds.getMinX() + event.getScreenX() - xOffset.get());
                ExtendedScreen.timeBoardStage.setY(bounds.getMinY() + event.getScreenY() - yOffset.get());
            });

            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 时间 - 得分表
    public static Object scoreWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        
        try {
           
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
            
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);

            Configs configs = configsMapper.selectById(1);
            // 0x664db3ff%%
            // 0xb3661aff%%
            // 1%%
            // 1%%
            // 正确答案:%%
            // 200%%
            // 50%%
            // 500%%
            // 200%%
            // 2%%
            // 1%%
            // 0x6848a9ff%%
            // false%%
            // 3%%
            // 2%%
            // 0x1a1a1aff%%
            // 0x70dcdeff%%
            // 300%%
            // 600%%
            // 200%%
            // 50%%
            // 0xffffffff%%
            // 0x70dcdeff%%
            // 0xffcce6ff%%
            // 24%%
            // 0x664db3ff%%
            // 0xff6666ff%%
            // 0xb3b34dff%%
            // 0xffffffff%%
            // 300%%
            // 300
            // 19 20 21 22
           

            loader.setControllerFactory(applicationContext::getBean);
            Parent load = loader.load();
            
            Stage stage = scoreBoardStage;

            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                String[] split = config.split(ConfigSpliter.SPLITER);
                stage.setX(Double.parseDouble(split[17]));
                stage.setY(Double.parseDouble(split[18]));
            }
            stage.setTitle(title);
            Scene  scene = new Scene(load, Color.TRANSPARENT);
            if (hiddenControl) {
                //stage.initStyle(StageStyle.TRANSPARENT);
            }
            load.setOnMousePressed(event -> {
                xOffset.set(event.getSceneX());
                yOffset.set(event.getSceneY());
            });

            // 拖动时更新窗口位置
            load.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset.get());
                stage.setY(event.getScreenY() - yOffset.get());
                ObservableList<Screen> screens = Screen.getScreens();
                if (screens.size() < 2) {
                    return;
                }
                Screen extendedScreen = screens.get(1); // 获取第二个屏幕
                Rectangle2D bounds = extendedScreen.getBounds();
                ExtendedScreen.scoreBoardStage.setX(bounds.getMinX() + event.getScreenX() - xOffset.get());
                ExtendedScreen.scoreBoardStage.setY(bounds.getMinY() +event.getScreenY() - yOffset.get());
            });
            
            
            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Node load(String fxml){
        Node load = null;
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            load = loader.load();
            return load;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
