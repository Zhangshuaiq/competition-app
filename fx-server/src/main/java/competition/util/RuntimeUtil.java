package competition.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RuntimeUtil {

    public static volatile boolean showBuzzBar = false;

    public static volatile long buzzStart = 0;

    public static volatile long buzzCalculate = 0;

    public static LinkedHashMap<String, Long> buzzTimeMap = new LinkedHashMap<>();

    public static LinkedHashMap<String, Boolean> buzzResult = new LinkedHashMap<>();
}
