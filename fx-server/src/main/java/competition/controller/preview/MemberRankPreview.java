package competition.controller.preview;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class MemberRankPreview extends RankPane {

    @FXML
    private AnchorPane container;

    @FXML
    private StackPane titleBox;

    @FXML
    private Text competitionLab;

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
    public void initPage() {
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    // 显示属性配置弹出
                    Navigator.navigatorTo("/view/match/MemberRankPreviewAttr.fxml", "", "/view/style.css", false,
                            false);
                }
            }
        });

        Navigator.previewStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != MemberRankPreview.this) {
                    return;
                }
                // 上
                if (event.getCode() == KeyCode.UP) {
                    if (page > 0) {
                        page--;
                    }
                    showData();
                } else if (event.getCode() == KeyCode.DOWN) {
                    // 下
                    page++;
                    showData();
                }
            }
        });

        ExtendedScreen.extendScreen.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != MemberRankPreview.this) {
                    return;
                }
                // 上
                if (event.getCode() == KeyCode.UP) {
                    if (page > 0) {
                        page--;
                    }
                    showData();
                } else if (event.getCode() == KeyCode.DOWN) {
                    // 下
                    page++;
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
                    new Image(new FileInputStream(ImageResource.选手排名背景())), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false))));
        } catch (FileNotFoundException e) {
            container.setBackground(
                    new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        RankStyle rankStyle = rankStyleMapper.selectById(3);
        String rankConfig = rankStyle.getRankConfig();
        String headConfig = rankStyle.getHeadConfig();

        Configs configs = configsMapper.selectById(7);
        String config = configs.getConfig();
        String[] cfgArr = ConfigSpliter.split(config);

        int x = Integer.parseInt(cfgArr[9]);
        int y = Integer.parseInt(cfgArr[10]);
        int width = Integer.parseInt(cfgArr[11]);
        int height = Integer.parseInt(cfgArr[12]);
        int count = 8;
        double rowSpace = 10;

        double size = 12;
        Font font = FontMap.load(FontMap.defaultFontName(), 12);
        boolean rankBold = false;
        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = ConfigSpliter.split(rankConfig);
            x = Integer.parseInt(split[0]);
            y = Integer.parseInt(split[1]);
            width = Integer.parseInt(split[2]);
            height = Integer.parseInt(split[3]);
            rowSpace = Integer.parseInt(split[6]);
            size = Integer.parseInt(split[4]);
            font = FontMap.load(split[5], ScreenAdapter.calculateMainX(size));
            count = Integer.parseInt(split[7]);
            if (split.length >= 9) {
                rankBold = Boolean.parseBoolean(split[8]);
            }
        }

        if (!StringUtils.isEmpty(headConfig)) {
            String[] split = ConfigSpliter.split(headConfig);
            titleBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[0])));
            titleBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[1])));
            titleBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[2])));
            titleBox.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
            // competitionLab.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[0])));
            // competitionLab.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[1])));
            // competitionLab.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[2])));
            // competitionLab.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
            int fs = Integer.parseInt(split[5]);
            Color color = Color.valueOf(split[4]);
            String fn = split[6];
            Font f = FontMap.load(fn, ScreenAdapter.calculateMainX(fs));
            competitionLab.setFont(f);
            competitionLab.setFill(color);
            boolean headBold = false;
            if (split.length >= 8) {
                headBold = Boolean.parseBoolean(split[7]);
            }
            if (headBold) {
                competitionLab.setStroke(competitionLab.getFill());
                competitionLab.setStrokeWidth(1);
            } else {
                competitionLab.setStroke(null);
            }
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
        rankBox.setPrefWidth(ScreenAdapter.calculateMainX(width));
        rankBox.setPrefHeight(ScreenAdapter.calculateMainY(height * count + (rowSpace) * (count - 1)));
        rankBox.setSpacing(ScreenAdapter.calculateMainY(rowSpace));

        // 获取排名
        List<Teams> teams = TeamRuntime.getTeams();
        List<TeamMemberRankData> rankList = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            Teams item = teams.get(i);
            String members = item.getMembers();
            String memberScore = item.getMemberScore();
            JSONObject jsonObject = JSON.parseObject(memberScore);
            String[] mArr = TeamMemberCodec.split(members);
            for (int j = 0; j < mArr.length; j++) {
                TeamMemberRankData rankData = new TeamMemberRankData();
                rankData.setScore(Long.valueOf(jsonObject.getInteger(mArr[j])));
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
            if (first.compareTo(rankData) > 0) {
                rank++;
            }
            rankData.setRank(rank);
            first = rankData;
        }

        // 分页
        int end = (page + 1) * count;
        int start = page * count;
        if (end > rankList.size()) {
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

        String path = TextResource.MEMBER_TITLE;
        List<String> lines = FileUtil.readLines(path, StandardCharsets.UTF_8);

        HBox title = new HBox();
        title.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
        title.setPrefWidth(rankBox.getPrefWidth());

        if (lines.size() > 0) {
            competitionLab.setText(lines.get(0));
        }

        StackPane rankPane = new StackPane();
        Text rankL = new Text("排名");
        if (lines.size() > 1) {
            rankL.setText(lines.get(1));
        }
        rankL.setFill(fcolor);
        rankL.setTextAlignment(TextAlignment.CENTER);
        rankL.prefHeight(ScreenAdapter.calculateMainY(height));
        rankL.setFont(font);
        rankL.prefWidth(ScreenAdapter.calculateMainX(w0w));
        rankPane.setPrefWidth(ScreenAdapter.calculateMainX(w0w));
        rankPane.setPrefHeight(ScreenAdapter.calculateMainY(height));
        if (rankBold) {
            rankL.setStroke(rankL.getFill());
            rankL.setStrokeWidth(1);
        }
        rankPane.getChildren().add(rankL);

        StackPane teamPane = new StackPane();
        Text teamL = new Text("团队");
        if (lines.size() > 2) {
            teamL.setText(lines.get(2));
        }
        teamL.setFill(fcolor);
        teamL.setTextAlignment(TextAlignment.CENTER);
        teamL.setFont(font);

        teamPane.setPrefHeight(ScreenAdapter.calculateMainY(height));
        teamPane.setPrefWidth(ScreenAdapter.calculateMainX(w1w));
        if (rankBold) {
            teamL.setStroke(teamL.getFill());
            teamL.setStrokeWidth(1);
        }
        teamPane.getChildren().add(teamL);

        StackPane scorePane = new StackPane();
        Text scoreL = new Text("分数");
        if (lines.size() > 4) {
            scoreL.setText(lines.get(4));
        }
        scoreL.setFill(fcolor);
        scorePane.setPrefHeight(ScreenAdapter.calculateMainY(height));
        scorePane.setPrefWidth(ScreenAdapter.calculateMainX(w2w));
        scoreL.setTextAlignment(TextAlignment.CENTER);
        scoreL.setFont(font);
        if (rankBold) {
            scoreL.setStroke(scoreL.getFill());
            scoreL.setStrokeWidth(1);
        }
        scorePane.getChildren().add(scoreL);

        StackPane timePane = new StackPane();
        Text timeL = new Text("选手");
        if (lines.size() > 3) {
            timeL.setText(lines.get(3));
        }
        timeL.setFill(fcolor);
        timePane.setPrefHeight(ScreenAdapter.calculateMainY(height));
        timePane.setPrefWidth(ScreenAdapter.calculateMainX(w3w));
        timeL.setTextAlignment(TextAlignment.CENTER);
        timeL.setFont(font);
        if (rankBold) {
            timeL.setStroke(timeL.getFill());
            timeL.setStrokeWidth(1);
        }
        timePane.getChildren().add(timeL);

        title.getChildren().addAll(rankPane, teamPane, timePane, scorePane);
        title.setBackground(new Background(new BackgroundFill(bgcolor, new CornerRadii(10), Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = start; i < end; i++) {
            TeamMemberRankData rankData = rankList.get(i);
            HBox itemRow = new HBox();
            // 随机背景色
            Color c = bgcolor;
            if (rankData.getRank() == 1) {
                c = color1;
            } else if (rankData.getRank() == 2) {
                c = color2;
            } else if (rankData.getRank() == 3) {
                c = color3;
            }
            itemRow.setBackground(new Background(new BackgroundFill(c, new CornerRadii(10), Insets.EMPTY)));
            itemRow.setPrefWidth(rankBox.getPrefWidth());

            StackPane rankItemPane = new StackPane();
            Text rankD = new Text((rankData.getRank()) + "");
            rankD.setFill(fcolor);
            rankItemPane.setPrefHeight(ScreenAdapter.calculateMainY(height));
            rankItemPane.setPrefWidth(ScreenAdapter.calculateMainX(w0w));
            rankD.setTextAlignment(TextAlignment.CENTER);
            rankD.setFont(font);
            if (rankBold) {
                rankD.setStroke(rankD.getFill());
                rankD.setStrokeWidth(1);
            }
            rankItemPane.getChildren().add(rankD);

            StackPane teamItemPane = new StackPane();
            Text teamD = new Text(rankData.getTeam());
            teamD.setFill(fcolor);
            teamItemPane.setPrefHeight(ScreenAdapter.calculateMainY(height));
            teamItemPane.setPrefWidth(ScreenAdapter.calculateMainX(w1w));
            teamD.setTextAlignment(TextAlignment.CENTER);
            teamD.setFont(font);
            if (rankBold) {
                teamD.setStroke(teamD.getFill());
                teamD.setStrokeWidth(1);
            }

            teamItemPane.getChildren().add(teamD);

            StackPane scoreItemPane = new StackPane();
            Text scoreD = new Text(rankData.getMember() + "");
            scoreD.setFill(fcolor);
            scoreItemPane.setPrefHeight(ScreenAdapter.calculateMainY(height));
            scoreItemPane.setPrefWidth(ScreenAdapter.calculateMainX(w2w));
            scoreD.setTextAlignment(TextAlignment.CENTER);
            scoreD.setFont(font);
            if (rankBold) {
                scoreD.setStroke(scoreD.getFill());
                scoreD.setStrokeWidth(1);
            }
            scoreItemPane.getChildren().add(scoreD);

            StackPane timeItemPane = new StackPane();
            Text timeD = new Text(rankData.getScore() + "");
            timeD.setFill(fcolor);
            timeItemPane.setPrefHeight(ScreenAdapter.calculateMainY(height));
            timeItemPane.setPrefWidth(ScreenAdapter.calculateMainX(w3w));
            timeD.setTextAlignment(TextAlignment.CENTER);
            timeD.setFont(font);
            if (rankBold) {
                timeD.setStroke(timeD.getFill());
                timeD.setStrokeWidth(1);
            }
            timeItemPane.getChildren().add(timeD);

            itemRow.getChildren().addAll(rankItemPane, teamItemPane, scoreItemPane, timeItemPane);
            rankBox.getChildren().add(itemRow);
        }

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("title", "团 队 排 行 榜");
        mapData.put("rank", rankList.subList(start, end));
        messageHandler.notifyPage("memberRank", mapData);
        // 外屏展示
        if (ExtendedScreen.hasShow()) {
            Object res = ExtendedScreen.showWithControl("/view/match/MemberRank.fxml", rankList.subList(start, end));
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
