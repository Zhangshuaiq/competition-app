package competition.test;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * JavaFX Text 上下标显示完整解决方案
 */
public class SuperscriptSubscriptDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20px;");

        Label title = new Label("JavaFX Text 上下标显示方法");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 方法1: 使用Unicode上下标字符
        VBox method1 = createMethod1Example();

        // 方法2: 使用TextFlow和字体大小调整
        VBox method2 = createMethod2Example();

        // 方法3: 使用Group和坐标偏移
        VBox method3 = createMethod3Example();

        // 方法4: 富文本解析器
        VBox method4 = createMethod4Example();

        // 方法5: 数学公式渲染
        VBox method5 = createMethod5Example();

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(15);
        content.getChildren().addAll(title, method1, method2, method3, method4, method5);
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 700);
        primaryStage.setTitle("JavaFX 上下标显示演示");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 方法1: Unicode上下标字符
    private VBox createMethod1Example() {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-padding: 15;");

        Label methodTitle = new Label("方法1: Unicode上下标字符");
        methodTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 上标示例
        Text superscriptExample = new Text();
        superscriptExample.setText("数学公式: E=mc² | 面积: 25m² | 体积: 8cm³ | 指数: x⁵⁺ⁿ");
        superscriptExample.setFont(Font.font("Arial", 16));

        // 下标示例
        Text subscriptExample = new Text();
        subscriptExample.setText("化学公式: H₂O | CO₂ | C₆H₁₂O₆ | 变量: x₁, x₂, xₙ");
        subscriptExample.setFont(Font.font("Arial", 16));

        // 代码示例
        TextArea codeArea = new TextArea();
        codeArea.setText(
                "// Unicode上下标字符代码示例\n" +
                        "Text text = new Text(\"E=mc²\");\n" +
                        "Text formula = new Text(\"H₂O + CO₂\");\n\n" +
                        "// 常用上标字符: ⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁼⁽⁾\n" +
                        "// 常用下标字符: ₀₁₂₃₄₅₆₇₈₉₊₋₌₍₎"
        );
        codeArea.setPrefRowCount(6);
        codeArea.setEditable(false);
        codeArea.setStyle("-fx-font-family: monospace;");

        box.getChildren().addAll(methodTitle,
                new Label("上标示例:"), superscriptExample,
                new Label("下标示例:"), subscriptExample,
                new Label("代码:"), codeArea);

        return box;
    }

    // 方法2: TextFlow + 字体大小调整
    private VBox createMethod2Example() {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-padding: 15;");

        Label methodTitle = new Label("方法2: TextFlow + 字体大小调整");
        methodTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 创建E=mc²示例
        TextFlow emc2 = createSuperscriptFormula("E=mc", "2");

        // 创建H₂O示例
        TextFlow h2o = createSubscriptFormula("H", "2", "O");

        // 创建复杂公式示例
        TextFlow complexFormula = createComplexFormula();

        // 代码示例
        TextArea codeArea = new TextArea();
        codeArea.setText(
                "// TextFlow + 字体大小调整方法\n" +
                        "private TextFlow createSuperscript(String base, String sup) {\n" +
                        "    Text baseText = new Text(base);\n" +
                        "    baseText.setFont(Font.font(16));\n" +
                        "    \n" +
                        "    Text supText = new Text(sup);\n" +
                        "    supText.setFont(Font.font(12));\n" +
                        "    supText.setTranslateY(-4); // 向上偏移\n" +
                        "    \n" +
                        "    return new TextFlow(baseText, supText);\n" +
                        "}"
        );
        codeArea.setPrefRowCount(10);
        codeArea.setEditable(false);
        codeArea.setStyle("-fx-font-family: monospace;");

        box.getChildren().addAll(methodTitle,
                new Label("示例效果:"),
                emc2, h2o, complexFormula,
                new Label("代码:"), codeArea);

        return box;
    }

    // 方法3: Group + 坐标偏移
    private VBox createMethod3Example() {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-padding: 15;");

        Label methodTitle = new Label("方法3: Group + 精确坐标偏移");
        methodTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 创建精确定位的上下标
        Group preciseFormula = createPrecisePositionedFormula();

        // 代码示例
        TextArea codeArea = new TextArea();
        codeArea.setText(
                "// Group + 精确坐标偏移方法\n" +
                        "private Group createPreciseFormula() {\n" +
                        "    Text base = new Text(0, 0, \"x\");\n" +
                        "    base.setFont(Font.font(20));\n" +
                        "    \n" +
                        "    Text superscript = new Text(15, -8, \"2\");\n" +
                        "    superscript.setFont(Font.font(14));\n" +
                        "    \n" +
                        "    Text subscript = new Text(25, 8, \"i\");\n" +
                        "    subscript.setFont(Font.font(14));\n" +
                        "    \n" +
                        "    return new Group(base, superscript, subscript);\n" +
                        "}"
        );
        codeArea.setPrefRowCount(12);
        codeArea.setEditable(false);
        codeArea.setStyle("-fx-font-family: monospace;");

        box.getChildren().addAll(methodTitle,
                new Label("精确定位效果:"),
                preciseFormula,
                new Label("代码:"), codeArea);

        return box;
    }

    // 方法4: 富文本解析器
    private VBox createMethod4Example() {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-padding: 15;");

        Label methodTitle = new Label("方法4: 富文本标记解析器");
        methodTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 解析标记文本示例
        String markupText1 = "E=mc^{2} + H_{2}O → CO_{2}";
        String markupText2 = "x^{n+1} = x^{n} + f(x_{n})";

        TextFlow parsed1 = parseMarkupText(markupText1);
        TextFlow parsed2 = parseMarkupText(markupText2);

        // 输入框和实时解析
        TextField inputField = new TextField();
        inputField.setPromptText("输入标记文本，如: x^{2} + y_{1}");

        TextFlow livePreview = new TextFlow();
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            TextFlow parsed = parseMarkupText(newText);
            livePreview.getChildren().setAll(parsed.getChildren());
        });

        // 代码示例
        TextArea codeArea = new TextArea();
        codeArea.setText(
                "// 富文本解析器示例\n" +
                        "// 标记格式: ^{上标} _{下标}\n" +
                        "String markup = \"E=mc^{2} + H_{2}O\";\n" +
                        "TextFlow result = parseMarkupText(markup);\n\n" +
                        "// 支持的标记:\n" +
                        "// x^{2}     -> x²\n" +
                        "// H_{2}O    -> H₂O\n" +
                        "// x^{n+1}   -> x^(n+1)"
        );
        codeArea.setPrefRowCount(8);
        codeArea.setEditable(false);
        codeArea.setStyle("-fx-font-family: monospace;");

        box.getChildren().addAll(methodTitle,
                new Label("解析示例:"),
                new Label("输入: " + markupText1), parsed1,
                new Label("输入: " + markupText2), parsed2,
                new Label("实时解析:"),
                inputField, livePreview,
                new Label("代码:"), codeArea);

        return box;
    }

    // 方法5: 数学公式渲染
    private VBox createMethod5Example() {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-padding: 15;");

        Label methodTitle = new Label("方法5: 数学公式渲染器");
        methodTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 复杂数学公式示例
        TextFlow quadraticFormula = createQuadraticFormula();
        TextFlow binomialTheorem = createBinomialTheorem();

        // 说明文本
        Label description = new Label(
                "这种方法适合复杂的数学公式渲染，可以处理分数、根号、求和等复杂结构"
        );
        description.setWrapText(true);

        box.getChildren().addAll(methodTitle,
                new Label("二次方程公式:"), quadraticFormula,
                new Label("二项式定理:"), binomialTheorem,
                description);

        return box;
    }

    // 辅助方法实现

    private TextFlow createSuperscriptFormula(String base, String superscript) {
        Text baseText = new Text(base);
        baseText.setFont(Font.font("Arial", 16));

        Text supText = new Text(superscript);
        supText.setFont(Font.font("Arial", 12));
        supText.setTranslateY(-4);

        return new TextFlow(baseText, supText);
    }

    private TextFlow createSubscriptFormula(String before, String subscript, String after) {
        Text beforeText = new Text(before);
        beforeText.setFont(Font.font("Arial", 16));

        Text subText = new Text(subscript);
        subText.setFont(Font.font("Arial", 12));
        subText.setTranslateY(4);

        Text afterText = new Text(after);
        afterText.setFont(Font.font("Arial", 16));

        return new TextFlow(beforeText, subText, afterText);
    }

    private TextFlow createComplexFormula() {
        Text x = new Text("x");
        x.setFont(Font.font("Arial", 16));

        Text sup1 = new Text("2");
        sup1.setFont(Font.font("Arial", 12));
        sup1.setTranslateY(-4);

        Text plus = new Text(" + ");
        plus.setFont(Font.font("Arial", 16));

        Text y = new Text("y");
        y.setFont(Font.font("Arial", 16));

        Text sub1 = new Text("1");
        sub1.setFont(Font.font("Arial", 12));
        sub1.setTranslateY(4);

        Text equals = new Text(" = ");
        equals.setFont(Font.font("Arial", 16));

        Text z = new Text("z");
        z.setFont(Font.font("Arial", 16));

        Text sub2 = new Text("n");
        sub2.setFont(Font.font("Arial", 12));
        sub2.setTranslateY(4);

        Text sup2 = new Text("3");
        sup2.setFont(Font.font("Arial", 12));
        sup2.setTranslateY(-4);

        return new TextFlow(x, sup1, plus, y, sub1, equals, z, sub2, sup2);
    }

    private Group createPrecisePositionedFormula() {
        Text base = new Text(0, 20, "A");
        base.setFont(Font.font("Arial", 24));

        Text superscript = new Text(20, 8, "n");
        superscript.setFont(Font.font("Arial", 16));

        Text subscript = new Text(20, 32, "i");
        subscript.setFont(Font.font("Arial", 16));

        Text plus = new Text(35, 20, " + B");
        plus.setFont(Font.font("Arial", 24));

        Text sup2 = new Text(75, 8, "m");
        sup2.setFont(Font.font("Arial", 16));

        Text sub2 = new Text(75, 32, "j");
        sub2.setFont(Font.font("Arial", 16));

        Group group = new Group(base, superscript, subscript, plus, sup2, sub2);
        group.setStyle("-fx-border-color: lightgray; -fx-border-style: dashed;");

        return group;
    }

    private TextFlow parseMarkupText(String markup) {
        TextFlow textFlow = new TextFlow();

        if (markup == null || markup.isEmpty()) {
            return textFlow;
        }

        StringBuilder current = new StringBuilder();
        int i = 0;

        while (i < markup.length()) {
            char c = markup.charAt(i);

            if (c == '^' && i + 1 < markup.length() && markup.charAt(i + 1) == '{') {
                // 处理上标
                if (current.length() > 0) {
                    Text normalText = new Text(current.toString());
                    normalText.setFont(Font.font("Arial", 16));
                    textFlow.getChildren().add(normalText);
                    current.setLength(0);
                }

                int end = markup.indexOf('}', i + 2);
                if (end != -1) {
                    String superscript = markup.substring(i + 2, end);
                    Text supText = new Text(superscript);
                    supText.setFont(Font.font("Arial", 12));
                    supText.setTranslateY(-4);
                    textFlow.getChildren().add(supText);
                    i = end + 1;
                } else {
                    current.append(c);
                    i++;
                }
            } else if (c == '_' && i + 1 < markup.length() && markup.charAt(i + 1) == '{') {
                // 处理下标
                if (current.length() > 0) {
                    Text normalText = new Text(current.toString());
                    normalText.setFont(Font.font("Arial", 16));
                    textFlow.getChildren().add(normalText);
                    current.setLength(0);
                }

                int end = markup.indexOf('}', i + 2);
                if (end != -1) {
                    String subscript = markup.substring(i + 2, end);
                    Text subText = new Text(subscript);
                    subText.setFont(Font.font("Arial", 12));
                    subText.setTranslateY(4);
                    textFlow.getChildren().add(subText);
                    i = end + 1;
                } else {
                    current.append(c);
                    i++;
                }
            } else {
                current.append(c);
                i++;
            }
        }

        if (current.length() > 0) {
            Text normalText = new Text(current.toString());
            normalText.setFont(Font.font("Arial", 16));
            textFlow.getChildren().add(normalText);
        }

        return textFlow;
    }

    private TextFlow createQuadraticFormula() {
        // x = (-b ± √(b²-4ac)) / 2a
        Text x = new Text("x = ");
        x.setFont(Font.font("Arial", 16));

        Text fraction1 = new Text("(-b ± √(b");
        fraction1.setFont(Font.font("Arial", 16));

        Text sup1 = new Text("2");
        sup1.setFont(Font.font("Arial", 12));
        sup1.setTranslateY(-4);

        Text fraction2 = new Text("-4ac)) / 2a");
        fraction2.setFont(Font.font("Arial", 16));

        return new TextFlow(x, fraction1, sup1, fraction2);
    }

    private TextFlow createBinomialTheorem() {
        // (a+b)ⁿ = Σ(k=0 to n) C(n,k) * a^(n-k) * b^k
        Text formula = new Text("(a+b)");
        formula.setFont(Font.font("Arial", 16));

        Text sup = new Text("n");
        sup.setFont(Font.font("Arial", 12));
        sup.setTranslateY(-4);

        Text equals = new Text(" = Σ C(n,k) * a");
        equals.setFont(Font.font("Arial", 16));

        Text sup2 = new Text("n-k");
        sup2.setFont(Font.font("Arial", 10));
        sup2.setTranslateY(-4);

        Text times = new Text(" * b");
        times.setFont(Font.font("Arial", 16));

        Text sup3 = new Text("k");
        sup3.setFont(Font.font("Arial", 12));
        sup3.setTranslateY(-4);

        return new TextFlow(formula, sup, equals, sup2, times, sup3);
    }

    public static void main(String[] args) {
        launch(args);
    }
}