package competition.controller.preview;

import competition.mapper.ConfigsMapper;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class BuzzResultPreview extends PreviewPane{

    @FXML
    private VBox container;

    @Resource
    private ConfigsMapper configsMapper;

    @FXML
    public void initialize() {

    }
}
