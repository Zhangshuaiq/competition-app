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
public class Model61RuntimeData {
    // 实时回答状态
    private static List<ModelData> runtimeData = new ArrayList<>();
    
    private static List<Teams> teams = new ArrayList<>();

    public static List<Teams> getTeams() {
        return teams;
    }

    public static void setTeams(List<Teams> teams) {
        runtimeData.clear();
        Model61RuntimeData.teams = teams;
        for (Teams team : teams) {
            String members = team.getMembers();
            if (!StringUtils.isEmpty(members)) {
                String[] marr = members.split(",");
                for (String m : marr) {
                    ModelData md = new ModelData();
                    md.setTeamName(team.getFullName());
                    md.setUserName(m);
                    runtimeData.add(md);
                }
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
            datum.setSort(data.getSort());
            datum.setStageName(data.getStageName());
            datum.setStageNumber(data.getStageNumber());
            datum.setUserSelect("");
        }
    }
    
    public static void setRuntimeData(ModelData data) {
        Optional<ModelData> first = runtimeData.stream().filter(x -> x.getTeamName().equals(data.getTeamName()) && x.getUserName().equals(data.getUserName())).findFirst();
        if (first.isPresent()) {
            ModelData modelData = first.get();
            int i = runtimeData.indexOf(modelData);
            runtimeData.set(i, data);
            List<Teams> teams = TeamRuntime.getTeams();
            // 团队加teams = {ArrayList@11567}  size = 8分,同步到界面

                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {

                        String members = team.getMembers();
                        if (!StringUtils.isEmpty(members)) {
                            String[] split = members.split(",");
                            for (int j = 0; j < split.length; j++) {
                                if (split[j].equals(data.getUserName())) {
                                    if (data.getUserSelect().equals(data.getAnswer())) {
                                        // 更新队员获取的总分数
                                        Integer x = team.getMemberScores().get(j);
                                        team.getMemberScores().set(j, x + data.getScore());
                                        // 更新队员当前环节的分数
                                        team.getQScores().set(j, data.getScore());
                                    }
                                    // 设置为已经回答完成
                                    team.getStatusList().set(j, 1);
                                }
                            }
                        }

                        if (data.getUserSelect().equals(data.getAnswer())) {
                            // 更新队伍分数
                            team.setNewScore(team.getCurrentScore() + data.getScore());
                            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
                            Platform.runLater(() -> {
                                controlPane.teamScore.setText(team.getCurrentScore().toString());
                            });
                        }
                    }
            }
        }
    }
   

    public static List<ModelData> getRuntimeData() {
        return runtimeData;
    }
    

    @Data
    public static class ModelData {
        
        // 队伍信息
        private String teamName;
        
        // 选手信息
        private String userName;
        
        // 题目分值
        private Integer score;

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

    }

}
