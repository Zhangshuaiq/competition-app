package competition;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScrollableTextExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        for (Screen screen : Screen.getScreens()) {
            double dpi = screen.getDpi();
            System.out.println("Screen DPI: " + dpi);
            System.out.println("Screen Resolution: " + screen.getBounds().getWidth() + "x" + screen.getBounds().getHeight());
        }

        primaryStage.setTitle("Screen Info");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
