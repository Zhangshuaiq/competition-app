package competition.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.board.preview.TeamConnectionBarPreview;
import competition.controller.preview.PreviewPane;
import competition.entity.Buzz;
import competition.entity.Configs;
import competition.mapper.BuzzMapper;
import competition.mapper.ConfigsMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Navigator {


    public static final Stage previewStage = new Stage();

    public static final Stage backendStage = new Stage();

    public static final Stage activeStage = new Stage();


    public static final Stage timeBoardStage = new Stage();

    // 抢答计时器面板
    public static final Stage buzzTimeBoardStage = new Stage();

    // 分数面板
    public static final Stage scoreBoardStage = new Stage();

    // 客户端连接状态面板
    public static final Stage connectionStage = new Stage();

    // 抢答结束面板
    public static final Stage buzzBoardStage = new Stage();

    private static final Set<String> attrCache = new HashSet<>();

    // 缓存的界面，和对应的控制器
    private static final Map<String, PreviewPane> cachedPreviews = new HashMap<>();
    private static final Map<String, Parent> cachedLoadRoot = new HashMap<>();
    private static final Map<String, Scene> cachedScenes = new HashMap<>();

    private static volatile Scene previewScene;

    static {
        previewStage.initStyle(StageStyle.TRANSPARENT);

        timeBoardStage.setAlwaysOnTop(true);
        timeBoardStage.initStyle(StageStyle.TRANSPARENT);

        buzzTimeBoardStage.setAlwaysOnTop(true);
        buzzTimeBoardStage.initStyle(StageStyle.TRANSPARENT);

        buzzBoardStage.setAlwaysOnTop(true);
        buzzBoardStage.initStyle(StageStyle.TRANSPARENT);

        scoreBoardStage.setResizable(true);
        scoreBoardStage.setAlwaysOnTop(true);
        scoreBoardStage.initStyle(StageStyle.TRANSPARENT);

        connectionStage.setResizable(true);
        connectionStage.setAlwaysOnTop(true);
        connectionStage.initStyle(StageStyle.TRANSPARENT);

        backendStage.setOnCloseRequest(event -> GlobalCache.backendOpen = false);
        previewStage.setOnHidden(event -> {
            GlobalCache.showTeamRank = false;
            GlobalCache.showMemberRank = false;
            GlobalCache.showModel7Rank = false;
            GlobalCache.showBuzzRank = false;
        });
        activeStage.setAlwaysOnTop(true);
        backendStage.setResizable(false);
        activeStage.setTitle("软件激活");
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


    public static PreviewPane preview(String fxml, String title, String cssFile, boolean hiddenControl, Object data){
        ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
        ControlPane controlPane = applicationContext.getBean(ControlPane.class);
        controlPane.reloadScoreBoard();
        Parent load = null;
        try {
            PreviewPane previewPane;
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                previewPane = cachedPreviews.get(fxml);
                // 关键：清理之前的状态
                previewPane.destroy();
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
                load = loader.load();
                previewPane =  loader.getController();
                previewPane.setSecondFxml(fxml.replace("preview", "match"));
                cachedLoadRoot.put(fxml, load);
                cachedPreviews.put(fxml, previewPane);
            }

            previewPane.setData(data);
            previewPane.initPage();
            previewStage.setTitle(title);
            final Rectangle2D bounds = Screen.getPrimary().getBounds();

            // 避免再次使用 scene
            if (previewScene == null) {
                previewScene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            } else {
                previewScene.setRoot(load);
            }

            if (hiddenControl) {
                //previewStage.initStyle(StageStyle.TRANSPARENT);
            }
            previewScene.setFill(Color.TRANSPARENT);
            previewScene.getStylesheets().add(cssFile); // 应用 CSS

            previewStage.setScene(previewScene);
            previewStage.show();
            return previewPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void openBackend(String fxml, String title, String cssFile, boolean hiddenControl, boolean fullScreen){
        Parent load = null;
        try {
            if (cachedLoadRoot.containsKey(fxml)) {
                load = cachedLoadRoot.get(fxml);
                log.info("从缓存加载预览界面: {}", fxml);
            } else {
                FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
                loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean);
                load = loader.load();
                cachedLoadRoot.put(fxml, load);
            }
            Stage stage = backendStage;
            stage.setTitle(title);
            Scene scene;
            if (fullScreen) {
                final Rectangle2D bounds = Screen.getPrimary().getBounds();
                scene = new Scene(load, bounds.getWidth(), bounds.getHeight());
            } else {
                if (cachedScenes.containsKey(fxml)) {
                    scene = cachedScenes.get(fxml);
                    scene.setRoot(load);
                } else {
                    scene = new Scene(load);
                    cachedScenes.put(fxml, scene);
                }
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
        if (attrCache.contains(fxml)) {
            return;
        }
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
            stage.setOnCloseRequest(event -> {
                attrCache.remove(fxml);
            });
            stage.setScene(scene);
            stage.show();
            attrCache.add(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearAttrCache(){
        attrCache.clear();
    }

    public static void clearCache() {
        cachedPreviews.values().forEach(PreviewPane::destroy);
        cachedPreviews.clear();
        cachedLoadRoot.clear();
        cachedScenes.clear();
        attrCache.clear();
        previewScene = null;
    }

    public static Object buzzResult(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        try {

            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
            Configs configs = configsMapper.selectById(10);
            load = loader.load();
            PreviewPane controller = loader.getController();
            controller.initPage();
            Stage stage = buzzBoardStage;
            String config = configs.getConfig();
            if (!StringUtils.isEmpty(config)) {
                JSONObject jsonObject = JSON.parseObject(config);
                stage.setX(ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("x", "0").toString())));
                stage.setY(ScreenAdapter.calculateMainY(Double.parseDouble(jsonObject.getOrDefault("y", "0").toString())));
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

                ExtendedScreen.buzzBoardStage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(event.getScreenX() - xOffset.get()));
                ExtendedScreen.buzzBoardStage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(event.getScreenY() - yOffset.get()));
            });


            load.setOnMouseReleased(event -> {
                if (!StringUtils.isEmpty(config)) {
                    JSONObject jsonObject = JSON.parseObject(config);
                    jsonObject.put("x", (int)stage.getX() + "");
                    jsonObject.put("y", (int)stage.getY() + "");
                    configs.setConfig(jsonObject.toJSONString());
                    configsMapper.updateById(configs);
                }
            });

            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object buzzTimeWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        try {

            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            BuzzMapper configsMapper = applicationContext.getBean(BuzzMapper.class);
            Buzz configs = configsMapper.selectById(1);
            load = loader.load();

            PreviewPane controller = loader.getController();
            controller.initPage();
            Stage stage = buzzTimeBoardStage;

            if (configs != null) {
                stage.setX(ScreenAdapter.calculateMainX(Double.parseDouble(configs.getX())));
                stage.setY(ScreenAdapter.calculateMainY(Double.parseDouble(configs.getY())));
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

                ExtendedScreen.buzzTimeBoardStage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(event.getScreenX() - xOffset.get()));
                ExtendedScreen.buzzTimeBoardStage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(event.getScreenY() - yOffset.get()));
            });

            load.setOnMouseReleased(event -> {
                BuzzMapper buzzMapper = JavaFxApplication.getApplicationContext().getBean(BuzzMapper.class);
                Buzz buzz = buzzMapper.selectById(1);
                buzz.setX((int)stage.getX() + "");
                buzz.setY((int)stage.getY() + "");
                buzzMapper.updateById(buzz);
            });

            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object timeWithPos(String fxml, String title, String cssFile, boolean hiddenControl){
        Parent load = null;
        try {

            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            ConfigsMapper configsMapper = applicationContext.getBean(ConfigsMapper.class);
            AtomicReference<Configs> configs = new AtomicReference<>(configsMapper.selectById(1));
            AtomicReference<String> config = new AtomicReference<>(configs.get().getConfig());
            load = loader.load();

            PreviewPane controller = loader.getController();
            controller.initPage();
            Stage stage = timeBoardStage;
            if (!StringUtils.isEmpty(config)) {
                String[] split = config.get().split(ConfigSpliter.SPLITER);
                stage.setX(ScreenAdapter.calculateMainX(Double.parseDouble(split[29])));
                stage.setY(ScreenAdapter.calculateMainY(Double.parseDouble(split[30])));
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

                ExtendedScreen.timeBoardStage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(event.getScreenX() - xOffset.get()));
                ExtendedScreen.timeBoardStage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(event.getScreenY() - yOffset.get()));
            });

            load.setOnMouseReleased(event -> {
                configs.set(configsMapper.selectById(1));
                config.set(configs.get().getConfig());
                String[] split = config.get().split(ConfigSpliter.SPLITER);
                split[29] = (int)stage.getX() + "";
                split[30] = (int)stage.getY() + "";
                configs.get().setConfig(String.join(ConfigSpliter.SPLITER, split));
                configsMapper.updateById(configs.get());
            });

            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object connectionBar(String fxml, String title, String cssFile, boolean hiddenControl){

        try {

            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            ConfigurableApplicationContext applicationContext = JavaFxApplication.getApplicationContext();
            loader.setControllerFactory(applicationContext::getBean);
            Parent load = loader.load();
            TeamConnectionBarPreview controller = loader.getController();


            Stage stage = connectionStage;



            stage.setTitle(title);
            Scene  scene = new Scene(load, Color.TRANSPARENT);
            stage.setOnCloseRequest(event -> controller.onWindowClose());

            if (hiddenControl) {
                //stage.initStyle(StageStyle.TRANSPARENT);
            }
            load.setOnMousePressed(event -> {
                xOffset.set(event.getSceneX());
                yOffset.set(event.getSceneY());
            });

            // 拖动时更新窗口位置
            load.setOnMouseDragged(event -> {
                stage.setX(ScreenAdapter.calculateMainX(event.getScreenX() - xOffset.get()));
                stage.setY(ScreenAdapter.calculateMainY(event.getScreenY() - yOffset.get()));
                ObservableList<Screen> screens = Screen.getScreens();
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

            AtomicReference<Configs> configs = new AtomicReference<>(configsMapper.selectById(1));
            AtomicReference<String> config = new AtomicReference<>(configs.get().getConfig());
            loader.setControllerFactory(applicationContext::getBean);
            Parent load = loader.load();
            PreviewPane controller = loader.getController();
            controller.initPage();
            Stage stage = scoreBoardStage;
            if (!StringUtils.isEmpty(config)) {
                String[] split = config.get().split(ConfigSpliter.SPLITER);
                stage.setX(ScreenAdapter.calculateMainX(Double.parseDouble(split[17])));
                stage.setY(ScreenAdapter.calculateMainY(Double.parseDouble(split[18])));
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
                stage.setX(ScreenAdapter.calculateMainX(event.getScreenX() - xOffset.get()));
                stage.setY(ScreenAdapter.calculateMainY(event.getScreenY() - yOffset.get()));
                ObservableList<Screen> screens = Screen.getScreens();
                if (screens.size() < 2) {
                    return;
                }
                Screen extendedScreen = screens.get(1); // 获取第二个屏幕
                Rectangle2D bounds = extendedScreen.getBounds();
                ExtendedScreen.scoreBoardStage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(event.getScreenX() - xOffset.get()));
                ExtendedScreen.scoreBoardStage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(event.getScreenY() - yOffset.get()));
            });

            load.setOnMouseReleased(event -> {
                configs.set(configsMapper.selectById(1));
                config.set(configs.get().getConfig());
                String[] split = config.get().split(ConfigSpliter.SPLITER);
                split[17] = (int)stage.getX() + "";
                split[18] = (int)stage.getY() + "";
                configs.get().setConfig(String.join(ConfigSpliter.SPLITER, split));
                configsMapper.updateById(configs.get());
            });

            scene.getStylesheets().add(cssFile); // 应用 CSS

            stage.setScene(scene);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Node load(String fxml){
        Parent load = null;
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
