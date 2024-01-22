package competition.util;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class ExtendedScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 获取所有屏幕
        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D primaryBounds = primaryScreen.getVisualBounds();

        // 假设扩展屏幕是第二个屏幕
        Screen extendedScreen = Screen.getScreens().get(1);
        Rectangle2D extendedBounds = extendedScreen.getVisualBounds();

        // 在扩展屏幕上创建一个窗口
        Stage extendedStage = new Stage();
        extendedStage.setTitle("Extended Screen Window");
        extendedStage.setX(extendedBounds.getMinX());
        extendedStage.setY(extendedBounds.getMinY());
        extendedStage.setWidth(extendedBounds.getWidth() / 2);
        extendedStage.setHeight(extendedBounds.getHeight() / 2);

        // 添加一些内容
        Label label = new Label("Hello on Extended Screen!");
        Scene scene = new Scene(label, 200, 100);
        extendedStage.setScene(scene);

        // 显示窗口
        extendedStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
