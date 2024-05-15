package competition.ws;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.util.TeamRuntime;
import javafx.application.Platform;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 答题模式-个人 实时更新数据,后端使用
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
//        if (!runtimeData.isEmpty()) {
//            totalData.add(runtimeData);
//        }
        forceFinish();
        runtimeData.clear();
        Model60RuntimeData.teams = teams;
        for (Teams team : teams) {
            ModelData md = new ModelData();
            md.setTeamName(team.getFullName());
            runtimeData.add(md);
        }
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
            datum.setSort(data.getSort());
            datum.setStageName(data.getStageName());
            datum.setStageNumber(data.getStageNumber());
            datum.setUserSelect("");
        }
    }

    public static void setRuntimeData(ModelData data) {
        Optional<ModelData> first = runtimeData.stream().filter(x -> x.getTeamName().equals(data.getTeamName())).findFirst();
        if (first.isPresent()) {
            ModelData modelData = first.get();
            int i = runtimeData.indexOf(modelData);
            runtimeData.set(i, data);
            // 团队加分,同步到界面

            List<Teams> teams = TeamRuntime.getTeams();
            for (Teams team : teams) {
                if (team.getFullName().equals(data.getTeamName())) {
                    team.setStatus(1);
                    ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);

                    if (data.getUserSelect().equals(data.getAnswer())) {

                        // 需要判断是否计入总分
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            team.setNewScore(team.getCurrentScore() + data.getScore());
                        } else {
                            team.setNewScore(team.getCurrentScore());
                        }
                        
                        team.setQScore(data.getScore());
                        
                        Platform.runLater(() -> {
                            controlPane.teamScore.setText(team.getCurrentScore().toString());
                        });
                    } else {
                        team.setQScore(-data.getScore());
                        if (controlPane.getCurrentStage().getCountTotal() == 1) {
                            team.setNewScore(team.getCurrentScore() - controlPane.getCurrentStage().getDesValue());
                        } else {
                            team.setNewScore(team.getCurrentScore());
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

    public static void forceFinish(){
        if (!runtimeData.isEmpty()) {
            String stageNumber = runtimeData.get(0).getStageNumber();
            if (!StringUtils.isEmpty(stageNumber)) {
                totalData.add(new ArrayList<>(runtimeData));
            }
        }
        //runtimeData.clear();
    }

    @Data
    public static class ModelData {

        // 队伍信息
        private String teamName;

        // 题目分值
        private Integer score = 0;

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
