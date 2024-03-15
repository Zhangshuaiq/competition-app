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
}
