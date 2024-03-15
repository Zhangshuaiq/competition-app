package competition.controller.board;

import competition.controller.match.ReloadPane;

import java.util.Set;

public interface BoardPane extends ReloadPane {
    
    public Set<String> getSelected();
}
