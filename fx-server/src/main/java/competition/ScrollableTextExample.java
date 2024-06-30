package competition;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ScrollableTextExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 创建 AnchorPane 并设置背景图片
        AnchorPane anchorPane = new AnchorPane();
        BackgroundImage backgroundImage = new BackgroundImage(
                new javafx.scene.image.Image("file:background.jpg"), // 请确保此路径指向您的背景图片
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        anchorPane.setBackground(new Background(backgroundImage));

        // 创建 TextFlow 并添加内容
        TextFlow textFlow = new TextFlow();
        for (int i = 0; i < 50; i++) {
            Text text = new Text("This is line " + (i + 1) + "\n");
            textFlow.getChildren().add(text);
        }
        textFlow.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), Insets.EMPTY)));
        // 创建 ScrollPane 包装 TextFlow 并隐藏滚动条
        ScrollPane scrollPane = new ScrollPane(textFlow);
        scrollPane.setPrefSize(300, 300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // 将 ScrollPane 添加到 AnchorPane
        scrollPane.setLayoutX(100);
        scrollPane.setLayoutY(60);
//        AnchorPane.setTopAnchor(scrollPane, 50.0);
//        AnchorPane.setLeftAnchor(scrollPane, 50.0);
        anchorPane.getChildren().add(scrollPane);

        // 创建 Scene 并设置到 Stage
        Scene scene = new Scene(anchorPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Scrollable Text Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
