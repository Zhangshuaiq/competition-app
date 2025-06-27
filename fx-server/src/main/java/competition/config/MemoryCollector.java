package competition.config;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MemoryCollector {

    // 每隔 1 分钟检查一次内存使用
    @Scheduled(fixedRate = 60000)
    public void collectMemoryUsage() {
        // 这里可以实现内存使用情况的收集逻辑
        // 例如，使用 Runtime.getRuntime().totalMemory() 和 Runtime.getRuntime().freeMemory() 获取内存信息
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        // 内存使用大于  1.5 G， 进行垃圾回收
        if (usedMemory > 1.5 * 1024 * 1024 * 1024) {
            System.out.println("内存使用超过 1.5 GB，触发垃圾回收");
            System.gc();
        } else {
            System.out.println("当前内存使用情况：总内存 = " + totalMemory / (1024 * 1024) + " MB, "
                    + "空闲内存 = " + freeMemory / (1024 * 1024) + " MB, "
                    + "已用内存 = " + usedMemory / (1024 * 1024) + " MB");
        }
    }
}
