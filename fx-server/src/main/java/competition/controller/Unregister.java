package competition.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.JavaFxApplication;
import competition.entity.RegisterInfo;
import competition.mapper.RegisterInfoMapper;
import competition.util.HttpUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class Unregister {

    @FXML
    private Label unbindCount;

    @FXML
    private Label leftBindCount;

    @FXML
    private Label activeTime;

    @FXML
    private Label dueTime;
    
    @Resource
    private RegisterInfoMapper registerInfoMapper;


    @Value("${software.register.server}")
    private String registerServer;
    
    @FXML
    public void initialize(){
        RegisterInfo registerInfo = registerInfoMapper.selectById(1001);
        String activeVal = registerInfo.getActiveCode();
        String activeCode = activeVal.substring(8, 28);
        long endTime = Long.parseLong(activeCode.substring(1, 14));
        Date endData = new Date(endTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startData = new Date(endTime - 24 * 60 * 60 *1000);
        activeTime.setText(format.format(startData));
        if (activeCode.substring(0, 1).equals("1")) {
            dueTime.setText("永久");
        } else {
            dueTime.setText(format.format(endData));
        }
        Integer countVal = registerInfo.getCountVal();
        int unbind = countVal - 1;
        unbindCount.setText(unbind + " 次");
        leftBindCount.setText( (3 - unbind) + " 次");
    }

    public static String getCPUSerial() throws Exception {
        String result = "";
        String command = "powershell -Command \"Get-CimInstance Win32_Processor | Select-Object -ExpandProperty ProcessorId\"";
        Process process = Runtime.getRuntime().exec(command);
        process.getOutputStream().close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("ProcessorId")) {
                continue;
            }
            result = line.trim();
            if (!result.isEmpty()) {
                break;
            }
        }
        reader.close();
        return result;
    }
    
    @FXML
    public void unBind(){
        RegisterInfo registerInfo = registerInfoMapper.selectById(1001);
        String activeVal = registerInfo.getActiveCode();
        String activeCode = activeVal.substring(8, 28);
        try {
            String cpuSerial = getCPUSerial();
            Map<String, Object> body = new HashMap<>();
            body.put("deviceId", cpuSerial);
            body.put("activeCode", activeCode);
            String responseText = HttpUtil.sendPostRequest(registerServer + "/api/v1/unregister", JSON.toJSONString(body));
            JSONObject responseObj = JSON.parseObject(responseText);
            registerInfo.setActiveCode("");
            registerInfoMapper.updateById(registerInfo);
//            if (responseObj.getString("code").equals("200")) {
//                registerInfo.setActiveCode("");
//                registerInfoMapper.updateById(registerInfo);
//            } else {
//                registerInfo.setActiveCode("");
//                registerInfoMapper.updateById(registerInfo);
//            }
            Platform.runLater(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    //JavaFxApplication.restartJavaFxApplication();
                    System.exit(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
