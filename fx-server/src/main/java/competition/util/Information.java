package competition.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Information {
    
    public static void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("出错啦！！");
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.show();
    }

    public static void info(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示！！");
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.show();
    }

    public static boolean infoWithConfirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("提示！！");
        alert.setHeaderText("");
        alert.setContentText(msg);

        Optional<ButtonType> result = alert.showAndWait();

        // 如果用户点击了OK按钮返回true，否则返回false
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
