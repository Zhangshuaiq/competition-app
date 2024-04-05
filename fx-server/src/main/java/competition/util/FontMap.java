package competition.util;

import javafx.scene.text.Font;

import java.util.*;

public class FontMap {
    
    private static final  Map<String, String> fonts = new LinkedHashMap<>();
    
    static {
        for (String fontName : Font.getFontNames()) {
            fonts.put(fontName, fontName);
        }

        // 仿宋 - FangSong
        // 宋体 - SimSun
        // 微软雅黑 - Microsoft YaHei
        // 微软雅黑 Light - Microsoft YaHei Light
        // 微软雅黑 Bold - Microsoft YaHei Bold
        // 新宋体 - NSimSun
        // 楷体 - KaiTi
        // 黑体 - SimHei
        // 等线 Bold - DengXian Bold
        // 等线 Light - DengXian Light
        // 等线 - DengXian Regular
        fonts.put("仿宋", "FangSong");
        fonts.put("宋体", "SimSun");
        fonts.put("微软雅黑", "Microsoft YaHei");
        fonts.put("微软雅黑 Light", "Microsoft YaHei Light");
        fonts.put("微软雅黑 Bold", "Microsoft YaHei Bold");
        fonts.put("新宋体", "NSimSun");
        fonts.put("楷体", "KaiTi");
        fonts.put("黑体", "SimHei");
        fonts.put("等线", "DengXian Regular");
        fonts.put("等线 Light", "DengXian Light");
        fonts.put("等线 Bold", "DengXian Bold");
    }
    
    public static List<String> fontNames() {
        return new ArrayList<>(fonts.keySet());
    }
    
    public static String getFont(String name) {
        return fonts.get(name);
    }
}
