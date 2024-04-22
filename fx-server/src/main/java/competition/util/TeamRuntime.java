package competition.util;

import competition.entity.Teams;

import java.util.List;

public class TeamRuntime {

    private static List<Teams> teams;

    public static void setTeams(List<Teams> teams) {
        TeamRuntime.teams = teams;
    }


    public static List<Teams> getTeams() {
        return teams;
    }

    public static void updateScore(){
        for (Teams team : teams) {
            if (team.getNewScore() != null) {
                team.setCurrentScore(team.getNewScore());
            }
        }
    }

    public static void updateMS(){
        for (Teams team : teams) {
            List<Integer> qScores = team.getQScores();
            for (int i = 0; i < qScores.size(); i++) {
                if (qScores.get(i) == 0) {
                    team.getStatusList().set(i, 0);
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
            if (team.getCurrentScore() < team.getNewScore()) {
                team.setStatus(2);
            } else  if (team.getCurrentScore() > team.getNewScore()) {
                team.setStatus(3);
            } else {
                team.setStatus(0);
            }
        }
    }

    public static void resetStatus(){
        for (Teams team : teams) {
            team.setStatus(0);
            for (int i = 0; i < team.getStatusList().size(); i++) {
                team.getStatusList().set(i, 0);
                team.getQScores().set(i, 0);
            }
        }
    }
}
