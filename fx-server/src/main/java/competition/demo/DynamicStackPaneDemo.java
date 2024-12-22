package competition.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DynamicStackPaneDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建 HBox 容器
        HBox hbox = new HBox(10); // 子元素之间的间距为 10

        // 为 HBox 创建多个 VBox
        for (int i = 0; i < 3; i++) {
            VBox vbox = new VBox(10); // VBox 子元素之间的间距为 10

            // 每个 VBox 中创建多个 StackPane
            for (int j = 0; j < 2; j++) {
                StackPane stackPane = new StackPane();
                Text text = new Text("StackPane " + i + "-" + j);

                // 设置 StackPane 的初始宽高
                stackPane.setPrefSize(100, 50);
                stackPane.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");
                stackPane.getChildren().add(text);

                // 按钮点击时宽高增加 1
                Button button = new Button("Increase Size");
                button.setOnAction(event -> {
                    double newWidth = stackPane.getPrefWidth() + 1;
                    double newHeight = stackPane.getPrefHeight() + 1;
                    stackPane.setPrefSize(newWidth, newHeight);

                    // 强制刷新布局
                  //  Platform.runLater(() -> stackPane.getParent().layout());
                });

                // 添加 StackPane 和按钮到 VBox
                vbox.getChildren().addAll(stackPane, button);
            }

            // 将 VBox 添加到 HBox
            hbox.getChildren().add(vbox);
        }

        // 创建场景并显示
        Scene scene = new Scene(hbox, 600, 400);
        primaryStage.setTitle("Dynamic StackPane Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}