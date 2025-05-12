package competition.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.JavaFxApplication;
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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

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
    

    static {
       // previewStage.initStyle(StageStyle.TRANSPARENT);
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

        backendStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                GlobalCache.backendOpen = false;
            }
        });

//        activeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                System.exit(0);
//            }
//        });
        activeStage.setAlwaysOnTop(true);
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
            previewScene.setFill(Color.TRANSPARENT);
            previewScene.getStylesheets().add(cssFile); // 应用 CSS

            previewStage.setScene(previewScene);
            previewStage.show();
            previewStage.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                    // 关闭
                    GlobalCache.showTeamRank = false;
                    GlobalCache.showMemberRank = false;
                    GlobalCache.showModel7Rank = false;
                    GlobalCache.showBuzzRank = false;
                }
            });
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
                System.out.println("抢答提示框拖动后位置 ========================>");
                System.out.println(stage.getX());
                System.out.println(stage.getY());
                if (!StringUtils.isEmpty(config)) {
                    JSONObject jsonObject = JSON.parseObject(config);
                    jsonObject.put("x", stage.getX() + "");
                    jsonObject.put("y", stage.getY() + "");
                    configs.setConfig(jsonObject.toJSONString());
                    configsMapper.updateById(configs);
                }
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
                System.out.println("抢答计时拖动后位置 ========================>");
                System.out.println(stage.getX());
                System.out.println(stage.getY());
                BuzzMapper buzzMapper = JavaFxApplication.getApplicationContext().getBean(BuzzMapper.class);
                Buzz buzz = buzzMapper.selectById(1);
                buzz.setX(stage.getX() + "");
                buzz.setY(stage.getY() + "");
                buzzMapper.updateById(buzz);
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
                System.out.println("答题计时拖动后位置 ========================>");
                configs.set(configsMapper.selectById(1));
                config.set(configs.get().getConfig());
                System.out.println(stage.getX());
                System.out.println(stage.getY());
                String[] split = config.get().split(ConfigSpliter.SPLITER);
                split[29] = stage.getX() + "";
                split[30] = stage.getY() + "";
                configs.get().setConfig(String.join(ConfigSpliter.SPLITER, split));
                configsMapper.updateById(configs.get());
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
//                if (screens.size() < 2) {
//                    return;
//                }
//                Screen extendedScreen = screens.get(1); // 获取第二个屏幕
//                Rectangle2D bounds = extendedScreen.getBounds();
//                ExtendedScreen.scoreBoardStage.setX(bounds.getMinX() + ScreenAdapter.calculateExX(event.getScreenX() - xOffset.get()));
//                ExtendedScreen.scoreBoardStage.setY(bounds.getMinY() + ScreenAdapter.calculateExY(event.getScreenY() - yOffset.get()));
            });

//            load.setOnMouseReleased(event -> {
//                System.out.println("状态面板拖动后位置 ========================>");
//                System.out.println(stage.getX());
//                System.out.println(stage.getY());
//                String[] split = config.split(ConfigSpliter.SPLITER);
//                split[17] = stage.getX() + "";
//                split[18] = stage.getY() + "";
//                configs.setConfig(String.join(ConfigSpliter.SPLITER, split));
//                configsMapper.updateById(configs);
//            });

            scene.getStylesheets().add(cssFile); // 应用 CSS

//            SwingUtilities.invokeLater(() -> {
//                JDialog dialog = new JDialog();
//                dialog.setUndecorated(true); // 去掉对话框装饰
//                dialog.setAlwaysOnTop(true); // 可选：保持对话框在最上层
//                JFXPanel jfxPanel = new JFXPanel();
//
//                scene.setFill(null);
//
//                jfxPanel.setScene(scene);
//                dialog.add(jfxPanel);
//                dialog.setSize(1000, 200);
//                dialog.setVisible(true);
//            });

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
                System.out.println("分数面板拖动后位置 ========================>");
                configs.set(configsMapper.selectById(1));
                config.set(configs.get().getConfig());
                System.out.println(stage.getX());
                System.out.println(stage.getY());
                String[] split = config.get().split(ConfigSpliter.SPLITER);
                split[17] = stage.getX() + "";
                split[18] = stage.getY() + "";
                configs.get().setConfig(String.join(ConfigSpliter.SPLITER, split));
                configsMapper.updateById(configs.get());
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
