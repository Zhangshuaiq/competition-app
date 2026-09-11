package competition.controller.match;

import competition.entity.Model3Style;
import competition.mapper.Model3Mapper;
import competition.util.ConfigSpliter;
import competition.util.GlobalCache;
import competition.util.Navigator;
import competition.util.PreviewStack;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
public class Model7Attr {

    @FXML
    private TextField colInput;

    @FXML
    private TextField yInput;

    @FXML
    private TextField srInput;

    @FXML
    private TextField widthInput;

    @FXML
    private TextField heightInput;

    @FXML
    private CheckBox hideCtl, hideCtl1;

    @FXML
    private TextField scInput;

    @FXML
    private TextField xInput;
    
    @Resource
    private Model3Mapper model3Mapper;

    @FXML
    private TextField wfInput, thInput, qfInput;

    @FXML
    private ColorPicker wfColor, qfColor, bkColor;


    @FXML
    private CheckBox scoreSortBox;

    
    @FXML
    public void initialize(){
        Model3Style model3Style = model3Mapper.selectById(2);
        String titleStyle = model3Style.getTitleStyle();
        String[] split1 = model3Style.getHeadStyle().split(",");
        if (split1.length > 0) {
            hideCtl.setSelected(Boolean.parseBoolean(split1[0]));
        } else {
            hideCtl.setSelected(false);
        }
        if (split1.length > 1) {
            hideCtl1.setSelected(Boolean.parseBoolean(split1[1]));
        } else {
            hideCtl1.setSelected(false);
        }
        if (StringUtils.isEmpty(titleStyle)) {
            colInput.setText("4");
            srInput.setText("20");
            scInput.setText("20");
            widthInput.setText((int)((Navigator.getWidth() - 40) / 4) + "");
            heightInput.setText("287");
            xInput.setText("10");
            yInput.setText("0");
            wfInput.setText((int) (wfInput.getFont().getSize()) + "");
            qfInput.setText((int) (qfInput.getFont().getSize()) + "");
            thInput.setText("5");
            wfColor.setValue(Color.BLACK);
            qfColor.setValue(Color.BLACK);
            bkColor.setValue(Color.BLACK);
            scoreSortBox.setSelected(false);
        } else {
            String[] split = ConfigSpliter.split(titleStyle);
            colInput.setText(split[0]);
            srInput.setText(split[1]);
            scInput.setText(split[2]);
            widthInput.setText(split[3]);
            heightInput.setText(split[4]);
            xInput.setText(split[5]);
            yInput.setText(split[6]);
            if (split.length > 7) {
                wfInput.setText(split[7]);
            } else {
                wfInput.setText((int) (wfInput.getFont().getSize()) + "");
            }
            if (split.length > 8) {
                thInput.setText(split[8]);
            } else {
                thInput.setText("5");
            }
            if (split.length > 9) {
                qfInput.setText(split[9]);
            } else {
                qfInput.setText((int) (qfInput.getFont().getSize()) + "");
            }
            if (split.length > 10) {
                wfColor.setValue(Color.valueOf(split[10]));
            } else {
                wfColor.setValue(Color.BLACK);
            }
            if (split.length > 11) {
                qfColor.setValue(Color.valueOf(split[11]));
            } else {
                qfColor.setValue(Color.BLACK);
            }
            if (split.length > 12) {
                bkColor.setValue(Color.valueOf(split[12]));
            } else {
                bkColor.setValue(Color.BLACK);
            }
            if (split.length > 13) {
                scoreSortBox.setSelected(Boolean.parseBoolean(split[13]));
            } else {
                scoreSortBox.setSelected(false);
            }
        }
    }
    
    
    @FXML
    public void saveConfig(){
        // save to db
        String value = colInput.getText() + ConfigSpliter.SPLITER +
                srInput.getText() + ConfigSpliter.SPLITER +
                scInput.getText() + ConfigSpliter.SPLITER +
                widthInput.getText() + ConfigSpliter.SPLITER +
                heightInput.getText() + ConfigSpliter.SPLITER +
                xInput.getText() + ConfigSpliter.SPLITER +
                yInput.getText() + ConfigSpliter.SPLITER +
                wfInput.getText() + ConfigSpliter.SPLITER +
                thInput.getText() + ConfigSpliter.SPLITER +
                qfInput.getText() + ConfigSpliter.SPLITER +
                wfColor.getValue().toString() + ConfigSpliter.SPLITER +
                qfColor.getValue().toString() + ConfigSpliter.SPLITER +
                bkColor.getValue().toString() + ConfigSpliter.SPLITER +
                scoreSortBox.isSelected();
        Model3Style model3Style = model3Mapper.selectById(2);
        model3Style.setTitleStyle(value);
        String headStyle = hideCtl.isSelected() + "," + hideCtl1.isSelected();
        model3Style.setHeadStyle(headStyle);
        model3Mapper.updateById(model3Style);
        PreviewStack.reloadStyle();
    }
    
    
    @FXML
    public void closeWindow(){
        Stage stage = (Stage) (xInput.getScene().getWindow());
        stage.close();
    }
    
}
