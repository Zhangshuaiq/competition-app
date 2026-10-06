package competition.ws;

import competition.entity.Teams;
import competition.util.TeamRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static competition.ws.WsTeamMemberWaiter.ConnectionStatus.*;

class WsTeamMemberWaiterTest {
    @BeforeEach
    void setUp() {
        TeamRuntime.setTeams(Collections.emptyList());
        WsTeamMemberWaiter.initData();
        Teams team = new Teams();
        team.setShortName("一组");
        team.setMembers("[\"队员1\",\"队员2\",\"队员3\"]");
        TeamRuntime.setTeams(Collections.singletonList(team));
        WsTeamMemberWaiter.initData();
    }

    @AfterEach
    void reset() {
        TeamRuntime.setTeams(Collections.emptyList());
        WsTeamMemberWaiter.initData();
    }

    @Test
    void partialAndFullOnlineStatesDoNotDependOnLoginOrder() {
        assertEquals(OFFLINE, WsTeamMemberWaiter.getConnectionStatus("一组"));
        WsTeamMemberWaiter.addConnectedTeam("s2", "一组-队员2");
        assertEquals(PARTIAL, WsTeamMemberWaiter.getConnectionStatus("一组"));
        WsTeamMemberWaiter.addConnectedTeam("s3", "一组-队员3");
        WsTeamMemberWaiter.addConnectedTeam("s1", "一组-队员1");
        assertEquals(ONLINE, WsTeamMemberWaiter.getConnectionStatus("一组"));
        WsTeamMemberWaiter.removeConnectedTeam("s1");
        assertEquals(PARTIAL, WsTeamMemberWaiter.getConnectionStatus("一组"));
    }

    @Test
    void repeatedRegistrationDoesNotCountAsMoreOnlineMembers() {
        WsTeamMemberWaiter.addConnectedTeam("s1", "一组-队员1");
        WsTeamMemberWaiter.addConnectedTeam("s1", "一组-队员1");
        WsTeamMemberWaiter.addConnectedTeam("s2", "一组-队员1");
        WsTeamMemberWaiter.addConnectedTeam("s3", "一组-队员1");
        assertEquals(3, WsTeamMemberWaiter.getConnectTeams().get("一组").size());
        assertEquals(PARTIAL, WsTeamMemberWaiter.getConnectionStatus("一组"));
    }

    @Test
    void disconnectingOldSessionAfterReconnectKeepsMemberOnline() {
        WsTeamMemberWaiter.addConnectedTeam("old", "一组-队员2");
        WsTeamMemberWaiter.addReconnectedTeam("new", "一组-队员2");
        WsTeamMemberWaiter.removeConnectedTeam("old");
        assertEquals(Collections.singletonList("队员2"), WsTeamMemberWaiter.getConnectMembers().get("一组"));
        assertEquals(PARTIAL, WsTeamMemberWaiter.getConnectionStatus("一组"));
        WsTeamMemberWaiter.removeConnectedTeam("new");
        assertEquals(OFFLINE, WsTeamMemberWaiter.getConnectionStatus("一组"));
    }

    @Test
    void changingLoginOnSameSessionReplacesPreviousMember() {
        WsTeamMemberWaiter.addConnectedTeam("s1", "一组-队员1");
        WsTeamMemberWaiter.addConnectedTeam("s1", "一组-队员2");
        assertEquals(Collections.singletonList("队员2"), WsTeamMemberWaiter.getConnectMembers().get("一组"));
        WsTeamMemberWaiter.removeConnectedTeam("s1");
        assertEquals(OFFLINE, WsTeamMemberWaiter.getConnectionStatus("一组"));
    }

    @Test
    void invalidIdentityIsIgnoredAndHyphenatedNamesAreSupported() {
        WsTeamMemberWaiter.addConnectedTeam("bad", "一组");
        WsTeamMemberWaiter.addConnectedTeam("bad", "一组-未知队员");
        assertEquals(OFFLINE, WsTeamMemberWaiter.getConnectionStatus("一组"));
        Teams team = TeamRuntime.getTeams().get(0);
        team.setShortName("一-组");
        team.setMembers("队员-1");
        WsTeamMemberWaiter.addConnectedTeam("s1", "一-组-队员-1");
        assertEquals(ONLINE, WsTeamMemberWaiter.getConnectionStatus("一-组"));
    }

    @Test
    void refreshingTeamDataPreservesConnectionsAndSnapshotsAreIndependent() {
        WsTeamMemberWaiter.addConnectedTeam("s1", "一组-队员1");
        List<String> snapshot = WsTeamMemberWaiter.getConnectMembers().get("一组");
        WsTeamMemberWaiter.initData();
        snapshot.clear();
        assertEquals(PARTIAL, WsTeamMemberWaiter.getConnectionStatus("一组"));
        assertEquals(Collections.singletonList("队员1"), WsTeamMemberWaiter.getConnectMembers().get("一组"));
    }
}
