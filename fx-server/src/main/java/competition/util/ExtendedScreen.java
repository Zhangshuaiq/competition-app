package competition.util;

import competition.JavaFxApplication;
import competition.controller.board.preview.ScoreBoardVPreview;
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
import java.util.concurrent.atomic.AtomicReference;


public class ExtendedScreen {

    public static final Stage extendScreen = new Stage();

    public static final Stage timeBoardStage = new Stage();

    public static final Stage scoreBoardStage = new Stage();
    
    public static final Stage videoStage = new Stage();
    
    private static boolean extendShow = false;

    static {
        extendScreen.initStyle(StageStyle.TRANSPARENT);
        timeBoardStage.initStyle(StageStyle.TRANSPARENT);
        timeBoardStage.setAlwaysOnTop(true);
        scoreBoardStage.setAlwaysOnTop(true);
        scoreBoardStage.initStyle(StageStyle.TRANSPARENT);
        videoStage.initStyle(StageStyle.TRANSPARENT);
        videoStage.setAlwaysOnTop(true);
    }
    
    public static double getWidth(){
        ObservableList<Screen> screens = Screen.getScreens();
        Screen extendedScreen = screens.get(1); // 获取第二个屏幕
        Rectangle2D bounds = extendedScreen.getBounds();
        return bounds.getWidth();
    }

    public static double getHeight(){
        ObservableList<Screen> screens = Screen.getScreens();
        Screen extendedScreen = screens.get(1); // 获取第二个屏幕
        Rectangle2D bounds = extendedScreen.getBounds();
        return bounds.getHeight();
    }

    public static boolean extendShow() {
        ObservableList<Screen> screens = Screen.getScreens();
        if (screens.size() > 1) {
            ExtendedScreen.extendShow = true;
            show("/view/match/Empty.fxml");
            return true;
        }
        return false;
    }
    
    public static boolean hasShow(){
        return extendShow;
    }
    
    public static void close(){
        extendShow = false;
        extendScreen.close();
    }

    static {
       // extendScreen.initStyle(StageStyle.TRANSPARENT);
    }
    
    public static void show(Scene scene) {
        ObservableList<Screen> screens = Screen.getScreens();
        if (screens.size() < 2) {
            return ;
        }
        Screen extendedScreen = screens.get(1); // 获取第二个屏幕
        Rectangle2D bounds = extendedScreen.getBounds();

        // 将窗口定位到扩展屏
        extendScreen.setX(bounds.getMinX());
        extendScreen.setY(bounds.getMinY());
        extendScreen.setWidth(bounds.getWidth());
        extendScreen.setHeight(bounds.getHeight());
        scene.getStylesheets().add("/view/style.css"); // 应用 CSS
        extendScreen.setScene(scene);
        extendScreen.show();
    }

    public static void show(String fxml) {
        if (!extendShow) {
            return;
        }
        Parent load = null;
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            load = loader.load();
            ObservableList<Screen> screens = Screen.getScreens();
            if (screens.size() < 2) {
                return;
            }
            Screen extendedScreen = screens.get(1); // 获取第二个屏幕
            Rectangle2D bounds = extendedScreen.getBounds();

            // 将窗口定位到扩展屏
            extendScreen.setX(bounds.getMinX());
            extendScreen.setY(bounds.getMinY());
            extendScreen.setWidth(bounds.getWidth());
            extendScreen.setHeight(bounds.getHeight());
            Scene scene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            scene.getStylesheets().add("/view/style.css"); // 应用 CSS
            extendScreen.setScene(scene);
            extendScreen.show();
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
            loader.setControllerFactory(applicationContext::getBean);
            load = loader.load();
            Stage stage = timeBoardStage;
            
            // 没有扩展屏
            if (Screen.getScreens().size() < 2) {
                return null;
            }
            Screen extendedScreen = Screen.getScreens().get(1); // 假设扩展屏是列表中的第二个
            Rectangle2D bounds = extendedScreen.getVisualBounds();
            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                String[] split = config.split(ConfigSpliter.SPLITER);
                stage.setX(bounds.getMinX() + Double.parseDouble(split[29]));
                stage.setY(bounds.getMinY() + Double.parseDouble(split[30]));
            }
            // 将窗口定位到扩展屏
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
            if (!ExtendedScreen.hasShow()) {
                return null;
            }
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
            if (Screen.getScreens().size() < 2) {
                return null;
            }
            Screen extendedScreen = Screen.getScreens().get(1); // 假设扩展屏是列表中的第二个
            Rectangle2D bounds = extendedScreen.getVisualBounds();
            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                String[] split = config.split(ConfigSpliter.SPLITER);
                stage.setX(bounds.getMinX() + Double.parseDouble(split[17]));
                stage.setY(bounds.getMinY() + Double.parseDouble(split[18]));
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
    
    
    public static Object showWithControl(String fxml) {
        if (!extendShow) {
            return null;
        }
        Parent load = null;
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
            load = loader.load();
            final Rectangle2D bounds = Screen.getPrimary().getBounds();
            Scene scene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            scene.getStylesheets().add("/view/style.css"); // 应用 CSS
            extendScreen.setScene(scene);
            extendScreen.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //    @Override
//    public void start(Stage primaryStage) {
//        // 获取所有屏幕
//        Screen primaryScreen = Screen.getPrimary();
//        Rectangle2D primaryBounds = primaryScreen.getVisualBounds();
//
//        // 假设扩展屏幕是第二个屏幕
//        Screen extendedScreen = Screen.getScreens().get(1);
//        Rectangle2D extendedBounds = extendedScreen.getVisualBounds();
//
//        // 在扩展屏幕上创建一个窗口
//        Stage extendedStage = new Stage();
//        extendedStage.setTitle("Extended Screen Window");
//        extendedStage.setX(extendedBounds.getMinX());
//        extendedStage.setY(extendedBounds.getMinY());
//        extendedStage.setWidth(extendedBounds.getWidth() / 2);
//        extendedStage.setHeight(extendedBounds.getHeight() / 2);
//
//        // 添加一些内容
//        Label label = new Label("Hello on Extended Screen!");
//        Scene scene = new Scene(label, 200, 100);
//        extendedStage.setScene(scene);
//
//        // 显示窗口
//        extendedStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
}
