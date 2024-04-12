package competition.controller.backend;

import competition.util.ScoreSaver;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class GradeMng {
    
    
    @FXML
    public void clearData(){
        ScoreSaver.clear();
    }
}
