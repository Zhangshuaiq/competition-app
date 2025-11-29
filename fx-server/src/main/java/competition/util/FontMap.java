package competition.util;

import cn.hutool.core.io.FileUtil;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class FontMap {


    public static final String SYS_FONT_GROUP = "系统字体";
    public static final String CUSTOM_FONT_GROUP = "自定义字体";
    private static final Map<String, Font> fontCache = new HashMap<>();

    private static final Map<String, String> fontGroupMapping = new HashMap<>();

    public static Map<String,List<String>> fontNames() {
        List<String> sysFontNames = Font.getFamilies();
        List<String> customFontNames = FileUtil.listFileNames(BaseDir.FONT_DIR);
        Map<String,List<String>> allFonts = new LinkedHashMap<>();
        allFonts.put(SYS_FONT_GROUP, sysFontNames);
        allFonts.put(CUSTOM_FONT_GROUP, customFontNames);

        for (String sysFontName : sysFontNames) {
            fontGroupMapping.put(sysFontName, SYS_FONT_GROUP);
        }

        for (String sysFontName : customFontNames) {
            fontGroupMapping.put(sysFontName, CUSTOM_FONT_GROUP);
        }

        return allFonts;
    }

    public static String getFont(String name) {
        return BaseDir.FONT_DIR + name;
    }

    public static String defaultFont() {
        String font = Font.getDefault().getFamily();
        return font;
    }

    public static Font load(String name, double size) {
        String key = name + "=" + size;
        if (fontCache.containsKey(key)) {
            return fontCache.get(key);
        }

        String group = fontGroupMapping.get(name);

        Font font = null;
        if (Objects.equals(group, CUSTOM_FONT_GROUP)) {
            try {
                font = Font.loadFont(new FileInputStream(new File(FontMap.getFont(name))), size);
                fontCache.put(key, font);
            } catch (FileNotFoundException e) {
                try {
                    font =  Font.loadFont(new FileInputStream(new File(FontMap.defaultFont())), size);
                    fontCache.put(key, font);
                } catch (FileNotFoundException ex) {
                    font = Font.font(size);
                }
            }
        } else {
            try {
                font = Font.font(name, size);
                fontCache.put(key, font);
            } catch (Exception e) {
                font = Font.font(size);
            }
        }
        return font;
    }

    public static String defaultFontName() {
        return defaultFont();
    }
}
