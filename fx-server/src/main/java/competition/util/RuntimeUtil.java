package competition.util;

import java.util.LinkedHashMap;

public class RuntimeUtil {

    public static volatile boolean addLast = true;

    public static boolean showTimeBoard = true;

    public static final String SUPER_ACTIVE_CODE = "99999999991234567890";

    public static final long UN_BUZZ_TIME = Integer.MAX_VALUE - 100;

    public static volatile boolean showBuzzBar = false;

    // 分数条显示
    public static volatile boolean showScoreBar = true;

    // 扩展屏显示分数面板
    public static volatile boolean showSecondScorePane = false;

    public static volatile long buzzStart = 0;

    public static volatile long buzzCalculate = 0;

    public static LinkedHashMap<String, Long> buzzTimeMap = new LinkedHashMap<>();

    public static LinkedHashMap<String, Boolean> buzzResult = new LinkedHashMap<>();

    // 链接状态
    public static boolean showConnectionStatus = false;

    // 计时状态
    public static volatile boolean isTiming = false;
}
