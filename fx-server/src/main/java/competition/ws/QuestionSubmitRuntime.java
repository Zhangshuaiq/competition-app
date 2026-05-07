package competition.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestionSubmitRuntime {

    // key: teamFullName, value: 提交的答案
    private static final Map<String, String> submitMap = new ConcurrentHashMap<>();

    public static void save(String teamName, String userSelect) {
        submitMap.put(teamName, userSelect);
    }

    public static String get(String teamName) {
        return submitMap.get(teamName);
    }

    public static Map<String, String> getAll() {
        return submitMap;
    }

    public static void clear() {
        submitMap.clear();
    }
}
