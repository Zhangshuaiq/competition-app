package competition.controller.match;

import cn.hutool.core.io.FileUtil;
import competition.controller.ControlPane;
import competition.entity.Configs;
import competition.entity.RankStyle;
import competition.mapper.ConfigsMapper;
import competition.mapper.RankStyleMapper;
import competition.modal.TeamMemberRankData;
import competition.util.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
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
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Scope("prototype")
public class BuzzRank implements ReloadPane {

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

    private static List<TeamMemberRankData> rankList;

    public static void setRankList(List<TeamMemberRankData> rankList) {
        BuzzRank.rankList = rankList;
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
                    new Image(new FileInputStream(ImageResource.抢答排名背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
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
        

        double x = ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[8]));
        double y = ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[9]));
        double width = ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[10]));
        double height = ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[11]));

        int count = 8;
        double rowSpace = ScreenAdapter.calculateExY(10);

        Font font = FontMap.load(FontMap.defaultFontName(), 12);
        double size = ScreenAdapter.calculateExX(competitionLab.getFont().getSize());
        boolean rankBold = false;
        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = rankConfig.split(ConfigSpliter.SPLITER);
            x = ScreenAdapter.calculateExX(Integer.parseInt(split[0]));
            y = ScreenAdapter.calculateExY(Integer.parseInt(split[1]));
            width = ScreenAdapter.calculateExX(Integer.parseInt(split[2]));
            height = ScreenAdapter.calculateExY(Integer.parseInt(split[3]));
            rowSpace = ScreenAdapter.calculateExY(Integer.parseInt(split[6]));
            size = ScreenAdapter.calculateExX(Integer.parseInt(split[4]));
            font = FontMap.load(split[5], size);
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

//            competitionLab.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(split[0])));
//            competitionLab.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(split[1])));
//            competitionLab.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[2])));
//            competitionLab.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
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
        rankBox.getChildren().clear();

        String path = TextResource.BUZZ_TITLE;
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
        rankPane.setPrefHeight(height);
        rankL.setFont(font);
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
        teamL.setFont(font);
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
        scoreL.setFont(font);
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
        statusL.setFont(font);
        if (rankBold) {
            statusL.setStroke(statusL.getFill());
            statusL.setStrokeWidth(1);
        }
        statusPane.getChildren().add(statusL);

        title.getChildren().addAll(rankPane, teamPane, scorePane, statusPane);

        title.setBackground(new Background(new BackgroundFill(bgcolor, new CornerRadii(10), Insets.EMPTY)));
        rankBox.getChildren().add(title);

        for (int i = 0; i < rankList.size(); i++) {
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
            itemRow.setBackground(new Background(new BackgroundFill(c, new CornerRadii(10), Insets.EMPTY)));
            itemRow.setPrefWidth(rankBox.getPrefWidth());

            StackPane rankItemPane = new StackPane();
            Text rankD = new Text((rankData.getRank()) + "");
            rankD.setFill(fcolor);
            rankItemPane.setPrefHeight(height);
            rankItemPane.setPrefWidth(w0w);
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
            teamItemPane.setPrefHeight(height);
            teamItemPane.setPrefWidth(w1w);
            teamD.setTextAlignment(TextAlignment.CENTER);
            teamD.setFont(font);
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
            scoreD.setFont(font);
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
            stausD.setFont(font);
            if (rankBold) {
                stausD.setStroke(stausD.getFill());
                stausD.setStrokeWidth(1);
            }
            statusItemPane.getChildren().add(stausD);

            itemRow.getChildren().addAll(rankItemPane, teamItemPane, scoreItemPane, statusItemPane);

            rankBox.getChildren().add(itemRow);
        }

    }

    @Override
    public void reload() {
        showData();
    }
}
