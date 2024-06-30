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
import javafx.scene.image.Image;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.选手排名背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        RankStyle rankStyle = rankStyleMapper.selectById(3);
        String rankConfig = rankStyle.getRankConfig();
        String headConfig = rankStyle.getHeadConfig();


        Configs configs = configsMapper.selectById(7);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        double x = ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[9]));
        double y = ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[10]));
        double width = ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[11]));
        double height = ScreenAdapter.calculateExH(Integer.parseInt(cfgArr[12]));
        int count = 8;
        double rowSpace = ScreenAdapter.calculateExH(10);

        String font = competitionLab.getFont().getName();
        double size = ScreenAdapter.calculateExW(competitionLab.getFont().getSize());

        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = rankConfig.split(ConfigSpliter.SPLITER);
            x = ScreenAdapter.calculateExX(Integer.parseInt(split[0]));
            y = ScreenAdapter.calculateExY(Integer.parseInt(split[1]));
            width = ScreenAdapter.calculateExW(Integer.parseInt(split[2]));
            height = ScreenAdapter.calculateExH(Integer.parseInt(split[3]));
            rowSpace = ScreenAdapter.calculateExH(Integer.parseInt(split[6]));
            font = FontMap.getFont(split[5]);
            size = ScreenAdapter.calculateExW(Integer.parseInt(split[4]));
            count = Integer.parseInt(split[7]);
        }

        if (!StringUtils.isEmpty(headConfig)) {
            String[] split = headConfig.split(ConfigSpliter.SPLITER);
            competitionLab.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(split[0])));
            competitionLab.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(split[1])));
            competitionLab.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(split[2])));
            competitionLab.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(split[3])));
            double fs = ScreenAdapter.calculateExW(Integer.parseInt(split[5]));
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
        title.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
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
        title.setBackground(new Background(new BackgroundFill(bgcolor,  new CornerRadii(10), Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = 0; i < rankDataList.size(); i++) {
            TeamMemberRankData rankData = rankDataList.get(i);
            HBox itemRow = new HBox();
            // 随机背景色
            Color c = bgcolor;
            if (rankData.getRank() == 1) {
                c = color1;
            } else  if (rankData.getRank() == 2) {
                c = color2;
            } else  if (rankData.getRank() == 3) {
                c = color3;
            }
            itemRow.setBackground(new Background(new BackgroundFill(c,  new CornerRadii(10), Insets.EMPTY)));
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
