package competition.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import competition.util.CRC16;
import competition.util.Information;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SerialPortConnector {


    private static SerialPortConnector instance;

    private static final String FRAME_HEAD = "#";

    private static final String FRAME_TAIL = "&";

    private static final String SPLITTER = ",";

    private SerialPort serialPort;

    private String portName;

    private volatile String response;

    public static synchronized SerialPortConnector createInstance(String portName) {
        if (instance == null) {
            instance = new SerialPortConnector(portName);
        }
        return instance;
    }

    public static SerialPortConnector getInstance() {
        return instance;
    }

    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    private SerialPortConnector(String portName) {
        this.portName = portName;
        serialPort = SerialPort.getCommPort(portName); // 替换为你的串口名
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
    }

    public void connect() {
        if (serialPort.openPort()) {
            System.out.println("Port opened successfully.");
            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }
                    byte[] newData = new byte[serialPort.bytesAvailable()];
                    serialPort.readBytes(newData, newData.length);
                    String receivedData = new String(newData);

                    // 解析协议数据
                    if (receivedData.startsWith(FRAME_HEAD) && receivedData.endsWith(FRAME_TAIL)) {
                        String[] split = receivedData.split(FRAME_HEAD);
                        for (String s : split) {
                            if (!StringUtils.isEmpty(s.trim())) {
                                String responseData = FRAME_HEAD + s.trim();
                                switch (Command.valueOf(responseData.substring(7, 11))) {
                                    case HEAT:
                                        System.out.println(" 心跳包:" + Command.HEAT);
                                        break;
                                    case SMOD:
                                        response = responseData;
                                        System.out.println("response: " + Command.SMOD);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case STAR:
                                        response = responseData;
                                        System.out.println("response: " + Command.STAR);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case EEND:
                                        response = responseData;
                                        System.out.println("response: " + Command.EEND);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SBLK:
                                        response = responseData;
                                        System.out.println("response: " + Command.SBLK);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SDSP:
                                        response = responseData;
                                        System.out.println("response: " + Command.SDSP);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SHOW:
                                        response = responseData;
                                        System.out.println("response: " + Command.SHOW);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SKEY:
                                        response = responseData;
                                        System.out.println("response: " + Command.SKEY);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case UKEY:
                                        response = responseData;
                                        System.out.println("response: " + Command.UKEY);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SMAT:
                                        response = responseData;
                                        System.out.println("response: " + Command.SMAT);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SKUP:
                                        response = responseData;
                                        System.out.println("response: " + Command.SKUP);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    case SDUP:
                                        response = responseData;
                                        System.out.println("response: " + Command.SDUP);
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                    default:
                                        System.out.println("Received Data: " +responseData);
                                        break;
                                }
                            }
                        }
                    }
                }
            });

        } else {
            Information.error("串口打开失败,请检查硬件");
        }
    }

    public String getResponse() {
        if (response != null) {
            String temp = response;
            response = null;
            return temp;
        }
        return response;
    }

    public String getPortName() {
        return portName;
    }

    public static List<String> getAvailableSerialPorts() {
        List<String> serialPorts = new ArrayList<String>();
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Available serial ports:");
        for (SerialPort port : ports) {
            serialPorts.add(port.getSystemPortName());
        }
        return serialPorts;
    }


    public void close() {
        serialPort.closePort();
        serialPort = null;
        instance = null;
    }

    public void send(String cmd) {
        serialPort.writeBytes(cmd.getBytes(), cmd.length());
        System.out.println("Data sent: " + cmd);
    }

    public void send(Command cmd, String data) {
        // 长度 = 1(帧头) + 1(分隔符) + 4(帧长度) + 1(分隔符) + 4(命令码) + 1(分隔符) + len(数据区域) + 1(分隔符) + 4(校验区域) +  1(分隔符) + 1(帧尾) = 18
        int totalLength = 1 + 1 + 4 + 1 + 4 + 1 + data.length() + 1 + 4 + 1 + 1;
        String frameLength = String.format("%04d", totalLength); // 不足4位补0

        // 校验区域
        String toCheck;
        if (!data.isEmpty()) {
            toCheck = FRAME_HEAD + SPLITTER + frameLength + SPLITTER + cmd + SPLITTER + data + SPLITTER;
        } else {
            toCheck = FRAME_HEAD + SPLITTER + frameLength + SPLITTER + cmd + SPLITTER;
        }

        String crc16 = CRC16.calculateCRC16(toCheck.getBytes(StandardCharsets.US_ASCII)); // 计算CRC16校验码

        // 完整帧格式
        String fullCmd;
        if (!data.isEmpty()) {
            fullCmd = FRAME_HEAD + SPLITTER + frameLength + SPLITTER + cmd + SPLITTER + data + SPLITTER + crc16 + SPLITTER + FRAME_TAIL;
        } else {
            fullCmd = FRAME_HEAD + SPLITTER + frameLength + SPLITTER + cmd + SPLITTER + crc16 + SPLITTER + FRAME_TAIL;
        }
        send(fullCmd);
    }

    public static void main(String[] args) {
        SerialPortConnector connector = new SerialPortConnector("COM3");
        connector.send(Command.STAR, "");

    }
}
