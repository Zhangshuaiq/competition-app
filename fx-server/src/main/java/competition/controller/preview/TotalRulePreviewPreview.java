package competition.controller.preview;

import cn.hutool.core.io.FileUtil;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.TotalRulePreview;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class TotalRulePreviewPreview extends PreviewPane {

    @FXML
    private AnchorPane container;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ControlPane controlPane;

    @Resource
    private WsMessageHandler messageHandler;

    @FXML
    public void initPage() {
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 打开设置
                if (event.getButton() == MouseButton.SECONDARY) {
                    // RulePreviewAttr.setReloadPane(controlPane.getQuestionPreview());
                    Navigator.navigatorTo("/view/match/RulePreviewAttr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        reload();
    }

    private void show() {
        String path = TextResource.ZGZ;
        List<String> lines = FileUtil.readLines(path, StandardCharsets.UTF_8);

        if (ExtendedScreen.hasShow()) {
            // 外屏展示总规则
            Object res = ExtendedScreen.showWithControl("/view/match/TotalRulePreview.fxml", null);
            if (res != null) {
                controlPane.setQuestionPreview((TotalRulePreview) res);
            }
        }
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.总规则背景())), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false))));
        } catch (FileNotFoundException e) {
            container.setBackground(
                    new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }
        container.getChildren().clear();
        final Configs ruleConfig = configsMapper.selectById(5);
        final String config = ruleConfig.getConfig();
        String details = ruleConfig.getDetails();
        final String ruleContent = String.join("\n", lines);
        Text text = new Text(ruleContent);
        if (StringUtils.isEmpty(details)) {
            final String[] cfgArr = ConfigSpliter.split(config);
            text.setX(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[1])));
            text.setY(ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[2])));
            text.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[3])));
            text.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[4])));
            text.setFill(Color.valueOf(cfgArr[0]));
            text.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[3])));
            container.getChildren().add(text);
        } else {
            final String[] cfgArr = ConfigSpliter.split(details);
            text.setX(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[0])));
            text.setY(ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[1])));
            text.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[2])));
            text.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[3])));
            text.setFill(Color.valueOf(cfgArr[5]));
            Font font = FontMap.load(cfgArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[8])));

            text.setFont(font);
            if (Boolean.valueOf(cfgArr[10])) {
                text.setStroke(text.getFill());
                text.setStrokeWidth(1);
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
            text.setLineSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(cfgArr[14])));
            text.setWrappingWidth(ScreenAdapter.calculateMainX(Integer.parseInt(cfgArr[2])));
            container.getChildren().add(text);
        }
        messageHandler.notifyPage("rule", ruleContent);

    }

    @Override
    public void reload() {
        show();
    }
}
