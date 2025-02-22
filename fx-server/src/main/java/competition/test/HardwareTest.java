package competition.test;

import competition.serial.Command;
import competition.serial.SerialPortConnector;

import java.util.Scanner;

public class HardwareTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SerialPortConnector instance = SerialPortConnector.createInstance("COM3");
        instance.connect();
        while (instance.isConnected()){
            System.out.println("请输入要发送的数据：");
            String data = scanner.nextLine();
            if (data.isEmpty()) {
                continue;
            }
            instance.send(data);
//            String[] split = data.split(" ");
//            if (split.length < 2) {
//                instance.send(Command.of(split[0]), "");
//            } else {
//                instance.send(Command.of(split[0]), split[1]);
//            }

        }
    }
}
