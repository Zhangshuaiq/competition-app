package competition.ws;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.preview.Model7Preview;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.QuestionsMapper;
import competition.mapper.TeamsMapper;
import competition.modal.Model7RankData;
import competition.service.ScoreService;
import competition.util.Information;
import competition.util.NumberUtil;
import competition.util.TeamRuntime;
import javafx.application.Platform;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

// 竞技模式实时更新数据,后端使用
@Slf4j
public class Model7RuntimeData {

    public static volatile boolean executeStart = false; // 是否从 0 计时
    // 实时回答状态
    private static List<List<ModelData>> runtimeData = new ArrayList<>();

    private static List<Teams> teams = new ArrayList<>();

    private static List<String> times = new ArrayList<>();

    private static List<String> fts = new ArrayList<>();

    private static List<Integer> seconds = new ArrayList<>();

    private static List<Boolean> finished = new ArrayList<>();

    public static int totalNumber = 0;

    // UI更新回调接口
    public interface UIUpdateCallback {
        void onDataUpdated(String teamName, int teamIndex);
    }

    private static UIUpdateCallback uiUpdateCallback;

    public static void setUIUpdateCallback(UIUpdateCallback callback) {
        uiUpdateCallback = callback;
    }

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
            times.add("00:00");
            seconds.add(0);
            fts.add("00:00");
            finished.add(false);
        }
    }

    public static void exportResult(){
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        List<String> timeData = Model7RuntimeData.getFts();
        List<Model7RankData> rankList = new ArrayList<>();

        Model7Preview model7Preview = JavaFxApplication.getApplicationContext().getBean(Model7Preview.class);

        List<Object> values = (List<Object>) model7Preview.getQuestionCache().get("questions");

        List<Questions> model7QuestionList = values.stream().map(obj -> (Questions) obj).collect(Collectors.toList());

        Map<Integer, String> desMap = model7QuestionList.stream().collect(Collectors.toMap(Questions::getQuestionNumber, q -> q.getDescScore() != null ? q.getDescScore() : ""));

        for (int i = 0; i < runtimeData.size(); i++) {
            List<Model7RuntimeData.ModelData> questionList = runtimeData.get(i);
            int score = 0;
            for (Model7RuntimeData.ModelData data : questionList) {
                if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                    score += data.getScore();
                } else {
                    score -= data.getDesValue();
                }
            }
            // 扣除未答题分数[需要知道到底是哪些题没有回答]
            Integer unAnsweredValue = 0;
            List<Integer> answeredQuestion = questionList.stream().map(ModelData::getQuestionNumber).collect(Collectors.toList());

            for (Entry<Integer,String> entrySet : desMap.entrySet()) {
                int num = entrySet.getKey();
                String val = entrySet.getValue();
                if (!answeredQuestion.contains(num) && NumberUtil.isInt(val)) {
                    unAnsweredValue += Integer.parseInt(val);
                } else if (!answeredQuestion.contains(num)) {
                    unAnsweredValue += controlPane.getCurrentStage().getDesValue();
                }
            }
            
            
            score = score - unAnsweredValue;
            String team = Model7RuntimeData.getTeams().get(i).getFullName();

            String time = timeData.get(i);

            Model7RankData rankData = new Model7RankData();
            rankData.setScore(score);
            rankData.setSecond(seconds.get(i));
            rankData.setTeam(team);
            rankData.setTime(time);
            rankList.add(rankData);
        }

        Platform.runLater(() -> {
            try {
                // 设置默认文件名
                String defaultFileName = "竞技答题结果_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                        ".xlsx";

                File file = new File(System.getProperty("user.dir") +  File.separator + defaultFileName);
                // 使用 EasyExcel 导出
                EasyExcel.write(file, Model7RankData.class)
                        .sheet("竞技答题结果")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                        .doWrite(rankList);
            } catch (Exception e) {
                e.printStackTrace();
                Information.error("导出失败: " + e.getMessage());
            }
        });

    }

    public static void setRuntimeData(int i, ModelData data, String time, String second) {
        if (finished.get(i)) {
            return;
        }

        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        QuestionsMapper questionsMapper = JavaFxApplication.getApplicationContext().getBean(QuestionsMapper.class);
        ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);

        times.set(i, time);
        seconds.set(i, Integer.parseInt(second));

        if (data == null) {
            return;
        }

        List<ModelData> modelData = runtimeData.get(i);
        boolean existed = false;
        boolean dataChanged = false;

        for (int j = 0; j < modelData.size(); j++) {
            ModelData modelDatum = modelData.get(j);
            if (modelDatum.getQuestionNumber().equals(data.getQuestionNumber())) {
                existed = true;
                if (modelDatum.getUserSelect() == null) {
                    if (data.getAnswer().equals(data.getUserSelect())) {
                        modelData.set(j, data);
                        dataChanged = true;

                        List<Teams> teams = TeamRuntime.getTeams();
                        for (Teams team : teams) {
                            if (team.getFullName().equals(data.getTeamName())) {
                                if (controlPane.getCurrentStage().getCountTotal() == 1) {
                                    team.setTeamScore(team.getTeamScore() + data.getScore());
                                    // 需要更新环节获取分数
                                    scoreService.addScore(null, team, controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(), null,
                                        data.getScore(), team.getTeamScore().toString(), data.getUserSelect());
                                } else {
                                    scoreService.addScore(null, team, controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(), null,
                                            0, team.getTeamScore().toString(), data.getUserSelect());
                                }
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }

        if (!existed) {
            modelData.add(data);
            dataChanged = true;

            if (data.getAnswer().equals(data.getUserSelect())) {
                List<Teams> teams = TeamRuntime.getTeams();
                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            team.setTeamScore(team.getTeamScore() + data.getScore());
                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(), null, data.getScore(),
                                team.getTeamScore().toString(), data.getUserSelect());
                        } else {
                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(), null, 0,
                                    team.getTeamScore().toString(), data.getUserSelect()); 
                        }
                        break;
                    }
                }
            } else {
                // 答错扣分
                List<Teams> teams = TeamRuntime.getTeams();
                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            Integer desValue = data.getDesValue();
                            team.setTeamScore(team.getTeamScore() - desValue);
                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(),
                                null, -desValue, team.getTeamScore().toString(), data.getUserSelect());
                        } else {
                            scoreService.addScore(null, team, controlPane.getCurrentStage().getName(), data.getQuestionNumber().toString(),
                                    null, 0, team.getTeamScore().toString(), data.getUserSelect());
                        }
                        break;
                    }
                }
            }
        }

        // 检查答题完成状态
        List<Questions> questions = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
            .eq(Questions::getStageNumber, controlPane.getCurrentStage().getSortVal())
        );

        boolean teamJustFinished = false;
        if (modelData.size() == questions.size()) {
            fts.set(i, times.get(i));
            finished.set(i, true);
            dataChanged = true;
            teamJustFinished = true;
            log.info("团队 {} 答题完成", data.getTeamName());
        }

        // 通过回调通知UI更新
        if (dataChanged && uiUpdateCallback != null) {
            uiUpdateCallback.onDataUpdated(data.getTeamName(), i);
        }

        // 如果有团队刚完成答题，检查是否所有团队都完成了
        if (teamJustFinished) {
            checkAllTeamsFinished();
        }
    }

    /**
     * 检查是否所有团队都已完成答题
     */
    private static void checkAllTeamsFinished() {
        boolean allFinished = true;
        for (Boolean teamFinished : finished) {
            if (!teamFinished) {
                allFinished = false;
                break;
            }
        }

        if (allFinished) {
            log.info("所有团队答题完成！");
            // 通知WsMessageHandler停止定时器
            try {
                WsMessageHandler wsMessageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
                wsMessageHandler.stopPeriodicUIUpdate();
            } catch (Exception e) {
                log.error("通知停止UI更新失败: {}", e.getMessage());
            }
        }
    }


    // 强制停止答题
    public static void forceFinish() {
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        ScoreService scoreService = JavaFxApplication.getApplicationContext().getBean(ScoreService.class);
        TeamsMapper teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamsMapper.class);
        
        Model7Preview model7Preview = JavaFxApplication.getApplicationContext().getBean(Model7Preview.class);

        List<Object> values = (List<Object>) model7Preview.getQuestionCache().get("questions");

        List<Questions> questionList = values.stream().map(obj -> (Questions) obj).collect(Collectors.toList());
        Map<Integer, String> desMap = questionList.stream().collect(Collectors.toMap(Questions::getQuestionNumber, q -> q.getDescScore() != null ? q.getDescScore() : ""));

        boolean dataChanged = false;
        for (int i = 0; i < teams.size(); i++) {
            if (!finished.get(i)) {
                // 异步操作减少代码执行时间
                Teams teams = Model7RuntimeData.teams.get(i);
                List<ModelData> modelData = runtimeData.get(i);
                if (controlPane.getCurrentStage().getCountTotal() == 1) {
                    Integer unAnsweredValue = 0;
                    List<Integer> answeredQuestion = modelData.stream().map(ModelData::getQuestionNumber).collect(Collectors.toList());

                    for (Entry<Integer,String> entrySet : desMap.entrySet()) {
                        int num = entrySet.getKey();
                        String val = entrySet.getValue();
                        if (!answeredQuestion.contains(num) && NumberUtil.isInt(val)) {
                            unAnsweredValue += Integer.parseInt(val);
                        } else if (!answeredQuestion.contains(num)) {
                            unAnsweredValue += controlPane.getCurrentStage().getDesValue();
                        }
                    }
                    teams.setTeamScore(teams.getTeamScore() - unAnsweredValue);
                }
                fts.set(i, times.get(i));
                finished.set(i, true);
                dataChanged = true;
            }
        }

        // 通知UI更新
        if (dataChanged && uiUpdateCallback != null) {
            uiUpdateCallback.onDataUpdated("", -1); // 全局更新
        }

        // 强制结束后，通知停止定时器
        try {
            WsMessageHandler wsMessageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
            wsMessageHandler.stopPeriodicUIUpdate();
           log.info("强制结束答题，已停止UI定时更新");
        } catch (Exception e) {
            log.error("通知停止UI更新失败: {}", e.getMessage());
        }

        // 优化，从缓存读数据，防止数据库加载慢

        
        // 防止空指针出现
        if (CollectionUtils.isEmpty(questionList)) {
            return;
        }
        new Thread(() -> {
            try {
                int desValue = controlPane.getCurrentStage().getDesValue();
                for (int i = 0; i < teams.size(); i++) {
                    // 异步操作减少代码执行时间
                    Teams teams = Model7RuntimeData.teams.get(i);
                    List<ModelData> modelData = runtimeData.get(i);
                    int size = modelData.size();
                    teamsMapper.updateById(teams);
                    // 均没回答
                    if (size == 0) {
                        for (Questions questions : questionList) {
                            if (NumberUtil.isInt(questions.getDescScore())) {
                                desValue = Integer.parseInt(questions.getDescScore());
                            }
                            scoreService.addScore(null, teams, controlPane.getCurrentStage().getName(), questions.getQuestionNumber().toString(),
                                null, -desValue, teams.getTeamScore().toString(), "");
                        }
                    } else {
                        // 回答一部分
                        List<Integer> total = questionList.stream().map(Questions::getQuestionNumber).collect(Collectors.toList());
                        List<Integer> answered = modelData.stream().map(ModelData::getQuestionNumber).collect(Collectors.toList());
                        Map<Integer,String> collect = questionList.stream().collect(Collectors.toMap(Questions::getQuestionNumber, Questions::getDescScore));
                        total.removeAll(answered);
                        for (Integer integer : total) {
                            if (NumberUtil.isInt(collect.get(integer))) {
                                desValue = Integer.parseInt(collect.get(integer));
                            }
                            scoreService.addScore(null, teams, controlPane.getCurrentStage().getName(), integer.toString(), null,
                                -desValue, teams.getTeamScore().toString(), "");
                        }
                    }
                }
                exportResult();
            } catch (Exception ex) {
                log.error("强制结束答题时记录分数发生错误: {}", ex.getMessage());
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
            times.add("00:00");
            seconds.add(0);
            fts.add("00:00");
            finished.add(false);
        }

        // 通知UI更新
        if (uiUpdateCallback != null) {
            uiUpdateCallback.onDataUpdated("", -1); // 全局更新
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