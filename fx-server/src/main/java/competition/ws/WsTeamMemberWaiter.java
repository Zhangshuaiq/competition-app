package competition.ws;

import java.util.HashSet;
import java.util.Set;

public class WsTeamMemberWaiter {
    
    // 等待中的队伍
    public static Set<String> waitingMember = new HashSet<>();
    
    public static void addWaiter(String member) {
        waitingMember.add(member);
    }
}
