package competition.controller.preview;

import competition.controller.match.ReloadPane;
import competition.entity.Teams;
import javafx.scene.Node;
import javafx.scene.Scene;

public abstract class PreviewPane implements ReloadPane {
    
    private Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public Node getPane() {
        return null;
    }
    
    public void hideTime(){}



}
