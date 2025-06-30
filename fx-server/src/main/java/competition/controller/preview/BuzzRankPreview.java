package competition.controller.preview;

import cn.hutool.core.io.FileUtil;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.BuzzRank;
import competition.controller.match.TeamRank;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class BuzzRankPreview extends RankPane{

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
                    Navigator.navigatorTo("/view/match/BuzzRankPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });


        Navigator.previewStage.addEventFilter(KeyEvent.KEY_PRESSED,new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != BuzzRankPreview.this) {
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


        ExtendedScreen.extendScreen.addEventFilter(KeyEvent.KEY_PRESSED,new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != BuzzRankPreview.this) {
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
        page = 0;
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.抢答排名背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }
        RankStyle rankStyle = rankStyleMapper.selectById(4);
        String rankConfig = rankStyle.getRankConfig();
        String headConfig = rankStyle.getHeadConfig();

        Configs configs = configsMapper.selectById(6);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);


        double x = ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[8]));
        double y = ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[9]));
        double width = ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[10]));
        double height = ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[11]));

        int count = 8;
        double rowSpace = ScreenAdapter.calculateMainY(10);

        String font = FontMap.defaultFontName();
        double size = ScreenAdapter.calculateMainX(competitionLab.getFont().getSize());
        boolean rankBold = false;
        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = rankConfig.split(ConfigSpliter.SPLITER);
            x = ScreenAdapter.calculateMainX(Integer.parseInt(split[0]));
            y = ScreenAdapter.calculateMainY(Integer.parseInt(split[1]));
            width = ScreenAdapter.calculateMainX(Integer.parseInt(split[2]));
            height = ScreenAdapter.calculateMainY(Integer.parseInt(split[3]));
            rowSpace = Integer.parseInt(split[6]);
            font = split[5];
            size = ScreenAdapter.calculateMainX(Integer.parseInt(split[4]));
            count = Integer.parseInt(split[7]);
            if (split.length >= 9) {
                rankBold = Boolean.parseBoolean(split[8]);
            }
        }

        if (!StringUtils.isEmpty(headConfig)) {
            String[] split = headConfig.split(ConfigSpliter.SPLITER);
            titleBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[0])));
            titleBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[1])));
            titleBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[2])));
            titleBox.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));

            double fs = ScreenAdapter.calculateMainX(Integer.parseInt(split[5]));
            Color color = Color.valueOf(split[4]);
            String fn = split[6];
            Font f = FontMap.load(fn, fs);
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
        LinkedHashMap<String, Long> buzzTimeMap = RuntimeUtil.buzzTimeMap;


        List<Teams> teams = TeamRuntime.getTeams();
        List<TeamMemberRankData> rankList = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            Teams item = teams.get(i);
            Long buzzTime = buzzTimeMap.get(item.getShortName());
            if (Objects.isNull(buzzTime)) {
                continue;
            }
            TeamMemberRankData rankData = new TeamMemberRankData();
            rankData.setTeam(item.getFullName());
            rankData.setShortName(item.getShortName());
            rankData.setScore(buzzTime);
            rankList.add(rankData);
        }
        // sort 根据时间进行排序
        rankList.sort((o1, o2) -> {
            if (o1.getScore() > 0 && o2.getScore() > 0) {
                if (o1.getScore() < o2.getScore()) {
                    return -1;
                } else if (o1.getScore() > o2.getScore()) {
                    return 1;
                }
                return 0;
            }
            if (o1.getScore() > 0 && o2.getScore() < 0) {
                return -1;
            }

            if (o1.getScore() < 0 && o2.getScore() > 0) {
                return 1;
            }

            if (o1.getScore() < 0 && o2.getScore() < 0) {
               return Math.toIntExact(o2.getScore() - o1.getScore());
            }
            return 0;
        });


        int rank = 1;
        for (TeamMemberRankData rankData : rankList) {
            rankData.setRank(rank);
            rank ++;
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
        }

        if (start < 0) {
            start = 0;
        }

        rankBox.getChildren().clear();

        String path = TextResource.BUZZ_TITLE;
        List<String> lines = FileUtil.readLines(path, StandardCharsets.UTF_8);


        Font font1 = FontMap.load(font, size);
        HBox title = new HBox();
        title.setBackground(new Background(new BackgroundFill(Color.RED,  new CornerRadii(10), Insets.EMPTY)));
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
        rankPane.setPrefHeight(height);
        rankL.setFont(font1);
        rankPane.setPrefWidth(w0w);
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
        teamPane.setPrefHeight(height);
        teamPane.setPrefWidth(w1w);
        teamL.setTextAlignment(TextAlignment.CENTER);
        teamL.setFont(font1);
        if (rankBold) {
            teamL.setStroke(teamL.getFill());
            teamL.setStrokeWidth(1);
        }
        teamPane.getChildren().add(teamL);


        StackPane scorePane = new StackPane();
        Text scoreL = new Text("抢答时间");
        if (lines.size() > 3) {
            scoreL.setText(lines.get(3));
        }
        scoreL.setFill(fcolor);
        scorePane.setPrefHeight(height);
        scorePane.setPrefWidth(w2w);
        scoreL.setTextAlignment(TextAlignment.CENTER);
        scoreL.setFont(font1);
        if (rankBold) {
            scoreL.setStroke(scoreL.getFill());
            scoreL.setStrokeWidth(1);
        }
        scorePane.getChildren().add(scoreL);


        StackPane statusPane = new StackPane();
        Text statusL = new Text("状态");
        if (lines.size() > 4) {
            statusL.setText(lines.get(4));
        }
        statusL.setFill(fcolor);
        statusPane.setPrefHeight(height);
        statusPane.setPrefWidth(w2w);
        statusL.setTextAlignment(TextAlignment.CENTER);
        statusL.setFont(font1);
        if (rankBold) {
            statusL.setStroke(statusL.getFill());
            statusL.setStrokeWidth(1);
        }
        statusPane.getChildren().add(statusL);

        title.getChildren().addAll(rankPane, teamPane, scorePane, statusPane);
        title.setBackground(new Background(new BackgroundFill(bgcolor,  new CornerRadii(10), Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = start; i < end; i++) {
            TeamMemberRankData rankData = rankList.get(i);
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

            StackPane rankItemPane = new StackPane();
            Text rankD = new Text((rankData.getRank()) + "");
            rankD.setFill(fcolor);
            rankItemPane.setPrefHeight(height);
            rankItemPane.setPrefWidth(w0w);
            rankD.setTextAlignment(TextAlignment.CENTER);
            rankD.setFont(font1);
            if (rankBold) {
                rankD.setStroke(rankD.getFill());
                rankD.setStrokeWidth(1);
            }
            rankItemPane.getChildren().add(rankD);

            StackPane teamItemPane = new StackPane();
            Text teamD = new Text(rankData.getTeam());
            teamD.setFill(fcolor);
            teamItemPane.setPrefHeight(height);
            teamItemPane.setPrefWidth(w1w);
            teamD.setTextAlignment(TextAlignment.CENTER);
            teamD.setFont(font1);
            if (rankBold) {
                teamD.setStroke(teamD.getFill());
                teamD.setStrokeWidth(1);
            }

            teamItemPane.getChildren().add(teamD);

            StackPane scoreItemPane = new StackPane();

            Text scoreD = new Text( NumberUtil.fix2PointWithDefault(rankData.getScore()));
            scoreD.setFill(fcolor);
            scoreItemPane.setPrefHeight(height);
            scoreItemPane.setPrefWidth(w2w);
            scoreD.setTextAlignment(TextAlignment.CENTER);
            scoreD.setFont(font1);
            if (rankBold) {
                scoreD.setStroke(scoreD.getFill());
                scoreD.setStrokeWidth(1);
            }
            scoreItemPane.getChildren().add(scoreD);


            StackPane statusItemPane = new StackPane();
            Text stausD = new Text();
            LinkedHashMap<String, Boolean> buzzResult = RuntimeUtil.buzzResult;
            Boolean status = buzzResult.getOrDefault(rankData.getShortName(), false);
            if (status) {
                stausD.setText("成功");
            } else {
                if (rankData.getScore() < 0) {
                    stausD.setText("犯规");
                } else {
                    stausD.setText("失败");
                }
            }
            stausD.setFill(fcolor);
            statusItemPane.setPrefHeight(height);
            statusItemPane.setPrefWidth(w2w);
            stausD.setTextAlignment(TextAlignment.CENTER);
            stausD.setFont(font1);
            if (rankBold) {
                stausD.setStroke(stausD.getFill());
                stausD.setStrokeWidth(1);
            }
            statusItemPane.getChildren().add(stausD);

            itemRow.getChildren().addAll(rankItemPane, teamItemPane, scoreItemPane, statusItemPane);
            rankBox.getChildren().add(itemRow);
        }
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("title", "抢 答 排 行 榜");
        mapData.put("rank", rankList.subList(start, end));
        messageHandler.notifyPage("buzzRank", mapData);
        if (ExtendedScreen.hasShow()) {
            Object res = ExtendedScreen.showWithControl("/view/match/BuzzRank.fxml", rankList.subList(start, end));
            if (res != null) {
                controlPane.setQuestionPreview((BuzzRank) res);
            }
        }
    }

    @Override
    public void reload() {
        showData();
    }
}
