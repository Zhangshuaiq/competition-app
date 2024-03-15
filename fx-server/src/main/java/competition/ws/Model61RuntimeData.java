package competition.ws;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
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
    
    public static void setRuntimeData(ModelData data) {
        Optional<ModelData> first = runtimeData.stream().filter(x -> x.getTeamName().equals(data.getTeamName()) && x.getUserName().equals(data.getUserName())).findFirst();
        if (first.isPresent()) {
            ModelData modelData = first.get();
            int i = runtimeData.indexOf(modelData);
            runtimeData.set(i, data);
            // 团队加分,同步到界面
            if (data.getUserSelect().equals(data.getAnswer())) {
                List<Teams> teams = TeamRuntime.getTeams();
                for (Teams team : teams) {
                    if (team.getFullName().equals(data.getTeamName())) {
                        team.setCurrentScore(team.getCurrentScore() + data.getScore());
                        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
                        Platform.runLater(() -> {
                            controlPane.teamScore.setText(team.getCurrentScore().toString());
                        });                        
                      
                        String members = team.getMembers();
                        if (!StringUtils.isEmpty(members)) {
                            // 给队员加分
                            String[] split = members.split(",");
                            for (int j = 0; j < split.length; j++) {
                                if (split[j].equals(data.getUserName())) {
                                    team.getMemberScores().set(j, data.getScore());
                                }
                            }
                        }
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
