package competition.controller.board.preview;

import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.board.match.ScoreBoardH;
import competition.controller.preview.PreviewPane;
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
import competition.ws.QuestionSubmitRuntime;
import competition.ws.StageSelectRuntime;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
public class ScoreBoardHPreview extends PreviewPane implements BoardPane {

    @FXML
    private GridPane container;

    @Resource
    private ConfigsMapper configsMapper;

    private final Set<String> selected = new HashSet<>();

    @Resource
    private ControlPane controlPane;

    @Resource
    private ScoreStyleMapper scoreStyleMapper;

    private static final double SCREEN_WIDTH = 1920.0;

    @FXML
    public void initPage() {
        show();
    }

    @Override
    public void reloadColor() {

    }

    private void show() {
        BoardPane scoreBoard = controlPane.getScoreBoard();
        if (RuntimeUtil.showSecondScorePane) {
            scoreBoard = (ScoreBoardH) ExtendedScreen.scoreWithPos("/view/board/match/ScoreBoardH.fxml", "", "/view/style.css", false);
            controlPane.setScoreBoard(scoreBoard);
            scoreBoard.reload();
        } else {
            if (scoreBoard != null) {
                scoreBoard.close();
                scoreBoard = null;
            }
        }
        if (scoreBoard != null) {
            controlPane.setScoreBoard(scoreBoard);
            scoreBoard.reload();
        }

        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        String[] split = ConfigSpliter.split(scoreStyle.getStyles());

        ScoreStyle scoreStyle1 = scoreStyleMapper.selectById(3);
        String[] ssplit = ConfigSpliter.split(scoreStyle1.getStyles());

        Font font = FontMap.load(split[0], ScreenAdapter.calculateMainX(Integer.parseInt(split[1])));
        Font sfont = FontMap.load(ssplit[0], ScreenAdapter.calculateMainX(Integer.parseInt(ssplit[1])));

        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] cfgArr = ConfigSpliter.split(config);

        container.getChildren().clear();
        container.setMaxHeight(Double.MAX_VALUE);

        double spacing = ScreenAdapter.calculateMainX(Integer.parseInt(split[6]));
        container.setHgap(spacing);
        container.setVgap(spacing);

        Color statusColor = Color.WHITE;
        double statusSize = new Text().getFont().getSize();

        if (cfgArr.length >= 33) {
            statusSize = ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[31]));
            statusColor = Color.valueOf(cfgArr[32]);
        }

        String sf = cfgArr.length >= 41 ? cfgArr[40] : FontMap.defaultFontName();
        boolean sb = cfgArr.length >= 42 && Boolean.parseBoolean(cfgArr[41]);

        Font statusFont = FontMap.load(sf, statusSize);

        List<Teams> teams = TeamRuntime.getTeams();

        double panelWidth = ScreenAdapter.calculateMainX(Double.parseDouble(cfgArr[19]));
        int cols = calculateColumns(panelWidth, spacing);
        int rows = (int) Math.ceil(teams.size() * 1.0 / cols);

        log.info("队伍总数: {}, 每行显示: {} 个, 总行数: {}, 面板宽度: {}, 间距: {}",
            teams.size(), cols, rows, panelWidth, spacing);

        for (int i = 0; i < teams.size(); i++) {
            Teams team = teams.get(i);

            int row = i / cols;
            int col = i % cols;

            VBox box = createTeamPanel(team, cfgArr, split, ssplit, font, sfont,
                statusFont, statusColor, sb);

            container.add(box, col, row);
        }

        double containerWidth = panelWidth * cols + spacing * (cols - 1);

        int firstBoxChildCount = 0;
        if (!container.getChildren().isEmpty()) {
            VBox firstBox = (VBox) container.getChildren().get(0);
            firstBoxChildCount = firstBox.getChildren().size();
        }

        double panelHeight = ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20]) * firstBoxChildCount + firstBoxChildCount - 1);
        double containerHeight = panelHeight * rows + spacing * (rows - 1);

        container.setPrefWidth(containerWidth);
        container.setPrefHeight(containerHeight);

        log.info("容器尺寸: {}x{}", containerWidth, containerHeight);

        Navigator.scoreBoardStage.setWidth(containerWidth);
        Navigator.scoreBoardStage.setHeight(containerHeight);
    }

    private int calculateColumns(double panelWidth, double spacing) {
        int cols = 1;
        while (true) {
            double requiredWidth = panelWidth * (cols + 1) + spacing * cols;
            if (requiredWidth > SCREEN_WIDTH) {
                break;
            }
            cols++;
        }
        return Math.max(1, cols);
    }

    private VBox createTeamPanel(Teams team, String[] cfgArr, String[] split, String[] ssplit,
                                 Font font, Font sfont, Font statusFont, Color statusColor, boolean sb) {
        int count = 1;
        VBox box = new VBox();
        box.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])));
        box.setMaxHeight(Double.MAX_VALUE);
        box.setSpacing(1);

        // 队名面板
        StackPane stackPane = new StackPane();
        stackPane.setMaxHeight(Double.MAX_VALUE);
        stackPane.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])),
            ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20])));
        VBox.setVgrow(stackPane, Priority.NEVER);

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
            Text teamScore = new Text(team.getTeamScore() + "");
            StackPane stackPane1 = new StackPane();
            stackPane1.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(stackPane1, Priority.NEVER);
            stackPane1.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])),
                ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20])));
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

        // 第三行：子环节名行，仅当 questionType == 8 时始终展示
        if (currentStage != null && currentStage.getQuestionType() == 8) {
            count++;
            StackPane stagePane = new StackPane();
            stagePane.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(stagePane, Priority.NEVER);
            stagePane.setPrefSize(
                ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])),
                ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20]))
            );
            Text stageText = new Text();
            stageText.setTextAlignment(TextAlignment.CENTER);
            stageText.setFill(Color.valueOf(cfgArr[15]));
            stageText.setFont(font);
            stageText.setUnderline(Boolean.valueOf(split[5]));
            stageText.setStrikethrough(Boolean.valueOf(split[2]));
            if (Boolean.valueOf(split[3])) {
                stageText.setStroke(stageText.getFill());
                stageText.setStrokeWidth(1);
            } else {
                stageText.setStroke(null);
            }
            stageText.getTransforms().clear();
            if (Boolean.valueOf(split[4])) {
                stageText.getTransforms().add(new Shear(-0.3, 0));
            }
            StageSelectRuntime.StageSelectData sel8 = StageSelectRuntime.getAll().get(team.getFullName());
            if (team.getStageSelectStatus() == 2) {
                // 已揭晓：显示子环节名（绿色）
                String display = sel8 != null && StringUtils.hasText(sel8.getStage()) ? sel8.getStage()
                        : StringUtils.hasText(team.getTeamSelect()) ? team.getTeamSelect()
                        : StringUtils.hasText(team.getStageSelect()) ? team.getStageSelect() : "已选择";
                stageText.setText(display);
                stagePane.setBackground(new Background(new BackgroundFill(
                    Color.valueOf(cfgArr[27]), new CornerRadii(10), Insets.EMPTY
                )));
            } else if (team.getStageSelectStatus() == 1) {
                // 已选择，未揭晓（蓝色）：管理面板直接显示实际环节名
                String display1 = sel8 != null ? sel8.getStage()
                        : (StringUtils.hasText(team.getStageSelect()) ? team.getStageSelect() : "已选择");
                stageText.setText(display1);
                stagePane.setBackground(new Background(new BackgroundFill(
                    Color.valueOf(cfgArr[26]), new CornerRadii(10), Insets.EMPTY
                )));
            } else {
                // 未选择（灰色）
                stageText.setText("未选择");
                stagePane.setBackground(new Background(new BackgroundFill(
                    Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY
                )));
            }
            stagePane.getChildren().add(stageText);
            box.getChildren().add(stagePane);
        }

        // 答案展示行：type8 为第四行，type6 为第三行，仅在答题界面（QuestionPreviewPreview）展示
        if (currentStage != null && currentStage.getBoardAnswer() == 1
                && controlPane.getPreviewPane() instanceof QuestionPreviewPreview) {
            boolean isType8AnswerPhase = currentStage.getQuestionType() == 8;
            boolean isType6AnswerPhase = currentStage.getQuestionType() == 6 && Objects.equals(0, currentStage.getM6());
            if (isType8AnswerPhase || isType6AnswerPhase) {
                count++;
                StackPane answerPane = new StackPane();
                answerPane.setMaxHeight(Double.MAX_VALUE);
                VBox.setVgrow(answerPane, Priority.NEVER);
                answerPane.setPrefSize(
                    ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])),
                    ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20]))
                );
                Text answerText = new Text();
                answerText.setTextAlignment(TextAlignment.CENTER);
                answerText.setFill(statusColor);
                answerText.setFont(statusFont);
                if (sb) {
                    answerText.setStroke(answerText.getFill());
                    answerText.setStrokeWidth(1);
                } else {
                    answerText.setStroke(null);
                }
                if (isType8AnswerPhase) {
                    String submitted = QuestionSubmitRuntime.getAll().get(team.getFullName());
                    if (team.getStatus() == 2) {
                        answerText.setText(StringUtils.hasText(submitted) ? submitted : "");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), new CornerRadii(10), Insets.EMPTY)));
                    } else if (team.getStatus() == 3) {
                        answerText.setText(StringUtils.hasText(submitted) ? submitted : "未提交");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]), new CornerRadii(10), Insets.EMPTY)));
                    } else if (team.getStatus() == 1) {
                        answerText.setText("已提交");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), new CornerRadii(10), Insets.EMPTY)));
                    } else {
                        answerText.setText("未提交");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    // type 6：揭示后 teamSelect 为提交答案，status 2 正确 / status 3 错误或零分
                    if (team.getStatus() == 2) {
                        answerText.setText(StringUtils.hasText(team.getTeamSelect()) ? team.getTeamSelect() : "");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[27]), new CornerRadii(10), Insets.EMPTY)));
                    } else if (team.getStatus() == 1) {
                        answerText.setText("已提交");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]), new CornerRadii(10), Insets.EMPTY)));
                    } else {
                        answerText.setText(StringUtils.hasText(team.getTeamSelect()) ? team.getTeamSelect() : "未提交");
                        answerPane.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]), new CornerRadii(10), Insets.EMPTY)));
                    }
                }
                answerPane.getChildren().add(answerText);
                box.getChildren().add(answerPane);
            }
        }

        // 设置点击事件
        box.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    Border border = box.getBorder();
                    if (border == null) {
                        box.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID,
                            new CornerRadii(10), new BorderWidths(5))));
                        selected.add(teamName.getText());
                        controlPane.emptyTeam();
                    } else {
                        box.setBorder(null);
                        selected.remove(teamName.getText());
                    }
                } else {
                    Navigator.navigatorTo("/view/board/preview/ScorePreviewAttr.fxml",
                        "分数面板设置", "/view/style.css", false, false);
                }
            }
        });

        box.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20]) * count + count - 1));
        return box;
    }

    private void addBuzzPanel(VBox box, Teams team, String[] cfgArr, String[] split) {
        Long timeVal = RuntimeUtil.buzzTimeMap.get(team.getShortName());
        Text buzzTime = new Text("0");
        if (timeVal != null) {
            buzzTime.setText(NumberUtil.fix2Point(timeVal / 1000000000.0));
        }
        StackPane buzzPane = new StackPane();
        buzzPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(buzzPane, Priority.NEVER);

        buzzPane.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])),
            ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20])));
        buzzTime.setTextAlignment(TextAlignment.CENTER);
        buzzTime.setFill(Color.valueOf(cfgArr[21]));

        Boolean b = RuntimeUtil.buzzResult.get(team.getShortName());
        Color color;
        if (b == null) {
            color = Color.valueOf(cfgArr[25]);
        } else if (b) {
            color = Color.valueOf(cfgArr[27]);
        } else {
            color = Color.valueOf(cfgArr[28]);
        }
        buzzPane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), Insets.EMPTY)));
        buzzTime.setFont(Font.font(ScreenAdapter.calculateMainX(12)));
        buzzTime.setUnderline(Boolean.valueOf(split[5]));
        buzzTime.setStrikethrough(Boolean.valueOf(split[2]));

        if (cfgArr.length >= 37) {
            buzzTime.setFont(Font.font(ScreenAdapter.calculateMainX(Double.parseDouble(cfgArr[36]))));
        } else {
            buzzTime.setFont(Font.font(ScreenAdapter.calculateMainX(12)));
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

    private int addModelPanels(VBox box, Teams team, Stages currentStage, String[] cfgArr,
                               Font statusFont, Color statusColor, boolean sb, int count) {
        Integer m6 = currentStage.getM6();
        Integer questionType = currentStage.getQuestionType();

        if (questionType == 6 && controlPane.getPreviewPane() instanceof QuestionPreviewPreview) {
            if (m6 == 0 && !Objects.equals(1, currentStage.getBoardAnswer())) {
                count = addModel60Panel(box, team, currentStage, cfgArr, statusFont, statusColor, sb, count);
            } else if (m6 == 1) {
                count = addModel61Panel(box, team, currentStage, cfgArr, statusFont, statusColor, sb, count);
            }
        } else if (questionType == 7) {
            count++;
        }

        return count;
    }

    private int addModel60Panel(VBox box, Teams team, Stages currentStage, String[] cfgArr,
                                Font statusFont, Color statusColor, boolean sb, int count) {
        List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();
        Map<String, Model60RuntimeData.ModelData> answerMap = runtimeData.stream()
            .collect(Collectors.toMap(Model60RuntimeData.ModelData::getTeamName, x -> x));
        count++;

        StackPane stackPane2 = new StackPane();
        stackPane2.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(stackPane2, Priority.NEVER);
        stackPane2.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])),
            ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20])));

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
            userSelect.setText("\u5DF2\u63D0\u4EA4");
        } else {
            if (userData == null || StringUtils.isEmpty(userData.getUserSelect())) {
                if (team.getStatus() == 3) {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]),
                        new CornerRadii(10), Insets.EMPTY)));
                    userSelect.setText("\u672A\u63D0\u4EA4");
                } else {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]),
                        new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                userSelect.setText(userData.getUserSelect().replace("\u00A0", " "));
                if (userData.getAnswer().replace("\u00A0", " ")
                    .equals(userData.getUserSelect().replace("\u00A0", " "))) {
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
            if (Objects.equals(1, currentStage.getAnswerSingle()) && team.getStatus() != 1) {
                userSelect.setText("");
            }
            stackPane2.getChildren().add(userSelect);
            box.getChildren().add(stackPane2);
        }

        return count;
    }

    private int addModel61Panel(VBox box, Teams team, Stages currentStage, String[] cfgArr,
                                Font statusFont, Color statusColor, boolean sb, int count) {
        List<Model61RuntimeData.ModelData> runtimeData = Model61RuntimeData.getRuntimeData();
        Map<String, Model61RuntimeData.ModelData> dataMap = runtimeData.stream()
            .collect(Collectors.toMap(x -> x.getTeamName() + "-" + x.getUserName(), x -> x));
        count++;

        HBox memberStatus = new HBox();
        memberStatus.setMaxHeight(Double.MAX_VALUE);
        memberStatus.setSpacing(1);
        memberStatus.setPadding(new Insets(1, 1, 1, 1));
        memberStatus.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[19])));

        String members = team.getMembers();
        if (!StringUtils.isEmpty(members)) {
            String[] ms = TeamMemberCodec.split(members);
            boolean freeAnswer = Objects.equals(1, currentStage.getBoardAnswer());
            int selectedMember = freeAnswer && RuntimeUtil.boardAnswerMemberInfo != null
                    ? RuntimeUtil.boardAnswerMemberInfo.getSelectMemberIndex() : 0;
            int displayCount = freeAnswer ? 1 : ms.length;
            double w = Integer.parseInt(cfgArr[19]) * 1.0 / displayCount;

            for (int i = 0; i < displayCount; i++) {
                int memberIndex = freeAnswer ? selectedMember : i;
                Model61RuntimeData.ModelData modelData = memberIndex < ms.length
                        ? dataMap.get(team.getFullName() + "-" + ms[memberIndex]) : null;
                StackPane stackPane2 = new StackPane();
                stackPane2.setMaxHeight(Double.MAX_VALUE);
                VBox.setVgrow(stackPane2, Priority.NEVER);
                stackPane2.setPrefSize(ScreenAdapter.calculateMainX(w),
                    ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[20])));

                Text l = new Text(freeAnswer ? team.getShortName() : (i + 1) + "");
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

                List<Integer> statusList = team.getStatusList();
                int memberStatus_i = (statusList != null && memberIndex < statusList.size())
                        ? statusList.get(memberIndex) : 0;
                if (memberStatus_i == 1) {
                    stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[26]),
                        new CornerRadii(10), Insets.EMPTY)));
                } else {
                    if (modelData == null || StringUtils.isEmpty(modelData.getUserSelect())) {
                        if (memberStatus_i == 3) {
                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[28]),
                                new CornerRadii(10), Insets.EMPTY)));
                        } else {
                            stackPane2.setBackground(new Background(new BackgroundFill(Color.valueOf(cfgArr[25]),
                                new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        if (modelData.getUserSelect().equals(modelData.getAnswer())) {
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
        BoardPane scoreBoard = controlPane.getScoreBoard();
        if (scoreBoard != null) {
            scoreBoard.close();
            scoreBoard = null;
            controlPane.setScoreBoard(null);
        }
    }

    @Override
    public void shows() {
        Stage stage = (Stage) container.getScene().getWindow();
        stage.show();
        BoardPane scoreBoard = controlPane.getScoreBoard();
        if (RuntimeUtil.showSecondScorePane) {
            if (scoreBoard == null) {
                scoreBoard = (ScoreBoardH) ExtendedScreen.scoreWithPos("/view/board/match/ScoreBoardH.fxml",
                    "", "/view/style.css", false);
                if (scoreBoard != null) {
                    controlPane.setScoreBoard(scoreBoard);
                    scoreBoard.reload();
                }
            }
        } else {
            if (scoreBoard != null) {
                scoreBoard.close();
                scoreBoard = null;
                controlPane.setScoreBoard(null);
            }
        }
    }

    @Override
    public void reload() {
        show();
    }

    @Override
    public Node getPane() {
        return container;
    }

    @Override
    public void selectAll(boolean select) {
        ObservableList<Node> lc = container.getChildren();
        for (Node node : lc) {
            if (!(node instanceof VBox)) {
                continue;
            }
            VBox box = (VBox) node;
            StackPane pane = (StackPane) box.getChildren().get(0);
            Text teamName = (Text) pane.getChildren().get(0);
            if (select) {
                controlPane.emptyTeam();
                box.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID,
                    new CornerRadii(10), new BorderWidths(5))));
                selected.add(teamName.getText());
            } else {
                box.setBorder(null);
                selected.remove(teamName.getText());
            }
        }
    }
}
