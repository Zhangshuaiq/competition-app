package competition.controller.backend;

import competition.entity.EffectConfig;
import competition.mapper.EffectConfigMapper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScoreConfig {
    
    @Resource
    private EffectConfigMapper configMapper;

    @FXML
    private CheckBox paneCheck, screenCheck;

    @FXML
    public void initialize() {
        EffectConfig effectConfig = configMapper.selectById(1);
        if (effectConfig != null) {
            paneCheck.setSelected(effectConfig.getScoreEffect() == 1);
            screenCheck.setSelected(effectConfig.getScreenEffect() == 1);
        }
    }

    @FXML
    public void saveConfig(){
        EffectConfig effectConfig = configMapper.selectById(1);
        if (effectConfig == null) {
            effectConfig = new EffectConfig();
            effectConfig.setId(1);
            effectConfig.setScoreEffect(paneCheck.isSelected() ? 1 : 0);
            effectConfig.setScreenEffect(screenCheck.isSelected() ? 1 : 0);
            configMapper.insert(effectConfig);
        } else {
            effectConfig.setScoreEffect(paneCheck.isSelected() ? 1 : 0);
            effectConfig.setScreenEffect(screenCheck.isSelected() ? 1 : 0);
            configMapper.updateById(effectConfig);
        }

    }

}
