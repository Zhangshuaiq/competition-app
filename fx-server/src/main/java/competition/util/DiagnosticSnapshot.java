package competition.util;

import java.lang.management.*;
import java.util.List;

/**
 * 诊断快照数据类
 */
public class DiagnosticSnapshot {

    public final long timestamp;
    public final long heapUsedMB;
    public final long heapMaxMB;
    public final double heapUtilization;
    public final double systemLoad;
    public final int threadCount;
    public final long gcCount;
    public final long gcTimeMs;

    public DiagnosticSnapshot() {
        this.timestamp = System.currentTimeMillis();

        // 内存信息
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        this.heapUsedMB = heapUsage.getUsed() / 1024 / 1024;
        this.heapMaxMB = heapUsage.getMax() / 1024 / 1024;
        this.heapUtilization = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;

        // 系统负载
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        this.systemLoad = osBean.getSystemLoadAverage();

        // 线程信息
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        this.threadCount = threadBean.getThreadCount();

        // GC信息
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long totalGcCount = 0;
        long totalGcTime = 0;
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            long count = gcBean.getCollectionCount();
            long time = gcBean.getCollectionTime();
            if (count >= 0) totalGcCount += count;
            if (time >= 0) totalGcTime += time;
        }
        this.gcCount = totalGcCount;
        this.gcTimeMs = totalGcTime;
    }

    @Override
    public String toString() {
        return String.format("堆内存: %dMB/%dMB (%.1f%%), 系统负载: %.2f, 线程: %d, GC: %d次/%.1fs",
            heapUsedMB, heapMaxMB, heapUtilization,
            systemLoad, threadCount, gcCount, gcTimeMs / 1000.0
        );
    }
}