package competition.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import competition.entity.RegisterInfo;
import competition.mapper.RegisterInfoMapper;
import competition.util.HttpUtil;
import competition.util.Information;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

@Component
@Scope("prototype")
public class Register {

    @Resource
    private RegisterInfoMapper registerInfoMapper;
    
    @Value("${software.register.server}")
    private String registerServer;


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
            field1.setDisable(true);
            String activeCode = registerInfo.getActiveCode().substring(8, 28);
            field1.setText(activeCode);
        } else {
            field1.setDisable(false);
            button.setText("激活软件");
        }

        button.setOnMouseClicked(event -> {
            String text = button.getText();
            if (text.equals("激活软件")) {
                String activeCode = field1.getText();
                if (StringUtils.isEmpty(activeCode)) {
                    Information.error("请输入激活码");
                    return;
                }
                if (activeCode.length() != 20) {
                    Information.error("激活码不正确");
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
                        registerInfoMapper.updateById(registerInfo);
                        Information.info(responseObj.getString("message"));
                    } else {
                        Information.error(responseObj.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 解绑
                try {
                    String cpuSerial = getCPUSerial();
                    String activeCode = field1.getText();
                    Map<String, Object> body = new HashMap<>();
                    body.put("deviceId", cpuSerial);
                    body.put("activeCode", activeCode);
                    String responseText = HttpUtil.sendPostRequest(registerServer + "/api/v1/unregister", JSON.toJSONString(body));
                    JSONObject responseObj = JSON.parseObject(responseText);
                    if (responseObj.getString("code").equals("200")) {
                        registerInfo.setActiveCode(null);
                        field1.setDisable(false);
                        registerInfoMapper.updateById(registerInfo);
                        Information.info(responseObj.getString("message"));
                    } else {
                        Information.error(responseObj.getString("message"));
                    }
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
        String command = "wmic cpu get ProcessorId";
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
