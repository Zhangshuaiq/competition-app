package competition.controller.backend;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.entity.ScoreStyle;
import competition.entity.Teams;
import competition.mapper.ScoreStyleMapper;
import competition.mapper.StageMemberScoreMapper;
import competition.mapper.TeamsMapper;
import competition.util.*;
import competition.ws.WsTeamMemberWaiter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TeamConfig {

    @Resource
    private StageMemberScoreMapper scoreMapper;
    
    @FXML
    private TextField holderPassword;
    
    @FXML
    private VBox memberBox;
    
    @FXML
    private TableView<Teams> tableView;
    
    @FXML
    private TextField codeInput, shortNameInput, fullNameInput, passwordInput;
    
    @FXML
    private TableColumn<Teams, String> codeCol, shortNameCol, fullNameCol, memberCol, passwordCol;
    
    @Resource
    private TeamsMapper teamsMapper;
    
    @FXML
    private TextField teamBaseScore;
    
    @FXML
    private TextField memberBaseScore;
    
    
    @Resource
    private ControlPane controlPane;
    
    @Resource
    private ScoreStyleMapper mapper;
    
    @FXML
    public void initialize(){


        ScoreStyle scoreStyle = mapper.selectById(2);

        holderPassword.setText(scoreStyle.getStyles());

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        shortNameCol.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        memberCol.setCellValueFactory(new PropertyValueFactory<>("members"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        showTeam();
    }
    
    @FXML
    public void updateHolderPassword(){

        ScoreStyle scoreStyle = mapper.selectById(2);

        String text = holderPassword.getText();
        scoreStyle.setStyles(text);
        mapper.updateById(scoreStyle);
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

        Label delete = new Label(" X ");
        delete.setPrefWidth(50);
        delete.setPrefHeight(30);
        delete.setAlignment(Pos.CENTER);
        delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                memberBox.getChildren().remove(size - 1);
            }
        });
        hBox.getChildren().addAll(code, textField, delete);
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

        Label delete = new Label(" X ");
        delete.setPrefWidth(50);
        delete.setPrefHeight(30);
        delete.setAlignment(Pos.CENTER);
        delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                memberBox.getChildren().remove(size - 1);
            }
        });
        
        hBox.getChildren().addAll(code, textField, delete);
        memberBox.getChildren().add(size - 1, hBox);
    }
    
    @FXML
    public void addTeamInfo(){

        Long count = teamsMapper.selectCount(new LambdaQueryWrapper<>());
        if (count >= GlobalCache.MAX_TEAM_NUMBER) {
            Information.error("团队个数不能超过 " + GlobalCache.MAX_TEAM_NUMBER);
            return;
        }

        final String codeVal = codeInput.getText();
        final String shortNameVal = shortNameInput.getText();
        final String fullNameVal = fullNameInput.getText();
        final String password = passwordInput.getText();

        List<String> members = new ArrayList<>();

        Map<String, Integer> memberMap = new HashMap<>();

        for (int i = 0; i < memberBox.getChildren().size() - 1; i++) {
            HBox box = (HBox) memberBox.getChildren().get(i);
            TextField node = (TextField) box.getChildren().get(1);
            members.add(node.getText());
            memberMap.put(node.getText(), 0);
        }
        
        final String memberVal = Strings.join(members, ',');
        final String memberScore = JSON.toJSONString(memberMap);

        Teams teams = new Teams();
        teams.setCode(codeVal);
        teams.setShortName(shortNameVal);
        teams.setFullName(fullNameVal);
        teams.setMembers(memberVal);
        teams.setPassword(password);
        teams.setMemberScore(memberScore);
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
        passwordInput.setText(selectedItem.getPassword());
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
        final String password = passwordInput.getText();

        List<String> members = new ArrayList<>();

        JSONObject jsonObject = JSON.parseObject(selectedItem.getMemberScore());

        for (int i = 0; i < memberBox.getChildren().size() - 1; i++) {
            HBox box = (HBox) memberBox.getChildren().get(i);
            TextField node = (TextField) box.getChildren().get(1);
            if (!jsonObject.containsKey(node.getText())) {
                jsonObject.put(node.getText(), 0);
            }
            members.add(node.getText());
        }

        final String memberVal = Strings.join(members, ',');
        final String memberScore = JSON.toJSONString(jsonObject);

        Teams teams = new Teams();
        teams.setId(selectedItem.getId());
        teams.setCode(codeVal);
        teams.setShortName(shortNameVal);
        teams.setFullName(fullNameVal);
        teams.setMembers(memberVal);
        teams.setPassword(password);
        teams.setMemberScore(memberScore);
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
        ObservableList<Teams> items = tableView.getItems();
        TeamRuntime.setTeams(new ArrayList<>(items));
    }
    
    @FXML
    public void importTeam(){
        //EasyExcel.
        //file
        // 创建FileChooser实例
        FileChooser fileChooser = new FileChooser();

        // 设置文件选择器的标题
        fileChooser.setTitle("Select Excel File");

        // 设置初始目录(可选)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // 添加文件过滤器以只显示Excel文件
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Excel files (*.xls, *.xlsx)", "*.xls", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            List<Teams> teamsList = ExcelParser.parse(selectedFile.getAbsolutePath(), Teams.class);
            
            // 删除旧的内容
            teamsMapper.delete(new LambdaQueryWrapper<>());
            for (Teams teams : teamsList) {
                Long count = teamsMapper.selectCount(new LambdaQueryWrapper<>());
                if (count >= GlobalCache.MAX_TEAM_NUMBER) {
                    Information.error("团队个数不能超过 " + GlobalCache.MAX_TEAM_NUMBER);
                    return;
                }
                String members = teams.getMembers();
                if (StringUtils.isEmpty(members)) {
                    Information.error("团队至少有一个成员 " + GlobalCache.MAX_TEAM_NUMBER);
                    return;
                }

                String[] memberArr = members.split(",");
                Map<String, Integer> memberScore = new HashMap<>();
                for (String s : memberArr) {
                    memberScore.put(s, 0);
                }
                final String memberScores = JSON.toJSONString(memberScore);
                teams.setMemberScore(memberScores);
                teams.setTeamScore(0);
                teamsMapper.insert(teams);
            }
            TeamRuntime.setTeams(teamsList);
        }

        // 显示最新的队伍信息
        showTeam();
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
       // ScoreSaver.clear();

        // 排序队伍，按照编码排序
        teams.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));

        for (Teams team : teams) {
            team.setQScores(new ArrayList<>());
            team.setStatusList(new ArrayList<>());
            team.setStatus(0);
            if (!StringUtils.isEmpty(team.getMembers())) {
                String[] split = team.getMembers().split(",");
                for (String s : split) {
                    team.getStatusList().add(0);
                    team.getQScores().add(0);
                }
            }
        }
        TeamRuntime.setTeams(teams);
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.reload();
        }

        scoreMapper.delete(new LambdaQueryWrapper<>());
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
            String members = team.getMembers();
            String[] memArr = members.split(",");
            Map<String, Integer> memberMap = new HashMap<>();
            for (String m : memArr) {
                memberMap.put(m, Integer.parseInt(text));
            }
            team.setMemberScore(JSON.toJSONString(memberMap));
            teamsMapper.updateById(team);
        }

        teams.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
        
        for (Teams team : teams) {
            team.setQScores(new ArrayList<>());
            team.setStatusList(new ArrayList<>());
            team.setStatus(0);
            if (!StringUtils.isEmpty(team.getMembers())) {
                String[] split = team.getMembers().split(",");
                for (String s : split) {
                    team.getStatusList().add(0);
                    team.getQScores().add(0);
                }
            }
        }
        TeamRuntime.setTeams(teams);
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.reload();
        }
    }
    
    
    private void showTeam(){
        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        teams = teams.stream().sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getCode()))).collect(Collectors.toList());
        tableView.setItems(FXCollections.observableList(teams));
        TeamRuntime.setTeams(teams);
        controlPane.reloadScoreBoard();
    }
}
