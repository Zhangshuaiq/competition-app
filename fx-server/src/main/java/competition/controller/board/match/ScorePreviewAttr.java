package competition.controller.board.match;


import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.entity.ScoreStyle;
import competition.mapper.ScoreStyleMapper;
import competition.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
public class ScorePreviewAttr {
    
    @Resource
    private ScoreStyleMapper scoreStyleMapper;
    
    @FXML
    private CheckBox secondOpenBox, scoreShowBox;

    @FXML
    private CheckBox deleteChoice, sdeleteChoice;

    @FXML
    private TextField fontSize, sfontSize;

    @FXML
    private CheckBox underlineChoice, sunderlineChoice;

    @FXML
    private ComboBox<String> fontSelector, sfontSelector;

    @FXML
    private CheckBox iticChoice, siticChoice;

    @FXML
    private CheckBox boldChoice, sboldChoice;
    
    @Resource
    private ControlPane controlPane;
    
    
    // 列间距控制 & 行间距控制 == > 行间距只对 对联模式生效
    @FXML
    private TextField colSpan, rowSpan;

    @FXML
    public void initialize(){

        // 队伍名称字体设置
        scoreShowBox.setSelected(RuntimeUtil.showScoreBar);
        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        fontSelector.setValue(FontMap.defaultFontName());
        fontSize.setText(12 + "");
        colSpan.setText("0");
        secondOpenBox.setSelected(RuntimeUtil.showSecondScorePane);
        // 默认 10
        rowSpan.setText("10");
        if (scoreStyle != null) {
            String styles = scoreStyle.getStyles();
            if (!StringUtils.isEmpty(styles)) {
                String[] split = styles.split(ConfigSpliter.SPLITER);
                fontSelector.setValue((split[0]));
                fontSize.setText((split[1]));
                deleteChoice.setSelected(Boolean.valueOf(split[2]));
                boldChoice.setSelected(Boolean.valueOf(split[3]));
                iticChoice.setSelected(Boolean.valueOf(split[4]));
                underlineChoice.setSelected(Boolean.valueOf(split[5]));
                if (split.length >= 7) {
                    colSpan.setText(split[6]);
                }
                if (split.length >= 8) {
                    rowSpan.setText(split[7]);
                }
                if (split.length >= 9) {
                    scoreShowBox.setSelected(Boolean.parseBoolean(split[8]));
                } else {
                    scoreShowBox.setSelected(false);
                }
                RuntimeUtil.showScoreBar = scoreShowBox.isSelected();
            }
        }

        // 分数字体设置
        sfontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        ScoreStyle sscoreStyle = scoreStyleMapper.selectById(3);
        sfontSelector.setValue(FontMap.defaultFontName());
        sfontSize.setText(12 + "");
        if (sscoreStyle != null) {
            String styles = sscoreStyle.getStyles();
            if (!StringUtils.isEmpty(styles)) {
                String[] split = styles.split(ConfigSpliter.SPLITER);
                sfontSelector.setValue((split[0]));
                sfontSize.setText((split[1]));
                sdeleteChoice.setSelected(Boolean.valueOf(split[2]));
                sboldChoice.setSelected(Boolean.valueOf(split[3]));
                siticChoice.setSelected(Boolean.valueOf(split[4]));
                sunderlineChoice.setSelected(Boolean.valueOf(split[5]));
            }
        }
    }
    
    @FXML
    public void saveConfig(){

        // 保存队伍名字字体配置
        String value = fontSelector.getValue() + ConfigSpliter.SPLITER
                + fontSize.getText() + ConfigSpliter.SPLITER
                + deleteChoice.isSelected() + ConfigSpliter.SPLITER
                + boldChoice.isSelected() + ConfigSpliter.SPLITER
                + iticChoice.isSelected() + ConfigSpliter.SPLITER
                + underlineChoice.isSelected()  + ConfigSpliter.SPLITER
                + colSpan.getText() + ConfigSpliter.SPLITER
                + rowSpan.getText() + ConfigSpliter.SPLITER
                + scoreShowBox.isSelected();
        ScoreStyle scoreStyle = scoreStyleMapper.selectById(1);
        scoreStyle.setStyles(value);
        scoreStyleMapper.updateById(scoreStyle);


        // 保存分数字体配置
        String svalue = sfontSelector.getValue() + ConfigSpliter.SPLITER
            + sfontSize.getText() + ConfigSpliter.SPLITER
            + sdeleteChoice.isSelected() + ConfigSpliter.SPLITER
            + sboldChoice.isSelected() + ConfigSpliter.SPLITER
            + siticChoice.isSelected() + ConfigSpliter.SPLITER
            + sunderlineChoice.isSelected();
        ScoreStyle sscoreStyle = scoreStyleMapper.selectById(3);
        sscoreStyle.setStyles(svalue);
        scoreStyleMapper.updateById(sscoreStyle);

        if (Screen.getScreens().size() > 1) {
            if (secondOpenBox.isSelected()) {
                RuntimeUtil.showSecondScorePane = true;
            } else {
                RuntimeUtil.showSecondScorePane = false;
            }
        }

        RuntimeUtil.showScoreBar = scoreShowBox.isSelected();

        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.reload();
        }
    }
    
    @FXML
    public void closeStage(){
        Stage stage = (Stage) (underlineChoice.getScene().getWindow());
        stage.close();
        Navigator.clearAttrCache();
    }
}
