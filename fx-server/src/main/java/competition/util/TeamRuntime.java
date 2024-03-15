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
}
