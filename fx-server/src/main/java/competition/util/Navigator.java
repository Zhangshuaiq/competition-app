package competition.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
    
    
    public static void navigatorTo(String fxml, String title, String cssFile){
        Parent load = null;
        try {
            load = FXMLLoader.load(Navigator.class.getResource(fxml));
            Stage stage = new Stage();
            stage.setTitle(title);
            Scene scene = new Scene(load);
            scene.getStylesheets().add(cssFile); // 应用 CSS
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Node load(String fxml){
        Node load = null;
        try {
            load = FXMLLoader.load(Navigator.class.getResource(fxml));
            return load;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
