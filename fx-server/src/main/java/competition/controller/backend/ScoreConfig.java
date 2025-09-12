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
    private CheckBox paneCheck, paneCheck1, screenCheck;

    @FXML
    public void initialize() {

        paneCheck.setOnAction(event -> {
            if (paneCheck.isSelected()) {
                paneCheck1.setSelected(false);
            }
        });

        paneCheck1.setOnAction(event -> {
            if (paneCheck1.isSelected()) {
                paneCheck.setSelected(false);
            }
        });

        EffectConfig effectConfig = configMapper.selectById(1);
        if (effectConfig != null) {
            paneCheck.setSelected(effectConfig.getScoreEffect() == 1);
            paneCheck1.setSelected(effectConfig.getScoreEffect() == 2);
            screenCheck.setSelected(effectConfig.getScreenEffect() == 1);
        }
    }

    @FXML
    public void saveConfig(){
        EffectConfig effectConfig = configMapper.selectById(1);
        if (effectConfig == null) {
            effectConfig = new EffectConfig();
            effectConfig.setId(1);
            if (paneCheck.isSelected()) {
                effectConfig.setScoreEffect(1);
            }else if (paneCheck1.isSelected()) {
                effectConfig.setScoreEffect(2);
            } else {
                effectConfig.setScoreEffect(0);
            }

            effectConfig.setScreenEffect(screenCheck.isSelected() ? 1 : 0);
            configMapper.insert(effectConfig);
        } else {
            if (paneCheck.isSelected()) {
                effectConfig.setScoreEffect(1);
            }else if (paneCheck1.isSelected()) {
                effectConfig.setScoreEffect(2);
            } else {
                effectConfig.setScoreEffect(0);
            }
            effectConfig.setScreenEffect(screenCheck.isSelected() ? 1 : 0);
            configMapper.updateById(effectConfig);
        }

    }

}
