package competition.util;

import javafx.stage.Screen;

public class PositionUtil {


    private static  final double baseScreenWidth = 1920.0;
    private static  final double baseScreenHeight = 1080.0;
    
    public static double calculateX(double screenWidth, double x){
        return x * (screenWidth / baseScreenWidth);
    }
    
    public static double calculateY(double screenHeight, double y){
        return y * (screenHeight / baseScreenHeight);
    }

    public static double calculateW(double width){
        return width * GlobalCache.scaleFactor;
    }

    public static double calculateH(double height){
        return height * GlobalCache.scaleFactor;
    }
}
