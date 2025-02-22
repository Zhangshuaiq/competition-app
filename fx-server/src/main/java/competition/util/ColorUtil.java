package competition.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColorUtil {

    public static final Map<String,String> COLOR_MAPPER = new LinkedHashMap<>();

    public static final String NO_COLOR = "不点亮";

    static {
        COLOR_MAPPER.put("不点亮", "0");
        COLOR_MAPPER.put("红色", "1");
        COLOR_MAPPER.put("绿色", "2");
        COLOR_MAPPER.put("黄色", "3");
    }


    public static String getColorCmd(String color1, String color2, String color3) {
        return COLOR_MAPPER.get(color1) + COLOR_MAPPER.get(color2) + COLOR_MAPPER.get(color3);
    }
}
