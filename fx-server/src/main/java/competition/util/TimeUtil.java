package competition.util;

import java.time.format.DateTimeFormatter;

public class TimeUtil {

   private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String currentTime() {
        return FORMATTER.format(java.time.LocalDateTime.now());
    }
}
