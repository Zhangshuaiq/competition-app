package competition.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class QuizBoardHBoxDemo extends Application {

    private final List<VBox> teamBoxes = new ArrayList<>(); // 保存所有队伍的 VBox

    @Override
    public void start(Stage primaryStage) {
        HBox container = new HBox();
        container.setSpacing(20); // 设置队伍之间的间距
        container.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");

        // 模拟三个队伍
        for (int i = 1; i <= 3; i++) {
            VBox teamBox = createTeamBox("Team " + i, String.valueOf(10 * i));
            teamBoxes.add(teamBox); // 保存队伍的 VBox
            container.getChildren().add(teamBox);
        }

        // 添加一个按钮来展示所有抢答时间条
        Button showBuzzBarsButton = new Button("显示抢答时间条");
        showBuzzBarsButton.setOnAction(e -> showBuzzBars());

        VBox mainLayout = new VBox();
        mainLayout.setSpacing(20);
        mainLayout.getChildren().addAll(container, showBuzzBarsButton);

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz Board with HBox");
        primaryStage.show();
    }

    private VBox createTeamBox(String teamName, String score) {
        VBox teamBox = new VBox();
        teamBox.setSpacing(5); // 设置队伍内元素的间距
        teamBox.setPrefWidth(150);
        teamBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-padding: 10;");

        // 队伍名称
        StackPane namePane = new StackPane();
        namePane.setPrefSize(150, 50); // 固定高度
        namePane.setStyle("-fx-background-color: #e8e8ff; -fx-border-radius: 10;");
        Text nameText = new Text(teamName);
        nameText.setFont(Font.font(16));
        nameText.setFill(Color.BLACK);
        namePane.getChildren().add(nameText);

        // 分数
        StackPane scorePane = new StackPane();
        scorePane.setPrefSize(150, 50); // 固定高度
        scorePane.setStyle("-fx-background-color: #ffe8e8; -fx-border-radius: 10;");
        Text scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font(16));
        scoreText.setFill(Color.BLACK);
        scorePane.getChildren().add(scoreText);

        teamBox.getChildren().addAll(namePane, scorePane);

        return teamBox;
    }

    private void showBuzzBars() {
        for (VBox teamBox : teamBoxes) {
            // 如果抢答时间条已存在，跳过
            if (teamBox.getChildren().size() > 2) {
                continue;
            }

            StackPane buzzPane = new StackPane();
            buzzPane.setPrefSize(150, 30); // 固定高度
            buzzPane.setStyle("-fx-background-color: #e8ffe8; -fx-border-radius: 10;");
            Text buzzText = new Text("Buzz Time: 5.00s");
            buzzText.setFont(Font.font(14));
            buzzText.setFill(Color.BLACK);
            buzzPane.getChildren().add(buzzText);

            teamBox.getChildren().add(buzzPane);

            // 更新 teamBox 高度（如果需要，可调整逻辑）
            teamBox.setPrefHeight(150 + 30);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

