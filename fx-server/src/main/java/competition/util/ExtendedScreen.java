package competition.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.board.preview.ScoreBoardVPreview;
import competition.controller.match.EmptyPreview;
import competition.controller.match.ReloadPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Buzz;
import competition.entity.Configs;
import competition.mapper.BuzzMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ExtendedScreen {

    public static final Stage extendScreen = new Stage();
    public static final Stage timeBoardStage = new Stage();
    public static final Stage buzzTimeBoardStage = new Stage();
    public static final Stage scoreBoardStage = new Stage();
    public static final Stage buzzBoardStage = new Stage();
    public static final Stage videoStage = new Stage();

    // 缓存机制 - 和Navigator保持一致
    private static final Map<String, Parent> cachedLoadRoot = new HashMap<>();
    private static final Map<String, ReloadPane> cachedControllers = new HashMap<>();
    private static final Map<String, Scene> cachedScenes = new HashMap<>();
    private static volatile Scene extendScene;
    private static volatile Scene scorePaneScene;

    private static volatile boolean extendShow = false;

    private static volatile boolean loadSecondPage = false;

    static {
        extendScreen.initStyle(StageStyle.TRANSPARENT);
        timeBoardStage.initStyle(StageStyle.TRANSPARENT);
        timeBoardStage.setAlwaysOnTop(true);

        buzzTimeBoardStage.setAlwaysOnTop(true);
        buzzTimeBoardStage.initStyle(StageStyle.TRANSPARENT);

        buzzBoardStage.initStyle(StageStyle.TRANSPARENT);
        buzzBoardStage.setAlwaysOnTop(true);

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

    public static boolean hasConnectedExtend(){
        ObservableList<Screen> screens = Screen.getScreens();
        return screens.size() > 1;
    }

    public static boolean extendShow() {
        ObservableList<Screen> screens = Screen.getScreens();
        if (screens.size() > 1) {
            ExtendedScreen.extendShow = true;
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            PreviewPane previewPane = controlPane.getPreviewPane();
            if (!loadSecondPage) {
                show("/view/match/Empty.fxml");
                loadSecondPage = true;
            }
            if (previewPane != null) {
                previewPane.reload();
            }
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

    /**
     * 使用缓存的show方法
     */
    public static void show(String fxml) {
        if (!extendShow) {
            return;
        }

        Parent load = null;
        try {
            // 检查缓存
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                log.info("从缓存加载扩展屏界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
                load = loader.load();

                // 缓存界面和控制器
                cachedLoadRoot.put(fxml, load);
                if (loader.getController() != null) {
                    cachedControllers.put(fxml, loader.getController());
                }
                log.info("首次加载并缓存扩展屏界面: {}", fxml);
            }
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

            // 检查是否有缓存的Scene
            Scene scene;
            if (cachedScenes.containsKey(fxml)) {
                scene = cachedScenes.get(fxml);
                scene.setRoot(load);
            } else {
                scene = new Scene(load, bounds.getWidth(), bounds.getHeight());
                cachedScenes.put(fxml, scene);
            }

            scene.getStylesheets().add("/view/style.css"); // 应用 CSS
            extendScreen.setScene(scene);
            extendScreen.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用缓存的buzzResult方法
     */
    public static Object buzzResult(String fxml, String title, String cssFile, boolean hiddenControl, Object data){
        Parent load = null;
        ReloadPane controller = null;

        try {
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            // 检查缓存
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                controller = cachedControllers.get(fxml);
                log.info("从缓存加载抢答结果界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
                loader.setControllerFactory(applicationContext::getBean);
                load = loader.load();
                controller = loader.getController();

                // 缓存界面和控制器
                cachedLoadRoot.put(fxml, load);
                cachedControllers.put(fxml, controller);
                log.info("首次加载并缓存抢答结果界面: {}", fxml);
            }
            controller.setData(data); // 设置数据
            controller.initPage();
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
            Configs configs = configsMapper.selectById(10);
            Stage stage = buzzBoardStage;

            // 没有扩展屏
            if (Screen.getScreens().size() < 2) {
                return controller;
            }
            Screen extendedScreen = Screen.getScreens().get(1); // 假设扩展屏是列表中的第二个
            Rectangle2D bounds = extendedScreen.getVisualBounds();
            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                JSONObject jsonObject = JSON.parseObject(config);
                stage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(Double.parseDouble(jsonObject.getOrDefault("x", "0").toString())));
                stage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(Double.parseDouble(jsonObject.getOrDefault("y", "0").toString())));
            }
            // 将窗口定位到扩展屏
            stage.setTitle(title);
            Scene scene = new Scene(load, Color.TRANSPARENT);
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
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    /**
     * 使用缓存的buzzTimeWithPos方法
     */
    public static Object buzzTimeWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        ReloadPane controller = null;

        try {
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            // 检查缓存
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                controller = cachedControllers.get(fxml);
                log.info("从缓存加载抢答计时界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
                loader.setControllerFactory(applicationContext::getBean);
                load = loader.load();
                controller = loader.getController();

                // 缓存界面和控制器
                cachedLoadRoot.put(fxml, load);
                cachedControllers.put(fxml, controller);
                log.info("首次加载并缓存抢答计时界面: {}", fxml);
            }
            controller.initPage();
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            BuzzMapper configsMapper = applicationContext.getBean(BuzzMapper.class);
            Buzz configs = configsMapper.selectById(1);
            Stage stage = buzzTimeBoardStage;
            Screen extendedScreen = Screen.getScreens().get(1); // 假设扩展屏是列表中的第二个
            Rectangle2D bounds = extendedScreen.getVisualBounds();
            if (configs != null) {
                stage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(Double.parseDouble(configs.getX())));
                stage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(Double.parseDouble(configs.getY())));
            }

            stage.setTitle(title);
            Scene scene = new Scene(load, Color.TRANSPARENT);
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
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    /**
     * 使用缓存的timeWithPos方法
     */
    public static Object timeWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        ReloadPane controller = null;

        try {
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            // 检查缓存
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                controller = cachedControllers.get(fxml);
                log.info("从缓存加载计时界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
                ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
                loader.setControllerFactory(applicationContext::getBean);
                load = loader.load();
                controller = loader.getController();

                // 缓存界面和控制器
                cachedLoadRoot.put(fxml, load);
                cachedControllers.put(fxml, controller);
                log.info("首次加载并缓存计时界面: {}", fxml);
            }
            controller.initPage();
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
            Configs configs = configsMapper.selectById(1);
            Stage stage = timeBoardStage;

            // 没有扩展屏
            if (Screen.getScreens().size() < 2) {
                return controller;
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
            Scene scene = new Scene(load, Color.TRANSPARENT);
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
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    /**
     * 使用缓存的scoreWithPos方法
     */
    public static Object scoreWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        ReloadPane controller = null;

        try {
            // 检查缓存
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                controller = cachedControllers.get(fxml);
                log.info("从缓存加载计分板界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
                loader.setControllerFactory(applicationContext::getBean);
                load = loader.load();
                controller = loader.getController();

                // 缓存界面和控制器
                cachedLoadRoot.put(fxml, load);
                cachedControllers.put(fxml, controller);
                log.info("首次加载并缓存计分板界面: {}", fxml);
            }
            controller.initPage();
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
            Configs configs = configsMapper.selectById(1);

            Stage stage = scoreBoardStage;
            if (Screen.getScreens().size() < 2) {
                return controller;
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
            if (scorePaneScene == null) {
                scorePaneScene = new Scene(load, Color.TRANSPARENT);
            } else {
                scorePaneScene.setRoot(load);
            }
            if (hiddenControl) {
                //stage.initStyle(StageStyle.TRANSPARENT);
            }

            scorePaneScene.getStylesheets().add(cssFile); // 应用 CSS
            stage.setScene(scorePaneScene);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    /**
     * 使用缓存的showWithControl方法
     */
    public static Object showWithControl(String fxml, Object data) {
        if (!extendShow) {
            return null;
        }

        Parent load = null;
        ReloadPane controller = null;

        try {
            // 检查缓存
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                controller = cachedControllers.get(fxml);
                log.info("从缓存加载控制界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
                load = loader.load();
                controller = loader.getController();
                // 缓存界面和控制器
                cachedLoadRoot.put(fxml, load);
                cachedControllers.put(fxml, controller);
                log.info("首次加载并缓存控制界面: {}", fxml);
            }
            controller.setData(data); // 设置数据
            controller.initPage();
            final Rectangle2D bounds = Screen.getPrimary().getBounds();
            if (extendScene == null) {
                extendScene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            } else {
                extendScene.setRoot(load);
            }
            extendScene.getStylesheets().add("/view/style.css"); // 应用 CSS
            extendScreen.setScene(extendScene);
            extendScreen.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清理缓存的方法 - 在应用退出时调用
     */
    public static void clearCache() {
        log.info("清理ExtendedScreen缓存，共清理{}个界面", cachedLoadRoot.size());
        cachedLoadRoot.clear();
        cachedControllers.clear();
        cachedScenes.clear();
    }

    /**
     * 移除特定界面的缓存
     */
    public static void removeCacheItem(String fxml) {
        if (cachedLoadRoot.containsKey(fxml)) {
            cachedLoadRoot.remove(fxml);
            cachedControllers.remove(fxml);
            cachedScenes.remove(fxml);
            log.info("移除缓存项: {}", fxml);
        }
    }

    /**
     * 获取缓存状态信息
     */
    public static String getCacheInfo() {
        return String.format("ExtendedScreen缓存状态 - 界面数量: %d, 控制器数量: %d, Scene数量: %d",
                cachedLoadRoot.size(), cachedControllers.size(), cachedScenes.size());
    }
}