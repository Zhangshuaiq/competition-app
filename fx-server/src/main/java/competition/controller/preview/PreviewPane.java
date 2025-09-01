package competition.controller.preview;

import competition.controller.match.ReloadPane;
import javafx.scene.Node;

public abstract class PreviewPane<T> implements ReloadPane<T> {

    @Override
    public Node getPane() {
        return null;
    }

    public void hideTime() {
    }

}
