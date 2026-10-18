package competition.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestionSubmitRuntime {

    // key: teamFullName, value: 提交的答案
    private static final Map<String, String> submitMap = new ConcurrentHashMap<>();

    private static boolean closed;

    public static synchronized boolean save(String teamName, String userSelect) {
        if (closed) {
            return false;
        }
        submitMap.put(teamName, userSelect);
        return true;
    }

    public static synchronized Map<String, String> snapshotAndClose() {
        closed = true;
        return new java.util.HashMap<>(submitMap);
    }

    public static String get(String teamName) {
        return submitMap.get(teamName);
    }

    public static Map<String, String> getAll() {
        return new java.util.HashMap<>(submitMap);
    }

    public static synchronized void clear() {
        closed = false;
        submitMap.clear();
    }
}
