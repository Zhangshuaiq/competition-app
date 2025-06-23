package competition.controller;

import competition.JavaFxApplication;
import competition.entity.RegisterInfo;
import competition.mapper.RegisterInfoMapper;
import competition.util.BaseDir;
import competition.util.GlobalCache;
import competition.ws.WsMessageHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class Valid {

    @FXML
    private Label info;
    
    @FXML
    private Button activeBtn;

    @Resource
    private RegisterInfoMapper registerInfoMapper;

    private Timeline timeline;

    @FXML
    public void initialize() {

        activeBtn.setVisible(false);

        activeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
                loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean); // 使用Spring上下文来创建控制器实例
                Parent load = null;
                try {
                    load = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(load);
                scene.getStylesheets().add("/view/style.css"); // 应用 CSS

                Stage primaryStage = new Stage();
                primaryStage.setOnCloseRequest(e -> {
                    System.exit(0);
                });
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.setAlwaysOnTop(true);
                primaryStage.setTitle("软件激活");
                primaryStage.show();
            }
        });

        timeline = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RegisterInfo registerInfo = registerInfoMapper.selectById(1001);
                if (registerInfo == null || StringUtils.isEmpty(registerInfo.getActiveCode())) {
                    info.setText(" 软件未激活， 请联系客服人员进行激活！！");
                    activeBtn.setVisible(true);
                } else {
                    String activeCode = registerInfo.getActiveCode();
                    if (activeCode.length() != 36) {
                        info.setText(" 激活码不正确， 请联系客服人员进行激活！！");
                        activeBtn.setVisible(true);
                        return;
                    }
                    String deviceId = activeCode.substring(0, 8) + activeCode.substring(28);

                    try {
                        String cpuSerial = getCPUSerial();
                        if (!cpuSerial.equals(deviceId)) {
                            info.setText(" 软件未激活， 请联系客服人员进行激活！！");
                            activeBtn.setVisible(true);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String realCode = activeCode.substring(8, 28);
                    String endTime = realCode.substring(0, 13);
                    if (Long.parseLong(endTime) < System.currentTimeMillis()) {
                        info.setText(" 激活码已过期");
                        activeBtn.setVisible(true);
                        return;
                    }

                    String teamNum = realCode.substring(13);
                    String realBit = teamNum.substring(1, 2);
                    int bitCount = Integer.parseInt(realBit);
                    if (bitCount > 5) {
                        info.setText(" 激活码不正确！！");
                        activeBtn.setVisible(true);
                        return;
                    }
                    GlobalCache.MAX_TEAM_NUMBER = Integer.parseInt(teamNum.substring(7 - bitCount));


                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ControlPage.fxml"));
                    loader.setControllerFactory(JavaFxApplication.getApplicationContext()::getBean); // 使用Spring上下文来创建控制器实例
                    Parent load = null;
                    try {
                        load = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene scene = new Scene(load);
                    scene.getStylesheets().add("/view/style.css"); // 应用 CSS
                    Stage stage = (Stage) info.getScene().getWindow();
                    stage.close();
                    
                    Stage primaryStage = new Stage();
                    primaryStage.setOnCloseRequest(e -> {
                        WsMessageHandler messageHandler = JavaFxApplication.getApplicationContext().getBean(WsMessageHandler.class);
                        messageHandler.notifyPage("wait", null);
                        System.exit(0);
                    });
                    primaryStage.setScene(scene);
                    primaryStage.setResizable(false);
                    primaryStage.setAlwaysOnTop(true);
                    primaryStage.setTitle("答赛答题竞赛系统");
                    try {
                        primaryStage.getIcons().add(new Image(new FileInputStream(BaseDir.SUCAI_DIR + "图标.jpg")));
                    } catch (Exception ex) {
                       log.error("加载不到图片");
                    }
                    primaryStage.show();
                }
            }
        }));

        timeline.setCycleCount(1);
        timeline.play();
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
}
