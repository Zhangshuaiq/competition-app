package competition.ws;

import competition.entity.Teams;
import competition.util.TeamRuntime;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WsTeamMemberWaiter {

    // 连接中的队伍
    private static LinkedHashMap<String, List<String>> connectTeams = new LinkedHashMap<>();

    private static LinkedHashMap<String, List<String>> connectMembers = new LinkedHashMap<>();

    public static void initData() {
        TeamRuntime.getTeams().forEach(team -> {
            connectTeams.put(team.getShortName(), new ArrayList<>());
            connectMembers.put(team.getShortName(), new ArrayList<>());
        });
    }

    // 添加连接的队伍，登录时调用
    public static void addConnectedTeam(String clientId, String member) {
        List<Teams> teams = TeamRuntime.getTeams();
        String[] split = member.split("-");
        String te = split[0];
        String mem = split[1].trim();
        teams.stream().filter(team -> te.equals(team.getShortName())).findFirst().ifPresent(team -> {
            if (!connectTeams.get(team.getShortName()).contains(clientId)) {
                connectTeams.get(team.getShortName()).add(clientId);
                connectMembers.get(team.getShortName()).add(mem);
            } else {
                if ( !connectMembers.get(team.getShortName()).contains(mem)) {
                    connectMembers.get(team.getShortName()).add(mem);
                }
            }
        });
    }


    // 添加重连的队伍，重连时调用
    public static void addReconnectedTeam(String clientId, String member) {
        if (CollectionUtils.isEmpty(connectMembers)) {
            return;
        }
        List<Teams> teams = TeamRuntime.getTeams();
        String[] split = member.split("-");
        String te = split[0];
        String mem = split[1].trim();
        Optional<Teams> first = teams.stream().filter(team -> te.equals(team.getShortName())).findFirst();
        first.ifPresent(team -> {
            if ( connectMembers.get(team.getShortName()).contains(mem)) {
                // 防止多个链接进入
                return;
            }
            connectTeams.get(team.getShortName()).add(clientId);
            connectMembers.get(team.getShortName()).add(mem);
        });
    }

    // 添加连接
    public static void addConnectedClient(String clientId) {
        connectTeams.put(clientId, new ArrayList<>());
        connectMembers.put(clientId, new ArrayList<>());
    }

    // 移除连接的队伍，登出时调用
    public static void removeConnectedTeam(String clientId) {
        for (Map.Entry<String, List<String>> entry : connectTeams.entrySet()) {
            if (entry.getValue().contains(clientId)) {
                int i = entry.getValue().indexOf(clientId);
                entry.getValue().remove(clientId);
                connectMembers.get(entry.getKey()).remove(i);
                break;
            }
        }
    }


    public static LinkedHashMap<String, List<String>> getConnectTeams() {
        return connectTeams;
    }

    public static LinkedHashMap<String, List<String>> getConnectMembers() {
        return connectMembers;
    }
}
