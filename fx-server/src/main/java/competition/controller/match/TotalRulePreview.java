package competition.controller.match;

import cn.hutool.core.io.FileUtil;
import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class TotalRulePreview implements ReloadPane {

    @FXML
    private AnchorPane container;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ControlPane controlPane;


    @FXML
    public void initialize() {
        reload();
    }

    private void show() {
        String path = TextResource.ZGZ;
        List<String> lines = FileUtil.readLines(path, StandardCharsets.UTF_8);

        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.总规则背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }
        container.getChildren().clear();
        final Configs ruleConfig = configsMapper.selectById(5);
        final String config = ruleConfig.getConfig();
        String details = ruleConfig.getDetails();
        final String ruleContent = String.join("\n", lines);
        Text text = new Text(ruleContent);
        if (StringUtils.isEmpty(details)) {
            final String[] cfgArr = config.split(ConfigSpliter.SPLITER);
            text.setX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[1])));
            text.setY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[2])));
            text.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[3])));
            text.prefHeight(ScreenAdapter.calculateExH(Integer.parseInt(cfgArr[4])));
            text.setFill(Color.valueOf(cfgArr[0]));
            text.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[3])));
            container.getChildren().add(text);
        } else {
            final String[] cfgArr = details.split(ConfigSpliter.SPLITER);
            text.setX(ScreenAdapter.calculateExX(Integer.parseInt(cfgArr[0])));
            text.setY(ScreenAdapter.calculateExY(Integer.parseInt(cfgArr[1])));
            text.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[2])));
            text.prefHeight(ScreenAdapter.calculateExH(Integer.parseInt(cfgArr[3])));
            text.setFill(Color.valueOf(cfgArr[5]));
            Font font = Font.font(FontMap.getFont(cfgArr[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[8])));
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
            text.setLineSpacing(ScreenAdapter.calculateExH(Integer.parseInt(cfgArr[14])));
            text.setWrappingWidth(ScreenAdapter.calculateExW(Integer.parseInt(cfgArr[2])));
            container.getChildren().add(text);
        }
    }

    @Override
    public void reload() {
        show();
    }

    @Override
    public Node getPane() {
        return container;
    }
}
