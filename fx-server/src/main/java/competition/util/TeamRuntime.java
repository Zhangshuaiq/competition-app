package competition.util;

import competition.JavaFxApplication;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamRuntime {

    private static List<Teams> teams;

    public static void setTeams(List<Teams> teams) {
        TeamRuntime.teams = teams;
    }

    public static Map<String, String> getSeqMap(){
        return teams.stream().collect(Collectors.toMap(Teams::getShortName, Teams::getCode));
    }

    public static Map<String, List<String>> getTeamMemberMapping(){
        return teams.stream().collect(Collectors.toMap(Teams::getShortName, x -> Stream.of(x.getMembers().split(",")).collect(Collectors.toList())));
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
            team.setNewScore(0);
        }
    }

    public static void updateScore(){

        TeamsMapper teamsMapper = JavaFxApplication.getApplicationContext().getBean(TeamsMapper.class);

        for (Teams team : teams) {
            if (team.getNewScore() != 0) {
                team.setTeamScore(team.getNewScore());
                team.setNewScore(0);
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

            if (team.getStatusList() != null) {
                team.getStatusList().replaceAll(ignored -> 0);
            }

            if (team.getQScores() != null) {
                team.getQScores().replaceAll(ignored -> 0);
            }
        }
    }
}
