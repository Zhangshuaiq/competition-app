package competition;

import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashPreloader extends Preloader {

    private Stage splashStage;
    private ProgressBar progressBar;
    private Label statusLabel;

    public static class SplashNotification implements PreloaderNotification {
        private final double progress;
        private final String status;

        public SplashNotification(double progress, String status) {
            this.progress = progress;
            this.status = status;
        }

        public double getProgress() { return progress; }
        public String getStatus() { return status; }
    }

    @Override
    public void start(Stage stage) {
        this.splashStage = stage;

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 60, 40, 60));
        root.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12;");

        Label title = new Label("答赛答题竞赛系统");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(360);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: #4fc3f7;");

        statusLabel = new Label("正在启动...");
        statusLabel.setFont(Font.font(13));
        statusLabel.setTextFill(Color.web("#90caf9"));

        root.getChildren().addAll(title, progressBar, statusLabel);

        Scene scene = new Scene(root, 480, 180);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof SplashNotification) {
            SplashNotification n = (SplashNotification) pn;
            progressBar.setProgress(n.getProgress());
            statusLabel.setText(n.getStatus());
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            splashStage.hide();
        }
    }
}
