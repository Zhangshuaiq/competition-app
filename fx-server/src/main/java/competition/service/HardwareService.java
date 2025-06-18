package competition.service;

import competition.dto.ScreenDevice;
import competition.entity.EffectConfig;
import competition.entity.Teams;
import competition.mapper.EffectConfigMapper;
import competition.serial.Command;
import competition.serial.SerialPortConfig;
import competition.serial.SerialPortConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class HardwareService {


    @Resource
    private EffectConfigMapper configMapper;

    /**
     * 异步处理硬件显示 - 使用通用异步执行器
     */
    @Async("taskExecutor")  // 使用通用异步执行器
    public void updateHardwareDisplayAsync(Teams team, String totalScore) {
        if (team == null || totalScore == null) return;

        try {
            SerialPortConnector instance = SerialPortConnector.getInstance();
            if (instance == null) return;

//            EffectConfig effectConfig = configMapper.selectById(1);
//            boolean light = effectConfig != null && Objects.equals(1, effectConfig.getScreenEffect());

            ScreenDevice screenDevice = SerialPortConfig.getScreenDevice(team.getCode());
            if (screenDevice != null) {
                instance.send(Command.SHOW, SerialPortConfig.buildScreenData(screenDevice, Integer.parseInt(totalScore), false));
            }
        } catch (Exception e) {
            System.err.println("硬件显示更新失败: " + e.getMessage());
        }
    }
}
