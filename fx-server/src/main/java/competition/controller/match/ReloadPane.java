package competition.controller.match;

import javafx.scene.Node;

public interface ReloadPane <T> {

    default void initPage() {
    }

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

//    default void showBuzzResult(Teams team, boolean success){
//    }
//
//    default void hideBuzzResult(){
//    }
    
    default void scrollUp(){}
    
    default void scrollDown(){}
    
    default void scrollUpAnswer(){}
    
    default void scrollDownAnswer(){}

    default void destroy(){}

    default void setData(T data) {

    }

    default T getData() {
        return null;
    }
}
