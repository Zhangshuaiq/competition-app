package competition.controller.backend;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TeamConfig {
    
    
    @FXML
    private VBox memberBox;
    
    @FXML
    public void addMember(){
        // prefHeight="38.0" prefWidth="38.0"
        final int size = memberBox.getChildren().size();
        Label code = new Label(size + "");
        code.setPrefWidth(38);
        code.setPrefHeight(38);
        code.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        final TextField textField = new TextField();
        textField.setPrefWidth(200);
        hBox.getChildren().addAll(code, textField);
        memberBox.getChildren().add(size - 1, hBox);
    }
}
