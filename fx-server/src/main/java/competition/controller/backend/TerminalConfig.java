package competition.controller.backend;


import competition.serial.Command;
import competition.serial.SerialPortConnector;
import competition.util.Information;
import competition.util.TimeUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Objects;

@Component
public class TerminalConfig {

    @FXML
    private ComboBox<String> portBox;

    @FXML
    private Button connectBtn;

    @FXML
    private TextArea logArea;

    private static TextArea logAreaRef;

    @FXML
    private TextField pressXdInput;

    @FXML
    private Button configColorBtn;

    @FXML
    private TextField codeInput;

    @FXML
    private ComboBox<String> screenColorSelector;

    @FXML
    private Button modelConfigBtn;

    @FXML
    private TextField addrInput;

    @FXML
    private TextField scoreXdInput;

    @FXML
    private Button configXdBtn, configScreenBtn, configPressBtn;

    @FXML
    public void initialize() {
        logAreaRef = logArea;
        List<String> ports = SerialPortConnector.getAvailableSerialPorts();
        portBox.getItems().addAll(ports);

        modelConfigBtn.setDisable(true);

        SerialPortConnector instance = SerialPortConnector.getInstance();
        if (instance != null) {
            portBox.setValue(instance.getPortName());
            connectBtn.setText("关闭");
            modelConfigBtn.setDisable(false);
        } else {
            connectBtn.setText("打开");
            modelConfigBtn.setDisable(true);
            configXdBtn.setDisable(true);
            configScreenBtn.setDisable(true);
            configPressBtn.setDisable(true);
        }
    }


    @FXML
    public void enterConfigModel() {
        String text = modelConfigBtn.getText();
        if (Objects.equals("进入设置模式", text)) {
            SerialPortConnector instance = SerialPortConnector.getInstance();
            if (instance == null) {
                Information.error("请先打开串口");
                return;
            }
            if (!instance.isConnected()) {
                Information.error("串口连接异常");
                return;
            }
            String cmd = instance.send(Command.SMOD, "1");
            logArea.appendText(TimeUtil.currentTime() + ":      设置为配置模式【" + cmd + "】\n");
            String response = waitResponse();
            if (!response.contains(Command.SMOD.getCmd()) || !response.contains("OK")) {
                logArea.appendText(TimeUtil.currentTime() + ":      设置为配置模式失败\n");
                return;
            }
            logArea.appendText(TimeUtil.currentTime() + ":     设置为配置模式成功\n");
            modelConfigBtn.setText("退出设置模式");
            configXdBtn.setDisable(false);
            configScreenBtn.setDisable(false);
            configPressBtn.setDisable(false);
        } else {
            // 设置为抢答模式
            SerialPortConnector instance = SerialPortConnector.getInstance();
            if (instance == null) {
                Information.error("请先打开串口");
                return;
            }
            if (!instance.isConnected()) {
                Information.error("串口连接异常");
                return;
            }
            String cmd = instance.send(Command.SMOD, "0");
            logArea.appendText(TimeUtil.currentTime() + ":      设置抢答模式【" + cmd + "】\n");

            String response = waitResponse();
            if (response == null) {
                logArea.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
                return;
            }
            if (!response.contains(Command.SMOD.getCmd()) || !response.contains("OK")) {
                logArea.appendText(TimeUtil.currentTime() + ":      设置为抢答模式失败\n");
                return;
            }
            logArea.appendText(TimeUtil.currentTime() + ":     设置为抢答模式成功\n");
            modelConfigBtn.setText("进入设置模式");
            configXdBtn.setDisable(true);
            configScreenBtn.setDisable(true);
            configPressBtn.setDisable(true);
        }
    }


    @FXML
    public void controlPort() {
        if (connectBtn.getText().equals("关闭")) {
//            SerialPortConnector.getInstance().close();
            connectBtn.setText("打开");
            modelConfigBtn.setDisable(true);
        } else {
            String selectedPort = portBox.getValue();
            if (StringUtils.isEmpty(selectedPort)) {
                Information.error("请选择串口");
                return;
            }
            SerialPortConnector instance = SerialPortConnector.createInstance(selectedPort);
            instance.connect();
            connectBtn.setText("关闭");
            modelConfigBtn.setDisable(false);
        }
    }


    public static void sendStartBuzz() {
        // 改为准备抢答
        String cmd = "#,0018,STAR,7D55,&";
        SerialPortConnector instance = SerialPortConnector.getInstance();
        if (instance == null) {
            return;
        }
        instance.send(cmd);
        logAreaRef.appendText(TimeUtil.currentTime() + ":      准备抢答【" + cmd + "】\n");
        String response = waitResponse();
        if (response == null) {
            logAreaRef.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
            return;
        }
        if (!response.contains(Command.STAR.getCmd()) || !response.contains("OK")) {
            logAreaRef.appendText(TimeUtil.currentTime() + ":      设置准备抢答失败\n");
            return;
        }
        logAreaRef.appendText(TimeUtil.currentTime() + ":     设置准备抢答成功\n");
    }

    public static void sendStopBuzz() {
        // 改为结束抢答
        SerialPortConnector instance = SerialPortConnector.getInstance();
        if (instance == null) {
            return;
        }
        String cmd = "#,0018,EEND,5C7D,&";
        instance.send(cmd);
        logAreaRef.appendText(TimeUtil.currentTime() + ":      结束抢答【" + cmd + "】\n");
        String response = waitResponse();
        if (response == null) {
            logAreaRef.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
            return;
        }
        if (!response.contains(Command.EEND.getCmd()) || !response.contains("OK")) {
            logAreaRef.appendText(TimeUtil.currentTime() + ":      设置结束抢答失败\n");
            return;
        }
        logAreaRef.appendText(TimeUtil.currentTime() + ":     设置结束抢答成功\n");
    }

    public static String waitResponse() {
        SerialPortConnector instance = SerialPortConnector.getInstance();
        String response;
        int retryCount = 0;
        while (StringUtils.isEmpty(response = instance.getResponse()) && retryCount < 20) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryCount++;
        }
        System.out.println(retryCount + "次尝试后获取到响应: " + response);
        return response;
    }

    @FXML
    public void configXd() {
        String data = pressXdInput.getText() + "," + scoreXdInput.getText();
        SerialPortConnector instance = SerialPortConnector.getInstance();
        String cmd = instance.send(Command.SMAT, data);
        logArea.appendText(TimeUtil.currentTime() + ":      设置主机信道【" + cmd + "】\n");
        String response = waitResponse();
        if (response == null) {
            logArea.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
            return;
        }
        if (!response.contains(Command.SMAT.getCmd()) || !response.contains("OK")) {
            logArea.appendText(TimeUtil.currentTime() + ":      设置主机信道失败\n");
            return;
        }
        logArea.appendText(TimeUtil.currentTime() + ":     设置主机信道成功\n");
    }

    @FXML
    public void configColor() {
    }

    @FXML
    public void configScreen() {
        // 设置显示器地址
        if (StringUtils.isEmpty(addrInput.getText()) || StringUtils.isEmpty(codeInput.getText())) {
            Information.error("地址或信道不能为空");
            return;
        }
        SerialPortConnector instance = SerialPortConnector.getInstance();
        String data = "0," + addrInput.getText() + "," + codeInput.getText();
        String cmd = instance.send(Command.SDSP, data);
        logArea.appendText(TimeUtil.currentTime() + ":      " + addrInput.getText() + "设置显示器地址【" + cmd + "】\n");
        String response = waitResponse();
        if (response == null) {
            logArea.appendText(TimeUtil.currentTime() + ":      " + addrInput.getText() + "读取响应超时\n");
            return;
        }
        if (!response.contains(Command.SDSP.getCmd()) || !response.contains("OK")) {
            logArea.appendText(TimeUtil.currentTime() + ":      " + addrInput.getText() + "设置显示器地址失败\n");
            return;
        }
        logArea.appendText(TimeUtil.currentTime() + ":     " + addrInput.getText() + "设置显示器地址成功\n");
    }

    @FXML
    public void configPress() {
        // 设置按钮地址

        if (StringUtils.isEmpty(addrInput.getText()) || StringUtils.isEmpty(codeInput.getText())) {
            Information.error("地址或信道不能为空");
            return;
        }

        SerialPortConnector instance = SerialPortConnector.getInstance();
        String data = "0," + addrInput.getText() + "," + codeInput.getText();
        String cmd = instance.send(Command.SKEY, data);
        logArea.appendText(TimeUtil.currentTime() + ":      " + addrInput.getText() + "设置抢答器地址【" + cmd + "】\n");
        String response = waitResponse();
        if (response == null) {
            logArea.appendText(TimeUtil.currentTime() + ":      " + addrInput.getText() + "读取响应超时\n");
            return;
        }
        if (!response.contains(Command.SKEY.getCmd()) || !response.contains("OK")) {
            logArea.appendText(TimeUtil.currentTime() + ":      " + addrInput.getText() + "设置抢答器地址失败\n");
            return;
        }
        logArea.appendText(TimeUtil.currentTime() + ":     " + addrInput.getText() + "设置抢答器地址成功\n");
    }


}
