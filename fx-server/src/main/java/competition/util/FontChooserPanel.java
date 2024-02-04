package competition.util;

import competition.controller.backend.ScoreConfig;
import competition.entity.FontAttr;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.SwingUtilities;

public class FontChooserPanel {
    public static void start(Stage stage, String config) {
        FontChooser fontChooser = new FontChooser();
        final SwingNode swingNode = new SwingNode();
        StackPane stackPane = new StackPane();
        stackPane.setLayoutX(5);
        stackPane.setLayoutY(5);
        stackPane.setPrefSize(400, 400);
        createSwingContent(swingNode,fontChooser);
        stackPane.getChildren().add(swingNode);

        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(410, 450);
        pane.getChildren().add(stackPane);
        Button confirmBtn = new Button("确认");
        confirmBtn.setLayoutX(50);
        confirmBtn.setLayoutY(410);
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FontMap.putFont(config, fontChooser.getFontAttr());
            }
        });
        Button closeBtn = new Button("关闭");
        closeBtn.setLayoutX(310);
        closeBtn.setLayoutY(410);
        closeBtn.setOnAction((event)-> {
            stage.close();
        });
        pane.getChildren().addAll(confirmBtn, closeBtn);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(pane));
        stage.setTitle("选择字体");
        stage.show();
    }

    private static void createSwingContent(final SwingNode swingNode, FontChooser fontChooser) {
        SwingUtilities.invokeLater(() -> swingNode.setContent(fontChooser));
    }


}

