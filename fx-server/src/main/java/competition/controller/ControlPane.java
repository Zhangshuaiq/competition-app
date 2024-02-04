package competition.controller;

import competition.util.Navigator;
import javafx.fxml.FXML;

public class ControlPane {
    
    @FXML
    public void toBackend(){
        Navigator.navigatorTo("/view/Backend.fxml", "后台设置", "/view/style.css");
    }
    
    @FXML
    public void exitApp(){
        System.exit(0);
    }
}
