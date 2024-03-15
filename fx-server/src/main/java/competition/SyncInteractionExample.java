package competition;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SyncInteractionExample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 定义共享的数据模型
        StringProperty sharedText = new SimpleStringProperty();

        // 获取屏幕信息
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        Rectangle2D secondaryScreenBounds = Screen.getScreens().get(1).getVisualBounds();

        // 创建A屏UI
        VBox rootA = new VBox();
        TextField textFieldA = new TextField();
        textFieldA.textProperty().bindBidirectional(sharedText); // 数据绑定
        rootA.getChildren().add(textFieldA);
        Stage stageA = new Stage();
        stageA.setScene(new Scene(rootA, 300, 200));
        stageA.setX(primaryScreenBounds.getMinX());
        stageA.setY(primaryScreenBounds.getMinY());
        stageA.show();

        // 创建B屏UI，使用相同的数据模型
        VBox rootB = new VBox();
        TextField textFieldB = new TextField();
        textFieldB.textProperty().bindBidirectional(sharedText); // 数据绑定
        textFieldB.setEditable(false); // B屏上的TextField不允许编辑，仅用于显示
        rootB.getChildren().add(textFieldB);
        Stage stageB = new Stage();
        stageB.setScene(new Scene(rootB, 300, 200));
        stageB.setX(secondaryScreenBounds.getMinX());
        stageB.setY(secondaryScreenBounds.getMinY());
        stageB.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
