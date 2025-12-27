package competition.service;

import competition.entity.Teams;
import competition.serial.Command;
import competition.serial.SerialPortConfig;
import competition.serial.SerialPortConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HardwareService {

    /**
     * 异步处理硬件显示 - 使用通用异步执行器
     */
    @Async("taskExecutor")  // 使用通用异步执行器
    public void updateHardwareDisplayAsync(Teams team, String totalScore) {
        if (team == null || totalScore == null) return;

        try {
            SerialPortConnector instance = SerialPortConnector.getInstance();
            if (instance == null) return;
            instance.send(Command.SHOW, SerialPortConfig.buildScreenData(team.getCode(), Integer.parseInt(totalScore), true));
        } catch (Exception e) {
            System.err.println("硬件显示更新失败: " + e.getMessage());
        }
    }
}
