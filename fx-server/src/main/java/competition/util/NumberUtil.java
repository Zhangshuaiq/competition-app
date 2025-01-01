package competition.util;

import java.text.DecimalFormat;

public class NumberUtil {

    public static String fix2Point(double number) {
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(number);
    }
}
