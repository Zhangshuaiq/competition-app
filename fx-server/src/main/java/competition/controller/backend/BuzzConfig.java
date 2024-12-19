package competition.controller.backend;

import competition.entity.Buzz;
import competition.mapper.BuzzMapper;
import competition.util.BaseDir;
import competition.util.FontMap;
import competition.util.ImageResource;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.text.DecimalFormat;

@Component
public class BuzzConfig {

    @FXML
    private Label buzzSpeedText;

    @FXML
    private CheckBox errorMusicCheck;

    @FXML
    private TextField fontSizeInput;

    @FXML
    private TextField paneBackgroundText;

    @FXML
    private CheckBox playErrorCheck;

    @FXML
    private ComboBox<String> fontSelector;

    @FXML
    private CheckBox lastSuccessCheck;

    @FXML
    private CheckBox successMusicCheck;

    @FXML
    private ComboBox<String> buzzCombox;

    @FXML
    private CheckBox fontItic;

    @FXML
    private CheckBox fontBold;

    @FXML
    private Slider speedAdjust;

    @FXML
    private TextField timeLimitText;

    @FXML
    private ColorPicker fontColor;

    @FXML
    private CheckBox fontUnderline;

    @Resource
    private BuzzMapper buzzMapper;

    @FXML
    public void initialize(){
        buzzCombox.getItems().addAll(
                "直接抢答",
                "标准模式",
                "淘汰模式"
        );
        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));

        DecimalFormat format = new DecimalFormat("#.##");

        speedAdjust.valueProperty().addListener((observable, oldValue, newValue) -> {
            buzzSpeedText.setText(format.format(newValue.doubleValue()));
        });
        speedAdjust.setMin(0);
        speedAdjust.setMax(1);
        Buzz buzz = buzzMapper.selectById(1);
        if (buzz != null) {
            buzzCombox.setValue(buzz.getMode());
            buzzSpeedText.setText(String.valueOf(buzz.getSpeed()));
            speedAdjust.setValue(Double.valueOf(buzz.getSpeed()));

            fontUnderline.setSelected(buzz.getUnderline() != null && buzz.getUnderline() == 1);
            fontItic.setSelected(buzz.getItic() != null && buzz.getItic() == 1);
            fontColor.setValue(Color.valueOf(buzz.getColor()));
            fontSizeInput.setText(String.valueOf(buzz.getFontSize()));
            fontBold.setSelected(buzz.getBold() != null && buzz.getBold() == 1);
            fontSelector.setValue(String.valueOf(buzz.getFont()));

            errorMusicCheck.setSelected(buzz.getErrorMusic() != null && buzz.getErrorMusic() == 1);
            successMusicCheck.setSelected(buzz.getSuccessMusic() != null && buzz.getSuccessMusic() == 1);
            playErrorCheck.setSelected(buzz.getPlayError() != null && buzz.getPlayError() == 1);
            lastSuccessCheck.setSelected(buzz.getLastSuccess() != null && buzz.getLastSuccess() == 1);
            timeLimitText.setText(String.valueOf(buzz.getTimeLimit()));
            paneBackgroundText.setText(buzz.getBackground());
        }

    }

    @FXML
    public void selectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.SUCAI_DIR));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            String name = file.getName();
            paneBackgroundText.setText(name);
        }
    }

    @FXML
    public void updateConfig() {
        //          buzzCombox.setValue(buzz.getMode());
        //            buzzSpeedText.setText(String.valueOf(buzz.getSpeed()));
        //            speedAdjust.setValue(Double.valueOf(buzz.getSpeed()));
        //
        //            fontUnderline.setSelected(buzz.getUnderline() != null && buzz.getUnderline() == 1);
        //            fontItic.setSelected(buzz.getItic() != null && buzz.getItic() == 1);
        //            fontColor.setValue(Color.valueOf(buzz.getColor()));
        //            fontSizeInput.setText(String.valueOf(buzz.getFontSize()));
        //            fontBold.setSelected(buzz.getBold() != null && buzz.getBold() == 1);
        //            fontSelector.setValue(String.valueOf(buzz.getFont()));
        //
        //            errorMusicCheck.setSelected(buzz.getErrorMusic() != null && buzz.getErrorMusic() == 1);
        //            successMusicCheck.setSelected(buzz.getSuccessMusic() != null && buzz.getSuccessMusic() == 1);
        //            playErrorCheck.setSelected(buzz.getPlayError() != null && buzz.getPlayError() == 1);
        //            lastSuccessCheck.setSelected(buzz.getLastSuccess() != null && buzz.getLastSuccess() == 1);
        //            timeLimitText.setText(String.valueOf(buzz.getTimeLimit()));
        //            paneBackgroundText.setText(buzz.getBackground());
        Buzz buzz = buzzMapper.selectById(1);
        boolean exist = true;
        if (buzz == null) {
            exist = false;
            buzz = new Buzz();
            buzz.setId(1);
        }
        String mode = buzzCombox.getValue();
        String speed = buzzSpeedText.getText();
        if (StringUtils.isEmpty(speed)){
            speed = "0";
        }
        boolean underlineSelected = fontUnderline.isSelected();
        boolean fontIticSelected = fontItic.isSelected();
        String color = fontColor.getValue().toString();
        String sizeInputText = fontSizeInput.getText();
        if (StringUtils.isEmpty(sizeInputText)){
            sizeInputText = "12";
        }
        boolean boldSelected = fontBold.isSelected();
        String fontFile = fontSelector.getValue();
        boolean errorMusicCheckSelected = errorMusicCheck.isSelected();
        boolean successMusicCheckSelected = successMusicCheck.isSelected();
        boolean playErrorCheckSelected = playErrorCheck.isSelected();
        boolean lastSuccessCheckSelected = lastSuccessCheck.isSelected();
        String limitTextText = timeLimitText.getText();
        if (StringUtils.isEmpty(limitTextText)){
            limitTextText = "3";
        }
        String text = paneBackgroundText.getText();

        buzz.setMode(mode);
        buzz.setSpeed(speed);
        buzz.setUnderline(underlineSelected?1:0);
        buzz.setItic(fontIticSelected?1:0);
        buzz.setColor(color);
        buzz.setFontSize(Integer.parseInt(sizeInputText));
        buzz.setBold(boldSelected?1:0);
        buzz.setFont(fontFile);
        buzz.setErrorMusic(errorMusicCheckSelected?1:0);
        buzz.setSuccessMusic(successMusicCheckSelected?1:0);
        buzz.setPlayError(playErrorCheckSelected?1:0);
        buzz.setLastSuccess(lastSuccessCheckSelected?1:0);
        buzz.setTimeLimit(Integer.parseInt(limitTextText));
        buzz.setBackground(text);

        if (exist) {
            buzzMapper.updateById(buzz);
        } else {
            buzzMapper.insert(buzz);
        }
    }
    
    

}
