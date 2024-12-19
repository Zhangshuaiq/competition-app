package competition.controller.backend;

import competition.serial.SerialPortConnector;
import competition.util.Information;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class TerminalConfig {

    @FXML
    private ComboBox<String> portBox;

    @FXML
    private Button connectBtn;

    @FXML
    public void initialize() {
        List<String> ports = SerialPortConnector.getAvailableSerialPorts();
        portBox.getItems().addAll(ports);

        SerialPortConnector instance = SerialPortConnector.getInstance();
        if (instance != null) {
            portBox.setValue(instance.getPortName());
            connectBtn.setText("关闭");
        } else {
            connectBtn.setText("打开");
        }
    }


    @FXML
    public void controlPort(){
        if (connectBtn.getText().equals("关闭")) {
            SerialPortConnector.getInstance().close();
            connectBtn.setText("打开");
        } else {
            String selectedPort  = portBox.getValue();
            if (StringUtils.isEmpty(selectedPort)) {
                Information.error("请选择串口");
                return;
            }
            SerialPortConnector instance = SerialPortConnector.createInstance(selectedPort);
            instance.connect();
            connectBtn.setText("关闭");
        }
    }





}
