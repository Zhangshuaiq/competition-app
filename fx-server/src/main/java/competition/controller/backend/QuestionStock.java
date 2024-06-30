package competition.controller.backend;

import cn.hutool.core.io.FileUtil;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.util.BaseDir;
import competition.util.CurrentQuestions;
import competition.util.ExcelParser;
import competition.util.RuntimeQuestionStock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionStock {


    @FXML
    private TableView<Questions> tableView;
    
    @FXML
    private ComboBox<String> stockList;
    

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

        List<String> fileNames = FileUtil.listFileNames(BaseDir.TIKU_DIR);
        for (String fileName : fileNames) {
            if (new File(BaseDir.TIKU_DIR + fileName).isDirectory()) {
                continue;
            }
            stockList.getItems().add(fileName);
        }
        
        if (stockList.getItems().size() > 0) {
            stockList.setValue(stockList.getItems().get(0));
            showData();
        }
    }

    public void showData() {
        final List<Questions> questions = ExcelParser.parse(BaseDir.TIKU_DIR + stockList.getValue(), Questions.class);
        tableView.setItems(FXCollections.observableList(questions));
    }
    
    @FXML
    public void useStock(){
        if (StringUtils.isEmpty(stockList.getValue())) {
            return;
        }
        showData();
        RuntimeQuestionStock.loadQuestion(BaseDir.TIKU_DIR + stockList.getValue());
        
    }
    
//    @FXML
//    public void openStyleData(){
//        String filePath = ExcelParser.S_PATH;
//        File file = new File(filePath);
//        if (Desktop.isDesktopSupported()) {
//            try {
//                Desktop.getDesktop().open(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    @FXML
    public void importData() {
        if (StringUtils.isEmpty(stockList.getValue())) {
            return;
        }
        String filePath = BaseDir.TIKU_DIR + stockList.getValue();
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
