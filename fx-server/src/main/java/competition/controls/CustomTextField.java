package competition.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * 自定义输入框，支持输入内容限制
 */
public class CustomTextField extends TextField {

    // 输入类型枚举
    public enum InputType {
        ANY,           // 任意字符
        NUMERIC,       // 仅整数
        DECIMAL,       // 数字（支持小数）
        ALPHA,         // 仅字母
        ALPHANUMERIC,  // 字母和数字
        EMAIL,         // 邮箱格式
        PHONE,         // 电话号码
        CUSTOM         // 自定义正则表达式
    }

    // 输入类型属性
    private final ObjectProperty<InputType> inputType = new SimpleObjectProperty<>(InputType.ANY);

    // 自定义正则表达式属性
    private final StringProperty customPattern = new SimpleStringProperty("");

    // 最大长度属性
    private final ObjectProperty<Integer> maxLength = new SimpleObjectProperty<>(Integer.MAX_VALUE);

    public CustomTextField() {
        super();
        setupTextFormatter();

        // 监听属性变化
        inputType.addListener((obs, oldVal, newVal) -> setupTextFormatter());
        customPattern.addListener((obs, oldVal, newVal) -> setupTextFormatter());
        maxLength.addListener((obs, oldVal, newVal) -> setupTextFormatter());
    }

    /**
     * 设置文本格式化器
     */
    private void setupTextFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // 检查最大长度
            if (newText.length() > maxLength.get()) {
                return null;
            }

            // 根据输入类型进行验证
            if (!validateInput(newText)) {
                return null;
            }

            return change;
        };

        setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * 验证输入内容
     */
    private boolean validateInput(String text) {
        if (text.isEmpty()) {
            return true;
        }

        switch (inputType.get()) {
            case ANY:
                return true;

            case NUMERIC:
                return text.matches("^-?\\d+$");

            case DECIMAL:
                // 允许数字和小数点
                return text.matches("^-?(\\d+\\.?\\d*|\\.\\d+)$");

            case ALPHA:
                // 仅允许字母
                return text.matches("^[a-zA-Z]*$");

            case ALPHANUMERIC:
                // 允许字母和数字
                return text.matches("^[a-zA-Z0-9]*$");

            case EMAIL:
                // 邮箱格式（实时验证较宽松）
                return text.matches("^[a-zA-Z0-9@._-]*$");

            case PHONE:
                // 电话号码（允许数字、空格、短横线、括号）
                return text.matches("^[0-9\\s\\-()]*$");

            case CUSTOM:
                // 自定义正则表达式
                if (!customPattern.get().isEmpty()) {
                    try {
                        return Pattern.matches(customPattern.get(), text);
                    } catch (Exception e) {
                        return true;
                    }
                }
                return true;

            default:
                return true;
        }
    }

    // Getter 和 Setter 方法
    public InputType getInputType() {
        return inputType.get();
    }

    public void setInputType(InputType inputType) {
        this.inputType.set(inputType);
    }

    public ObjectProperty<InputType> inputTypeProperty() {
        return inputType;
    }

    public String getCustomPattern() {
        return customPattern.get();
    }

    public void setCustomPattern(String pattern) {
        this.customPattern.set(pattern);
    }

    public StringProperty customPatternProperty() {
        return customPattern;
    }

    public Integer getMaxLength() {
        return maxLength.get();
    }

    public void setMaxLength(Integer length) {
        this.maxLength.set(length);
    }

    public ObjectProperty<Integer> maxLengthProperty() {
        return maxLength;
    }
}