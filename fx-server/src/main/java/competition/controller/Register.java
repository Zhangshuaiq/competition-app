package competition.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.entity.RegisterInfo;
import competition.mapper.RegisterInfoMapper;
import competition.util.HttpUtil;
import competition.util.RuntimeUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class Register {

    @Resource
    private RegisterInfoMapper registerInfoMapper;
    
    @Value("${software.register.server}")
    private String registerServer;


    
    @FXML
    private Label messageText;
    
    
    @FXML
    private TextField field1;

    @FXML
    private Button button;

    // 过期时间 13 + 团队个数 7

    @FXML
    public void initialize() {
        RegisterInfo registerInfo = registerInfoMapper.selectById(1001);
        if (!StringUtils.isEmpty(registerInfo.getActiveCode())) {
            button.setText("解绑");
            String activeCode = registerInfo.getActiveCode().substring(8, 28);
            field1.setText(activeCode);
        } else {
            button.setText("激活软件");
        }

        button.setOnMouseClicked(event -> {
            String text = button.getText();
            if (text.equals("激活软件")) {
                String activeCode = field1.getText();
                if (StringUtils.isEmpty(activeCode)) {
                    messageText.setStyle("-fx-text-fill: red");
                    messageText.setText("请输入激活码");
                    return;
                }
                // 超级激活码处理逻辑
                if (activeCode.equals(RuntimeUtil.SUPER_ACTIVE_CODE)) {
                    String cpuSerial = "";
                    try {
                        cpuSerial = getCPUSerial();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    messageText.setStyle("-fx-text-fill: green");
                    messageText.setText("激活成功");
                    registerInfo.setActiveCode(activeCode);
                    registerInfo.setDeviceCode(cpuSerial);
                    registerInfo.setCountVal(99999);
                    registerInfo.setLastCheckTime(System.currentTimeMillis() + "");
                    registerInfoMapper.updateById(registerInfo);
                    Platform.runLater(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                            System.exit(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                }

                // 超级激活码 99999999991234567890
                if (activeCode.length() != 20) {
                    messageText.setStyle("-fx-text-fill: red");
                    messageText.setText("激活码不正确");
                    return;
                }

                boolean temp = activeCode.startsWith("0");

                long deadline = Long.parseLong(activeCode.substring(1, 14));

                if (temp && deadline < System.currentTimeMillis()) {
                    messageText.setStyle("-fx-text-fill: red");
                    messageText.setText("临时激活码已过期");
                    return;
                }

                // 激活
                try {
                    String cpuSerial = getCPUSerial();
                    String starter = cpuSerial.substring(0, 8);
                    String ender = cpuSerial.substring(8, 16);
                    String activeText = starter + activeCode + ender;
                    Map<String, Object> body = new HashMap<>();
                    body.put("deviceId", cpuSerial);
                    body.put("activeCode", activeCode);
                    String responseText = HttpUtil.sendPostRequest(registerServer + "/api/v1/register", JSON.toJSONString(body));
                    JSONObject responseObj = JSON.parseObject(responseText);
                    if (responseObj.getString("code").equals("200")) {
                        registerInfo.setActiveCode(activeText);
                        registerInfo.setCountVal(Integer.parseInt(responseObj.getString("value")));
                        registerInfo.setLastCheckTime(System.currentTimeMillis() + "");
                        registerInfo.setDeviceCode(cpuSerial);
                        registerInfoMapper.updateById(registerInfo);
                        messageText.setStyle("-fx-text-fill: green");
                        messageText.setText(responseObj.getString("message"));
                        Platform.runLater(() -> {
                            try {
                                TimeUnit.SECONDS.sleep(2);
                                System.exit(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        messageText.setStyle("-fx-text-fill: red");
                        messageText.setText(responseObj.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 解绑
                try {
                    String cpuSerial = getCPUSerial();
                    String activeCode = field1.getText();

                    if (activeCode.equals(RuntimeUtil.SUPER_ACTIVE_CODE)) {
                        registerInfo.setActiveCode("");
                        registerInfoMapper.updateById(registerInfo);
                        messageText.setStyle("-fx-text-fill: green");
                        messageText.setText("解绑成功");
                        return;
                    }

                    Map<String, Object> body = new HashMap<>();
                    body.put("deviceId", cpuSerial);
                    body.put("activeCode", activeCode);

                    String responseText = HttpUtil.sendPostRequest(registerServer + "/api/v1/unregister", JSON.toJSONString(body));
                    JSONObject responseObj = JSON.parseObject(responseText);
                    if (responseObj.getString("code").equals("200")) {
                        registerInfo.setActiveCode("");
                        registerInfoMapper.updateById(registerInfo);
                        messageText.setStyle("-fx-text-fill: green");
                        messageText.setText(responseObj.getString("message"));
                    } else {
                        registerInfo.setActiveCode("");
                        registerInfoMapper.updateById(registerInfo);
                        messageText.setStyle("-fx-text-fill: red");
                        messageText.setText(responseObj.getString("message"));
                    }
                    Platform.runLater(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                            System.exit(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


//        public static void main(String[] args) {
//            try {
//                String cpuSerial = getCPUSerial();
//                if (cpuSerial == null || cpuSerial.isEmpty()) {
//                    System.out.println("Failed to get CPU serial number.");
//                    return;
//                }
//                //System.out.println("CPU Serial: " + cpuSerial);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

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


}
