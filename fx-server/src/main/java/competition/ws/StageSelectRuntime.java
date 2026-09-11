package competition.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StageSelectRuntime {

    // key: teamCode, value: 选择数据
    private static final Map<String, StageSelectData> selectDataMap = new ConcurrentHashMap<>();

    // 已揭示子环节选择的队伍（揭示后 status 被答题阶段覆盖也不受影响）
    private static final java.util.Set<String> revealedSet = java.util.Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 当前选择数据所属的 type8 环节 ID，用于重进时判断是否恢复
    private static volatile Integer ownerStageId = null;

    public static void setOwnerStageId(Integer stageId) { ownerStageId = stageId; }
    public static Integer getOwnerStageId() { return ownerStageId; }

    public static void save(StageSelectData data) {
        selectDataMap.put(data.getTeam(), data);
    }

    public static StageSelectData get(String team) {
        return selectDataMap.get(team);
    }

    public static Map<String, StageSelectData> getAll() {
        return selectDataMap;
    }

    /**
     * 判断队伍是否选择了当前正在答题的子环节。
     */
    public static boolean isSelectedForStage(String team, String stageName) {
        StageSelectData selection = get(team);
        return selection != null
                && selection.getStage() != null
                && stageName != null
                && selection.getStage().trim().equals(stageName.trim());
    }

    public static void markRevealed(String team) {
        revealedSet.add(team);
    }

    public static boolean isRevealed(String team) {
        return revealedSet.contains(team);
    }

    public static void clear() {
        selectDataMap.clear();
        revealedSet.clear();
        ownerStageId = null;
    }

    public static class StageSelectData {
        private String stage;   // 选中的环节
        private String team;    // 队伍
        private String member;  // 选手

        public String getStage() { return stage; }
        public void setStage(String stage) { this.stage = stage; }
        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
        public String getMember() { return member; }
        public void setMember(String member) { this.member = member; }
    }
}
