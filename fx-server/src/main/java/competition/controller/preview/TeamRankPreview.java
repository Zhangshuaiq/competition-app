package competition.controller.preview;

import competition.controller.ControlPane;
import competition.controller.match.TeamRank;
import competition.entity.Configs;
import competition.entity.RankStyle;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.RankStyleMapper;
import competition.modal.Model7RankData;
import competition.modal.TeamMemberRankData;
import competition.util.*;
import competition.ws.WsMessageHandler;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class TeamRankPreview extends PreviewPane{

    @FXML
    private AnchorPane container;

    @FXML
    private Label competitionLab;

    @FXML
    private VBox rankBox;

    @Resource
    private ControlPane controlPane;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private RankStyleMapper rankStyleMapper;
    
    @Resource
    private WsMessageHandler messageHandler;

    private int page = 0;
    

    @FXML
    public void initialize() {

        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    // 显示属性配置弹出
                    Navigator.navigatorTo("/view/match/TeamRankPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });


        Navigator.previewStage.addEventFilter(KeyEvent.KEY_PRESSED,new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != TeamRankPreview.this) {
                    return;
                }
                // 上
                if (event.getCode() == KeyCode.UP) {
                    if (page > 0) {
                        page --;
                    }
                    showData();
                } else if (event.getCode() == KeyCode.DOWN) {
                    // 下
                    page ++;
                    showData();
                }
            }
        });
        
        this.showData();
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }

    private void showData() {
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.团队排名背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }
        RankStyle rankStyle = rankStyleMapper.selectById(2);
        String rankConfig = rankStyle.getRankConfig();
        String headConfig = rankStyle.getHeadConfig();

        Configs configs = configsMapper.selectById(3);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);
        

        double x = ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[8]));
        double y = ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[9]));
        double width = ScreenAdapter.calculateMainW(Integer.parseInt(cfgArr[10]));
        double height = ScreenAdapter.calculateMainH(Integer.parseInt(cfgArr[11]));

        int count = 8;
        double rowSpace = ScreenAdapter.calculateMainH(10);
        
        String font = competitionLab.getFont().getName();
        double size = ScreenAdapter.calculateMainW(competitionLab.getFont().getSize());

        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = rankConfig.split(ConfigSpliter.SPLITER);
            x = ScreenAdapter.calculateMainX(Integer.parseInt(split[0]));
            y = ScreenAdapter.calculateMainY(Integer.parseInt(split[1]));
            width = ScreenAdapter.calculateMainW(Integer.parseInt(split[2]));
            height = ScreenAdapter.calculateMainH(Integer.parseInt(split[3]));
            rowSpace = Integer.parseInt(split[6]);
            font = FontMap.getFont(split[5]);
            size = ScreenAdapter.calculateMainW(Integer.parseInt(split[4]));
            count = Integer.parseInt(split[7]);
        }

        if (!StringUtils.isEmpty(headConfig)) {
            String[] split = headConfig.split(ConfigSpliter.SPLITER);
            competitionLab.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[0])));
            competitionLab.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[1])));
            competitionLab.setPrefWidth(ScreenAdapter.calculateMainW(Integer.parseInt(split[2])));
            competitionLab.setPrefHeight(ScreenAdapter.calculateMainH(Integer.parseInt(split[3])));
            double fs = ScreenAdapter.calculateMainW(Integer.parseInt(split[5]));
            Color color = Color.valueOf(split[4]);
            String fn = split[6];
            Font f = Font.font(fn, fs);
            competitionLab.setFont(f);
            competitionLab.setTextFill(color);
        }
        
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


        rankBox.setLayoutX(x);
        rankBox.setLayoutY(y);
        rankBox.setPrefWidth(width);
        rankBox.setPrefHeight(height * count + (rowSpace) * (count - 1));
        rankBox.setSpacing(rowSpace);
        

        // 获取排名
        List<Teams> teams = TeamRuntime.getTeams();
        List<TeamMemberRankData> rankList = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            Teams item = teams.get(i);
            TeamMemberRankData rankData = new TeamMemberRankData();
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


        int rank = 1;
        TeamMemberRankData first = rankList.get(0);
        for (TeamMemberRankData rankData : rankList) {
            if (first.compareTo(rankData)> 0) {
                rank ++;
            }
            rankData.setRank(rank);
            first = rankData;
        }

        // 分页
        int end = (page + 1) * count;
        int start = page * count;
        if (end > rankList.size() ) {
            end = rankList.size();
        }

        int totalPage = rankList.size() / count;
        int left = rankList.size() % count;
        if (left == 0) {
            totalPage = totalPage - 1;
        }

        if (page > totalPage) {
            page = totalPage;
            return;
        }

        if (start < 0) {
            start = 0;
        }
        
        rankBox.getChildren().clear();



        HBox title = new HBox();
        title.setBackground(new Background(new BackgroundFill(Color.RED,  new CornerRadii(10), Insets.EMPTY)));
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


        title.getChildren().addAll(rankL, teamL, scoreL);
        title.setBackground(new Background(new BackgroundFill(bgcolor,  new CornerRadii(10), Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = start; i < end; i++) {
            TeamMemberRankData rankData = rankList.get(i);
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

            Label scoreD = new Label(rankData.getScore() + "");
            scoreD.setTextFill(fcolor);
            scoreD.setPrefHeight(height);
            scoreD.setPrefWidth(w2w);
            scoreD.setAlignment(Pos.CENTER);
            scoreD.setFont(Font.font(font, size));

            itemRow.getChildren().addAll(rankD, teamD, scoreD);
            rankBox.getChildren().add(itemRow);
        }
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("title", "团 队 排 行 榜");
        mapData.put("rank", rankList.subList(start, end));
        messageHandler.notifyPage("teamRank", mapData);
        if (ExtendedScreen.hasShow()) {
            TeamRank.setRankList(rankList.subList(start, end));
            Object res = ExtendedScreen.showWithControl("/view/match/TeamRank.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((TeamRank) res);
            }
        }
    }

    @Override
    public void reload() {
        showData();
    }
}
