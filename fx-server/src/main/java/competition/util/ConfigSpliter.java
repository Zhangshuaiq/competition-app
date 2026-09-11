package competition.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class ConfigSpliter {
    
    public static final String SPLITER = DelimitedConfigCodec.FIELD_DELIMITER;

    public static final String DIFFER = DelimitedConfigCodec.SECTION_DELIMITER;

    public static String[] split(String value) {
        return DelimitedConfigCodec.splitFields(value);
    }

    public static String[] splitSections(String value) {
        return DelimitedConfigCodec.splitSections(value);
    }
    
    public static final String EMPTY = "EMPTY";
    
    public static  int DEFAULT_X = 0;

    public static  int DEFAULT_Y = 0;
    
    public static final int DEFAULT_W = 800;

    public static final int DEFAULT_H = 600;
    
    
    public static final int DEFAULT_ANSWER_X = 800;
    public static final int DEFAULT_ANSWER_Y = 800;

    static {
        Screen primary = Screen.getPrimary();
        Rectangle2D bounds = primary.getBounds();
        DEFAULT_X = (int)(bounds.getWidth() - DEFAULT_W) / 2;
        DEFAULT_Y = (int)(bounds.getHeight() - DEFAULT_H) / 2;
    }

}
