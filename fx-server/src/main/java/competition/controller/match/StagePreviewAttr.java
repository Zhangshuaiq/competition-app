package competition.controller.match;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.Configs;
import competition.entity.StagesStyle;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesStyleMapper;
import competition.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;

@Component
public class StagePreviewAttr {

    @FXML
    private TextField backgroundPath;

    @FXML
    private CheckBox deleteChoice;

    @FXML
    private ColorPicker colorInput, fontColorInput;

    @FXML
    private ComboBox<String> textAlignSelector;

    @FXML
    private TextField yInput;

    @FXML
    private TextField widthInput;

    @FXML
    private ComboBox<String> fontSelector;

    @FXML
    private CheckBox iticChoice;

    @FXML
    private TextField xInput;

    @FXML
    private TextField fontSize;

    @FXML
    private CheckBox underlineChoice;

    @FXML
    private TextField heightInput;

    @FXML
    private CheckBox boldChoice;
    
    @FXML
    private CheckBox backgroundChoice;

    @FXML
    private TextField spaceInput, colSpaceInput, wordInput, colNumber;
    
    @Resource
    private ConfigsMapper configsMapper;
    
    @Resource
    private StagesStyleMapper stagesStyleMapper;
    
    private static int configId;
    
    private static Integer level;
    

    public static void setConfigId(int configId) {
        StagePreviewAttr.configId = configId;
    }


    public static void setLevel(Integer level) {
        StagePreviewAttr.level = level;
    }

    @FXML
    public void initialize(){
        textAlignSelector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        fontSelector.setItems(FXCollections.observableList(FontMap.fontNames()));
        previewData();
    }
    

    @FXML
    public void selectImg(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(BaseDir.BACKGROUND_DIR));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(null);
        if (file!= null) {
            backgroundPath.setText(file.getAbsolutePath());
        }
    }
    
    
    @FXML
    public void cancelImg(){
        backgroundPath.setText("");
    }
    
    private void previewData(){
        final Configs configs = configsMapper.selectById(configId);
        final String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
            //  hjbh.setValue(Color.valueOf(huanjieArr[0]));
            //            bhbj.setValue(Color.valueOf(huanjieArr[1]));
            //            hjmc.setValue(Color.valueOf(huanjieArr[2]));
            //            mcbj.setValue(Color.valueOf(huanjieArr[3]));
            //            hjx.setText(huanjieArr[4]);
            //            hjy.setText(huanjieArr[5]);
            //            hjk.setText(huanjieArr[6]);
            //            hjg.setText(huanjieArr[7]);
            //            xscjan.setSelected(Boolean.valueOf(huanjieArr[8]));
            //            xshjlb.setSelected(Boolean.valueOf(huanjieArr[9]));
            widthInput.setText(huanjieArr[6]);
            heightInput.setText(huanjieArr[7]);
            xInput.setText(huanjieArr[4]);
            yInput.setText(huanjieArr[5]);
            colorInput.setValue(Color.valueOf(huanjieArr[3]));
            textAlignSelector.setValue(AlignmentUtil.alignNames().get(0));
            spaceInput.setText("0");
            colSpaceInput.setText("0");
            wordInput.setText("0");
            colNumber.setText("1");
          //  fontSelector
        }

        StagesStyle details ;
        if (level == null) {
            details = stagesStyleMapper.selectOne(new LambdaQueryWrapper<StagesStyle>()
                    .isNull(StagesStyle::getParentId)
            );
        } else {
            details = stagesStyleMapper.selectOne(new LambdaQueryWrapper<StagesStyle>()
                    .eq(StagesStyle::getParentId, level)
            );
        }
        String defaultStyle = "550%%300%%350%%80%%false%%0xffffffff%%%%默认.ttf%%28%%false%%false%%false%%false%%居中对齐%%60%%0%%2%%60%%0x000000ff";
        String[] detailArr = defaultStyle.split(ConfigSpliter.SPLITER);
        if (StringUtils.isEmpty(details)) {
//            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//            // 获取默认字体
//            Font defaultFont = new JLabel().getFont();
//            fontSelector.setValue(defaultFont.getFamily());
//            fontSize.setText(defaultFont.getSize() + "");
//            iticChoice.setSelected(false);
//            deleteChoice.setSelected(false);
//            spaceInput.setText("0");
        } else {
            detailArr = details.getStyle().split(ConfigSpliter.SPLITER);
        }

        widthInput.setText(detailArr[2]);
        heightInput.setText(detailArr[3]);
        xInput.setText(detailArr[0]);
        yInput.setText(detailArr[1]);
        backgroundChoice.setSelected(Boolean.valueOf(detailArr[4]));
        colorInput.setValue(Color.valueOf(detailArr[5].replace("0x", "#")));
        backgroundPath.setText(detailArr[6]);
        fontSelector.setValue(detailArr[7]);
        fontSize.setText(Integer.parseInt(detailArr[8]) + "");
        deleteChoice.setSelected(Boolean.valueOf(detailArr[9]));
        boldChoice.setSelected(Boolean.valueOf(detailArr[10]));
        iticChoice.setSelected(Boolean.valueOf(detailArr[11]));
        underlineChoice.setSelected(Boolean.valueOf(detailArr[12]));
        textAlignSelector.setValue(detailArr[13]);
        spaceInput.setText(detailArr[14]);
        if (detailArr.length >= 16) {
            wordInput.setText(detailArr[15]);
        }
        if (detailArr.length >= 17) {
            colNumber.setText(detailArr[16]);
        }
        if (detailArr.length >= 18) {
            colSpaceInput.setText(detailArr[17]);
        }
        if (detailArr.length >= 19) {
            fontColorInput.setValue(Color.valueOf(detailArr[18].replace("0x", "#")));
        }
    }

    public void closeStage(MouseEvent event) {
        Stage stage = (Stage) ((Node)(event.getTarget())).getScene().getWindow();
        stage.close();
    }
    
    
    @FXML
    public void saveConfig(){
        String x = xInput.getText();
        String y = yInput.getText();
        String width = widthInput.getText();
        String height = heightInput.getText();
        boolean fillBackground = backgroundChoice.isSelected();
        final String backgroundColor = colorInput.getValue().toString();
        final String fontColor = fontColorInput.getValue().toString();
        String backgroundImage = backgroundPath.getText();
        final String fontFamily = fontSelector.getValue();
        String fsize = fontSize.getText();
        final boolean delete = deleteChoice.isSelected();
        final boolean bold = boldChoice.isSelected();
        final boolean itic = iticChoice.isSelected();
        final boolean underlne = underlineChoice.isSelected();
        final String alignValue = textAlignSelector.getValue();
        final String rowSpace = spaceInput.getText();
        final String colSpace = colSpaceInput.getText();

        final Configs configs = configsMapper.selectById(configId);
        final String config = configs.getConfig();
        final String[] huanjieArr = config.split(ConfigSpliter.SPLITER);
        String huanjieVal = huanjieArr[0] + ConfigSpliter.SPLITER
                + huanjieArr[1]  + ConfigSpliter.SPLITER
                + huanjieArr[2]  + ConfigSpliter.SPLITER
                + colorInput.getValue()  + ConfigSpliter.SPLITER
                + x + ConfigSpliter.SPLITER
                + y + ConfigSpliter.SPLITER
                + width + ConfigSpliter.SPLITER
                + height + ConfigSpliter.SPLITER
                + huanjieArr[8]  + ConfigSpliter.SPLITER
                + huanjieArr[9] ;
        
        String detail =  x + ConfigSpliter.SPLITER
                + y  + ConfigSpliter.SPLITER
                + width  + ConfigSpliter.SPLITER
                + height  + ConfigSpliter.SPLITER
                + fillBackground + ConfigSpliter.SPLITER
                + backgroundColor + ConfigSpliter.SPLITER
                + backgroundImage + ConfigSpliter.SPLITER
                + fontFamily + ConfigSpliter.SPLITER
                + fsize  + ConfigSpliter.SPLITER
                + delete + ConfigSpliter.SPLITER
                + bold + ConfigSpliter.SPLITER
                + itic + ConfigSpliter.SPLITER
                + underlne + ConfigSpliter.SPLITER
                + alignValue + ConfigSpliter.SPLITER
                + rowSpace + ConfigSpliter.SPLITER
                + wordInput.getText() + ConfigSpliter.SPLITER
                + colNumber.getText() + ConfigSpliter.SPLITER
                + colSpace + ConfigSpliter.SPLITER
                + fontColor
                ;
        configs.setConfig(huanjieVal);
        
        
        StagesStyle stagesStyle = new StagesStyle();
        stagesStyle.setParentId(level);
        stagesStyle.setStyle(detail);
        StagesStyle stagesStyle1;
        if (level == null) {
            stagesStyle1 = stagesStyleMapper.selectOne(new LambdaQueryWrapper<StagesStyle>()
                    .isNull(StagesStyle::getParentId)
            );
        } else {
            stagesStyle1 = stagesStyleMapper.selectOne(new LambdaQueryWrapper<StagesStyle>()
                    .eq(StagesStyle::getParentId, level)
            );
        }
        if (stagesStyle1 == null) {
            stagesStyleMapper.insert(stagesStyle);
        } else {
            stagesStyle.setId(stagesStyle1.getId());
            stagesStyleMapper.updateById(stagesStyle);
        }
        
        configs.setDetails(detail);
        configsMapper.updateById(configs);
        PreviewStack.reloadStyle();
    }
}
