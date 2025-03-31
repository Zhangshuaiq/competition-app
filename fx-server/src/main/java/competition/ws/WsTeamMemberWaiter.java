package competition.ws;

import java.util.HashSet;
import java.util.Set;

public class WsTeamMemberWaiter {
    
    // 连接中的队伍
    public static Set<String> connectTeams = new HashSet<>();
    
    public static void addConnectedTeam(String member) {
        connectTeams.add(member);
    }
}
