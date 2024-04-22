package competition.controller.board;

import competition.controller.match.ReloadPane;

import java.util.Set;

public interface BoardPane extends ReloadPane {
    
    public Set<String> getSelected();
    
    public default void selectAll(boolean selected) {};

    public default void reloadColor(){};
}
