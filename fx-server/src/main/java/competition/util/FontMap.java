package competition.util;

import cn.hutool.core.io.FileUtil;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class FontMap {
    
    
    private static final Map<String, Font> fontCache = new HashMap<>();
    
    public static List<String> fontNames() {
        List<String> fontNames = Font.getFamilies();
//        List<String> fontNames = FileUtil.listFileNames(BaseDir.FONT_DIR);
        return fontNames;
    }
    
    public static String getFont(String name) {
        return BaseDir.FONT_DIR + name;
    }

    public static String defaultFont() {
        String font = getFont("默认.ttf");
        return font;
    }
    
    public static Font load(String name, double size) {
        String key = name + "=" + size;
        if (fontCache.containsKey(key)) {
            return fontCache.get(key);
        }
        Font font = null;
//        try {
//            font = Font.loadFont(new FileInputStream(new File(FontMap.getFont(name))), size);
//            fontCache.put(key, font);
//        } catch (FileNotFoundException e) {
//            try {
//                font =  Font.loadFont(new FileInputStream(new File(FontMap.defaultFont())), size);
//                fontCache.put(key, font);
//            } catch (FileNotFoundException ex) {
//                font = Font.font(size);
//            }
//        }
        try {
            font = Font.font(name, size);
            fontCache.put(key, font);
        } catch (Exception e) {
            font = Font.font(size);
        }
        return font;
    }

    public static String defaultFontName() {
        return "默认.ttf";
    }
}
