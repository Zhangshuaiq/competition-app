package competition.controller.match;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public interface ReloadPane {

    default void reload(){};
    
    default void reload(int time, String timeTxt){};
    
    default void showAnswer(){};
    
    default void startTiming(boolean order, int time){};

    default void startBuzz(int time, int limitTime){};

    default   void buzzTiming(int time) {}

    default void stopTiming(){};
    
    default void continueTiming(){};
    
    default void close(){};
    
    default void shows(){};
    
    Node getPane();
    
    default void reloadStyle(){}

    default void hideTime(){}
    
    default void scrollUp(){}
    
    default void scrollDown(){}
    
    default void scrollUpAnswer(){}
    
    default void scrollDownAnswer(){}
}
