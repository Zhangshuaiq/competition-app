package competition.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import competition.util.Information;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamConfig {
    
    
    @FXML
    private VBox memberBox;
    
    @FXML
    private TableView<Teams> tableView;
    
    @FXML
    private TextField codeInput, shortNameInput, fullNameInput;
    
    @FXML
    private TableColumn<Teams, String> codeCol, shortNameCol, fullNameCol, memberCol;
    
    @Resource
    private TeamsMapper teamsMapper;
    
    @FXML
    private TextField teamBaseScore;
    
    @FXML
    private TextField memberBaseScore;
    
    
    @FXML
    public void initialize(){
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        shortNameCol.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        memberCol.setCellValueFactory(new PropertyValueFactory<>("members"));
        showTeam();
    }
    
    @FXML
    public void addMember(){
        // prefHeight="38.0" prefWidth="38.0"
        final int size = memberBox.getChildren().size();
        Label code = new Label(size + "");
        code.setPrefWidth(38);
        code.setPrefHeight(38);
        code.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        final TextField textField = new TextField();
        textField.setPrefWidth(200);
        hBox.getChildren().addAll(code, textField);
        memberBox.getChildren().add(size - 1, hBox);
    }
    
    
    private void showMember(String member) {
        final int size = memberBox.getChildren().size();
        Label code = new Label(size + "");
        code.setPrefWidth(38);
        code.setPrefHeight(38);
        code.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        final TextField textField = new TextField(member);
        textField.setPrefWidth(200);
        hBox.getChildren().addAll(code, textField);
        memberBox.getChildren().add(size - 1, hBox);
    }
    
    @FXML
    public void addTeamInfo(){
        final String codeVal = codeInput.getText();
        final String shortNameVal = shortNameInput.getText();
        final String fullNameVal = fullNameInput.getText();

        List<String> members = new ArrayList<>();

        for (int i = 0; i < memberBox.getChildren().size() - 1; i++) {
            HBox box = (HBox) memberBox.getChildren().get(i);
            TextField node = (TextField) box.getChildren().get(1);
            members.add(node.getText());
        }
        
        final String memberVal = Strings.join(members, ',');

        Teams teams = new Teams();
        teams.setCode(codeVal);
        teams.setShortName(shortNameVal);
        teams.setFullName(fullNameVal);
        teams.setMembers(memberVal);
        
        teamsMapper.insert(teams);
        
        showTeam();
    }
    
    @FXML
    public void clickTable(){
        final Teams selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        codeInput.setText(selectedItem.getCode());
        shortNameInput.setText(selectedItem.getShortName());
        fullNameInput.setText(selectedItem.getFullName());
        final String[] split = selectedItem.getMembers().split(",");
        memberBox.getChildren().removeIf(x -> x instanceof HBox);
        for (String m : split) {
            if (!StringUtils.isEmpty(m)) {
                showMember(m);
            }
        }

    }
    
    @FXML
    public void updateTeamInfo(){
        final Teams selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        final String codeVal = codeInput.getText();
        final String shortNameVal = shortNameInput.getText();
        final String fullNameVal = fullNameInput.getText();

        List<String> members = new ArrayList<>();

        for (int i = 0; i < memberBox.getChildren().size() - 1; i++) {
            HBox box = (HBox) memberBox.getChildren().get(i);
            TextField node = (TextField) box.getChildren().get(1);
            members.add(node.getText());
        }

        final String memberVal = Strings.join(members, ',');

        Teams teams = new Teams();
        teams.setId(selectedItem.getId());
        teams.setCode(codeVal);
        teams.setShortName(shortNameVal);
        teams.setFullName(fullNameVal);
        teams.setMembers(memberVal);
        teamsMapper.updateById(teams);
        showTeam();
    }
    
    @FXML
    public void deleteTeamInfo(){
        final Teams selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        teamsMapper.deleteById(selectedItem.getId());
        showTeam();
        tableView.getSelectionModel().select(0);
    }
    
    @FXML
    public void importTeam(){
        
    }
    
    
    @FXML
    public void resetTeamScore(){
        String text = teamBaseScore.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请完善团队基础分！！");
            return;
        }

        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        for (Teams team : teams) {
            team.setTeamScore(Integer.parseInt(text));
            teamsMapper.updateById(team);
        }
    }
    
    
    @FXML
    public void resetMemberScore(){
        String text = memberBaseScore.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请完善个人基础分！！");
            return;
        }
        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        for (Teams team : teams) {
            team.setMemberScore(Integer.parseInt(text));
            teamsMapper.updateById(team);
        }
    }
    
    
    private void showTeam(){
        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        teams = teams.stream().sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getCode()))).collect(Collectors.toList());
        tableView.setItems(FXCollections.observableList(teams));
    }
}
