package competition.controller.preview;

import competition.controller.ControlPane;
import competition.controller.match.MemberRank;
import competition.entity.Configs;
import competition.entity.RankStyle;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.RankStyleMapper;
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
import java.util.*;

@Component
@Scope("prototype")
public class MemberRankPreview extends PreviewPane{

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
                    Navigator.navigatorTo("/view/match/MemberRankPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });

        Navigator.previewStage.addEventFilter(KeyEvent.KEY_PRESSED,new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != MemberRankPreview.this) {
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
                    new Image(new FileInputStream(ImageResource.选手排名背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
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

        int x = Integer.parseInt(cfgArr[9]);
        int y = Integer.parseInt(cfgArr[10]);
        int width = Integer.parseInt(cfgArr[11]);
        int height = Integer.parseInt(cfgArr[12]);
        int count = 8;
        double rowSpace = 10 ;

        String font = competitionLab.getFont().getName();
        double size = competitionLab.getFont().getSize();

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
            competitionLab.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[0])));
            competitionLab.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[1])));
            competitionLab.setPrefWidth(ScreenAdapter.calculateMainW(Integer.parseInt(split[2])));
            competitionLab.setPrefHeight(ScreenAdapter.calculateMainH(Integer.parseInt(split[3])));
            int fs = Integer.parseInt(split[5]);
            Color color = Color.valueOf(split[4]);
            String fn = split[6];
            Font f = Font.font(fn, ScreenAdapter.calculateMainW(fs));
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


        rankBox.setLayoutX(ScreenAdapter.calculateMainX(x));
        rankBox.setLayoutY(ScreenAdapter.calculateMainY(y));
        rankBox.setPrefWidth(ScreenAdapter.calculateMainW(width));
        rankBox.setPrefHeight(ScreenAdapter.calculateMainH(height * count + (rowSpace) * (count - 1)));
        rankBox.setSpacing(ScreenAdapter.calculateMainH(rowSpace));


        // 获取排名
        List<Teams> teams = TeamRuntime.getTeams();
        List<TeamMemberRankData> rankList = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            Teams item = teams.get(i);
            String members = item.getMembers();
            List<Integer> memberScores = item.getMemberScores();
            String[] mArr = members.split(",");
            for (int j = 0; j < mArr.length; j++) {
                TeamMemberRankData rankData = new TeamMemberRankData();
                rankData.setScore(0);
                if (j < memberScores.size()){
                    rankData.setScore(memberScores.get(j));
                }
                rankData.setTeam(item.getFullName());
                rankData.setMember(mArr[j]);
                rankList.add(rankData);
            }
        }
        // sort 根据分数和时间进行排序
        rankList.sort(new Comparator<TeamMemberRankData>() {
            @Override
            public int compare(TeamMemberRankData o1, TeamMemberRankData o2) {
                if (o1.getScore() < o2.getScore()) {
                    return 1;
                } else if (o1.getScore() > o2.getScore()) {
                    return -1;
                } 
                return 0;
            }
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
        rankL.setPrefHeight(ScreenAdapter.calculateMainH(height));
        rankL.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));
        rankL.setPrefWidth(ScreenAdapter.calculateMainW(w0w));

        Label teamL = new Label("团队");
        teamL.setTextFill(fcolor);
        teamL.setPrefHeight(ScreenAdapter.calculateMainH(height));
        teamL.setPrefWidth(ScreenAdapter.calculateMainW(w1w));
        teamL.setAlignment(Pos.CENTER);
        teamL.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

        Label scoreL = new Label("分数");
        scoreL.setTextFill(fcolor);
        scoreL.setPrefHeight(ScreenAdapter.calculateMainH(height));
        scoreL.setPrefWidth( ScreenAdapter.calculateMainW(w2w));
        scoreL.setAlignment(Pos.CENTER);
        scoreL.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

        Label timeL = new Label("选手");
        timeL.setTextFill(fcolor);
        timeL.setPrefHeight(ScreenAdapter.calculateMainH(height));
        timeL.setPrefWidth( ScreenAdapter.calculateMainW(w3w));
        timeL.setAlignment(Pos.CENTER);
        timeL.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

        title.getChildren().addAll(rankL, teamL, timeL, scoreL);
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
            rankD.setPrefHeight(ScreenAdapter.calculateMainH(height));
            rankD.setPrefWidth(ScreenAdapter.calculateMainW( w0w));
            rankD.setAlignment(Pos.CENTER);
            rankD.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

            Label teamD = new Label(rankData.getTeam());
            teamD.setTextFill(fcolor);
            teamD.setPrefHeight(ScreenAdapter.calculateMainH(height));
            teamD.setPrefWidth(ScreenAdapter.calculateMainW(w1w));
            teamD.setAlignment(Pos.CENTER);
            teamD.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

            Label scoreD = new Label(rankData.getMember() + "");
            scoreD.setTextFill(fcolor);
            scoreD.setPrefHeight(ScreenAdapter.calculateMainH(height));
            scoreD.setPrefWidth(ScreenAdapter.calculateMainW(w2w));
            scoreD.setAlignment(Pos.CENTER);
            scoreD.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

            Label timeD = new Label(rankData.getScore() + "");
            timeD.setTextFill(fcolor);
            timeD.setPrefHeight(ScreenAdapter.calculateMainH(height));
            timeD.setPrefWidth(ScreenAdapter.calculateMainW(w3w));
            timeD.setAlignment(Pos.CENTER);
            timeD.setFont(Font.font(font, ScreenAdapter.calculateMainW(size)));

            itemRow.getChildren().addAll(rankD, teamD, scoreD, timeD);
            rankBox.getChildren().add(itemRow);
        }

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("title", "团 队 排 行 榜");
        mapData.put("rank", rankList.subList(start, end));
        messageHandler.notifyPage("memberRank", mapData);
        // 外屏展示
        if (ExtendedScreen.hasShow()) {
            MemberRank.setRankDataList(rankList.subList(start, end));
            Object res = ExtendedScreen.showWithControl("/view/match/MemberRank.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((MemberRank) res);
            }
        }
    }

    @Override
    public void reload() {
        showData();
    }
}
