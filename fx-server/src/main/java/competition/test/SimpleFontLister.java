package competition.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

/**
 * 简化的JavaFX系统字体列表工具
 */
public class SimpleFontLister extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20px;");

        Label title = new Label("系统字体列表");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 字体列表
        ListView<String> fontListView = new ListView<>();
        List<String> fontFamilies = Font.getFamilies();
        fontListView.getItems().addAll(fontFamilies);
        fontListView.setPrefHeight(300);

        // 预览文本
        Text previewText = new Text("预览文本: Hello World! 你好世界! ㎛℃℉²³");
        previewText.setStyle("-fx-font-size: 16px;");

        // 字体信息标签
        Label fontInfoLabel = new Label("选择一个字体查看预览");

        // 选择监听器
        fontListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Font selectedFont = Font.font(newValue, 16);
                        previewText.setFont(selectedFont);

                        // 显示字体信息
                        fontInfoLabel.setText(String.format(
                                "字体: %s | 实际字体族: %s | 字体名: %s",
                                newValue, selectedFont.getFamily(), selectedFont.getName()
                        ));
                    }
                });

        // 统计信息
        Label statsLabel = new Label("共找到 " + fontFamilies.size() + " 个系统字体");
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // 打印到控制台按钮
        Button printButton = new Button("打印字体列表到控制台");
        printButton.setOnAction(e -> printFontList());

        root.getChildren().addAll(
                title,
                new Label("字体列表:"),
                fontListView,
                new Label("预览:"),
                previewText,
                fontInfoLabel,
                printButton,
                statsLabel
        );

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setTitle("简单字体列表");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 默认选择第一个字体
        if (!fontFamilies.isEmpty()) {
            fontListView.getSelectionModel().selectFirst();
        }
    }

    private void printFontList() {
        List<String> fontFamilies = Font.getFamilies();

//        System.out.println("=".repeat(60));
        System.out.println("JavaFX 系统字体列表 (共 " + fontFamilies.size() + " 个)");
//        System.out.println("=".repeat(60));

        for (int i = 0; i < fontFamilies.size(); i++) {
            String fontFamily = fontFamilies.get(i);
            Font font = Font.font(fontFamily, 14);

            System.out.printf("%3d. %-40s (实际: %s)%n",
                    i + 1, fontFamily, font.getFamily());
        }

//        System.out.println("\n" + "=".repeat(60));
        System.out.println("列表已打印完成！");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
