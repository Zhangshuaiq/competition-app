package competition.controller.match;

import cn.hutool.core.io.FileUtil;
import competition.controller.ControlPane;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import competition.util.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
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
public class EmptyPreview implements ReloadPane {

    @FXML
    private AnchorPane container;

    @FXML
    public void initPage() {
        reload();
    }

    private void show() {
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.扩展屏打开背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
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
