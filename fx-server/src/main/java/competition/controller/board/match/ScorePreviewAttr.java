package competition.controller.board.match;


import competition.controller.ControlPane;
import competition.entity.ScoreStyle;
import competition.mapper.ScoreStyleMapper;
import competition.util.AlignmentUtil;
import competition.util.ConfigSpliter;
import competition.util.ExtendedScreen;
import competition.util.FontMap;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@Component
public class ScorePreviewAttr {
    
    @Resource
    private ScoreStyleMapper scoreStyleMapper;
    
    @FXML
    private CheckBox secondOpenBox;

    @FXML
    private CheckBox deleteChoice;

    @FXML
    private TextField fontSize;

    @FXML
    private CheckBox underlineChoice;

    @FXML
    private ComboBox<String> fontSelector;

    @FXML
    private CheckBox iticChoice;

    @FXML
    private CheckBox boldChoice;
    
    @Resource
    private ControlPane controlPane;
    
    
    // 列间距控制
    @FXML
    private TextField colSpan;
    
    
    @FXML
    public void initialize(){
        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        Font defaultFont = new JLabel().getFont();
        fontSelector.setValue(defaultFont.getFamily());
        fontSize.setText(defaultFont.getSize() + "");
        colSpan.setText("0");
        if (scoreStyle != null) {
            String styles = scoreStyle.getStyles();
            if (!StringUtils.isEmpty(styles)) {
                String[] split = styles.split(ConfigSpliter.SPLITER);
                secondOpenBox.setSelected( ExtendedScreen.scoreBoardStage.isShowing());
                fontSelector.setValue((split[0]));
                fontSize.setText((split[1]));
                deleteChoice.setSelected(Boolean.valueOf(split[2]));
                boldChoice.setSelected(Boolean.valueOf(split[3]));
                iticChoice.setSelected(Boolean.valueOf(split[4]));
                underlineChoice.setSelected(Boolean.valueOf(split[5]));
                if (split.length >= 7) {
                    colSpan.setText(split[6]);
                }
            }
        } 
    }
    
    @FXML
    public void saveConfig(){
        String value = fontSelector.getValue() + ConfigSpliter.SPLITER
                + fontSize.getText() + ConfigSpliter.SPLITER
                + deleteChoice.isSelected() + ConfigSpliter.SPLITER
                + boldChoice.isSelected() + ConfigSpliter.SPLITER
                + iticChoice.isSelected() + ConfigSpliter.SPLITER
                + underlineChoice.isSelected()  + ConfigSpliter.SPLITER
                + colSpan.getText();
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        scoreStyle.setStyles(value);
        scoreStyleMapper.updateById(scoreStyle);
        if (Screen.getScreens().size() > 1) {
            if (secondOpenBox.isSelected()) {
                ExtendedScreen.scoreBoardStage.show();
            } else {
                ExtendedScreen.scoreBoardStage.hide();
            }
        }
        
        controlPane.getScoreBoardPreview().reload();
        closeStage();
    }
    
    @FXML
    public void closeStage(){
        Stage stage = (Stage) (underlineChoice.getScene().getWindow());
        stage.close();
    }
}
