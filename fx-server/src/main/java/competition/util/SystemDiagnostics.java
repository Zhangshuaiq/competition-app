package competition.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统诊断服务 - 启动时自动开始后台检测
 * 用于检测内存泄漏和CPU异常
 */
@Slf4j
@Component
public class SystemDiagnostics {

    private final List<DiagnosticSnapshot> snapshots = new ArrayList<>();
    private final AtomicLong startTime = new AtomicLong();
    private int snapshotCount = 0;
    private boolean diagnosticsEnabled = true;

    /**
     * Spring容器初始化后立即启动诊断
     */
    @PostConstruct
    public void initDiagnostics() {
        startTime.set(System.currentTimeMillis());
        log.info("=== 系统诊断服务初始化 ===");

        // 输出JVM基本信息
        logJvmInfo();

        // 立即记录第一个快照
        recordSnapshot();

        log.info("=== 后台诊断已启动，每分钟自动检测 ===");
    }

    /**
     * 应用完全就绪后输出就绪信息
     */
   // @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=== 应用就绪，诊断服务正在后台运行 ===");
//        log.info("进程ID: {}", ProcessHandle.current().pid());
        log.info("诊断状态: {}", getStatusSummary());
    }

    /**
     * 输出JVM信息
     */
    private void logJvmInfo() {
        log.info("JVM信息: {} {}",
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"));

        Runtime runtime = Runtime.getRuntime();
        log.info("系统信息: 可用处理器={}, 最大内存={}MB, 当前内存={}MB",
            runtime.availableProcessors(),
            runtime.maxMemory() / 1024 / 1024,
            runtime.totalMemory() / 1024 / 1024);

        // 记录GC算法
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : gcBeans) {
            log.info("GC算法: {}", gc.getName());
        }

        // 记录重要的JVM参数
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        log.info("JVM参数数量: {}", jvmArgs.size());

        // 输出关键的JVM参数
        jvmArgs.stream()
            .filter(arg -> arg.contains("Xmx") || arg.contains("Xms") ||
                arg.contains("GC") || arg.contains("heap"))
            .forEach(arg -> log.info("关键JVM参数: {}", arg));
    }

    /**
     * 每分钟自动记录系统快照 - 后台运行
     */
   // @Scheduled(fixedRate = 1000) // 60秒
    public void recordSnapshot() {
        if (!diagnosticsEnabled) {
            return;
        }

        try {
            DiagnosticSnapshot snapshot = new DiagnosticSnapshot();
            snapshots.add(snapshot);
            snapshotCount++;

            // 使用DEBUG级别，减少日志输出
            log.debug("后台快照 #{}: {}", snapshotCount, snapshot);

            // 每5分钟输出一次INFO级别的状态
            if (snapshotCount % 5 == 0) {
                log.info("运行状态检查 #{}: {}", snapshotCount, snapshot);
            }

            // 只保留最近30分钟的数据
            while (snapshots.size() > 30) {
                snapshots.remove(0);
            }

            // 每5分钟分析一次问题
            if (snapshotCount % 5 == 0 && snapshots.size() >= 5) {
                analyzeProblems();
            }

        } catch (Exception e) {
            log.error("后台诊断记录失败", e);
        }
    }

    /**
     * 静默模式的问题分析 - 只在发现问题时输出
     */
    public void analyzeProblems() {
        if (snapshots.size() < 5) {
            return; // 数据不足，静默跳过
        }

        boolean hasMemoryIssue = analyzeMemoryTrend();
        boolean hasPerformanceIssue = analyzePerformanceTrend();
        boolean hasGcIssue = analyzeGcTrend();
        boolean hasThreadIssue = analyzeThreadTrend();

        if (hasMemoryIssue || hasPerformanceIssue || hasGcIssue || hasThreadIssue) {
            log.warn("=== 🚨 检测到系统异常 ===");
            provideDiagnosticAdvice();
        } else {
            // 静默模式：只在DEBUG级别记录正常状态
            log.debug("=== ✅ 系统运行正常 ===");
        }
    }

    /**
     * 后台模式的内存趋势分析
     */
    private boolean analyzeMemoryTrend() {
        List<DiagnosticSnapshot> recent = getRecentSnapshots(10);
        if (recent.size() < 3) return false;

        DiagnosticSnapshot first = recent.get(0);
        DiagnosticSnapshot last = recent.get(recent.size() - 1);

        long memoryGrowthMB = last.heapUsedMB - first.heapUsedMB;
        double timeMinutes = (last.timestamp - first.timestamp) / 1000.0 / 60.0;
        double growthPerMinute = memoryGrowthMB / timeMinutes;

        boolean hasIssue = false;

        // 检查内存增长速度
        if (growthPerMinute > 5.0) {
            log.warn("🔥 疑似内存泄漏: 内存增长过快 ({:.2f}MB/分钟)", growthPerMinute);
            log.warn("   当前内存: {}MB/{}MB ({:.1f}%)", last.heapUsedMB, last.heapMaxMB, last.heapUtilization);
            hasIssue = true;
        }

        // 检查内存使用率
        if (last.heapUtilization > 85) {
            log.warn("⚠️  内存使用率过高: {:.1f}% ({}MB/{}MB)",
                last.heapUtilization, last.heapUsedMB, last.heapMaxMB);
            hasIssue = true;
        }

        // 检查是否持续增长
        int increasingCount = 0;
        for (int i = 1; i < recent.size(); i++) {
            if (recent.get(i).heapUsedMB > recent.get(i-1).heapUsedMB) {
                increasingCount++;
            }
        }

        if (increasingCount > recent.size() * 0.8) {
            log.warn("📈 内存持续增长: {}%的时间在增长 (共{:.1f}分钟)",
                increasingCount * 100 / recent.size(), timeMinutes);
            hasIssue = true;
        }

        return hasIssue;
    }

    /**
     * 后台模式的性能趋势分析
     */
    private boolean analyzePerformanceTrend() {
        List<DiagnosticSnapshot> recent = getRecentSnapshots(10);
        if (recent.size() < 3) return false;

        double avgLoad = recent.stream()
            .mapToDouble(s -> s.systemLoad > 0 ? s.systemLoad : 0)
            .average().orElse(0);

        int processors = Runtime.getRuntime().availableProcessors();
        double loadPerProcessor = avgLoad / processors;

        boolean hasIssue = false;

        if (loadPerProcessor > 0.8) {
            log.warn("💻 系统负载过高: 每核负载 {:.2f} (平均负载 {:.2f}, {}核心)",
                loadPerProcessor, avgLoad, processors);
            hasIssue = true;
        }

        return hasIssue;
    }

    /**
     * 后台模式的GC趋势分析
     */
    private boolean analyzeGcTrend() {
        List<DiagnosticSnapshot> recent = getRecentSnapshots(10);
        if (recent.size() < 3) return false;

        DiagnosticSnapshot first = recent.get(0);
        DiagnosticSnapshot last = recent.get(recent.size() - 1);

        long gcIncrease = last.gcCount - first.gcCount;
        long gcTimeIncrease = last.gcTimeMs - first.gcTimeMs;
        double timeMinutes = (last.timestamp - first.timestamp) / 1000.0 / 60.0;

        double gcPerMinute = gcIncrease / timeMinutes;
        double gcSecondsPerMinute = gcTimeIncrease / timeMinutes / 1000.0;

        boolean hasIssue = false;

        if (gcPerMinute > 10.0) {
            log.warn("🗑️  GC频率过高: {:.1f}次/分钟 (共{}次GC)", gcPerMinute, gcIncrease);
            hasIssue = true;
        }

        if (gcSecondsPerMinute > 2.0) {
            log.warn("⏱️  GC耗时过长: {:.2f}秒/分钟 (共{:.1f}秒)",
                gcSecondsPerMinute, gcTimeIncrease/1000.0);
            hasIssue = true;
        }

        return hasIssue;
    }

    /**
     * 后台模式的线程趋势分析
     */
    private boolean analyzeThreadTrend() {
        List<DiagnosticSnapshot> recent = getRecentSnapshots(10);
        if (recent.size() < 3) return false;

        DiagnosticSnapshot first = recent.get(0);
        DiagnosticSnapshot last = recent.get(recent.size() - 1);

        int threadGrowth = last.threadCount - first.threadCount;
        int maxThreads = recent.stream().mapToInt(s -> s.threadCount).max().orElse(0);

        boolean hasIssue = false;

        if (threadGrowth > 20) {
            log.warn("🧵 线程数快速增长: 从{}增长到{} (+{}个线程)",
                first.threadCount, last.threadCount, threadGrowth);
            hasIssue = true;
        }

        if (maxThreads > 200) {
            log.warn("🔢 线程数过多: 当前{}个线程 (峰值{}个)",
                last.threadCount, maxThreads);
            hasIssue = true;
        }

        return hasIssue;
    }

    /**
     * 获取最近N分钟的快照
     */
    private List<DiagnosticSnapshot> getRecentSnapshots(int minutes) {
        long cutoffTime = System.currentTimeMillis() - (minutes * 60 * 1000L);
        List<DiagnosticSnapshot> result = new ArrayList<>();
        for (DiagnosticSnapshot snapshot : snapshots) {
            if (snapshot.timestamp > cutoffTime) {
                result.add(snapshot);
            }
        }
        return result;
    }

    /**
     * 提供诊断建议 - 自动化处理
     */
    private void provideDiagnosticAdvice() {
        // 自动处理：如果内存使用率很高，自动生成堆转储
        if (!snapshots.isEmpty()) {
            DiagnosticSnapshot latest = snapshots.get(snapshots.size() - 1);
            if (latest.heapUtilization > 90) {
                log.warn("🚨 内存使用率超过90%，自动生成堆转储");
                generateHeapDumpAsync();
            }
        }
    }

    /**
     * 自动异步生成堆转储
     */
    private void generateHeapDumpAsync() {
    }

    /**
     * 获取当前状态摘要
     */
    public String getStatusSummary() {
        if (snapshots.isEmpty()) {
            return "后台诊断启动中...";
        }

        DiagnosticSnapshot latest = snapshots.get(snapshots.size() - 1);
        long uptimeMinutes = (System.currentTimeMillis() - startTime.get()) / 1000 / 60;

        return String.format("运行%d分钟 | 内存:%dMB(%.1f%%) | 负载:%.2f | 线程:%d | 已检测%d次",
            uptimeMinutes, latest.heapUsedMB, latest.heapUtilization,
            latest.systemLoad, latest.threadCount, snapshotCount);
    }

    /**
     * 手动触发问题分析（用于外部调用）
     */
    public void triggerAnalysis() {
        log.info("🔍 手动触发系统诊断分析");
        recordSnapshot();
        analyzeProblems();
    }

    /**
     * 启用/禁用诊断
     */
    public void setDiagnosticsEnabled(boolean enabled) {
        this.diagnosticsEnabled = enabled;
        log.info("🔄 后台诊断已{}", enabled ? "启用" : "禁用");
    }

    /**
     * 获取诊断状态
     */
    public boolean isDiagnosticsEnabled() {
        return diagnosticsEnabled;
    }

    /**
     * 获取最近的快照数据（用于外部查询）
     */
    public List<DiagnosticSnapshot> getRecentSnapshots() {
        return getRecentSnapshots(10); // 默认返回最近10分钟
    }

    /**
     * 应用关闭时的清理
     */
    @PreDestroy
    public void shutdown() {
        log.info("=== 🔚 后台诊断服务正在关闭 ===");
        log.info("运行统计: {}", getStatusSummary());

        if (!snapshots.isEmpty()) {
            DiagnosticSnapshot latest = snapshots.get(snapshots.size() - 1);
            log.info("最终状态: {}", latest);
        }

        log.info("=== 后台诊断服务已停止 ===");
    }
}