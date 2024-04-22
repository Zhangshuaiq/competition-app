package competition.controller.match;

import competition.controller.ControlPane;
import competition.entity.AnswerStyle;
import competition.mapper.AnswerStyleMapper;
import competition.util.AlignmentUtil;
import competition.util.ConfigSpliter;
import competition.util.FontMap;
import competition.util.PreviewStack;
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
public class AnswerAttr {
    
    @Resource
    private AnswerStyleMapper answerStyleMapper;
    
    @FXML
    private ComboBox<String> l1Selector, l2Selector, l3Selector, l4Selector, l5Selector;

    @FXML
    private TextField f1Size, f2Size, f3Size, f4Size, f5Size;

    @FXML
    private ComboBox<String> f4Selector;

    @FXML
    private ColorPicker f5Color;

    @FXML
    private TextField widthInput;

    @FXML
    private ComboBox<String> f2Selector;

    @FXML
    private ColorPicker f3BackColor;

    @FXML
    private CheckBox f4IT;

    @FXML
    private CheckBox f2IT;

    @FXML
    private CheckBox f3BD;

    @FXML
    private ColorPicker f5BackColor;

    @FXML
    private CheckBox f1BD;

    @FXML
    private ColorPicker f1Color;

    @FXML
    private TextField f2Path;

    @FXML
    private TextField colsInput;

    @FXML
    private CheckBox f5Fill;

    @FXML
    private ColorPicker f4Color;

    @FXML
    private CheckBox f4UD;

    @FXML
    private CheckBox f2UD;

    @FXML
    private ComboBox<String> f1Selector;

    @FXML
    private TextField f1Path;

    @FXML
    private CheckBox f4DL;

    @FXML
    private CheckBox f2DL;

    @FXML
    private CheckBox f4Fill;

    @FXML
    private CheckBox f5BD;

    @FXML
    private ComboBox<String> f5Selector;

    @FXML
    private TextField colInput;

    @FXML
    private TextField f4Path;

    @FXML
    private ColorPicker f2Color;

    @FXML
    private ComboBox<String> f3Selector;

    @FXML
    private ColorPicker f4BackColor;

    @FXML
    private TextField f5Path;

    @FXML
    private CheckBox f1Fill;

    @FXML
    private CheckBox f3Fill;

    @FXML
    private TextField xInput;

    @FXML
    private TextField f3Path;

    @FXML
    private CheckBox f5IT;

    @FXML
    private CheckBox f3IT;

    @FXML
    private CheckBox f1IT;

    @FXML
    private ColorPicker f1BackColor;

    @FXML
    private TextField heightInput;

    @FXML
    private CheckBox f2Fill;

    @FXML
    private CheckBox f2BD;

    @FXML
    private ColorPicker f2BackColor;

    @FXML
    private TextField yInput;

    @FXML
    private TextField rowsInput;

    @FXML
    private ColorPicker f3Color;

    @FXML
    private CheckBox f5UD;


    @FXML
    private CheckBox f3UD;

    @FXML
    private CheckBox f1UD;

    @FXML
    private CheckBox f5DL;

    @FXML
    private CheckBox f3DL;

    @FXML
    private CheckBox f1DL;

    @FXML
    private CheckBox f4BD;

    @Resource
    private ControlPane controlPane;
    
    
    
    @FXML
    public void saveAttr(){
        // 保存到数据库
        AnswerStyle answerStyle = answerStyleMapper.selectById(1);


        String style1 = colInput.getText() + ConfigSpliter.SPLITER
                + rowsInput.getText() + ConfigSpliter.SPLITER
                + colsInput.getText() + ConfigSpliter.SPLITER
                + xInput.getText() + ConfigSpliter.SPLITER
                + yInput.getText() + ConfigSpliter.SPLITER
                + widthInput.getText() + ConfigSpliter.SPLITER
                + heightInput.getText() ;

        String style2 = f1Selector.getValue() + ConfigSpliter.SPLITER
                + f1Color.getValue() + ConfigSpliter.SPLITER
                + l1Selector.getValue() + ConfigSpliter.SPLITER
                + f1Size.getText() + ConfigSpliter.SPLITER
                + f1DL.isSelected() + ConfigSpliter.SPLITER
                + f1IT.isSelected() + ConfigSpliter.SPLITER
                + f1BD.isSelected() + ConfigSpliter.SPLITER
                + f1UD.isSelected() + ConfigSpliter.SPLITER
                + f1Fill.isSelected() + ConfigSpliter.SPLITER
                + f1BackColor.getValue() + ConfigSpliter.SPLITER
                + f1Path.getText()
                ;

        String style3 = f2Selector.getValue() + ConfigSpliter.SPLITER
                + f2Color.getValue() + ConfigSpliter.SPLITER
                + l2Selector.getValue() + ConfigSpliter.SPLITER
                + f2Size.getText() + ConfigSpliter.SPLITER
                + f2DL.isSelected() + ConfigSpliter.SPLITER
                + f2IT.isSelected() + ConfigSpliter.SPLITER
                + f2BD.isSelected() + ConfigSpliter.SPLITER
                + f2UD.isSelected() + ConfigSpliter.SPLITER
                + f2Fill.isSelected() + ConfigSpliter.SPLITER
                + f2BackColor.getValue() + ConfigSpliter.SPLITER
                + f2Path.getText()
                ;

        String style4 =f3Selector.getValue() + ConfigSpliter.SPLITER
                + f3Color.getValue() + ConfigSpliter.SPLITER
                + l3Selector.getValue() + ConfigSpliter.SPLITER
                + f3Size.getText() + ConfigSpliter.SPLITER
                + f3DL.isSelected() + ConfigSpliter.SPLITER
                + f3IT.isSelected() + ConfigSpliter.SPLITER
                + f3BD.isSelected() + ConfigSpliter.SPLITER
                + f3UD.isSelected() + ConfigSpliter.SPLITER
                + f3Fill.isSelected() + ConfigSpliter.SPLITER
                + f3BackColor.getValue() + ConfigSpliter.SPLITER
                + f3Path.getText()
                ;

        String style5 = f4Selector.getValue() + ConfigSpliter.SPLITER
                + f4Color.getValue() + ConfigSpliter.SPLITER
                + l4Selector.getValue() + ConfigSpliter.SPLITER
                + f4Size.getText() + ConfigSpliter.SPLITER
                + f4DL.isSelected() + ConfigSpliter.SPLITER
                + f4IT.isSelected() + ConfigSpliter.SPLITER
                + f4BD.isSelected() + ConfigSpliter.SPLITER
                + f4UD.isSelected() + ConfigSpliter.SPLITER
                + f4Fill.isSelected() + ConfigSpliter.SPLITER
                + f4BackColor.getValue() + ConfigSpliter.SPLITER
                + f4Path.getText()
                ;

        String style6 = f5Selector.getValue() + ConfigSpliter.SPLITER
                + f5Color.getValue() + ConfigSpliter.SPLITER
                + l5Selector.getValue() + ConfigSpliter.SPLITER
                + f5Size.getText() + ConfigSpliter.SPLITER
                + f5DL.isSelected() + ConfigSpliter.SPLITER
                + f5IT.isSelected() + ConfigSpliter.SPLITER
                + f5BD.isSelected() + ConfigSpliter.SPLITER
                + f5UD.isSelected() + ConfigSpliter.SPLITER
                + f5Fill.isSelected() + ConfigSpliter.SPLITER
                + f5BackColor.getValue() + ConfigSpliter.SPLITER
                + f5Path.getText()
                ;

        answerStyle.setStyle1(style1);
        answerStyle.setStyle2(style2);
        answerStyle.setStyle3(style3);
        answerStyle.setStyle4(style4);
        answerStyle.setStyle5(style5);
        answerStyle.setStyle6(style6);

        answerStyleMapper.updateById(answerStyle);
        controlPane.getPreviewPane().reload();
        closeDialog();
    }
    
    
    @FXML
    public void closeDialog(){
        Stage stage = (Stage) (xInput.getScene().getWindow());
        stage.close();
    }
    
    
    // 加载数据库的数据
    private void showAttr(){
        AnswerStyle answerStyle = answerStyleMapper.selectById(1);

        if (!StringUtils.isEmpty(answerStyle.getStyle1())) {
            //  String style1 = colInput.getText() + ConfigSpliter.SPLITER
            //                + rowsInput.getText() + ConfigSpliter.SPLITER
            //                + colsInput.getText() + ConfigSpliter.SPLITER
            //                + xInput.getText() + ConfigSpliter.SPLITER
            //                + yInput.getText() + ConfigSpliter.SPLITER
            //                + widthInput.getText() + ConfigSpliter.SPLITER
            //                + heightInput.getText() ;
            String[] split = answerStyle.getStyle1().split(ConfigSpliter.SPLITER);
            colInput.setText(split[0]);
            rowsInput.setText(split[1]);
            colsInput.setText(split[2]);
            xInput.setText(split[3]);
            yInput.setText(split[4]);
            widthInput.setText(split[5]);
            heightInput.setText(split[6]);
        }

        if (!StringUtils.isEmpty(answerStyle.getStyle2())) {
            //  String style2 = f1Selector.getValue() + ConfigSpliter.SPLITER
            //                + f1Color.getValue() + ConfigSpliter.SPLITER
            //                + l1Selector.getValue() + ConfigSpliter.SPLITER
            //                + f1Size.getText() + ConfigSpliter.SPLITER
            //                + f1DL.isSelected() + ConfigSpliter.SPLITER
            //                + f1IT.isSelected() + ConfigSpliter.SPLITER
            //                + f1BD.isSelected() + ConfigSpliter.SPLITER
            //                + f1UD.isSelected() + ConfigSpliter.SPLITER
            //                + f1Fill.isSelected() + ConfigSpliter.SPLITER
            //                + f1BackColor.getValue() + ConfigSpliter.SPLITER
            //                + f1Path.getText() 
            //                ;
            String[] split = answerStyle.getStyle2().split(ConfigSpliter.SPLITER);
            f1Selector.setValue(split[0]);
            f1Color.setValue(Color.valueOf(split[1]));
            l1Selector.setValue(split[2]);
            f1Size.setText(split[3]);
            f1DL.setSelected(Boolean.valueOf(split[4]));
            f1IT.setSelected(Boolean.valueOf(split[5]));
            f1BD.setSelected(Boolean.valueOf(split[6]));
            f1UD.setSelected(Boolean.valueOf(split[7]));
            f1Fill.setSelected(Boolean.valueOf(split[8]));
            f1BackColor.setValue(Color.valueOf(split[9]));
            if (split.length >= 11) {
                f1Path.setText(split[10]);
            }
        }

        if (!StringUtils.isEmpty(answerStyle.getStyle3())) {
            String[] split = answerStyle.getStyle3().split(ConfigSpliter.SPLITER);
            f2Selector.setValue(split[0]);
            f2Color.setValue(Color.valueOf(split[1]));
            l2Selector.setValue(split[2]);
            f2Size.setText(split[3]);
            f2DL.setSelected(Boolean.valueOf(split[4]));
            f2IT.setSelected(Boolean.valueOf(split[5]));
            f2BD.setSelected(Boolean.valueOf(split[6]));
            f2UD.setSelected(Boolean.valueOf(split[7]));
            f2Fill.setSelected(Boolean.valueOf(split[8]));
            f2BackColor.setValue(Color.valueOf(split[9]));
            if (split.length >= 11) {
                f2Path.setText(split[10]);
            }
        }

        if (!StringUtils.isEmpty(answerStyle.getStyle4())) {
            String[] split = answerStyle.getStyle4().split(ConfigSpliter.SPLITER);
            f3Selector.setValue(split[0]);
            f3Color.setValue(Color.valueOf(split[1]));
            l3Selector.setValue(split[2]);
            f3Size.setText(split[3]);
            f3DL.setSelected(Boolean.valueOf(split[4]));
            f3IT.setSelected(Boolean.valueOf(split[5]));
            f3BD.setSelected(Boolean.valueOf(split[6]));
            f3UD.setSelected(Boolean.valueOf(split[7]));
            f3Fill.setSelected(Boolean.valueOf(split[8]));
            f3BackColor.setValue(Color.valueOf(split[9]));
            if (split.length >= 11) {
                f3Path.setText(split[10]);
            }
        }


        if (!StringUtils.isEmpty(answerStyle.getStyle5())) {
            String[] split = answerStyle.getStyle5().split(ConfigSpliter.SPLITER);
            f4Selector.setValue(split[0]);
            f4Color.setValue(Color.valueOf(split[1]));
            l4Selector.setValue(split[2]);
            f4Size.setText(split[3]);
            f4DL.setSelected(Boolean.valueOf(split[4]));
            f4IT.setSelected(Boolean.valueOf(split[5]));
            f4BD.setSelected(Boolean.valueOf(split[6]));
            f4UD.setSelected(Boolean.valueOf(split[7]));
            f4Fill.setSelected(Boolean.valueOf(split[8]));
            f4BackColor.setValue(Color.valueOf(split[9]));
            if (split.length >= 11) {
                f4Path.setText(split[10]);
            }
        }


        if (!StringUtils.isEmpty(answerStyle.getStyle6())) {
            String[] split = answerStyle.getStyle6().split(ConfigSpliter.SPLITER);
            f5Selector.setValue(split[0]);
            f5Color.setValue(Color.valueOf(split[1]));
            l5Selector.setValue(split[2]);
            f5Size.setText(split[3]);
            f5DL.setSelected(Boolean.valueOf(split[4]));
            f5IT.setSelected(Boolean.valueOf(split[5]));
            f5BD.setSelected(Boolean.valueOf(split[6]));
            f5UD.setSelected(Boolean.valueOf(split[7]));
            f5Fill.setSelected(Boolean.valueOf(split[8]));
            f5BackColor.setValue(Color.valueOf(split[9]));
            if (split.length >= 11) {
                f5Path.setText(split[10]);
            }
        }
        
    }
    
    
    @FXML
    public void initialize(){

        // 默认数据
        
        colInput.setText("1");
        rowsInput.setText("0");
        colsInput.setText("0");
        xInput.setText("50");
        yInput.setText("50");
        widthInput.setText("500");
        heightInput.setText("50");
        
        Font defaultFont = Font.getDefault();

        f1Color.setValue(Color.BLACK);
        f2Color.setValue(Color.BLACK);
        f3Color.setValue(Color.BLACK);
        f4Color.setValue(Color.BLACK);
        f5Color.setValue(Color.BLACK);
        f1BackColor.setValue(Color.WHITE);
        f2BackColor.setValue(Color.WHITE);
        f3BackColor.setValue(Color.WHITE);
        f4BackColor.setValue(Color.WHITE);
        f5BackColor.setValue(Color.WHITE);
        f1Selector.setItems(FXCollections.observableList(FontMap.fontNames()));
        f2Selector.setItems(FXCollections.observableList(FontMap.fontNames()));
        f3Selector.setItems(FXCollections.observableList(FontMap.fontNames()));
        f4Selector.setItems(FXCollections.observableList(FontMap.fontNames()));
        f5Selector.setItems(FXCollections.observableList(FontMap.fontNames()));

        f1Selector.setValue(defaultFont.getName());
        f2Selector.setValue(defaultFont.getName());
        f3Selector.setValue(defaultFont.getName());
        f4Selector.setValue(defaultFont.getName());
        f5Selector.setValue(defaultFont.getName());
        
        f1Size.setText((int)defaultFont.getSize() + "");
        f2Size.setText((int)defaultFont.getSize() + "");
        f3Size.setText((int)defaultFont.getSize() + "");
        f4Size.setText((int)defaultFont.getSize() + "");
        f5Size.setText((int)defaultFont.getSize() + "");

        l1Selector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        l2Selector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        l3Selector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        l4Selector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        l5Selector.setItems(FXCollections.observableList(AlignmentUtil.alignNames()));
        l1Selector.setValue("居中对齐");
        l2Selector.setValue("居中对齐");
        l3Selector.setValue("居中对齐");
        l4Selector.setValue("居中对齐");
        l5Selector.setValue("居中对齐");
        
        showAttr();
    }
}
