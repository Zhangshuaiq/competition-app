package competition.util;

import competition.entity.FontAttr;

import java.util.HashMap;
import java.util.Map;

public class FontMap {
    private static final Map<String, FontAttr> map = new HashMap<>();

    public static final FontAttr getFontAttr(String fontName) {
        return map.getOrDefault(fontName, new FontAttr());
    }

    public static final void putFont(String fontName, FontAttr attr) {
        map.put(fontName, attr);
    }
}
