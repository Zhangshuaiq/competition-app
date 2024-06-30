package competition.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.StageMemberScore;
import competition.mapper.StageMemberScoreMapper;
import competition.util.ScoreSaver;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class GradeMng {

    @Resource
    private StageMemberScoreMapper scoreMapper;

    @FXML
    private TableColumn<StageMemberScore, String> teamScoreCol;

    @FXML
    private TableColumn<StageMemberScore, String> addScoreCol;

    @FXML
    private TableColumn<StageMemberScore, String> questionCol;

    @FXML
    private TableColumn<StageMemberScore, String> stageScoreCol;

    @FXML
    private TableColumn<StageMemberScore, String> teamCol;

    @FXML
    private TableColumn<StageMemberScore, String> memberCol;

    @FXML
    private TableColumn<StageMemberScore, String> desScoreCol;

    @FXML
    private TableColumn<StageMemberScore, String> stageCol;

    @FXML
    private TableView<StageMemberScore> tableView;

    @FXML
    private TableColumn<StageMemberScore, String> timeCol;

    @FXML
    private TableColumn<StageMemberScore, String> groupCol;
    
    
    @FXML
    private void initialize(){
        //     private String team;
        //    // 环节
        //    private String stage;
        //    // 队员
        //    private String member;
        //    // 题号
        //    private String question;
        //    // 用时
        //    private String time;
        //    // 加分
        //    private String addScore;
        //    // 减分
        //    private String desScore;
        //    // 环节小计
        //    private String score;
        //    // 成绩合计
        //    private String totalScore;
        
        List<StageMemberScore> scoreList = scoreMapper.selectList(new LambdaQueryWrapper<>());
        teamCol.setCellValueFactory(new PropertyValueFactory<>("team"));
        stageCol.setCellValueFactory(new PropertyValueFactory<>("stage"));
       // groupCol.setCellValueFactory(new PropertyValueFactory<>(""));
        memberCol.setCellValueFactory(new PropertyValueFactory<>("member"));
        questionCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        addScoreCol.setCellValueFactory(new PropertyValueFactory<>("addScore"));
        desScoreCol.setCellValueFactory(new PropertyValueFactory<>("desScore"));
        stageScoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        teamScoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));

        tableView.setItems(FXCollections.observableList(scoreList));
    }


    @FXML
    public void clearData(){
       // ScoreSaver.clear();
        scoreMapper.delete(new LambdaQueryWrapper<>());
        tableView.setItems(FXCollections.emptyObservableList());
    }
    
    
}
