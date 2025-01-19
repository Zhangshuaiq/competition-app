package competition.util;

import java.util.HashMap;
import java.util.Map;

public class RuntimeUtil {

    public static volatile boolean showBuzzBar = false;

    public static volatile long buzzStart = 0;

    public static volatile long buzzCalculate = 0;

    public static Map<String, Long> buzzTimeMap = new HashMap<>();

    public static Map<String, Boolean> buzzResult = new HashMap<>();
}
