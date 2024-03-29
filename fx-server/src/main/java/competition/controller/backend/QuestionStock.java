package competition.controller.backend;

import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.util.ExcelParser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class QuestionStock {


    @FXML
    private TableView<Questions> tableView;
    
    @FXML
    private Label statusLabel;

    @FXML
    private TableColumn<Questions, String> stageNumberCol, stageNameCol, questionTypeCol, resourceTypeCol, resourceCol,
            questionCol, choiceACol, choiceBCol, choiceCCol, choiceDCol,
            choiceECol, choiceFCol, answerCol, remarkCol;


    @FXML
    public void initialize() {
        stageNumberCol.setCellValueFactory(new PropertyValueFactory<>("stageNumber"));
        stageNameCol.setCellValueFactory(new PropertyValueFactory<>("stageName"));
        questionTypeCol.setCellValueFactory(new PropertyValueFactory<>("questionType"));
        resourceTypeCol.setCellValueFactory(new PropertyValueFactory<>("resourceType"));
        resourceCol.setCellValueFactory(new PropertyValueFactory<>("resource"));
        questionCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        choiceACol.setCellValueFactory(new PropertyValueFactory<>("choiceA"));
        choiceBCol.setCellValueFactory(new PropertyValueFactory<>("choiceB"));
        choiceCCol.setCellValueFactory(new PropertyValueFactory<>("choiceC"));
        choiceDCol.setCellValueFactory(new PropertyValueFactory<>("choiceD"));
        choiceECol.setCellValueFactory(new PropertyValueFactory<>("choiceE"));
        choiceFCol.setCellValueFactory(new PropertyValueFactory<>("choiceF"));
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answer"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        showData();
    }

    public void showData() {
        final List<Questions> questions = ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
        final List<QuestionStyle> parse = ExcelParser.parse(ExcelParser.S_PATH,  QuestionStyle.class);
        if (questions.size() == parse.size()) {
            statusLabel.setText("正常");
            statusLabel.setTextFill(Color.GREEN);
        } else {
            statusLabel.setText("样式和题目数量不一致，请修改");
            statusLabel.setTextFill(Color.RED);
        }
        tableView.setItems(FXCollections.observableList(questions));
    }
    
    
    @FXML
    public void openStyleData(){
        String filePath = ExcelParser.S_PATH;
        File file = new File(filePath);
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void importData() {
        String filePath = ExcelParser.Q_PATH;
        File file = new File(filePath);
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
