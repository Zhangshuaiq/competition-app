// 新增内存存储类 - 放到合适的包下
package competition.ws;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StageSelectRuntime {

    // key: teamCode, value: 选择数据
    private static final Map<String, StageSelectData> selectDataMap = new ConcurrentHashMap<>();

    public static void save(StageSelectData data) {
        selectDataMap.put(data.getTeam(), data);
    }

    public static StageSelectData get(String team) {
        return selectDataMap.get(team);
    }

    public static Map<String, StageSelectData> getAll() {
        return selectDataMap;
    }

    public static void clear() {
        selectDataMap.clear();
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