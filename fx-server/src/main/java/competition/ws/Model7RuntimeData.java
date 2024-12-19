package competition.ws;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.QuestionsMapper;
import competition.mapper.TeamsMapper;
import competition.service.ScoreService;
import competition.util.CurrentQuestions;
import competition.util.TeamRuntime;
import javafx.application.Platform;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 竞技模式实时更新数据,后端使用
public class Model7RuntimeData {
    // 实时回答状态
    private static List<List<ModelData>> runtimeData = new ArrayList<>();

    private static List<Teams> teams = new ArrayList<>();

    private static List<String> times = new ArrayList<>();

    private static List<String> fts = new ArrayList<>();

    private static List<Integer> seconds = new ArrayList<>();

    private static List<Boolean> finished = new ArrayList<>();

    public static List<Boolean> getFinished() {
        return finished;
    }

    public static List<String> getFts() {
        return fts;
    }

    public static List<String> getTimes() {
        return times;
    }

    public static List<Integer> getSeconds() {
        return seconds;
    }

    public static int totalNumber = 0;


    public static List<Teams> getTeams() {
        return teams;
    }

    public static void setTeams(List<Teams> teams) {
        runtimeData.clear();
        finished.clear();
        times.clear();
        seconds.clear();
        fts.clear();
        Model7RuntimeData.teams = teams;
        for (Teams team : teams) {
            runtimeData.add(new ArrayList<>());
            times.add("0分00秒");
            seconds.add(0);
            fts.add("0分00秒");
            finished.add(false);
        }
    }

    public static void setRuntimeData(int i, ModelData data, String time, String second) {
        if (finished.get(i)) {
            return;
        }
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        QuestionsMapper questionsMapper = JavaFxApplication.getApplicationContext().getBean(QuestionsMapper.class);
        ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
        //TeamsMapper teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamsMapper.class);
        times.set(i, time);
        seconds.set(i, Integer.parseInt(second));
        if (data == null) {
            return;
        }
        List<ModelData> modelData = runtimeData.get(i);
        boolean existed = false;
        for (int j = 0; j < modelData.size(); j++) {
            ModelData modelDatum = modelData.get(j);
            if (modelDatum.getQuestionNumber().equals(data.getQuestionNumber())) {
                existed = true;
                if (modelDatum.getUserSelect() == null) {
                    if (data.getAnswer().equals(data.getUserSelect())) {
                        modelData.set(j, data);
                        List<Teams> teams = TeamRuntime.getTeams();
                        for (Teams team : teams) {
                            if (team.getFullName().equals(data.getTeamName())) {
                                team.setTeamScore(team.getTeamScore() + data.getScore());
                                //teamsMapper.updateById(team);
                                // 需要更新环节获取分数
                                scoreService.addScore(null, team.getFullName(), controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(), null,
                                        data.getScore(), team.getTeamScore().toString(), data.getUserSelect());
                                Platform.runLater(() -> {
                                    controlPane.teamScore.setText(team.getTeamScore().toString());
                                    PreviewPane previewPane = controlPane.getPreviewPane();
                                    if (previewPane != null) {
                                        previewPane.reload();
                                    }
                                });

                            }
                        }
                    }
                }
                break;
            }
        }

        if (!existed) {
            modelData.add(data);
            if (data.getAnswer().equals(data.getUserSelect())) {
                List<Teams> teams = TeamRuntime.getTeams();
                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {
                        team.setTeamScore(team.getTeamScore() + data.getScore());
                        //teamsMapper.updateById(team);
                        scoreService.addScore(null, team.getFullName(), controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(), null, data.getScore(),
                                team.getTeamScore().toString(), data.getUserSelect());
                        Platform.runLater(() -> {
                            controlPane.teamScore.setText(team.getTeamScore().toString());
                            //controlPane.updateScoreBoard();
                            PreviewPane previewPane = controlPane.getPreviewPane();
                            if (previewPane != null) {
                                previewPane.reload();
                            }
                        });

                    }
                }
            } else {
                // 答错扣分
                List<Teams> teams = TeamRuntime.getTeams();
                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {
                        Integer desValue = controlPane.getCurrentStage().getDesValue();
                        team.setTeamScore(team.getTeamScore() - desValue);
                        //teamsMapper.updateById(team);
                        scoreService.addScore(null, team.getFullName(), controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(),
                                null, -desValue, team.getTeamScore().toString(), data.getUserSelect());

                        Platform.runLater(() -> {
                            //controlPane.updateScoreBoard();
                            PreviewPane previewPane = controlPane.getPreviewPane();
                            if (previewPane != null) {
                                previewPane.reload();
                            }
                        });

                    }
                }
            }
        }
        // 答题完成
        List<Questions> questions = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                .eq(Questions::getStageNumber, controlPane.getCurrentStage().getSortVal())
        );
        if (modelData.size() == questions.size()) {
            fts.set(i, times.get(i));
            finished.set(i, true);
        }
    }


    // 强制停止答题
    public static void forceFinish() {
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
        QuestionsMapper questionsMapper = JavaFxApplication.getApplicationContext().getBean(QuestionsMapper.class);
        TeamsMapper teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamsMapper.class);

        List<Questions> questionList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                .eq(Questions::getStageNumber, controlPane.getCurrentStage().getSortVal())
        );

        for (int i = 0; i < teams.size(); i++) {
            if (!finished.get(i)) {
                // 异步操作减少代码执行时间
                Teams teams = Model7RuntimeData.teams.get(i);
                List<ModelData> modelData = runtimeData.get(i);
                int size = modelData.size();
                teams.setTeamScore(teams.getTeamScore() - (totalNumber - size) * controlPane.getCurrentStage().getDesValue());

                fts.set(i, times.get(i));
                finished.set(i, true);
            }
        }
        new Thread(() -> {
            for (int i = 0; i < teams.size(); i++) {
                // 异步操作减少代码执行时间
                Teams teams = Model7RuntimeData.teams.get(i);
                List<ModelData> modelData = runtimeData.get(i);
                int size = modelData.size();
                teamsMapper.updateById(teams);
                // 均没回答
                if (size == 0) {
                    for (Questions questions : questionList) {
                        scoreService.addScore(null, teams.getFullName(), controlPane.getCurrentStage().getName(), questions.getQuestionNumber().toString(),
                                null, -controlPane.getCurrentStage().getDesValue(), teams.getTeamScore().toString(), "");
                    }
                } else {
                    // 回答一部分
                    List<Integer> total = questionList.stream().map(Questions::getQuestionNumber).collect(Collectors.toList());
                    List<Integer> answered = modelData.stream().map(ModelData::getQuestionNumber).collect(Collectors.toList());
                    total.removeAll(answered);
                    for (Integer integer : total) {
                        scoreService.addScore(null, teams.getFullName(), controlPane.getCurrentStage().getName(), integer.toString(), null,
                                -controlPane.getCurrentStage().getDesValue(), teams.getTeamScore().toString(), "");
                    }
                }

            }
        }).start();
    }

    public static void reset() {
        runtimeData.clear();
        finished.clear();
        times.clear();
        seconds.clear();
        fts.clear();
        for (Teams team : teams) {
            runtimeData.add(new ArrayList<>());
            times.add("0分00秒");
            seconds.add(0);
            fts.add("0分00秒");
            finished.add(false);
        }
    }


    public static List<List<ModelData>> getRuntimeData() {
        return runtimeData;
    }


    @Data
    public static class ModelData {

        // 队伍信息
        private String teamName;

        // 题目分值
        private Integer score;

        // 阶段
        private String stageNumber;

        private String stageName;

        private String questionType;

        private String resourceType;

        private String resource;

        private String question;

        private String choiceA;

        private String choiceB;

        private String choiceC;

        private String choiceD;

        private String choiceE;

        private String choiceF;

        private String answer;

        private String remark;

        private Integer questionNumber;

        private Integer sort;

        // 选手选项
        private String userSelect;

        // 选错扣分
        private Integer desValue;

    }

}
