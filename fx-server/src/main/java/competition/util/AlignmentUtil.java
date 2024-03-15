package competition.util;

import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class AlignmentUtil {
    
    private static final Map<String, Pos> alignMap = new HashMap<>();
    
    private static final Map<String, TextAlignment> TEXT_ALIGNMENT_MAP = new HashMap<>();
    
    static {
        alignMap.put("左对齐", Pos.CENTER_LEFT);
        alignMap.put("右对齐", Pos.CENTER_RIGHT);
        alignMap.put("居中对齐", Pos.CENTER);


        TEXT_ALIGNMENT_MAP.put("左对齐", TextAlignment.LEFT);
        TEXT_ALIGNMENT_MAP.put("右对齐", TextAlignment.RIGHT);
        TEXT_ALIGNMENT_MAP.put("居中对齐", TextAlignment.CENTER);
    }
    
    public static List<String> alignNames(){
        return new ArrayList<>(alignMap.keySet());
    }
    
    public static Pos getAlign(String name) {
        return alignMap.get(name);
    }

    public static TextAlignment getTextAlign(String name) {
        return TEXT_ALIGNMENT_MAP.getOrDefault(name, TextAlignment.LEFT);
    }
}
