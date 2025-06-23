package competition.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Component
@Slf4j
public class StartupDiagnostics {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=== 应用启动诊断 ===");

        // 记录启动时的系统状态
        Runtime runtime = Runtime.getRuntime();
        log.info("JVM信息: {} {}",
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"));
        log.info("可用处理器: {}", runtime.availableProcessors());
        log.info("最大内存: {}MB", runtime.maxMemory() / 1024 / 1024);
        log.info("初始内存: {}MB", runtime.totalMemory() / 1024 / 1024);

        // 记录JVM参数
        ManagementFactory.getRuntimeMXBean().getInputArguments()
            .forEach(arg -> log.info("JVM参数: {}", arg));

        // 检查GC算法
        ManagementFactory.getGarbageCollectorMXBeans()
            .forEach(gc -> log.info("GC算法: {}", gc.getName()));
    }
}