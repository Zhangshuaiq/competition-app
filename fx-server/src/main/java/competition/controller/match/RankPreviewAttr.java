package competition.controller.match;

import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.controls.GroupedComboBox;
import competition.entity.Configs;
import competition.entity.RankStyle;
import competition.mapper.ConfigsMapper;
import competition.mapper.RankStyleMapper;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
public class RankPreviewAttr {

    @FXML
    private TextField rxInput;

    @FXML
    private ColorPicker colorInput;

    @FXML
    private TextField yInput;

    @FXML
    private TextField srInput;

    @FXML
    private GroupedComboBox<String> hfSelector;

    @FXML
    private TextField widthInput;

    @FXML
    private TextField rheightInput;

    @FXML
    private GroupedComboBox<String> rfSelector;

    @FXML
    private TextField xInput;

    @FXML
    private TextField rowInput;

    @FXML
    private TextField rwidthInput;

    @FXML
    private TextField ryInput;

    @FXML
    private TextField heightInput;

    @FXML
    private TextField rfsInput;

    @FXML
    private TextField hfsInput;

    @FXML
    private CheckBox hfBold, rfsBold;
    
    @Resource
    private ControlPane controlPane;
    
    @Resource
    private RankStyleMapper rankStyleMapper;
    
    @Resource
    private ConfigsMapper configsMapper;
    
    
    
    @FXML
    public void initialize(){

        FontMap.fontNames().forEach((key, value) -> {
            hfSelector.addGroup(key, value);
            rfSelector.addGroup(key, value);
        });

        RankStyle rankStyle = rankStyleMapper.selectById(1);

        showHeadConfig(rankStyle.getHeadConfig());
        showRankConfig(rankStyle.getRankConfig());
    }
    
    
    
    private void showHeadConfig(String headConfig){
        
        if (StringUtils.isEmpty(headConfig)) {
            Font font = hfsInput.getFont();
            xInput.setText("30");
            yInput.setText("30");
            widthInput.setText("580");
            heightInput.setText("44");
            colorInput.setValue(Color.BLACK);
            hfsInput.setText(((int)font.getSize()) + "");
            hfSelector.setSelectedValue(font.getName());
            hfBold.setSelected(false);
        } else {
            String[] split = ConfigSpliter.split(headConfig);
            xInput.setText(split[0]);
            yInput.setText(split[1]);
            widthInput.setText(split[2]);
            heightInput.setText(split[3]);
            colorInput.setValue(Color.valueOf(split[4]));
            hfsInput.setText(split[5]);
            hfSelector.setSelectedValue(split[6]);
            if (split.length >= 8) {
                hfBold.setSelected(Boolean.parseBoolean(split[7]));
            } else {
                hfBold.setSelected(false);
            }
        }
    }
    
    
    
    private void showRankConfig(String rankConfig){
        if (StringUtils.isEmpty(rankConfig)) {
            Font font = hfsInput.getFont();
            Configs configs = configsMapper.selectById(8);
            String config = configs.getConfig();
            String[] split = ConfigSpliter.split(config);
            rxInput.setText(split[9]);
            ryInput.setText(split[10]);
            rwidthInput.setText(split[11]);
            rheightInput.setText(split[12]);
            rfsInput.setText(((int)font.getSize()) + "");
            rfSelector.setSelectedValue(font.getName());
            srInput.setText("10");
            rowInput.setText("8");
            rfsBold.setSelected(false);
        } else {
            String[] split = ConfigSpliter.split(rankConfig);
            rxInput.setText(split[0]);
            ryInput.setText(split[1]);
            rwidthInput.setText(split[2]);
            rheightInput.setText(split[3]);
            rfsInput.setText(split[4]);
            rfSelector.setSelectedValue(split[5]);
            if (split.length >= 7) {
                srInput.setText(split[6]);
            }

            if (split.length >= 8) {
                rowInput.setText(split[7]);
            }

            if (split.length >= 9) {
                rfsBold.setSelected(Boolean.parseBoolean(split[8]));
            } else {
                rfsBold.setSelected(false);
            }

        }
    }

    @FXML
    void saveHeadConfig() {
        String x = xInput.getText();
        String y = yInput.getText();
        String width = widthInput.getText();
        String height = heightInput.getText();
        String color = colorInput.getValue().toString();
        String fontSize = hfsInput.getText();
        String font = hfSelector.getSelectedValue();
        String bold = hfBold.isSelected() + "";
        
        String configStr = x + ConfigSpliter.SPLITER
                + y + ConfigSpliter.SPLITER
                + width + ConfigSpliter.SPLITER
                + height + ConfigSpliter.SPLITER
                + color + ConfigSpliter.SPLITER
                + fontSize + ConfigSpliter.SPLITER
                + font + ConfigSpliter.SPLITER
                + bold;

        RankStyle rankStyle = rankStyleMapper.selectById(1);
        rankStyle.setHeadConfig(configStr);
        rankStyleMapper.updateById(rankStyle);
        PreviewPane previewPane = controlPane.getPreviewPane();
        if (previewPane != null) {
            previewPane.reload();
        }
    }

    @FXML
    void saveRankConfig() {
        String x = rxInput.getText();
        String y = ryInput.getText();
        String width = rwidthInput.getText();
        String height = rheightInput.getText();
        String fontSize = rfsInput.getText();
        String font = rfSelector.getSelectedValue();
        String rowSpace = srInput.getText();
        String rowCount = rowInput.getText();
        String bold = rfsBold.isSelected() + "";
        String configStr = x + ConfigSpliter.SPLITER
                + y + ConfigSpliter.SPLITER
                + width + ConfigSpliter.SPLITER
                + height + ConfigSpliter.SPLITER
                + fontSize + ConfigSpliter.SPLITER
                + font  + ConfigSpliter.SPLITER
                + rowSpace + ConfigSpliter.SPLITER
                + rowCount + ConfigSpliter.SPLITER
                + bold
                ;

        RankStyle rankStyle = rankStyleMapper.selectById(1);
        rankStyle.setRankConfig(configStr);
        rankStyleMapper.updateById(rankStyle);
        PreviewPane previewPane = controlPane.getPreviewPane();
        if (previewPane != null) {
            previewPane.reload();
        }
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) xInput.getScene().getWindow();
        stage.close();
    }

}
