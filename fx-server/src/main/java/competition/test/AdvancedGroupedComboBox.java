package competition.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdvancedGroupedComboBox extends Application {

    static class GroupItem {
        String group;
        String value;
        String displayText;

        public GroupItem(String group, String value) {
            this.group = group;
            this.value = value;
            this.displayText = value;
        }

        public GroupItem(String group) {
            this.group = group;
            this.value = null;
            this.displayText = group;
        }

        public boolean isGroupHeader() {
            return value == null;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    @Override
    public void start(Stage stage) {
        ComboBox<GroupItem> comboBox = new ComboBox<>();

        // 添加数据
        comboBox.getItems().addAll(
                new GroupItem("数据库"),
                new GroupItem("数据库", "MySQL"),
                new GroupItem("数据库", "PostgreSQL"),
                new GroupItem("数据库", "Oracle"),
                new GroupItem("编程语言"),
                new GroupItem("编程语言", "Java"),
                new GroupItem("编程语言", "Python"),
                new GroupItem("编程语言", "JavaScript"));

        comboBox.setCellFactory(lv -> new ListCell<GroupItem>() {
            @Override
            protected void updateItem(GroupItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    if (item.isGroupHeader()) {
                        setText(item.group);
                        setStyle("-fx-font-weight: bold; " +
                                "-fx-font-size: 11px; " +
                                "-fx-background-color: #e8e8e8; " +
                                "-fx-text-fill: #333; " +
                                "-fx-padding: 5 5 5 5;");
                        setDisable(true);
                    } else {
                        setText(item.value);
                        setStyle("-fx-padding: 3 5 3 25; " +
                                "-fx-font-size: 12px;");
                        setDisable(false);
                    }
                }
            }
        });

        comboBox.setButtonCell(new ListCell<GroupItem>() {
            @Override
            protected void updateItem(GroupItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isGroupHeader()) {
                    setText(null);
                } else {
                    setText(item.value);
                }
            }
        });

        // 添加选择监听
        comboBox.setOnAction(e -> {
            GroupItem selected = comboBox.getValue();
            if (selected != null && !selected.isGroupHeader()) {
                System.out.println("Selected: " + selected.value + " from group: " + selected.group);
            }
        });

        VBox root = new VBox(10, new Label("选择一个选项："), comboBox);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Advanced Grouped ComboBox");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
