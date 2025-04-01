package competition.ws;

import competition.util.TeamRuntime;

import java.util.LinkedHashMap;
import java.util.Map;

public class WsTeamMemberWaiter {

    // 连接中的队伍
    private static LinkedHashMap<String, String> connectTeams = new LinkedHashMap<>();


    public static void initData() {
        if (connectTeams.size() > 0) {
            return;
        }
        TeamRuntime.getTeams().forEach(team -> {
            connectTeams.put(team.getShortName(), "");
        });
    }

    // 添加连接的队伍，登录时调用
    public static void addConnectedTeam(String clientId, String member) {
        TeamRuntime.getTeams().stream().filter(team -> member.contains(team.getFullName())).findFirst().ifPresent(team -> {
            connectTeams.put(team.getShortName(), clientId);
        });
    }


    // 添加重连的队伍，重连时调用
    public static void addReconnectedTeam(String clientId, String member) {
        TeamRuntime.getTeams().stream().filter(team -> member.equals(team.getShortName())).findFirst().ifPresent(team -> {
            connectTeams.put(team.getShortName(), clientId);
        });
    }

    // 添加连接
    public static void addConnectedClient(String clientId) {
        connectTeams.put(clientId, null);
    }

    // 移除连接的队伍，登出时调用
    public static void removeConnectedTeam(String clientId) {
        for (Map.Entry<String, String> entry : connectTeams.entrySet()) {
            if (entry.getValue().equals(clientId)) {
                connectTeams.put(entry.getKey(), "");
                break;
            }
        }
    }


    public static LinkedHashMap<String, String> getConnectTeams() {
        return connectTeams;
    }
}
