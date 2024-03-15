package competition.controller.match;

import competition.modal.Model7RankData;
import competition.util.ExtendedScreen;
import competition.util.PreviewStack;
import competition.ws.Model7RuntimeData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Scope("prototype")
public class Model7Rank implements ReloadPane{

    @FXML
    private AnchorPane container;

    @FXML
    private Label competitionLab;

    @FXML
    private VBox rankBox, outBox;


    @Override
    public Node getPane() {
        return container;
    }

    @FXML
    public void initialize() {
        this.showData();
    }


    private void showData() {
        double oldWidth = competitionLab.getPrefWidth();
        container.setPrefWidth(ExtendedScreen.getWidth());
        container.setPrefHeight(ExtendedScreen.getHeight());

        Font font = competitionLab.getFont();
        competitionLab.setPrefWidth(ExtendedScreen.getWidth());
        outBox.setPrefWidth(ExtendedScreen.getWidth());
        rankBox.setPrefWidth(ExtendedScreen.getWidth());
        rankBox.setSpacing(30);
        rankBox.setPadding(new Insets(30, 100, 30, 100));
        double ratio = ExtendedScreen.getWidth() / oldWidth;
        // 动态调整字体的比例
        competitionLab.setFont(Font.font(font.getSize() * ratio * 0.5));

        // 获取排名
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
        List<String> timeData = Model7RuntimeData.getTimes();
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
            rankData.setTeam(team);
            rankData.setTime(time);
            rankList.add(rankData);
        }
        // sort 根据分数和时间进行排序
        rankList.sort(new Comparator<Model7RankData>() {
            @Override
            public int compare(Model7RankData o1, Model7RankData o2) {
                if (o1.getScore() < o2.getScore()) {
                    return -1;
                } else if (o1.getScore() > o2.getScore()) {
                    return 1;
                } else {
                    String t1 = o1.getTime().replace("分", "").replace("秒", "");
                    String t2 = o2.getTime().replace("分", "").replace("秒", "");
                    return Integer.parseInt(t1) - Integer.parseInt(t2);
                }
            }
        });

        rankBox.getChildren().clear();
        
        double colWidth = (ExtendedScreen.getWidth() - 200) / 4;
        

        HBox title = new HBox();
        title.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        title.setPrefWidth(rankBox.getPrefWidth());
        Label rankL = new Label("排名");
        rankL.setTextFill(Color.WHITE);
        rankL.setAlignment(Pos.CENTER);
        rankL.setPrefHeight(60);
        rankL.setFont(Font.font(48));
        rankL.setPrefWidth(colWidth);

        Label teamL = new Label("团队");
        teamL.setTextFill(Color.WHITE);
        teamL.setPrefHeight(60);
        teamL.setPrefWidth(colWidth);
        teamL.setAlignment(Pos.CENTER);
        teamL.setFont(Font.font(48));

        Label scoreL = new Label("分数");
        scoreL.setTextFill(Color.WHITE);
        scoreL.setPrefHeight(60);
        scoreL.setPrefWidth(colWidth);
        scoreL.setAlignment(Pos.CENTER);
        scoreL.setFont(Font.font(48));

        Label timeL = new Label("时间");
        timeL.setTextFill(Color.WHITE);
        timeL.setPrefHeight(60);
        timeL.setPrefWidth(colWidth);
        timeL.setAlignment(Pos.CENTER);
        timeL.setFont(Font.font(48));

        title.getChildren().addAll(rankL, teamL, scoreL, timeL);
        rankBox.getChildren().add(title);

        for (int i = 0; i < rankList.size(); i++) {
            Model7RankData rankData = rankList.get(i);
            HBox itemRow = new HBox();
            // 随机背景色
            itemRow.setBackground(new Background(new BackgroundFill(Color.color(Math.random(), Math.random(), Math.random()), CornerRadii.EMPTY, Insets.EMPTY)));
            itemRow.setPrefWidth(rankBox.getPrefWidth());
            Label rankD = new Label((i + 1) + "");
            rankD.setTextFill(Color.WHITE);
            rankD.setPrefHeight(60);
            rankD.setPrefWidth(colWidth);
            rankD.setAlignment(Pos.CENTER);
            rankD.setFont(Font.font(48));

            Label teamD = new Label(rankData.getTeam());
            teamD.setTextFill(Color.WHITE);
            teamD.setPrefHeight(60);
            teamD.setPrefWidth(colWidth);
            teamD.setAlignment(Pos.CENTER);
            teamD.setFont(Font.font(48));

            Label scoreD = new Label(rankData.getScore() + "");
            scoreD.setTextFill(Color.WHITE);
            scoreD.setPrefHeight(60);
            scoreD.setPrefWidth(colWidth);
            scoreD.setAlignment(Pos.CENTER);
            scoreD.setFont(Font.font(48));

            Label timeD = new Label(rankData.getTime());
            timeD.setTextFill(Color.WHITE);
            timeD.setPrefHeight(60);
            timeD.setPrefWidth(colWidth);
            timeD.setAlignment(Pos.CENTER);
            timeD.setFont(Font.font(48));

            itemRow.getChildren().addAll(rankD, teamD, scoreD, timeD);
            rankBox.getChildren().add(itemRow);
        }
        
    }
}
