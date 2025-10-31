package competition.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.InputStream;

public class TextExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建 Text 对象
        Text text1 = new Text("4、患者男性，45岁，发热2天，伴乏力、纳差、恶心、呕吐就诊。\n");
        Text text2 = new Text("A.200-400㎛    B.100-200㎛   C.2-50㎛    D.2-50mm");

        // 设置字体
        Font font = loadCustomFont();
        text1.setFont(font);
        text2.setFont(font);

        // 设置样式
        text1.setFill(javafx.scene.paint.Color.BLACK);
        text2.setFill(javafx.scene.paint.Color.DARKRED);

        // 使用 TextFlow 来容纳多个 Text（支持自动换行）
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().addAll(text1, text2);
        textFlow.setPrefWidth(400);

        // 或者直接使用 VBox
        VBox root = new VBox(10);
        root.getChildren().add(textFlow);

        Scene scene = new Scene(root, 450, 200);
        primaryStage.setTitle("Text 类字体测试");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("使用字体: " + font.getFamily());
    }

    private Font loadCustomFont() {
        try {
            // 尝试加载自定义字体
            InputStream fontStream = getClass().getResourceAsStream("/fonts/NotoSans-Regular.ttf");
            if (fontStream != null) {
                return Font.loadFont(fontStream, 14);
            }
        } catch (Exception e) {
            System.out.println("自定义字体加载失败: " + e.getMessage());
        }

        // 回退到系统字体
        String[] fallbackFonts = {
                "Segoe UI Symbol",
                "Arial Unicode MS",
                "Noto Sans",
                "DejaVu Sans",
                "Microsoft YaHei",
                "SansSerif"
        };

        for (String fontName : fallbackFonts) {
            Font font = Font.font(fontName, 14);
            if (font != null) {
                return font;
            }
        }

        return Font.getDefault();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
