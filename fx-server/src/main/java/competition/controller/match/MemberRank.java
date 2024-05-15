package competition.controller.match;

import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.entity.RankStyle;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.RankStyleMapper;
import competition.modal.TeamMemberRankData;
import competition.util.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Scope("prototype")
public class MemberRank implements ReloadPane {

    @FXML
    private AnchorPane container;

    @FXML
    private Label competitionLab;

    @FXML
    private VBox rankBox;
    
    @Resource 
    private ConfigsMapper configsMapper;

    @Resource
    private RankStyleMapper rankStyleMapper;

    
    private static List<TeamMemberRankData> rankDataList;

    public static void setRankDataList(List<TeamMemberRankData> rankDataList) {
        MemberRank.rankDataList = rankDataList;
    }

    @FXML
    public void initialize() {
        this.showData();
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }

    private void showData() {

        RankStyle rankStyle = rankStyleMapper.selectById(3);
        String rankConfig = rankStyle.getRankConfig();
        String headConfig = rankStyle.getHeadConfig();


        Configs configs = configsMapper.selectById(7);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        int x = Integer.parseInt(cfgArr[9]);
        int y = Integer.parseInt(cfgArr[10]);
        int width = Integer.parseInt(cfgArr[11]);
        int height = Integer.parseInt(cfgArr[12]);
        int count = 8;
        int rowSpace = 10;

        String font = competitionLab.getFont().getName();
        int size = (int) competitionLab.getFont().getSize();

        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = rankConfig.split(ConfigSpliter.SPLITER);
            x = Integer.parseInt(split[0]);
            y = Integer.parseInt(split[1]);
            width = Integer.parseInt(split[2]);
            height = Integer.parseInt(split[3]);
            rowSpace = Integer.parseInt(split[6]);
            font = FontMap.getFont(split[5]);
            size = Integer.parseInt(split[4]);
            count = Integer.parseInt(split[7]);
        }

        if (!StringUtils.isEmpty(headConfig)) {
            String[] split = headConfig.split(ConfigSpliter.SPLITER);
            competitionLab.setLayoutX(Integer.parseInt(split[0]));
            competitionLab.setLayoutY(Integer.parseInt(split[1]));
            competitionLab.setPrefWidth(Integer.parseInt(split[2]));
            competitionLab.setPrefHeight(Integer.parseInt(split[3]));
            int fs = Integer.parseInt(split[5]);
            Color color = Color.valueOf(split[4]);
            String fn = split[6];
            Font f = Font.font(fn, fs);
            competitionLab.setFont(f);
            competitionLab.setTextFill(color);
        }
        
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


        rankBox.setLayoutX(x);
        rankBox.setLayoutY(y);
        rankBox.setPrefWidth(width);
        rankBox.setPrefHeight(height * count + (rowSpace) * (count - 1));
        rankBox.setSpacing(rowSpace);
        
        rankBox.getChildren().clear();
        
        

        HBox title = new HBox();
        title.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        title.setPrefWidth(rankBox.getPrefWidth());
        Label rankL = new Label("排名");
        rankL.setTextFill(fcolor);
        rankL.setAlignment(Pos.CENTER);
        rankL.setPrefHeight(height);
        rankL.setFont(Font.font(font, size));
        rankL.setPrefWidth(w0w);

        Label teamL = new Label("团队");
        teamL.setTextFill(fcolor);
        teamL.setPrefHeight(height);
        teamL.setPrefWidth(w1w);
        teamL.setAlignment(Pos.CENTER);
        teamL.setFont(Font.font(font, size));

        Label scoreL = new Label("分数");
        scoreL.setTextFill(fcolor);
        scoreL.setPrefHeight(height);
        scoreL.setPrefWidth(w2w);
        scoreL.setAlignment(Pos.CENTER);
        scoreL.setFont(Font.font(font, size));

        Label timeL = new Label("选手");
        timeL.setTextFill(fcolor);
        timeL.setPrefHeight(height);
        timeL.setPrefWidth(w3w);
        timeL.setAlignment(Pos.CENTER);
        timeL.setFont(Font.font(font, size));

        title.getChildren().addAll(rankL, teamL, timeL, scoreL);
        title.setBackground(new Background(new BackgroundFill(bgcolor, CornerRadii.EMPTY, Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = 0; i < rankDataList.size(); i++) {
            TeamMemberRankData rankData = rankDataList.get(i);
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
            Label rankD = new Label((rankData.getRank()) + "");
            rankD.setTextFill(fcolor);
            rankD.setPrefHeight(height);
            rankD.setPrefWidth(w0w);
            rankD.setAlignment(Pos.CENTER);
            rankD.setFont(Font.font(font, size));

            Label teamD = new Label(rankData.getTeam());
            teamD.setTextFill(fcolor);
            teamD.setPrefHeight(height);
            teamD.setPrefWidth(w1w);
            teamD.setAlignment(Pos.CENTER);
            teamD.setFont(Font.font(font, size));

            Label scoreD = new Label(rankData.getMember() + "");
            scoreD.setTextFill(fcolor);
            scoreD.setPrefHeight(height);
            scoreD.setPrefWidth(w2w);
            scoreD.setAlignment(Pos.CENTER);
            scoreD.setFont(Font.font(font, size));

            Label timeD = new Label(rankData.getScore() + "");
            timeD.setTextFill(fcolor);
            timeD.setPrefHeight(height);
            timeD.setPrefWidth(w3w);
            timeD.setAlignment(Pos.CENTER);
            timeD.setFont(Font.font(font, size));

            itemRow.getChildren().addAll(rankD, teamD, scoreD, timeD);
            rankBox.getChildren().add(itemRow);
        }
        
    }

    @Override
    public void reload() {
        showData();
    }
}
