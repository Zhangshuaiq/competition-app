package competition;

import competition.entity.FontAttr;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;

public class FontSelector {

    GridPane grid = new GridPane();
    
    //private FontAttr fontAttr;

    public FontSelector(Stage primaryStage) {
        // 创建界面元素
       
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label lblFont = new Label("字体:");
        grid.add(lblFont, 0, 1);

        ComboBox<String> cbFont = new ComboBox<>();
        cbFont.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        cbFont.getSelectionModel().select("Arial");
        grid.add(cbFont, 1, 1);

        Label lblSize = new Label("大小:");
        grid.add(lblSize, 0, 2);

        ComboBox<Integer> cbSize = new ComboBox<>();
        cbSize.setItems(FXCollections.observableArrayList(8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32));
        cbSize.getSelectionModel().select(Integer.valueOf(12));
        grid.add(cbSize, 1, 2);

        CheckBox cbBold = new CheckBox("加粗");
        grid.add(cbBold, 0, 3);

        CheckBox cbItalic = new CheckBox("斜体");
        grid.add(cbItalic, 1, 3);

        CheckBox cbUnderline = new CheckBox("下划线");
        grid.add(cbUnderline, 0, 4);

        CheckBox cbStrikethrough = new CheckBox("删除线");
        grid.add(cbStrikethrough, 1, 4);

        Text text = new Text("演示字体");
        grid.add(text, 0, 5, 2, 1);

        // 监听器用于更新文本区域的字体
        cbFont.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough));
        cbSize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough));
        cbBold.selectedProperty().addListener((observable, oldValue, newValue) -> updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough));
        cbItalic.selectedProperty().addListener((observable, oldValue, newValue) -> updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough));
        cbUnderline.selectedProperty().addListener((observable, oldValue, newValue) -> updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough));
        cbStrikethrough.selectedProperty().addListener((observable, oldValue, newValue) -> updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough));
        updateFont(text, cbFont, cbSize, cbBold, cbItalic, cbUnderline, cbStrikethrough);
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("字体选择");
        primaryStage.show();
    }

    private void updateFont(Text textArea, ComboBox<String> cbFont, ComboBox<Integer> cbSize, CheckBox cbBold, CheckBox cbItalic, CheckBox cbUnderline, CheckBox cbStrikethrough) {


        FontWeight fontWeight =  FontWeight.NORMAL;
        FontPosture fontPosture = FontPosture.REGULAR;
        Font font = Font.font(cbFont.getValue(), fontWeight, fontPosture, cbSize.getValue());
        textArea.setFont(font);

        if (cbBold.isSelected() ) {
            textArea.setStroke(textArea.getFill());
        } else {
            textArea.setStroke(null);

        }
        textArea.getTransforms().clear();
        if (cbItalic.isSelected()) {
            textArea.getTransforms().add(new Shear(-0.3, 0));
        }
        
        textArea.setUnderline(cbUnderline.isSelected());
        textArea.setStrikethrough(cbStrikethrough.isSelected());
    }
}

