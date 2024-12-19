package competition.util;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class ScreenAdapter {

    
    


    public static double calculateMainY(double value){

        final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        final double targetScreenHeight = screenBounds.getHeight();
        final double originalScreenHeight = 1080.0;

        // 需要映射的绝对值（原始屏幕上的值）
        double originalHeight = value;

        // 计算比例因子
        double scaleY = targetScreenHeight / originalScreenHeight;

        // 在目标屏幕上计算新的值
        double targetHeight = originalHeight * scaleY;
        return targetHeight;
    }
    

    public static double calculateMainX(double value){
        final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        final double targetScreenWidth = screenBounds.getWidth();
        final double originalScreenWidth = 1920.0;

        // 计算比例因子
        double scaleX = targetScreenWidth / originalScreenWidth;

        // 在目标屏幕上计算新的值
        double targetX = value * scaleX;
        return targetX;
    }

    

    public static double calculateExY(double value){

        ObservableList<Screen> screens = Screen.getScreens();
        final Rectangle2D screenBounds = screens.size() > 1? screens.get(1).getVisualBounds(): screens.get(0).getVisualBounds();
        final double targetScreenHeight = screenBounds.getHeight();
        final double originalScreenHeight = 1080.0;

        // 需要映射的绝对值（原始屏幕上的值）
        double originalHeight = value;

        // 计算比例因子
        double scaleY = targetScreenHeight / originalScreenHeight;

        // 在目标屏幕上计算新的值
        double targetHeight = originalHeight * scaleY;
        return targetHeight;
    }
    
    public static double calculateExX(double value){
        ObservableList<Screen> screens = Screen.getScreens();
        final Rectangle2D screenBounds = screens.size() > 1? screens.get(1).getVisualBounds(): screens.get(0).getVisualBounds();
        final double targetScreenWidth = screenBounds.getWidth();
        final double originalScreenWidth = 1920.0;

        // 计算比例因子
        double scaleX = targetScreenWidth / originalScreenWidth;

        // 在目标屏幕上计算新的值
        double targetX = value * scaleX;
        return targetX;
    }

}
