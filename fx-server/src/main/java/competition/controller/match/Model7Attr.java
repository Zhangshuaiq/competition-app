package competition.controller.match;

import competition.entity.Model3Style;
import competition.mapper.Model3Mapper;
import competition.util.ConfigSpliter;
import competition.util.GlobalCache;
import competition.util.Navigator;
import competition.util.PreviewStack;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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
    private CheckBox hideCtl;

    @FXML
    private TextField scInput;

    @FXML
    private TextField xInput;
    
    @Resource
    private Model3Mapper model3Mapper;

    
    @FXML
    public void initialize(){
        Model3Style model3Style = model3Mapper.selectById(2);
        String titleStyle = model3Style.getTitleStyle();
        hideCtl.setSelected(Boolean.parseBoolean(model3Style.getHeadStyle()));
        if (StringUtils.isEmpty(titleStyle)) {
            colInput.setText("4");
            srInput.setText("20");
            scInput.setText("20");
            widthInput.setText((int)((Navigator.getWidth() - 40) / 4) + "");
            heightInput.setText("287");
            xInput.setText("10");
            yInput.setText("0");
        } else {
            String[] split = titleStyle.split(ConfigSpliter.SPLITER);
            colInput.setText(split[0]);
            srInput.setText(split[1]);
            scInput.setText(split[2]);
            widthInput.setText(split[3]);
            heightInput.setText(split[4]);
            xInput.setText(split[5]);
            yInput.setText(split[6]);
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
        yInput.getText();
        Model3Style model3Style = model3Mapper.selectById(2);
        model3Style.setTitleStyle(value);
        model3Style.setHeadStyle(hideCtl.isSelected() + "");
        model3Mapper.updateById(model3Style);
        PreviewStack.reloadStyle();
    }
    
    
    @FXML
    public void closeWindow(){
        Stage stage = (Stage) (xInput.getScene().getWindow());
        stage.close();
    }
    
}
