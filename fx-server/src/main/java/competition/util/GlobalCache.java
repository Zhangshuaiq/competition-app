package competition.util;

import java.util.HashMap;
import java.util.Map;

public class GlobalCache {
    
    // 是否显示抢答排行
    public static boolean qdRank = false;
    
    // 后台设置是否打开
    public static boolean backendOpen = false;
    
    // 答案显示状态
    public static boolean showAnswer = false;
    
    // 规则页的显示状态
    public static boolean showRule = false;
    
    // 总规则页的显示状态
    public static boolean showTotalRule = false;
    
    // 当前题目是否应该显示
    private static final Map<Integer, Boolean> stageMap = new HashMap<>();
    
    // 是否正在滚动选题
    public static boolean isRolling = false;
    
    public static void recordStage(Integer code) {
        stageMap.put(code, true);
    }
    
    
    public static boolean isRecord(Integer code){
        return stageMap.getOrDefault(code, false);
    }
}
