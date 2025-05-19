package competition.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.entity.RegisterInfo;
import competition.mapper.RegisterInfoMapper;
import competition.util.*;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private FrontInterceptor interceptor;

    @Value("${software.register.server}")
    private String registerServer;

    @Resource
    private RegisterInfoMapper registerInfoMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(interceptor).addPathPatterns("/**");
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

    private boolean checkNetwork() {
        // 有网络，验证是否合法
        RegisterInfo registerInfo = registerInfoMapper.selectById(1001);
        try {
            String cpuSerial = getCPUSerial();
            Map<String, Object> body = new HashMap<>();
            body.put("deviceId", cpuSerial);
            String responseText = HttpUtil.sendPostRequest(registerServer + "/api/v1/valid", JSON.toJSONString(body));
            JSONObject responseObj = JSON.parseObject(responseText);
            if (responseObj.getString("code").equals("200")) {
                String activeCode = responseObj.getString("data");
                if (registerInfo.getActiveCode().contains(activeCode)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        RegisterInfo registerInfo = registerInfoMapper.selectById(1001);
        if (registerInfo == null || StringUtils.isEmpty(registerInfo.getActiveCode())) {
            return;
        }

        // 超级激活码
        if (RuntimeUtil.SUPER_ACTIVE_CODE.equals(registerInfo.getActiveCode())) {
            String cpuSerial = "";
            try {
//                cpuSerial = getCPUSerial();
                cpuSerial = "12345678";
                if (Objects.equals(cpuSerial, registerInfo.getDeviceCode()) ) {
                    GlobalCache.MAX_TEAM_NUMBER = 9999;
                    registry.addResourceHandler("/resource/**", "/**")
                            .addResourceLocations("file:" + BaseDir.TIKU_RES_DIR, "file:" + BaseDir.PB_RES_DIR, "classpath:/frontend/");
                } else {
                    registerInfo.setActiveCode("");
                    registerInfo.setLastCheckTime("");
                    registerInfo.setDeviceCode("");
                    registerInfoMapper.updateById(registerInfo);
                    return;
                }
            } catch (Exception e) {
                registerInfo.setActiveCode("");
                registerInfo.setLastCheckTime("");
                registerInfo.setDeviceCode("");
                registerInfoMapper.updateById(registerInfo);
            }
            return;
        }

        // 检测网络匹配
        boolean internetAvailable = NetworkChecker.isInternetAvailable();
        if (internetAvailable) {
            boolean checked = checkNetwork();
            if (!checked){
                registerInfo.setActiveCode("");
                registerInfo.setLastCheckTime("");
                registerInfo.setDeviceCode("");
                registerInfoMapper.updateById(registerInfo);
                return;
            }
            registerInfo.setLastCheckTime(System.currentTimeMillis() + "");
        } else {
            long checkTime = Long.parseLong(registerInfo.getLastCheckTime());
            if (System.currentTimeMillis() - checkTime > (1000L * 60 * 60 * 24 * 30)) {
                registerInfo.setActiveCode("");
                registerInfo.setLastCheckTime("");
                registerInfo.setDeviceCode("");
                registerInfoMapper.updateById(registerInfo);
                return;
            }
        }
        // BFEBFBFF                    000B06F2
        // BFEBFBFF11730611860050120010000B06F2
        // BFEBFBFF                    000806D1
        {
            String activeCode = registerInfo.getActiveCode();
            if (activeCode.length() != 36) {
                registerInfo.setActiveCode("");
                registerInfo.setLastCheckTime("");
                registerInfo.setDeviceCode("");
                registerInfoMapper.updateById(registerInfo);
                return;
            }
            String deviceId = activeCode.substring(0, 8) + activeCode.substring(28);

            try {
                String cpuSerial = getCPUSerial();
                if (!cpuSerial.equals(deviceId)) {
                    registerInfo.setActiveCode("");
                    registerInfo.setLastCheckTime("");
                    registerInfo.setDeviceCode("");
                    registerInfoMapper.updateById(registerInfo);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String realCode = activeCode.substring(8, 28);
            String endTime = realCode.substring(0, 14);
            if (Long.parseLong(endTime.substring(1)) < System.currentTimeMillis() && endTime.charAt(0) == '0') {
                registerInfo.setActiveCode("");
                registerInfo.setLastCheckTime("");
                registerInfo.setDeviceCode("");
                registerInfoMapper.updateById(registerInfo);
                return;
            }

            String teamNum = realCode.substring(14);


            String boardBit = teamNum.substring(0, 1);
            String realBit = teamNum.substring(1, 2);
            int bitCount = Integer.parseInt(realBit);
            if (bitCount > 4) {
                registerInfo.setActiveCode("");
                registerInfo.setLastCheckTime("");
                registerInfo.setDeviceCode("");
                registerInfoMapper.updateById(registerInfo);
                return;
            }
            // 更新检查时间
            registerInfoMapper.updateById(registerInfo);
            GlobalCache.MAX_TEAM_NUMBER = Integer.parseInt(teamNum.substring(6 - bitCount));
            if (!boardBit.equals("0")) {
                registry.addResourceHandler("/resource/**", "/**")
                        .addResourceLocations("file:" + BaseDir.TIKU_RES_DIR, "file:" + BaseDir.PB_RES_DIR, "classpath:/frontend/");
            }
        }



//        if (enabled){
//            registry.addResourceHandler("/resource/**", "/**")
//                    .addResourceLocations("file:" + BaseDir.TIKU_RES_DIR, "file:" + BaseDir.PB_RES_DIR, "classpath:/frontend/");
//        }
//        registry.addResourceHandler("/resource/**")
//                .addResourceLocations("file:" + BaseDir.TIKU_RES_DIR, "file:" + BaseDir.PB_RES_DIR, "classpath:/frontend/");
    }


}


