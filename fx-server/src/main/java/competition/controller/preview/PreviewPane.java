package competition.controller.preview;

import competition.controller.match.ReloadPane;
import javafx.scene.Node;

public abstract class PreviewPane<T> implements ReloadPane<T> {

    private String secondFxml;

    @Override
    public Node getPane() {
        return null;
    }

    public void hideTime() {
    }


    public void setSecondFxml(String secondFxml) {
        this.secondFxml = secondFxml;
    }

    public String getSecondFxml() {
        return secondFxml;
    }

}
