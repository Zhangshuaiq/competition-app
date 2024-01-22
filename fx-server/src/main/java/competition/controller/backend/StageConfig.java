package competition.controller.backend;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;

public class StageConfig {
    
    @FXML
    private ScrollPane stageAddress;
    
    @FXML
    private ComboBox<String> answerAction;
    
    
    @FXML
    public void initialize(){
        answerAction.getItems().addAll("直接抢答",
                "1秒后第一个抢答成功,提前为违规",
                "3秒后第一个抢答成功,提前为违规",
                "4秒后第一个抢答成功,提前为违规",
                "5秒后第一个抢答成功,提前为违规");
    }
}
