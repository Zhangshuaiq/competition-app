package competition.ws;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.service.ScoreService;
import competition.service.TeamService;
import competition.util.NumberUtil;
import competition.util.TeamRuntime;
import competition.util.TeamMemberCodec;
import javafx.application.Platform;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// 答题模式-个人 实时更新数据,后端使用
public class Model61RuntimeData {

    // 已回答的所有状态
    public static List<List<ModelData>> totalData = new ArrayList<>();

    // 实时回答状态
    private static List<ModelData> runtimeData = new ArrayList<>();

    private static List<Teams> teams = new ArrayList<>();

    public static List<Teams> getTeams() {
        return teams;
    }

    public static void reset() {
        totalData.clear();
        runtimeData.clear();
        teams.clear();
    }

    public static void setTeams(List<Teams> teams) {

        setTeams(teams, null);
    }

    /**
     * 自由答题只创建被选团队中指定序号队员的运行时数据。未进入该集合的
     * 团队无法提交、不会被判未答，也不会参与本题计分和计时。
     */
    public static void setTeams(List<Teams> teams, Integer memberIndex) {

        // 保存已有的数据，用于计算排名
        // forceFinish();
        if (!runtimeData.isEmpty()) {
            runtimeData.clear();
        }
        // runtimeData.clear();
        Model61RuntimeData.teams = new ArrayList<>(teams);
        for (Teams team : Model61RuntimeData.teams) {
            String members = team.getMembers();
            if (!StringUtils.isEmpty(members)) {
                String[] marr = TeamMemberCodec.split(members);
                int from = memberIndex == null ? 0 : memberIndex;
                int to = memberIndex == null ? marr.length : Math.min(marr.length, memberIndex + 1);
                for (int i = from; i < to; i++) {
                    ModelData md = new ModelData();
                    md.setTeamName(team.getFullName());
                    md.setUserName(marr[i]);
                    runtimeData.add(md);
                }
            }
        }
    }

    // 更新计时
    public static void freshTime(int second) {
        for (ModelData datum : runtimeData) {
            if (StringUtils.isEmpty(datum.getUserSelect())) {
                datum.second = second;
            }
        }
    }

    public static void forceFinish() {
        if (!runtimeData.isEmpty()) {
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
            TeamService teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamService.class);
            for (int i = 0; i < runtimeData.size(); i++) {
                ModelData data = runtimeData.get(i);
                // 已提交的数据已在 setRuntimeData 中完成计分；这里只处理超时未答，
                // 避免停止答题时对已答选手重复累计分数和流水。
                if (!StringUtils.isEmpty(data.getUserSelect())) {
                    continue;
                }
                int unansweredScore = -(Objects.isNull(data.getDescScore())
                        ? controlPane.getCurrentStage().getDesValue() : data.getDescScore());
                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {
                        String members = team.getMembers();
                        if (!StringUtils.isEmpty(members)) {
                            String[] split = TeamMemberCodec.split(members);
                            for (int j = 0; j < split.length; j++) {
                                if (split[j].equals(data.getUserName())) {
                                    String memberScore = team.getMemberScore();
                                    JSONObject jsonObject = JSON.parseObject(memberScore);
                                    jsonObject.put(data.getUserName(),
                                            jsonObject.getIntValue(data.getUserName()) + unansweredScore);
                                    team.setMemberScore(JSON.toJSONString(jsonObject));
                                    if (team.getQScores() != null && j < team.getQScores().size()) {
                                        team.getQScores().set(j, unansweredScore);
                                    }
                                    teamsMapper.updateTeamAsync(team);

                                }
                            }
                        }

                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            int scores = team.getQScores() != null
                                    ? team.getQScores().stream().mapToInt(x -> x).sum() : 0;
                            team.setNewScore(team.getTeamScore() + scores);

                            scoreService.addScore(data.getUserName(), team, controlPane.getCurrentStage().getName(),
                                    data.getQuestionNumber().toString(),
                                    data.getSecond().toString(), unansweredScore, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        } else {
                            team.setNewScore(team.getTeamScore());
                            scoreService.addScore(data.getUserName(), team, controlPane.getCurrentStage().getName(),
                                    data.getQuestionNumber().toString(),
                                    data.getSecond().toString(), 0, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        }
                        Platform.runLater(() -> {
                            controlPane.teamScore.setText(team.getTeamScore().toString());
                        });
                    }
                }
            }

            String stageNumber = runtimeData.get(0).getStageNumber();
            if (!StringUtils.isEmpty(stageNumber)) {
                totalData.add(new ArrayList<>(runtimeData));
            }

        }
    }

    public static void setRuntimeData(Questions data) {
        for (ModelData datum : runtimeData) {
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
                datum.setDescScore(null); // null 表示使用环节配置的扣分值
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
        Optional<ModelData> first = runtimeData.stream()
                .filter(x -> x.getTeamName().equals(data.getTeamName()) && x.getUserName().equals(data.getUserName()))
                .findFirst();
        if (first.isPresent()) {
            ModelData modelData = first.get();
            int i = runtimeData.indexOf(modelData);
            runtimeData.set(i, data);
            List<Teams> teams = TeamRuntime.getTeams();
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
            TeamService teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamService.class);
            for (Teams team : teams) {
                if (team.getFullName().equals(data.getTeamName())) {
                    JSONObject jsonObject = JSON.parseObject(team.getMemberScore());
                    String members = team.getMembers();
                    if (!StringUtils.isEmpty(members)) {
                        String[] split = TeamMemberCodec.split(members);
                        for (int j = 0; j < split.length; j++) {
                            if (split[j].equals(data.getUserName())) {
                                if (data.getUserSelect().equals(data.getAnswer())) {
                                    // 更新队员获取的总分数
                                    Integer x = jsonObject.getIntValue(data.getUserName());
                                    jsonObject.put(data.getUserName(), x + data.getScore());
                                    team.setMemberScore(JSON.toJSONString(jsonObject));
                                    teamsMapper.updateTeamAsync(team);
                                    // 更新队员当前环节的分数
                                    team.getQScores().set(j, data.getScore());
                                } else {
                                    int desScore = Objects.isNull(data.getDescScore())
                                            ? controlPane.getCurrentStage().getDesValue()
                                            : data.getDescScore();
                                    jsonObject.put(data.getUserName(),
                                            jsonObject.getIntValue(data.getUserName()) - desScore);
                                    team.setMemberScore(JSON.toJSONString(jsonObject));
                                    teamsMapper.updateTeamAsync(team);
                                    team.getQScores().set(j, -desScore);
                                }
                                // 设置为已经回答完成
                                team.getStatusList().set(j, 1);
                            }
                        }
                    }

                    if (data.getUserSelect().equals(data.getAnswer())) {
                        // 更新队伍分数
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            team.setNewScore(team.getTeamScore() + data.getScore());
                            scoreService.addScore(data.getUserName(), team, controlPane.getCurrentStage().getName(),
                                    data.getQuestionNumber().toString(),
                                    data.getSecond().toString(), data.getScore(), Objects.toString(team.getNewScore()),
                                    data.getUserSelect());

                        } else {
                            team.setNewScore(team.getTeamScore());
                            scoreService.addScore(data.getUserName(), team, controlPane.getCurrentStage().getName(),
                                    data.getQuestionNumber().toString(),
                                    data.getSecond().toString(), 0, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        }
                        Platform.runLater(() -> {
                            controlPane.teamScore.setText(team.getTeamScore().toString());
                        });
                    } else {
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            System.out.println("扣分" + data);
                            int desScore = Objects.isNull(data.getDescScore())
                                    ? controlPane.getCurrentStage().getDesValue()
                                    : data.getDescScore();

                            team.setNewScore(team.getTeamScore() - desScore);

                            scoreService.addScore(data.getUserName(), team, controlPane.getCurrentStage().getName(),
                                    data.getQuestionNumber().toString(),
                                    data.getSecond().toString(), -desScore, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        } else {
                            team.setNewScore(team.getTeamScore());
                            scoreService.addScore(data.getUserName(), team, controlPane.getCurrentStage().getName(),
                                    data.getQuestionNumber().toString(),
                                    data.getSecond().toString(), 0, Objects.toString(team.getNewScore()),
                                    data.getUserSelect());
                        }
                    }
                }
            }
        }
    }

    public static boolean accepts(String teamName, String userName) {
        return runtimeData.stream().anyMatch(item -> Objects.equals(item.getTeamName(), teamName)
                && Objects.equals(item.getUserName(), userName));
    }

    public static void updateParticipantStatuses() {
        for (ModelData data : runtimeData) {
            for (Teams team : teams) {
                if (!Objects.equals(team.getFullName(), data.getTeamName())) {
                    continue;
                }
                String[] members = TeamMemberCodec.split(team.getMembers());
                for (int i = 0; i < members.length; i++) {
                    if (Objects.equals(members[i], data.getUserName())
                            && team.getStatusList() != null && i < team.getStatusList().size()) {
                        team.getStatusList().set(i,
                                Objects.equals(data.getAnswer(), data.getUserSelect()) ? 2 : 3);
                        break;
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

    @Data
    public static class ModelData {

        // 队伍信息
        private String teamName;

        // 选手信息
        private String userName;

        // 题目分值
        private Integer score = 0;

        // 答错扣分
        private Integer descScore = 0;

        // 显示答题结果
        private boolean show;

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

        // 回答时间
        private Integer second = 0;

    }

}
