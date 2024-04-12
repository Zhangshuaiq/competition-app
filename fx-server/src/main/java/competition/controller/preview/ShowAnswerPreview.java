package competition.controller.preview;

import competition.modal.TeamResult;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ShowAnswerPreview extends PreviewPane{
    
    @FXML
    private AnchorPane container;
    
    @FXML
    private StackPane answerBack;
    
    @FXML
    private Text correctText;
    
    @FXML
    private GridPane gridPane;
    
    private static TeamResult teamResult;

    public static void setTeamResult(TeamResult teamResult) {
        ShowAnswerPreview.teamResult = teamResult;
    }

    @FXML
    public void initialize(){
        this.showData();
    }

    
    private void showData(){
        correctText.setText("正确答案：" + teamResult.getCorrectAnswer());
    }
    

    @Override
    public void reload() {
        this.showData();
    }
}
