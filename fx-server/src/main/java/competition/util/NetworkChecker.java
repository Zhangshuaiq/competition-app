package competition.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkChecker {

    public static boolean isInternetAvailable() {
        try {
            // 使用一个可靠的公共服务器进行测试
            URL url = new URL("http://www.baidu.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000); // 设置超时，防止等待过久
            connection.setReadTimeout(3000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode < 400); // 如果返回的状态码在 200-399 范围内，说明有网络
        } catch (IOException e) {
            return false; // 网络不可用
        }
    }

//    public static void main(String[] args) {
//        if (isInternetAvailable()) {
//            System.out.println("当前设备有网络连接");
//        } else {
//            System.out.println("当前设备无网络连接");
//        }
//    }
}
