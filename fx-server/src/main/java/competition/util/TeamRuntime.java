package competition.util;

import competition.JavaFxApplication;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TeamRuntime {

    private static List<Teams> teams;

    public static void setTeams(List<Teams> teams) {
        TeamRuntime.teams = teams;
    }


    public static List<Teams> getTeams() {
        return teams;
    }


    public static void resetBuzzStatus(){
        for (Teams team : teams) {
            team.setBuzzStatus(0);
        }
    }


    public static Teams getByCode(String code){
        if (teams == null || CollectionUtils.isEmpty(teams)) {
            return null;
        }
        List<Teams> collect = teams.stream().filter(x -> x.getCode().equals(code)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }
        return collect.get(0);
    }

    public static Teams getByShortName(String shortName){
        if (teams == null || CollectionUtils.isEmpty(teams)) {
            return null;
        }
        List<Teams> collect = teams.stream().filter(x -> x.getShortName().equals(shortName)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }
        return collect.get(0);
    }


    public static void resetScore(){
        for (Teams team : teams) {
            team.setNewScore(null);
        }
    }

    public static void updateScore(){

        TeamsMapper teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamsMapper.class);

        for (Teams team : teams) {
            if (team.getNewScore() != null) {
                team.setTeamScore(team.getNewScore());
                team.setNewScore(null);
                teamsMapper.updateById(team);
            }
        }
    }

    public static void updateMS(){
        for (Teams team : teams) {
            List<Integer> qScores = team.getQScores();
            for (int i = 0; i < qScores.size(); i++) {
                if (qScores.get(i) == 0) {
                    team.getStatusList().set(i, 3);
                } else if (qScores.get(i) > 0) {
                    team.getStatusList().set(i, 2);
                } else {
                    team.getStatusList().set(i, 3);
                }
            }
        }
    }


    public static void updateS(){
        for (Teams team : teams) {
            if (team.getQScore() == 0) {
                team.setStatus(3);
            } else if (team.getQScore() > 0) {
                team.setStatus(2);
            } else  if (team.getQScore() < 0) { 
                team.setStatus(3);
            } 
        }
    }

    public static void resetStatus(){
        for (Teams team : teams) {
            team.setStatus(0);
            team.setQScore(0);
            for (int i = 0; i < team.getStatusList().size(); i++) {
                team.getStatusList().set(i, 0);
                team.getQScores().set(i, 0);
            }
        }
    }
}
