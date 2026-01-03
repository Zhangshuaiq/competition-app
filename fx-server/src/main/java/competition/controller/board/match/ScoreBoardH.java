package competition.controller.board.match;

import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.preview.QuestionPreviewPreview;
import competition.controller.preview.ShowAnswerPreview;
import competition.entity.Configs;
import competition.entity.ScoreStyle;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreStyleMapper;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScoreBoardH implements BoardPane {

    @FXML
    private GridPane container; // 改为 GridPane

    @Resource
    private ConfigsMapper configsMapper;

    private final Set<String> selected = new HashSet<>();

    @Resource
    private ControlPane controlPane;

    @Resource
    private ScoreStyleMapper scoreStyleMapper;

    // 固定屏幕宽度
    private static final double SCREEN_WIDTH = 1920.0;

    @FXML
    public void initPage() {
        show();
    }

    @Override
    public void reloadColor() {

    }

    private void show() {
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        String[] split = scoreStyle.getStyles().split(ConfigSpliter.SPLITER);

        ScoreStyle scoreStyle1 = scoreStyleMapper.selectById(3);
        String[] ssplit = scoreStyle1.getStyles().split(ConfigSpliter.SPLITER);

        Font font = FontMap.load(split[0], ScreenAdapter.calculateExX(Integer.parseInt(split[1])));
        Font sfont = FontMap.load(ssplit[0], ScreenAdapter.calculateExX(Integer.parseInt(ssplit[1])));

        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = config.split(ConfigSpliter.SPLITER);

        container.getChildren().clear();
        
        // 🔧 计算间距
        double spacing = ScreenAdapter.calculateExX(Integer.parseInt(split[6]));
        container.setHgap(spacing); // 水平间距
        container.setVgap(spacing); // 垂直间距

        Color statusColor = Color.WHITE;
        double statusSize = new Text().getFont().getSize();

        if (cfgArr.length >= 33) {
            statusSize = ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[31]));
            statusColor = Color.valueOf(cfgArr[32]);
        }

        String sf = cfgArr.length >= 41 ? cfgArr[40] : FontMap.defaultFontName();
        boolean sb = cfgArr.length >= 42 && Boolean.parseBoolean(cfgArr[41]);

        Font statusFont = FontMap.load(sf, statusSize);
        
        List<Teams> teams = TeamRuntime.getTeams();
        
        // 🔧 计算网格布局的行列数
        double panelWidth = ScreenAdapter.calculateExX(Double.parseDouble(cfgArr[19]));
        int cols = calculateColumns(panelWidth, spacing);
        int rows = (int) Math.ceil(teams.size() * 1.0 / cols);
        
        log.info("队伍总数: {}, 每行显示: {} 个, 总行数: {}, 面板宽度: {}, 间距: {}", 
                 teams.size(), cols, rows, panelWidth, spacing);

        // 遍历所有队伍，添加到网格中
        for (int i = 0; i < teams.size(); i++) {
            Teams team = teams.get(i);
            
            int row = i / cols;  // 当前行
            int col = i % cols;  // 当前列
            
            VBox box = createTeamPanel(team, cfgArr, split, ssplit, font, sfont, 
                                      statusFont, statusColor, sb);
            
            // 添加到 GridPane
            container.add(box, col, row);
        }

        // 🔧 计算容器尺寸
        double containerWidth = panelWidth * cols + spacing * (cols - 1);
        
        int firstBoxChildCount = 0;
        if (!container.getChildren().isEmpty()) {
            VBox firstBox = (VBox) container.getChildren().get(0);
            firstBoxChildCount = firstBox.getChildren().size();
        }
        
        double panelHeight = ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20]) * firstBoxChildCount + firstBoxChildCount - 1);
        double containerHeight = panelHeight * rows + spacing * (rows - 1);
        
        container.setPrefWidth(containerWidth);
        container.setPrefHeight(containerHeight);
        
        log.info("容器尺寸: {}x{}", containerWidth, containerHeight);
        
        ExtendedScreen.scoreBoardStage.setWidth(containerWidth);
        ExtendedScreen.scoreBoardStage.setHeight(containerHeight);
    }

    /**
     * 计算每行可以显示多少列
     * @param panelWidth 单个面板宽度
     * @param spacing 间距
     * @return 列数
     */
    private int calculateColumns(double panelWidth, double spacing) {
        // 从1列开始尝试，找到最大可容纳的列数
        int cols = 1;
        while (true) {
            double requiredWidth = panelWidth * (cols + 1) + spacing * cols;
            if (requiredWidth > SCREEN_WIDTH) {
                break;
            }
            cols++;
        }
        
        // 至少显示1列
        return Math.max(1, cols);
    }

    /**
     * 创建单个队伍的面板
     */
    private VBox createTeamPanel(Teams team, String[] cfgArr, String[] split, String[] ssplit,
                                 Font font, Font sfont, Font statusFont, 
                                 Color statusColor, boolean sb) {
        int count = 1;
        VBox box = new VBox();
        box.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])));
        box.setSpacing(1);
        
        // 队名面板
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])),
                             ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));

        Text teamName = new Text(team.getShortName());
        teamName.setTextAlignment(TextAlignment.CENTER);
        teamName.setFill(Color.valueOf(cfgArr[15]));
        stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[16]), 
                                                                   new CornerRadii(10), Insets.EMPTY)));
        teamName.setFont(font);
        teamName.setUnderline(Boolean.valueOf(split[5]));
        teamName.setStrikethrough(Boolean.valueOf(split[2]));
        if (Boolean.valueOf(split[3])) {
            teamName.setStroke(teamName.getFill());
            teamName.setStrokeWidth(1);
        } else {
            teamName.setStroke(null);
        }
        teamName.getTransforms().clear();
        stackPane.getChildren().add(teamName);
        if (Boolean.valueOf(split[4])) {
            teamName.getTransforms().add(new Shear(-0.3, 0));
        }
        box.getChildren().add(stackPane);

        // 分数面板
        if (RuntimeUtil.showScoreBar) {
            count++;
            addScorePanel(box, team, cfgArr, ssplit, sfont);
        }

        // 抢答计时面板
        if (RuntimeUtil.showBuzzBar) {
            count++;
            addBuzzPanel(box, team, cfgArr, split);
        }

        // Model60/Model61 面板
        Stages currentStage = controlPane.getCurrentStage();
        if (currentStage != null) {
            count = addModelPanels(box, team, currentStage, cfgArr, statusFont, statusColor, sb, count);
        }

        box.setPrefHeight(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[20]) * count + count - 1));
        return box;
    }

    /**
     * 添加分数面板
     */
    private void addScorePanel(VBox box, Teams team, String[] cfgArr, String[] ssplit, Font sfont) {
        Text teamScore = new Text(team.getTeamScore() + "");
        StackPane stackPane1 = new StackPane();
        stackPane1.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])), 
                               ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
        teamScore.setTextAlignment(TextAlignment.CENTER);

        teamScore.setFill(Color.valueOf(cfgArr[21]));
        stackPane1.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[22]), 
                                                                    new CornerRadii(10), Insets.EMPTY)));
        teamScore.setFont(sfont);
        teamScore.setUnderline(Boolean.valueOf(ssplit[5]));
        teamScore.setStrikethrough(Boolean.valueOf(ssplit[2]));
        
        if (Boolean.valueOf(ssplit[3])) {
            teamScore.setStroke(teamScore.getFill());
            teamScore.setStrokeWidth(1);
        } else {
            teamScore.setStroke(null);
        }
        teamScore.getTransforms().clear();
        stackPane1.getChildren().add(teamScore);
        if (Boolean.valueOf(ssplit[4])) {
            teamScore.getTransforms().add(new Shear(-0.3, 0));
        }
        box.getChildren().add(stackPane1);
    }

    /**
     * 添加抢答面板
     */
    private void addBuzzPanel(VBox box, Teams team, String[] cfgArr, String[] split) {
        Long timeVal = RuntimeUtil.buzzTimeMap.get(team.getShortName());
        Text buzzTime = new Text("0");
        if (timeVal != null) {
            buzzTime.setText(NumberUtil.fix2Point(timeVal / 1000000000.0));
        }
        StackPane buzzPane = new StackPane();
        buzzPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(buzzPane, Priority.NEVER);

        buzzPane.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])), 
                            ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
        buzzTime.setTextAlignment(TextAlignment.CENTER);
        buzzTime.setFill(Color.valueOf(cfgArr[21]));

        Boolean b = RuntimeUtil.buzzResult.get(team.getShortName());
        Color color = null;
        if (b == null) {
            color = Color.valueOf(cfgArr[25]);
        } else if (b) {
            color = Color.valueOf(cfgArr[27]);
        } else {
            color = Color.valueOf(cfgArr[28]);
        }
        buzzPane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), Insets.EMPTY)));
        buzzTime.setFont(Font.font(ScreenAdapter.calculateExX(12)));
        buzzTime.setUnderline(Boolean.valueOf(split[5]));
        buzzTime.setStrikethrough(Boolean.valueOf(split[2]));

        if (cfgArr.length >= 37) {
            buzzTime.setFont(Font.font(ScreenAdapter.calculateExX(Double.parseDouble(cfgArr[36]))));
        } else {
            buzzTime.setFont(Font.font(ScreenAdapter.calculateExX(12)));
        }
        if (cfgArr.length >= 38) {
            buzzTime.setFill(Color.valueOf(cfgArr[37]));
        }

        if (cfgArr.length >= 39) {
            Font f = FontMap.load(cfgArr[38], buzzTime.getFont().getSize());
            buzzTime.setFont(f);
        }

        if (cfgArr.length >= 40) {
            if (Boolean.parseBoolean(cfgArr[39])) {
                buzzTime.setStroke(buzzTime.getFill());
                buzzTime.setStrokeWidth(1);
            } else {
                buzzTime.setStroke(null);
            }
        } else {
            buzzTime.setStroke(null);
        }

        buzzTime.getTransforms().clear();
        buzzPane.getChildren().add(buzzTime);
        box.getChildren().add(buzzPane);
    }

    /**
     * 添加 Model 面板
     */
    private int addModelPanels(VBox box, Teams team, Stages currentStage, String[] cfgArr,
                               Font statusFont, Color statusColor, boolean sb, int count) {
        Integer m6 = currentStage.getM6();
        Integer questionType = currentStage.getQuestionType();
        
        if (questionType == 6 && controlPane.getPreviewPane() instanceof QuestionPreviewPreview) {
            if (m6 == 0) {
                count = addModel60Panel(box, team, currentStage, cfgArr, statusFont, statusColor, sb, count);
            } else if (m6 == 1) {
                count = addModel61Panel(box, team, currentStage, cfgArr, statusFont, statusColor, sb, count);
            }
        } else if (questionType == 7) {
            count++;
        }
        
        return count;
    }

    /**
     * 添加 Model60 面板
     */
    private int addModel60Panel(VBox box, Teams team, Stages currentStage, String[] cfgArr,
                                Font statusFont, Color statusColor, boolean sb, int count) {
        List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
        Map<String, Model60RuntimeData.ModelData> answerMap = runtimeData.stream()
                .collect(Collectors.toMap(Model60RuntimeData.ModelData::getTeamName, x -> x));
        count++;
        
        StackPane stackPane2 = new StackPane();
        stackPane2.setPrefSize(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])), 
                              ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
        
        Model60RuntimeData.ModelData userData = answerMap.get(team.getFullName());
        Text userSelect = new Text();
        userSelect.setTextAlignment(TextAlignment.CENTER);
        userSelect.setFont(statusFont);
        userSelect.setFill(statusColor);
        
        if (sb) {
            userSelect.setStroke(userSelect.getFill());
            userSelect.setStrokeWidth(1);
        } else {
            userSelect.setStroke(null);
        }
        
        if (team.getStatus() == 1) {
            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), 
                                                                       new CornerRadii(10), Insets.EMPTY)));
        } else {
            if (StringUtils.isEmpty(userData.getUserSelect())) {
                if (team.getStatus() == 3) {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), 
                                                                               new CornerRadii(10), Insets.EMPTY)));
                } else {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), 
                                                                               new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                userSelect.setText(userData.getUserSelect().replace("\u00A0", " "));
                if (userData.getAnswer().equals(userData.getUserSelect())) {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), 
                                                                               new CornerRadii(10), Insets.EMPTY)));
                } else {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), 
                                                                               new CornerRadii(10), Insets.EMPTY)));
                }
            }
        }

        if (!(Objects.equals(1, currentStage.getAnswerSingle()) 
              && controlPane.getPreviewPane() instanceof ShowAnswerPreview)) {
            if (Objects.equals(1, currentStage.getAnswerSingle())) {
                userSelect.setText("");
            }
            stackPane2.getChildren().add(userSelect);
            box.getChildren().add(stackPane2);
        }
        
        return count;
    }

    /**
     * 添加 Model61 面板
     */
    private int addModel61Panel(VBox box, Teams team, Stages currentStage, String[] cfgArr,
                                Font statusFont, Color statusColor, boolean sb, int count) {
        List<Model61RuntimeData.ModelData> runtimeData = Model61RuntimeData.getRuntimeData();
        Map<String, Model61RuntimeData.ModelData> dataMap = runtimeData.stream()
                .collect(Collectors.toMap(x -> x.getTeamName() + "-" + x.getUserName(), x -> x));
        count++;
        
        HBox memberStatus = new HBox();
        memberStatus.setSpacing(1);
        memberStatus.setPadding(new Insets(1, 1, 1, 1));
        memberStatus.setPrefWidth(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[19])));
        
        String members = team.getMembers();
        if (!StringUtils.isEmpty(members)) {
            String[] ms = members.split(",");
            double w = Integer.parseInt(cfgArr[19]) * 1.0 / ms.length;
            
            for (int i = 0; i < ms.length; i++) {
                Model61RuntimeData.ModelData modelData = dataMap.get(team.getFullName() + "-" + ms[i]);
                StackPane stackPane2 = new StackPane();
                stackPane2.setPrefSize(ScreenAdapter.calculateExX(w), 
                                      ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[20])));
                
                Text l = new Text((i + 1) + "");
                l.setTextAlignment(TextAlignment.CENTER);
                stackPane2.getChildren().add(l);
                l.setFont(statusFont);
                l.setFill(statusColor);
                
                if (sb) {
                    l.setStroke(l.getFill());
                    l.setStrokeWidth(1);
                } else {
                    l.setStroke(null);
                }
                
                if (team.getStatusList().get(i) == 1) {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), 
                                                                               new CornerRadii(10), Insets.EMPTY)));
                } else {
                    if (StringUtils.isEmpty(modelData.getUserSelect())) {
                        if (team.getStatusList().get(i) == 3) {
                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), 
                                                                                       new CornerRadii(10), Insets.EMPTY)));
                        } else {
                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), 
                                                                                       new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        if (modelData.getUserSelect().replace("\u00A0", " ")
                                .equals(modelData.getAnswer().replace("\u00A0", " "))) {
                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), 
                                                                                       new CornerRadii(10), Insets.EMPTY)));
                        } else {
                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), 
                                                                                       new CornerRadii(10), Insets.EMPTY)));
                        }
                    }
                }

                memberStatus.getChildren().add(stackPane2);
            }
            box.getChildren().add(memberStatus);
        }
        
        return count;
    }

    public Set<String> getSelected() {
        return selected;
    }

    @Override
    public void close() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.hide();
    }

    @Override
    public void shows() {
        Stage stage = (Stage) (container.getScene().getWindow());
        stage.show();
        reload();
    }

    @Override
    public void reload() {
        show();
    }

    @Override
    public Node getPane() {
        return container;
    }
}