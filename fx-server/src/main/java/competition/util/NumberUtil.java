package competition.util;

import java.text.DecimalFormat;

import org.apache.poi.util.StringUtil;
import org.springframework.util.StringUtils;

public class NumberUtil {

    public static String fix2PointWithDefault(long number) {
        if (number == RuntimeUtil.UN_BUZZ_TIME) {
            return "--";
        }
        return fix2Point(number / 1000000000.0);
    }

    public static String fix2Point(double number) {
        DecimalFormat df = new DecimalFormat("0.0000");
        return df.format(number);
    }

    public static boolean isNumber(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInt(String number) {
        if (!StringUtils.hasText(number)) {
            return false;
        }
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
