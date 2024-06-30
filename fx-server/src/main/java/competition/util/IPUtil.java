package competition.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
public class IPUtil {

        public static void main(String[] args) {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                for (NetworkInterface iface : Collections.list(interfaces)) {
                    // 检查接口是否激活，且不是回环接口
                    if (iface.isUp() && !iface.isLoopback()) {
                        Enumeration<InetAddress> addresses = iface.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            InetAddress addr = addresses.nextElement();
                            // 确保IPv4地址且非localhost
                            if (addr.getHostAddress().indexOf(':') == -1 && !addr.isLoopbackAddress()) {
                                System.out.println(addr.getHostAddress());
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

}
