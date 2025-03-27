package competition.serial;

import competition.dto.BuzzDevice;
import competition.dto.ScreenDevice;

import java.util.*;
import java.util.stream.Collectors;

public class SerialPortConfig {


    private static List<ScreenDevice> screenDevices = new ArrayList<>();

    private static List<BuzzDevice> buzzDevices = new ArrayList<>();


    public static void setScreenDevices(List<ScreenDevice> screenDevices) {
        SerialPortConfig.screenDevices.clear();
        SerialPortConfig.screenDevices.addAll(screenDevices);
    }

    public static void setBuzzDevices(List<BuzzDevice> buzzDevices){
        SerialPortConfig.buzzDevices.clear();
        SerialPortConfig.buzzDevices.addAll(buzzDevices);
    }

    public static ScreenDevice getScreenDevice(String name){
        List<ScreenDevice> collect = screenDevices.stream().filter(x -> x.getFlag().equals(name)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(0);
        }
        return null;
    }

    public static BuzzDevice getBuzzDevice(String address){
        List<BuzzDevice> collect = buzzDevices.stream().filter(x -> x.getAddress().equals(address)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(0);
        }
        return null;
    }

    private static String getColor(String color){
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("不点亮", "0");
        colorMap.put("红色", "1");
        colorMap.put("绿色", "2");
        colorMap.put("黄色", "3");
        String[] split = color.split(SerialPortConnector.SPLITTER);
        List<String> collect = Arrays.stream(split).map(colorMap::get).collect(Collectors.toList());
        return String.join("", collect);
    }

    private static String getLight(String light) {
        int number = Integer.parseInt(light, 2);
        int reversedNumber = Integer.reverse(number) >>> (Integer.SIZE - light.length());
        return Integer.toBinaryString(reversedNumber);
    }

    public static String buildScreenData(ScreenDevice screenDevice, int score){
        return screenDevice.getAddress() + SerialPortConnector.SPLITTER
                +  String.format("%03d", score) + SerialPortConnector.SPLITTER
                + getColor(screenDevice.getColor()) + SerialPortConnector.SPLITTER
                + getLight(screenDevice.getLight())
                ;
    }
}
