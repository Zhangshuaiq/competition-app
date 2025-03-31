package competition.controller.backend;

import com.alibaba.fastjson2.JSON;
import competition.dto.BuzzDevice;
import competition.dto.ScreenDevice;
import competition.entity.TerminalInfo;
import competition.mapper.TerminalInfoMapper;
import competition.serial.Command;
import competition.serial.SerialPortConfig;
import competition.serial.SerialPortConnector;
import competition.util.ColorUtil;
import competition.util.Information;
import competition.util.ObjectUtil;
import competition.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TerminalConfig {

    @FXML
    private ComboBox<String> portBox;

    @FXML
    private Button connectBtn;

    @FXML
    private TextField screenAddressField;

    @FXML
    private TextArea logArea;

    @FXML
    private TableView<ScreenDevice> screenTable;


    @FXML
    private TableColumn<ScreenDevice, String> screenAddressCol;

    @FXML
    private ComboBox<String> color3Selector;

    @FXML
    private TableColumn<ScreenDevice, String> screenColorCol;


    @FXML
    private TableView<BuzzDevice> buzzTable;


    @FXML
    private ComboBox<String> color2Selector;

    @FXML
    private TextField portField, portField1;

    @FXML
    private TableColumn<BuzzDevice, String> buzzAddressCol;

    @FXML
    private TextField buzzAddressField;

    @FXML
    private ComboBox<String> color1Selector;

    @Resource
    private TerminalInfoMapper terminalInfoMapper;

    private static TextArea logAreaRef;

    @FXML
    public void initialize() {
        logAreaRef = logArea;
        bindTableProperty();
        initDeviceInfo();
        loadDataFromTable();

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


    private void showScreenInfo() {
        int selectedIndex = screenTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        ScreenDevice screenDevice = screenTable.getItems().get(selectedIndex);
        screenAddressField.setText(screenDevice.getAddress());
        String[] colors = screenDevice.getColor().split(",");
        color1Selector.setValue(colors[0]);
        color2Selector.setValue(colors[1]);
        color3Selector.setValue(colors[2]);
    }

    private void showBuzzInfo() {
        int selectedIndex = buzzTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        BuzzDevice buzzDevice = buzzTable.getItems().get(selectedIndex);
        buzzAddressField.setText(buzzDevice.getAddress());
    }

    private void bindTableProperty() {
        screenAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        screenColorCol.setCellValueFactory(new PropertyValueFactory<>("color"));

        screenTable.setOnMouseClicked(event -> showScreenInfo());

        buzzAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        buzzTable.setOnMouseClicked(event -> showBuzzInfo());

    }

    private void initDeviceInfo() {
        ObservableList<String> colorList = FXCollections.observableList(new ArrayList<>(ColorUtil.COLOR_MAPPER.keySet()));
        color1Selector.setItems(colorList);
        color1Selector.setValue(ColorUtil.NO_COLOR);
        color2Selector.setItems(colorList);
        color2Selector.setValue(ColorUtil.NO_COLOR);
        color3Selector.setItems(colorList);
        color3Selector.setValue(ColorUtil.NO_COLOR);
    }

    private void loadDataFromTable() {
        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        loadPort(terminalInfo);
        loadScreenTable(terminalInfo);
        loadBuzzTable(terminalInfo);
    }

    private void loadPort(TerminalInfo terminalInfo) {
        if (terminalInfo != null) {
            portField.setText(ObjectUtil.toString(terminalInfo.getConnectPort()));
            portField1.setText(ObjectUtil.toString(terminalInfo.getBuzzPort()));
        } else {
            portField.setText("0");
            portField1.setText("120");
        }
    }

    private void loadScreenTable(TerminalInfo terminalInfo) {
        screenTable.getItems().clear();
        if (terminalInfo != null) {
            String screenList = terminalInfo.getScreenList();
            JSON.parseArray(screenList, ScreenDevice.class).forEach(screenTable.getItems()::add);
            SerialPortConfig.setScreenDevices(screenTable.getItems());
        }
        screenTable.refresh();
    }

    private void loadBuzzTable(TerminalInfo terminalInfo) {
        buzzTable.getItems().clear();
        if (terminalInfo != null) {
            String buzzList = terminalInfo.getBuzzList();
            JSON.parseArray(buzzList, BuzzDevice.class).forEach(buzzTable.getItems()::add);
            SerialPortConfig.setBuzzDevices(buzzTable.getItems());
        }
        buzzTable.refresh();
    }


    @FXML
    public void controlPort() {
        if (connectBtn.getText().equals("关闭")) {
            SerialPortConnector.getInstance().close();
            connectBtn.setText("打开");
        } else {
            String selectedPort = portBox.getValue();
            if (StringUtils.isEmpty(selectedPort)) {
                Information.error("请选择串口");
                return;
            }
            SerialPortConnector instance = SerialPortConnector.createInstance(selectedPort);
            instance.connect();
            configHardwareDevice();
            connectBtn.setText("关闭");
        }
    }

    // 配置硬件设备
    private void configHardwareDevice() {
        SerialPortConnector instance = SerialPortConnector.getInstance();
        if (instance == null) {
            Information.error("请先打开串口");
            return;
        }
        if (instance.isConnected()) {
            // 设置为配置模式
            String cmd = instance.send(Command.SMOD, "1");
            logArea.appendText(TimeUtil.currentTime() + ":      设置为配置模式【" + cmd + "】\n");
            String response = waitResponse();
            if (!response.contains(Command.SMOD.getCmd()) || !response.contains("OK")) {
                logArea.appendText(TimeUtil.currentTime() + ":      设置为配置模式失败\n");
                return;
            }
            logArea.appendText(TimeUtil.currentTime() + ":     设置为配置模式成功\n");


            // 设置显示器地址
            String data;
            ObservableList<ScreenDevice> screenDevices = screenTable.getItems();
            for (ScreenDevice screenDevice : screenDevices) {
                data = screenDevice.getAddress() + "," + screenDevice.getAddress() + "," + portField.getText();
                cmd = instance.send(Command.SDSP, data);
                logArea.appendText(TimeUtil.currentTime() + ":      " + screenDevice.getAddress() + "设置显示器地址【" + cmd + "】\n");
                response = waitResponse();
                if (response == null) {
                    logArea.appendText(TimeUtil.currentTime() + ":      " + screenDevice.getAddress() + "读取响应超时\n");
                    return;
                }
                if (!response.contains(Command.SDSP.getCmd()) || !response.contains("OK")) {
                    logArea.appendText(TimeUtil.currentTime() + ":      " + screenDevice.getAddress() + "设置显示器地址失败\n");
                    return;
                }
                logArea.appendText(TimeUtil.currentTime() + ":     " + screenDevice.getAddress() + "设置显示器地址成功\n");
            }

            // 设置信道 抢答器，显示器，强制主机抢答器为 100，用于通信
            data = "100" + "," + portField.getText();
            cmd = instance.send(Command.SMAT, data);
            logArea.appendText(TimeUtil.currentTime() + ":      设置主机信道【" + cmd + "】\n");
            response = waitResponse();
            if (response == null) {
                logArea.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
                return;
            }
            if (!response.contains(Command.SMAT.getCmd()) || !response.contains("OK")) {
                logArea.appendText(TimeUtil.currentTime() + ":      设置主机信道失败\n");
                return;
            }
            logArea.appendText(TimeUtil.currentTime() + ":     设置主机信道成功\n");


            // 设置抢答器地址
            ObservableList<BuzzDevice> buzzDevices = buzzTable.getItems();
            for (BuzzDevice buzzDevice : buzzDevices) {
                data = "0" + "," + buzzDevice.getAddress() + "," + portField1.getText();
                cmd = instance.send(Command.SKEY, data);
                logArea.appendText(TimeUtil.currentTime() + ":      " + buzzDevice.getAddress() + "设置抢答器地址【" + cmd + "】\n");
                response = waitResponse();
                if (response == null) {
                    logArea.appendText(TimeUtil.currentTime() + ":      " + buzzDevice.getAddress() + "读取响应超时\n");
                    return;
                }
                if (!response.contains(Command.SKEY.getCmd()) || !response.contains("OK")) {
                    logArea.appendText(TimeUtil.currentTime() + ":      " + buzzDevice.getAddress() + "设置抢答器地址失败\n");
                    return;
                }
                logArea.appendText(TimeUtil.currentTime() + ":     " + buzzDevice.getAddress() + "设置抢答器地址成功\n");
            }

            // 设置信道 抢答器，显示器
            data = portField1.getText() + "," + portField.getText();
            cmd = instance.send(Command.SMAT, data);
            logArea.appendText(TimeUtil.currentTime() + ":      设置主机信道【" + cmd + "】\n");
            response = waitResponse();
            if (response == null) {
                logArea.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
                return;
            }
            if (!response.contains(Command.SMAT.getCmd()) || !response.contains("OK")) {
                logArea.appendText(TimeUtil.currentTime() + ":      设置主机信道失败\n");
                return;
            }
            logArea.appendText(TimeUtil.currentTime() + ":     设置主机信道成功\n");


//            // 同步显示器的信道 #,0018,SDUP,9F33,&
//            cmd = "#,0018,SDUP,9F33,&";
//            instance.send(cmd);
//            logArea.appendText(TimeUtil.currentTime() + ":      同步主机显示器信道【" + cmd + "】\n");
//            response = waitResponse();
//            if (response == null) {
//                logArea.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
//                return;
//            }
//            if (!response.contains(Command.SDUP.getCmd()) || !response.contains("OK")) {
//                logArea.appendText(TimeUtil.currentTime() + ":      同步主机显示器信道失败\n");
//                return;
//            }
//            logArea.appendText(TimeUtil.currentTime() + ":     同步主机显示器信道成功\n");


            // 设置为抢答模式
            cmd = instance.send(Command.SMOD, "0");
            logArea.appendText(TimeUtil.currentTime() + ":      设置抢答模式【" + cmd + "】\n");

            response = waitResponse();
            if (response == null) {
                logArea.appendText(TimeUtil.currentTime() + ":      读取响应超时\n");
                return;
            }
            if (!response.contains(Command.SMOD.getCmd()) || !response.contains("OK")) {
                logArea.appendText(TimeUtil.currentTime() + ":      设置为抢答模式失败\n");
                return;
            }
            logArea.appendText(TimeUtil.currentTime() + ":     设置为抢答模式成功\n");
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
        while (StringUtils.isEmpty(response = instance.getResponse()) && retryCount < 10) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryCount++;
        }
        return response;
    }


    @FXML
    public void saveScreenPort() {
        if (StringUtils.isEmpty(portField.getText())) {
            Information.error("请输入显示器信道");
            return;
        }
        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        if (terminalInfo != null) {
            terminalInfo.setConnectPort(Integer.parseInt(portField.getText()));
            terminalInfoMapper.updateById(terminalInfo);
        } else {
            terminalInfo = new TerminalInfo();
            terminalInfo.setId(1);
            terminalInfo.setConnectPort(Integer.parseInt(portField.getText()));
            terminalInfoMapper.insert(terminalInfo);
        }
    }


    @FXML
    public void saveBuzzPort() {
        if (StringUtils.isEmpty(portField1.getText())) {
            Information.error("请输入抢答器信道");
            return;
        }
        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        if (terminalInfo != null) {
            terminalInfo.setBuzzPort(Integer.parseInt(portField1.getText()));
            terminalInfoMapper.updateById(terminalInfo);
        } else {
            terminalInfo = new TerminalInfo();
            terminalInfo.setId(1);
            terminalInfo.setBuzzPort(Integer.parseInt(portField1.getText()));
            terminalInfoMapper.insert(terminalInfo);
        }
    }


    @FXML
    public void saveScreenInfo() {
        String address = screenAddressField.getText();
        String color = color1Selector.getValue() + "," + color2Selector.getValue() + "," + color3Selector.getValue();

        ScreenDevice screenDevice = new ScreenDevice();
        screenDevice.setAddress(address);
        screenDevice.setColor(color);

        List<ScreenDevice> deviceList = new ArrayList<>();

        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        if (terminalInfo != null) {
            String screenList = terminalInfo.getScreenList();
            deviceList.addAll(JSON.parseArray(screenList, ScreenDevice.class));
        }
        deviceList.add(screenDevice);
        terminalInfo.setScreenList(JSON.toJSONString(deviceList));
        terminalInfoMapper.updateById(terminalInfo);
        loadScreenTable(terminalInfo);
    }

    @FXML
    public void deleteScreenInfo() {
        int selectedIndex = screenTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        if (terminalInfo != null) {
            String screenList = terminalInfo.getScreenList();
            List<ScreenDevice> deviceList = JSON.parseArray(screenList, ScreenDevice.class);
            deviceList.remove(selectedIndex);
            terminalInfo.setScreenList(JSON.toJSONString(deviceList));
            terminalInfoMapper.updateById(terminalInfo);
            loadScreenTable(terminalInfo);
        }
    }

    @FXML
    public void deleteBuzzInfo() {
        int selectedIndex = buzzTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        if (terminalInfo != null) {
            String buzzList = terminalInfo.getBuzzList();
            List<BuzzDevice> deviceList = JSON.parseArray(buzzList, BuzzDevice.class);
            deviceList.remove(selectedIndex);
            terminalInfo.setBuzzList(JSON.toJSONString(deviceList));
            terminalInfoMapper.updateById(terminalInfo);
            loadBuzzTable(terminalInfo);
        }
    }

    @FXML
    public void saveBuzzInfo() {
        String address = buzzAddressField.getText();

        BuzzDevice buzzDevice = new BuzzDevice();
        buzzDevice.setAddress(address);

        List<BuzzDevice> deviceList = new ArrayList<>();

        TerminalInfo terminalInfo = terminalInfoMapper.selectById(1);
        if (terminalInfo != null) {
            String buzzList = terminalInfo.getBuzzList();
            deviceList.addAll(JSON.parseArray(buzzList, BuzzDevice.class));
        }
        deviceList.add(buzzDevice);
        terminalInfo.setBuzzList(JSON.toJSONString(deviceList));
        terminalInfoMapper.updateById(terminalInfo);
        loadBuzzTable(terminalInfo);
    }

}
