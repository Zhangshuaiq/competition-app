package competition.ws;

import competition.entity.Teams;
import competition.util.TeamMemberCodec;
import competition.util.TeamRuntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class WsTeamMemberWaiter {
    // 一个连接只登记一个团队成员；同一成员可有多个真实在线会话。
    private static final Map<String, MemberConnection> connections = new LinkedHashMap<>();

    public enum ConnectionStatus {
        OFFLINE, PARTIAL, ONLINE
    }

    public static synchronized void initData() {
        connections.entrySet().removeIf(entry -> !isKnownMember(entry.getValue()));
    }

    public static synchronized void addConnectedTeam(String clientId, String identity) {
        if (clientId == null || identity == null) {
            return;
        }
        // 按最长团队名前缀匹配，避免团队名或成员名中包含 "-" 时拆错。
        for (Teams team : teams().stream()
                .filter(item -> item.getShortName() != null)
                .sorted(Comparator.comparingInt((Teams item) -> item.getShortName().length()).reversed())
                .collect(Collectors.toList())) {
            String prefix = team.getShortName() + "-";
            if (!identity.startsWith(prefix)) {
                continue;
            }
            MemberConnection connection = new MemberConnection(team.getShortName(), identity.substring(prefix.length()).trim());
            if (isKnownMember(connection)) {
                connections.put(clientId, connection);
                return;
            }
        }
    }

    public static void addReconnectedTeam(String clientId, String identity) {
        addConnectedTeam(clientId, identity);
    }

    public static synchronized void removeConnectedTeam(String clientId) {
        connections.remove(clientId);
    }

    // 返回快照，避免 WebSocket 线程修改集合时界面遍历失败。
    public static synchronized LinkedHashMap<String, List<String>> getConnectTeams() {
        LinkedHashMap<String, List<String>> result = emptyTeamMap();
        connections.forEach((session, connection) -> {
            if (result.containsKey(connection.team)) {
                result.get(connection.team).add(session);
            }
        });
        return result;
    }

    public static synchronized LinkedHashMap<String, List<String>> getConnectMembers() {
        LinkedHashMap<String, List<String>> result = emptyTeamMap();
        connections.values().forEach(connection -> {
            if (result.containsKey(connection.team)) {
                result.get(connection.team).add(connection.member);
            }
        });
        return result;
    }

    public static synchronized ConnectionStatus getConnectionStatus(String teamName) {
        Set<String> expected = teams().stream()
                .filter(team -> Objects.equals(team.getShortName(), teamName))
                .flatMap(team -> TeamMemberCodec.decode(team.getMembers()).stream())
                .map(String::trim).collect(Collectors.toSet());
        Set<String> online = connections.values().stream()
                .filter(connection -> Objects.equals(connection.team, teamName))
                .map(connection -> connection.member)
                .filter(expected::contains).collect(Collectors.toSet());
        if (online.isEmpty()) {
            return ConnectionStatus.OFFLINE;
        }
        return online.containsAll(expected) ? ConnectionStatus.ONLINE : ConnectionStatus.PARTIAL;
    }

    private static boolean isKnownMember(MemberConnection connection) {
        return teams().stream().anyMatch(team -> Objects.equals(team.getShortName(), connection.team)
                && TeamMemberCodec.decode(team.getMembers()).stream()
                .anyMatch(member -> Objects.equals(member.trim(), connection.member)));
    }

    private static List<Teams> teams() {
        return TeamRuntime.getTeams() == null ? Collections.emptyList() : TeamRuntime.getTeams();
    }

    private static LinkedHashMap<String, List<String>> emptyTeamMap() {
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<>();
        teams().forEach(team -> result.put(team.getShortName(), new ArrayList<>()));
        return result;
    }

    private static class MemberConnection {
        private final String team;
        private final String member;

        private MemberConnection(String team, String member) {
            this.team = team;
            this.member = member;
        }
    }
}
