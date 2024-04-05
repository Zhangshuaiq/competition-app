package competition.util;

import javafx.scene.control.Alert;

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
}
