package competition.ws;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.service.ScoreService;
import competition.util.NumberUtil;
import competition.util.TeamRuntime;
import javafx.application.Platform;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// 答题模式-团队 实时更新数据,后端使用
public class Model60RuntimeData {

    // 已回答的所有状态
    public static List<List<ModelData>> totalData = new ArrayList<>();

    // 实时回答状态
    private static List<ModelData> runtimeData = new ArrayList<>();

    private static List<Teams> teams = new ArrayList<>();

    public static List<Teams> getTeams() {
        return teams;
    }

    public static void setTeams(List<Teams> teams) {
        // 保存已有的数据，用于计算排名
        // if (!runtimeData.isEmpty()) {
        // totalData.add(runtimeData);
        // }
        // forceFinish();
        runtimeData.clear();
        Model60RuntimeData.teams = teams;
        for (Teams team : teams) {
            ModelData md = new ModelData();
            md.setTeamName(team.getFullName());
            runtimeData.add(md);
        }
    }

    public static void reset() {
        totalData.clear();
        runtimeData.clear();
        teams.clear();
    }

    // 更新计时
    public static void freshTime(int second) {
        for (Model60RuntimeData.ModelData datum : runtimeData) {
            if (StringUtils.isEmpty(datum.getUserSelect())) {
                datum.second = second;
            }
        }
    }

    public static void setRuntimeData(Questions data) {
        for (Model60RuntimeData.ModelData datum : runtimeData) {
            datum.setAnswer(data.getAnswer());
            datum.setChoiceA(data.getChoiceA());
            datum.setChoiceB(data.getChoiceB());
            datum.setChoiceC(data.getChoiceC());
            datum.setChoiceD(data.getChoiceD());
            datum.setChoiceE(data.getChoiceE());
            datum.setChoiceF(data.getChoiceF());
            datum.setQuestion(data.getQuestion());
            datum.setQuestionNumber(data.getQuestionNumber());
            datum.setQuestionType(data.getQuestionType());
            datum.setResourceType(data.getResourceType());
            datum.setResource(data.getResource());
            datum.setRemark(data.getRemark());
            datum.setScore(data.getScore());
            if (StringUtils.isEmpty(data.getDescScore()) || !NumberUtil.isInt(data.getDescScore())) {

            } else {
                datum.setDescScore(Integer.parseInt(data.getDescScore()));
            }
            datum.setSort(data.getSort());
            datum.setStageName(data.getStageName());
            datum.setStageNumber(data.getStageNumber());
            datum.setUserSelect("");
        }
    }

    public static void setRuntimeData(ModelData data) {
        System.out.println("收到答题数据：" + data);
        Optional<ModelData> first = runtimeData.stream().filter(x -> x.getTeamName().equals(data.getTeamName()))
                .findFirst();
        if (first.isPresent()) {
            ModelData modelData = first.get();
            int i = runtimeData.indexOf(modelData);
            runtimeData.set(i, data);
            // 团队加分,同步到界面
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
            List<Teams> teams = TeamRuntime.getTeams();
            for (Teams team : teams) {
                if (team.getFullName().equals(data.getTeamName())) {
                    team.setStatus(1);
                    if (data.getUserSelect().equals(data.getAnswer())) {

                        // 需要判断是否计入总分
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            team.setNewScore(team.getTeamScore() + data.getScore());

                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(),
                                    modelData.getQuestionNumber().toString(),
                                    modelData.getSecond().toString(), data.getScore(),
                                    Objects.toString(team.getNewScore()), data.getUserSelect());
                        } else {
                            team.setNewScore(team.getTeamScore());
                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(),
                                    modelData.getQuestionNumber().toString(),
                                    modelData.getSecond().toString(), 0, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        }

                        team.setQScore(data.getScore());

                        Platform.runLater(() -> {
                            controlPane.teamScore.setText(team.getTeamScore().toString());
                        });
                    } else {
                        team.setQScore(-data.getScore());
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {

                            int desScore = Objects.isNull(data.getDescScore())
                                    ? controlPane.getCurrentStage().getDesValue()
                                    : data.getDescScore();

                            team.setNewScore(team.getTeamScore() - desScore);

                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(),
                                    modelData.getQuestionNumber().toString(),
                                    modelData.getSecond().toString(), -desScore, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        } else {
                            team.setNewScore(team.getTeamScore());
                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(),
                                    modelData.getQuestionNumber().toString(),
                                    modelData.getSecond().toString(), 0, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        }
                    }
                }
            }
        }
    }

    public static List<ModelData> getRuntimeData() {
        return runtimeData;
    }

    public static List<List<ModelData>> getTotalData() {
        return totalData;
    }

    public static void forceFinish() {
        if (!runtimeData.isEmpty()) {
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
            for (int i = 0; i < runtimeData.size(); i++) {
                Teams team = TeamRuntime.getTeams().get(i);
                ModelData modelData = runtimeData.get(i);
                if (StringUtils.isEmpty(modelData.getUserSelect())) {
                    if (controlPane.getCurrentStage().getCountTotal() == 1) {

                        int desScore = Objects.isNull(modelData.getDescScore())
                                ? controlPane.getCurrentStage().getDesValue()
                                : modelData.getDescScore();
                        team.setNewScore(team.getTeamScore() - desScore);

                        scoreService.addScore(null, team, controlPane.getCurrentStage().getName(),
                                modelData.getQuestionNumber().toString(),
                                modelData.getSecond().toString(), -desScore, Objects.toString(team.getNewScore()),
                                modelData.getUserSelect());
                    } else {
                        scoreService.addScore(null, team, controlPane.getCurrentStage().getName(),
                                modelData.getQuestionNumber().toString(),
                                modelData.getSecond().toString(), 0, Objects.toString(team.getNewScore()),
                                modelData.getUserSelect());
                    }
                }
            }
            String stageNumber = runtimeData.get(0).getStageNumber();
            if (!StringUtils.isEmpty(stageNumber)) {
                totalData.add(new ArrayList<>(runtimeData));
            }
        }
        // runtimeData.clear();
    }

    @Data
    public static class ModelData {

        // 队伍信息
        private String teamName;

        // 题目分值
        private Integer score = 0;

        // 罚分分值
        private Integer descScore = 0;

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

        // 用时
        private Integer second = 0;

    }

}
