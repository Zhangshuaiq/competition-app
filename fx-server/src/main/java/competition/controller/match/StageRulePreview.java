package competition.controller.match;


import competition.entity.Configs;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Component
@Scope("prototype")
public class StageRulePreview implements ReloadPane{
    
    private static Stages currentStage;
    
    @FXML
    private AnchorPane container;
    
    @Resource
    private ConfigsMapper configsMapper;

    public static void setCurrentStage(Stages currentStage) {
        StageRulePreview.currentStage = currentStage;
    }

    @Override
    public Node getPane() {
        return container;
    }
    
    
    @FXML
    public void initialize(){
        showRuleContent();
    }
    
    
    private void showRuleContent(){

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.环节规则背景)), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, true, true)
            )));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        container.getChildren().clear();
        final Configs ruleConfig = configsMapper.selectById(5);

        if (currentStage == null) {
            return;
        }
        final String config = ruleConfig.getConfig();
        String details = ruleConfig.getDetails();
        String ruleType = currentStage.getRuleType();
        final String ruleContent = currentStage.getRuleContent();
        Text text = new Text(ruleContent);
        if (StringUtils.isEmpty(details)) {
            final String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            text.setX(Integer.parseInt(cfgArr[1]));
            text.setY(Integer.parseInt(cfgArr[2]));
            text.prefWidth(Integer.parseInt(cfgArr[3]));
            text.prefHeight(Integer.parseInt(cfgArr[4]));
            text.setFill(Color.valueOf(cfgArr[0]));
            text.setWrappingWidth(Integer.parseInt(cfgArr[3]));
            container.getChildren().add(text);
        } else {
            final String[] cfgArr = details.split(ConfigSpliter.SPLITER);
            text.setX(Integer.parseInt(cfgArr[0]));
            text.setY(Integer.parseInt(cfgArr[1]));
            text.prefWidth(Integer.parseInt(cfgArr[2]));
            text.prefHeight(Integer.parseInt(cfgArr[3]));
            text.setFill(Color.valueOf(cfgArr[5]));
            Font font = Font.font(cfgArr[7], FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(cfgArr[8]));
            text.setFont(font);
            if (Boolean.valueOf(cfgArr[10])) {
                text.setStroke(text.getFill());
            } else {
                text.setStroke(null);
            }
            text.getTransforms().clear();
            if (Boolean.valueOf(cfgArr[11])) {
                text.getTransforms().add(new Shear(-0.3, 0));
            }
            text.setUnderline(Boolean.valueOf(cfgArr[12]));
            text.setStrikethrough(Boolean.valueOf(cfgArr[9]));
            text.setTextAlignment(AlignmentUtil.getTextAlign(cfgArr[13]));
            text.setLineSpacing(Integer.parseInt(cfgArr[14]));
            text.setWrappingWidth(Integer.parseInt(cfgArr[2]));
            container.getChildren().add(text);
        }
    }
}
