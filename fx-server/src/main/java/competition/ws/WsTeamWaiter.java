package competition.ws;

import java.util.HashSet;
import java.util.Set;

public class WsTeamWaiter {
    
    // 等待中的队伍
    public static Set<String> waitingTeams = new HashSet<>();
    
    public static void addWaiter(String team) {
        waitingTeams.add(team);
    }
}
