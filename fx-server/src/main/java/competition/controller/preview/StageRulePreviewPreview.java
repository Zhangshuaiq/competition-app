package competition.controller.preview;


import competition.controller.ControlPane;
import competition.controller.match.StageQuestionPreview;
import competition.controller.match.StageRulePreview;
import competition.controller.match.StageRulePreviewAttr;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
public class StageRulePreviewPreview extends PreviewPane{
    
    private static Stages currentStage;
    
    private Stages innerStage;
    
    @FXML
    private AnchorPane container;
    
    @Resource
    private ControlPane controlPane;
    
    @Resource
    private ConfigsMapper configsMapper;

    public static void setCurrentStage(Stages currentStage) {
        StageRulePreviewPreview.currentStage = currentStage;
    }


    @Override
    public void reload() {
        showRuleContent();
    }

    @FXML
    public void initialize(){
       // PreviewStack.addPreview(this);
        innerStage = currentStage;
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    StageRulePreviewAttr.setReloadPane(controlPane.getQuestionPreview());
                    Navigator.navigatorTo("/view/match/StageRulePreviewAttr.fxml", "", "/view/style.css", false, false);
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    // 进入选题列表
//                    StageQuestionPreviewPreview.setCurrentStage(innerStage);
//                    controlPane.setPreviewPane(
//                            Navigator.preview("/view/preview/StageQuestionPreview.fxml", "", "/view/style.css", true, true)
//                    );
                }
            }
        });
        
      
        //PreviewStack.addPreview(this);
        showRuleContent();
    }

    @Override
    public AnchorPane getPane() {
        return container;
    }
    
    private void showRuleContent(){
        if (ExtendedScreen.hasShow()) {
            StageRulePreview.setCurrentStage(innerStage);
            // 进入比赛
            Object res = ExtendedScreen.showWithControl("/view/match/StageRulePreview.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((StageRulePreview) res);
            }
        }

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.环节规则背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
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
            Font font = Font.font(FontMap.getFont(cfgArr[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(cfgArr[8]));
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
