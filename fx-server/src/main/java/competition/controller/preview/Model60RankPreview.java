package competition.controller.preview;

import competition.controller.ControlPane;
import competition.controller.match.Model7Rank;
import competition.entity.Configs;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.modal.Model61RankData;
import competition.modal.Model7RankData;
import competition.util.ConfigSpliter;
import competition.util.ExtendedScreen;
import competition.util.PreviewStack;
import competition.util.TeamRuntime;
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
public class Model60RankPreview extends PreviewPane{

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

        Configs configs = configsMapper.selectById(3);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        double oldWidth = competitionLab.getPrefWidth();
        int width = Integer.parseInt(cfgArr[10]);

        int height = Integer.parseInt(cfgArr[11]);

        int w0 = Integer.parseInt(cfgArr[0]);
        int w1 = Integer.parseInt(cfgArr[1]);
        int w2 = Integer.parseInt(cfgArr[2]);
        double sum = w0 + w1 + w2 ;
        double w0w = w0 * width / sum;
        double w1w = w1 * width / sum;
        double w2w = w2 * width / sum;
        Color fcolor = Color.valueOf(cfgArr[3]);
        Color bgcolor = Color.valueOf(cfgArr[4]);
        Color color1 = Color.valueOf(cfgArr[5]);
        Color color2 = Color.valueOf(cfgArr[6]);
        Color color3 = Color.valueOf(cfgArr[7]);

        if (ExtendedScreen.hasShow()) {
            // 进入比赛
//            Object res = ExtendedScreen.showWithControl("/view/match/Model7Rank.fxml");
//            if (res != null) {
//                controlPane.setQuestionPreview((Model7Rank) res);
//            }
        }

        outBox.setLayoutX(Integer.parseInt(cfgArr[8]));
        outBox.setLayoutY(Integer.parseInt(cfgArr[9]));
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
        List<Teams> teams = TeamRuntime.getTeams();
        List<Model61RankData> rankList = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            Teams item = teams.get(i);
            Model61RankData rankData = new Model61RankData();
            rankData.setTeam(item.getFullName());
            rankData.setScore(item.getCurrentScore());
            rankList.add(rankData);
        }
        // sort 根据分数和时间进行排序
        rankList.sort((o1, o2) -> {
            if (o1.getScore() < o2.getScore()) {
                return 1;
            } else if (o1.getScore() > o2.getScore()) {
                return -1;
            }
            return 0;
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


        title.getChildren().addAll(rankL, teamL, scoreL);
        title.setBackground(new Background(new BackgroundFill(bgcolor, CornerRadii.EMPTY, Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = 0; i < rankList.size(); i++) {
            Model61RankData rankData = rankList.get(i);
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

            itemRow.getChildren().addAll(rankD, teamD, scoreD);
            rankBox.getChildren().add(itemRow);
        }

    }

    @Override
    public void reload() {
        showData();
    }
}
