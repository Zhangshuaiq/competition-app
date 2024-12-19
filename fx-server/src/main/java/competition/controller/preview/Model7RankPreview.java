package competition.controller.preview;

import cn.hutool.core.io.FileUtil;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.Model7Rank;
import competition.entity.Configs;
import competition.entity.RankStyle;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.mapper.RankStyleMapper;
import competition.modal.Model7RankData;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import competition.ws.Model7RuntimeData;
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
import java.util.stream.Collectors;

/**
 * 排行（竞技模式、团队答题、个人答题排行）
 */
@Component
@Scope("prototype")
public class Model7RankPreview extends PreviewPane {

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
    public void initialize() {
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        PreviewStack.addPreview(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    // 显示属性配置弹出
                    Navigator.navigatorTo("/view/match/RankPreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });

//
        Navigator.previewStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != Model7RankPreview.this) {
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

        ExtendedScreen.extendScreen.addEventFilter(KeyEvent.KEY_PRESSED,new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (controlPane.getPreviewPane() != Model7RankPreview.this) {
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
                    new Image(new FileInputStream(ImageResource.竞技排名背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }

        RankStyle rankStyle = rankStyleMapper.selectById(1);
        String rankConfig = rankStyle.getRankConfig();
        String headConfig = rankStyle.getHeadConfig();

        Configs configs = configsMapper.selectById(8);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);


        double x = ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[9]));
        double y = ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[10]));
        double width = ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[11]));
        double height = ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[12]));

        int count = 8;
        double rowSpace = ScreenAdapter.calculateMainY(10);
        double size = ScreenAdapter.calculateMainX(competitionLab.getFont().getSize());
        Font font =  FontMap.load(FontMap.defaultFontName(), 12);
        boolean rankBold = false;

        if (!StringUtils.isEmpty(rankConfig)) {
            String[] split = rankConfig.split(ConfigSpliter.SPLITER);
            x = ScreenAdapter.calculateMainX(Integer.parseInt(split[0]));
            y = ScreenAdapter.calculateMainY(Integer.parseInt(split[1]));
            width = ScreenAdapter.calculateMainX(Integer.parseInt(split[2]));
            height = ScreenAdapter.calculateMainY(Integer.parseInt(split[3]));
            rowSpace = ScreenAdapter.calculateMainY(Integer.parseInt(split[6]));
            
            size = ScreenAdapter.calculateMainX(Integer.parseInt(split[4]));
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
//            competitionLab.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(split[2])));
//            competitionLab.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(split[3])));
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


        rankBox.setLayoutX(x);
        rankBox.setLayoutY(y);
        rankBox.setPrefWidth(width);
        rankBox.setPrefHeight(height * count + (rowSpace) * (count - 1));
        rankBox.setSpacing(rowSpace);

        Stages currentStage = controlPane.getCurrentStage();

        // true 正计时  false 倒计时
        boolean countType = currentStage.getOrderVal() == 1;
        String secondText = controlPane.timeSecondText.getText();
        int totalSecond = Integer.parseInt(secondText);

        List<Model7RankData> rankList = new ArrayList<>();

        Map<String, Object> mapData = new HashMap<>();
        if (currentStage.getQuestionType() == 6) {
            mapData.put("title", "排行榜");
            if (currentStage.getM6() == 0) {

                List<List<Model60RuntimeData.ModelData>> runtimeData60 = Model60RuntimeData.getTotalData();
                if (runtimeData60.isEmpty()) {
                    return;
                }

                List<Model7RankData> memeryData = new ArrayList<>();
                for (List<Model60RuntimeData.ModelData> modelDataList : runtimeData60) {
                    for (Model60RuntimeData.ModelData modelData : modelDataList) {
                        Model7RankData rankData = new Model7RankData();
                        if (modelData.getAnswer().equals(modelData.getUserSelect())) {
                            rankData.setScore(modelData.getScore());
                        } else {
                            rankData.setScore(-controlPane.getCurrentStage().getDesValue());
                        }

                        if (countType) {
                            rankData.setSecond(modelData.getSecond());
                        } else {
                            rankData.setSecond(totalSecond - modelData.getSecond());
                        }
                        rankData.setTeam(modelData.getTeamName());
                        memeryData.add(rankData);
                    }
                }

                Map<String, List<Model7RankData>> convertMap = memeryData.stream().collect(Collectors.groupingBy(Model7RankData::getTeam));
                for (Map.Entry<String, List<Model7RankData>> entry : convertMap.entrySet()) {
                    Model7RankData rankData = new Model7RankData();
                    List<Model7RankData> itemList = entry.getValue();

                    int second = 0;
                    int score = 0;
                    for (Model7RankData data : itemList) {
                        second += data.getSecond();
                        score += data.getScore();
                        rankData.setTeam(data.getTeam());
                    }
                    rankData.setSecond(second);
                    rankData.setTime(formatTime(second));
                    rankData.setScore(score);
                    rankList.add(rankData);
                }
            } else if (currentStage.getM6() == 1) {

                List<List<Model61RuntimeData.ModelData>> runtimeData61 = Model61RuntimeData.getTotalData();
                if (runtimeData61.isEmpty()) {
                    return;
                }
                List<Model7RankData> memeryData = new ArrayList<>();
                for (List<Model61RuntimeData.ModelData> modelDataList : runtimeData61) {
                    for (Model61RuntimeData.ModelData modelData : modelDataList) {
                        Model7RankData rankData = new Model7RankData();
                        if (modelData.getAnswer().equals(modelData.getUserSelect())) {
                            rankData.setScore(modelData.getScore());
                        } else {
                            rankData.setScore(-controlPane.getCurrentStage().getDesValue());
                        }

                        if (countType) {
                            rankData.setSecond(modelData.getSecond());
                        } else {
                            rankData.setSecond(totalSecond - modelData.getSecond());
                        }
                        rankData.setTeam(modelData.getTeamName() + "-" + modelData.getUserName());
                        memeryData.add(rankData);
                    }
                }

                Map<String, List<Model7RankData>> convertMap = memeryData.stream().collect(Collectors.groupingBy(Model7RankData::getTeam));
                for (Map.Entry<String, List<Model7RankData>> entry : convertMap.entrySet()) {
                    Model7RankData rankData = new Model7RankData();
                    List<Model7RankData> itemList = entry.getValue();

                    int second = 0;
                    int score = 0;
                    for (Model7RankData data : itemList) {
                        second += data.getSecond();
                        score += data.getScore();
                        rankData.setTeam(data.getTeam());
                    }
                    rankData.setSecond(second);
                    rankData.setTime(formatTime(second));
                    rankData.setScore(score);
                    rankList.add(rankData);
                }

            }
        } 
        else if (currentStage.getQuestionType() == 7) 
        {
            boolean finish = true;
            List<Boolean> finished = Model7RuntimeData.getFinished();
            for (Boolean b : finished) {
                finish = finish & b;
            }
            mapData.put("title", "排行榜");
            mapData.put("rank", rankList);
            // 获取竞技排名
            List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();
            List<String> timeData = Model7RuntimeData.getFts();
            List<Integer> seconds = Model7RuntimeData.getSeconds();
            for (int i = 0; i < runtimeData.size(); i++) {
                List<Model7RuntimeData.ModelData> questionList = runtimeData.get(i);
                int score = 0;
                if (finish) {
                    for (Model7RuntimeData.ModelData data : questionList) {
                        if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                            score += data.getScore();
                        } else {
                            score -= controlPane.getCurrentStage().getDesValue();
                        }
                    }
                    score = score - ((Model7RuntimeData.totalNumber - questionList.size()) * controlPane.getCurrentStage().getDesValue());
                } else {
                    for (Model7RuntimeData.ModelData data : questionList) {
                        if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                            score += data.getScore();
                        } else {
                            score -= controlPane.getCurrentStage().getDesValue();
                        }
                    }
                }
                String team = Model7RuntimeData.getTeams().get(i).getShortName();
                
                String time = timeData.get(i);

                Model7RankData rankData = new Model7RankData();
                rankData.setScore(score);
                rankData.setSecond(seconds.get(i));
                rankData.setTeam(team);
                rankData.setTime(time);
                rankList.add(rankData);
            }
        }

        // sort 根据分数和时间进行排序
        rankList.sort((o1, o2) -> {
            if (o1.getScore() < o2.getScore()) {
                return 1;
            } else if (o1.getScore() > o2.getScore()) {
                return -1;
            } else {
                return o1.getSecond().compareTo(o2.getSecond());
            }
        });

        int rank = 1;
        Model7RankData first = rankList.get(0);
        for (Model7RankData rankData : rankList) {
            if (first.compareTo(rankData) < 0) {
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

        String path = TextResource.MODEL_TITLE;
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
        Text scoreL = new Text("分数");
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

        StackPane timePane = new StackPane();
        Text timeL = new Text("时间");
        if (lines.size() >= 4) {
            timeL.setText(lines.get(3));
        }
        timeL.setFill(fcolor);
        timePane.setPrefHeight(height);
        timePane.setPrefWidth(w3w);
        timeL.setTextAlignment(TextAlignment.CENTER);
        timeL.setFont(font);
        if (rankBold) {
            timeL.setStroke(timeL.getFill());
            timeL.setStrokeWidth(1);
        }
        timePane.getChildren().add(timeL);

        title.getChildren().addAll(rankPane, teamPane, scorePane, timePane);
        title.setBackground(new Background(new BackgroundFill(bgcolor, new CornerRadii(10), Insets.EMPTY)));
        rankBox.getChildren().add(title);


        for (int i = start; i < end; i++) {
            Model7RankData rankData = rankList.get(i);
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
            itemRow.setPrefHeight(height);


            StackPane rankItemPane = new StackPane();
            Text rankD = new Text(rankData.getRank() + "");
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
            Text scoreD = new Text(rankData.getScore() + "");
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

            StackPane timeItemPane = new StackPane();
            Text timeD = new Text(rankData.getTime());
            timeD.setFill(fcolor);
            timeItemPane.setPrefHeight(height);
            timeItemPane.setPrefWidth(w3w);
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

        // 需要判断答题进程， 如果答题未结束，则不应该向网页端发送展示排行的指令
        boolean finish = true;
        if (currentStage.getQuestionType() == 7) {
            List<Boolean> finished = Model7RuntimeData.getFinished();
            for (Boolean b : finished) {
                finish = finish & b;
            }
        }
        
        
        messageHandler.notifyPage("hmodel7", mapData);

        if (finish) {
            mapData.put("rank", rankList.subList(start, end));
            messageHandler.notifyPage("stageRank", mapData);
        }
        

        if (ExtendedScreen.hasShow()) {
            Model7Rank.setRankList(rankList.subList(start, end));
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/Model7Rank.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((Model7Rank) res);
            }
        }
    }

    @Override
    public void reload(int time, String timeTxt) {
        showData();
    }

    @Override
    public void reload() {
        this.page = 0;
        showData();
    }

    private String formatTime(int second) {
        int s = second % 60;
        int m = second / 60;

        return String.format("%02d 分 %02d 秒", m, s);
    }
}
