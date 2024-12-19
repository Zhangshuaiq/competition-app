package competition.controller.backend;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.QuestionStyle;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.QuestionsMapper;
import competition.mapper.StagesMapper;
import competition.util.BaseDir;
import competition.util.CurrentQuestions;
import competition.util.ExcelParser;
import competition.util.RuntimeQuestionStock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QuestionStock {


    @FXML
    private TableView<Questions> tableView;
    
    @FXML
    private ComboBox<String> stockList;
    
    @Resource
    private QuestionsMapper questionsMapper;
    
    @Resource 
    private StagesMapper stagesMapper;

    @FXML
    private TableColumn<Questions, String> stageNumberCol, stageNameCol, questionTypeCol, resourceTypeCol, resourceCol,
            questionCol, choiceACol, choiceBCol, choiceCCol, choiceDCol,
            choiceECol, choiceFCol, answerCol, remarkCol;


    @FXML
    public void initialize() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        List<Questions> questions = questionsMapper.selectList(new LambdaQueryWrapper<>());
        if (!CollectionUtils.isEmpty(questions)) {
            tableView.setItems(FXCollections.observableList(questions));
            stockList.setValue(questions.get(0).getGroup());
        } else {
            if (stockList.getItems().size() > 0) {
                stockList.setValue(stockList.getItems().get(0));
                showData();
            }
        }
    }

    public void showData() {
        final List<Questions> questions = ExcelParser.parse(BaseDir.TIKU_DIR + stockList.getValue(), Questions.class);
        tableView.setItems(FXCollections.observableList(questions));
    }
    
    
    @FXML
    public void questionReset(){
        ObservableList<Questions> selectedItems = tableView.getSelectionModel().getSelectedItems();
        for (Questions selectedItem : selectedItems) {
            selectedItem.setHasAnswer(false);
            questionsMapper.updateById(selectedItem);
        }
    }
    
    
    @FXML
    public void useStock(){
        if (StringUtils.isEmpty(stockList.getValue())) {
            return;
        }
        showData();
        List<Questions> questions = RuntimeQuestionStock.loadQuestion(BaseDir.TIKU_DIR + stockList.getValue());
        questionsMapper.delete(new LambdaQueryWrapper<>());
        Map<String, List<Questions>> collect = questions.stream().collect(Collectors.groupingBy(Questions::getStageNumber));
        for (Map.Entry<String, List<Questions>> entry : collect.entrySet()) {
            List<Questions> list = entry.getValue();
//            Stages stages = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
//                    .eq(Stages::getSortVal, list.get(0).getStageNumber())
//                    .last(" LIMIT 1 "));
            for (int i = 0; i < list.size(); i++) {
                Questions q = list.get(i);
                q.setQuestionNumber(i + 1);
                q.setGroup(stockList.getValue());
                questionsMapper.insert(q);
            }
            // 乱序
            Collections.shuffle(list);
            for (int i = 0; i < list.size(); i++) {
                Questions q = list.get(i);
                q.setViewSort(i + 1);
                questionsMapper.updateById(q);
            }
        }
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
