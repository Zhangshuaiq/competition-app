package competition.controller.preview;

import com.sun.org.apache.xpath.internal.operations.Mod;
import competition.controller.ControlPane;
import competition.controller.match.Model7Rank;
import competition.controller.match.QuestionPreview;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.modal.Model7RankData;
import competition.util.ConfigSpliter;
import competition.util.ExtendedScreen;
import competition.util.PreviewStack;
import competition.ws.Model7RuntimeData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Scope("prototype")
public class Model7RankPreview extends PreviewPane{

    @FXML
    private AnchorPane container;

    @FXML
    private Label competitionLab;

    @FXML
    private VBox rankBox, outBox;
    
    @Resource
    private ControlPane controlPane;
    
    @Resource 
    private ConfigsMapper configsMapper;


    @FXML
    public void initialize() {
        PreviewStack.addPreview(this);
        this.showData();
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }

    private void showData() {

        Configs configs = configsMapper.selectById(8);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        double oldWidth = competitionLab.getPrefWidth();
        int width = Integer.parseInt(cfgArr[11]);
        
        int height = Integer.parseInt(cfgArr[12]);
        
        int w0 = Integer.parseInt(cfgArr[0]);
        int w1 = Integer.parseInt(cfgArr[1]);
        int w2 = Integer.parseInt(cfgArr[2]);
        int w3 = Integer.parseInt(cfgArr[3]);
        double sum = w0 + w1 + w2 + w3;
        double w0w = w0 * width / sum;
        double w1w = w1 * width / sum;
        double w2w = w2 * width / sum;
        double w3w = w3 * width / sum;
        Color fcolor = Color.valueOf(cfgArr[4]);
        Color bgcolor = Color.valueOf(cfgArr[5]);
        Color color1 = Color.valueOf(cfgArr[6]);
        Color color2 = Color.valueOf(cfgArr[7]);
        Color color3 = Color.valueOf(cfgArr[8]);

        if (ExtendedScreen.hasShow()) {
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/Model7Rank.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((Model7Rank) res);
            }
        }

        outBox.setLayoutX(Integer.parseInt(cfgArr[9]));
        outBox.setLayoutY(Integer.parseInt(cfgArr[10]));
        outBox.setPrefWidth(width);

        Font font = competitionLab.getFont();
        competitionLab.setPrefWidth(width);
        rankBox.setPrefWidth(width);
        rankBox.setSpacing(30);
        double ratio = width / oldWidth;
        competitionLab.setPrefHeight(competitionLab.getPrefHeight() * ratio);
        // 动态调整字体的比例
        competitionLab.setFont(Font.font(font.getSize() * ratio * 0.5));

        // 获取排名
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<String> timeData = Model7RuntimeData.getTimes();
        List<Integer> seconds = Model7RuntimeData.getSeconds();
        List<Model7RankData> rankList = new ArrayList<>();
        for (int i = 0; i < runtimeData.size(); i++) {
            List<Model7RuntimeData.ModelData> questionList = runtimeData.get(i);
            int score = 0;
            String team = null;
            for (Model7RuntimeData.ModelData data : questionList) {
                if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                    score += data.getScore();
                }
                team = data.getTeamName();
            }
            String time = timeData.get(i);

            Model7RankData rankData = new Model7RankData();
            rankData.setScore(score);
            rankData.setSecond(seconds.get(i));
            rankData.setTeam(team);
            rankData.setTime(time);
            rankList.add(rankData);
        }
        // sort 根据分数和时间进行排序
        rankList.sort(new Comparator<Model7RankData>() {
            @Override
            public int compare(Model7RankData o1, Model7RankData o2) {
                if (o1.getScore() < o2.getScore()) {
                    return 1;
                } else if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else {
                    return o1.getSecond().compareTo(o2.getSecond());
                }
            }
        });

        rankBox.getChildren().clear();
        

        HBox title = new HBox();
        title.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        title.setPrefWidth(rankBox.getPrefWidth());
        Label rankL = new Label("排名");
        rankL.setTextFill(fcolor);
        rankL.setAlignment(Pos.CENTER);
        rankL.setPrefHeight(height);
        rankL.setFont(Font.font(48));
        rankL.setPrefWidth(w0w);

        Label teamL = new Label("团队");
        teamL.setTextFill(fcolor);
        teamL.setPrefHeight(height);
        teamL.setPrefWidth(w1w);
        teamL.setAlignment(Pos.CENTER);
        teamL.setFont(Font.font(48));

        Label scoreL = new Label("分数");
        scoreL.setTextFill(fcolor);
        scoreL.setPrefHeight(height);
        scoreL.setPrefWidth(w2w);
        scoreL.setAlignment(Pos.CENTER);
        scoreL.setFont(Font.font(48));

        Label timeL = new Label("时间");
        timeL.setTextFill(fcolor);
        timeL.setPrefHeight(height);
        timeL.setPrefWidth(w3w);
        timeL.setAlignment(Pos.CENTER);
        timeL.setFont(Font.font(48));

        title.getChildren().addAll(rankL, teamL, scoreL, timeL);
        title.setBackground(new Background(new BackgroundFill(bgcolor, CornerRadii.EMPTY, Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = 0; i < rankList.size(); i++) {
            Model7RankData rankData = rankList.get(i);
            HBox itemRow = new HBox();
            // 随机背景色
            Color c = bgcolor;
            if (i == 0) {
                c = color1;
            } else  if (i == 1) {
                c = color2;
            } else  if (i == 2) {
                c = color3;
            }
            itemRow.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
            itemRow.setPrefWidth(rankBox.getPrefWidth());
            Label rankD = new Label((i + 1) + "");
            rankD.setTextFill(fcolor);
            rankD.setPrefHeight(height);
            rankD.setPrefWidth(w0w);
            rankD.setAlignment(Pos.CENTER);
            rankD.setFont(Font.font(48));

            Label teamD = new Label(rankData.getTeam());
            teamD.setTextFill(fcolor);
            teamD.setPrefHeight(height);
            teamD.setPrefWidth(w1w);
            teamD.setAlignment(Pos.CENTER);
            teamD.setFont(Font.font(48));

            Label scoreD = new Label(rankData.getScore() + "");
            scoreD.setTextFill(fcolor);
            scoreD.setPrefHeight(height);
            scoreD.setPrefWidth(w2w);
            scoreD.setAlignment(Pos.CENTER);
            scoreD.setFont(Font.font(48));

            Label timeD = new Label(rankData.getTime());
            timeD.setTextFill(fcolor);
            timeD.setPrefHeight(height);
            timeD.setPrefWidth(w3w);
            timeD.setAlignment(Pos.CENTER);
            timeD.setFont(Font.font(48));

            itemRow.getChildren().addAll(rankD, teamD, scoreD, timeD);
            rankBox.getChildren().add(itemRow);
        }
        
    }

    @Override
    public void reload() {
        showData();
    }
}
