package competition.controller.preview;

import competition.controller.match.ReloadPane;
import competition.entity.Teams;
import javafx.scene.Node;
import javafx.scene.Scene;

public abstract class PreviewPane implements ReloadPane {

    @Override
    public Node getPane() {
        return null;
    }
    
    public void hideTime(){}



}
