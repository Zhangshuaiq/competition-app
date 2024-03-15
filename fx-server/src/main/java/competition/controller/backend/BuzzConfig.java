package competition.controller.backend;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.springframework.stereotype.Component;

@Component
public class BuzzConfig {
    
    @FXML
    private ComboBox<String> buzzCombox;
    
    
    @FXML
    public void initialize(){
        buzzCombox.getItems().addAll(
                "直接抢答",
                "1秒后第一个抢答成功，提前为违规",
                "3秒后第一个抢答成功，提前为违规",
                "先判抢答"
        );
    }
}
