package competition.controller.backend;

import competition.entity.FontAttr;
import competition.util.FontChooserPanel;
import competition.util.FontMap;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ScoreConfig {
    
    
    @FXML
    public void codeFontConfig(){
        FontChooserPanel.start(new Stage(), ("codeFontAttr"));
    }
    
    @FXML
    public void teamFontConfig(){
        FontChooserPanel.start(new Stage(), ("teamFontAttr"));
    }
    
    @FXML
    public void scoreFontConfig(){
        FontChooserPanel.start(new Stage(), ("scoreFontAttr"));
    }
}
