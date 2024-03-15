package competition.controller;

import competition.util.BackendMap;
import competition.util.Navigator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class Backend {
    
    @FXML
    private VBox menuBox;
    
    @FXML
    private Pane contentPane;
    
    @FXML
    public void initialize(){
        final ObservableList<Node> children = menuBox.getChildren();
        for (Node child : children) {
            child.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                for (Node ch : children) {
                    ch.getStyleClass().remove("menu-label-clicked");
                }
                child.getStyleClass().add("menu-label-clicked");
                Label lb = (Label) child;
                showContent(lb.getText());
            });
        }
        children.get(0).getStyleClass().add("menu-label-clicked");
        showContent(((Label)children.get(0)).getText());
    }
    
    
    private void showContent(String text){
        final String backendPath = BackendMap.getBackendPath(text);
        if (backendPath != null) {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(Navigator.load(backendPath)); 
        }
    }
}
