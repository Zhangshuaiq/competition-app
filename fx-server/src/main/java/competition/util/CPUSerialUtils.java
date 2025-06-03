package competition.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPUSerialUtils {

    /**
     * 获取CPU序列号 - 多方法尝试
     */
    public static String getCPUSerial() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return getWindowsCPUSerial();
        } else if (osName.contains("linux")) {
            return getLinuxCPUSerial();
        } else if (osName.contains("mac")) {
            return getMacCPUSerial();
        } else {
            return "";
        }
    }

    /**
     * Windows系统 - 多种方法尝试
     */
    private static String getWindowsCPUSerial() {
        String result = "";

        // 方法1: PowerShell Get-CimInstance (推荐)
        result = tryWindowsMethod1();
        if (!result.isEmpty()) return result;

        // 方法2: wmic命令
        result = tryWindowsMethod2();
        if (!result.isEmpty()) return result;

        // 方法3: PowerShell Get-WmiObject (兼容老版本)
        result = tryWindowsMethod3();
        if (!result.isEmpty()) return result;

        // 方法4: 注册表方式
        result = tryWindowsMethod4();
        if (!result.isEmpty()) return result;

        return "";
    }

    /**
     * 方法1: PowerShell Get-CimInstance
     */
    private static String tryWindowsMethod1() {
        try {
            String[] commands = {
                "powershell.exe", "-Command",
                "Get-CimInstance Win32_Processor | Select-Object -ExpandProperty ProcessorId"
            };
            return executeWindowsCommand(commands);
        } catch (Exception e) {
            System.err.println("Method 1 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * 方法2: wmic命令
     */
    private static String tryWindowsMethod2() {
        try {
            String[] commands = {
                "wmic", "cpu", "get", "ProcessorId", "/value"
            };
            String output = executeCommand(commands);

            // 解析wmic输出格式 ProcessorId=xxx
            Pattern pattern = Pattern.compile("ProcessorId=(.+)");
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
            return "";
        } catch (Exception e) {
            System.err.println("Method 2 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * 方法3: PowerShell Get-WmiObject (兼容性更好)
     */
    private static String tryWindowsMethod3() {
        try {
            String[] commands = {
                "powershell.exe", "-Command",
                "Get-WmiObject Win32_Processor | Select-Object -ExpandProperty ProcessorId"
            };
            return executeWindowsCommand(commands);
        } catch (Exception e) {
            System.err.println("Method 3 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * 方法4: 注册表方式 (备用方案)
     */
    private static String tryWindowsMethod4() {
        try {
            String[] commands = {
                "reg", "query",
                "HKEY_LOCAL_MACHINE\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0",
                "/v", "ProcessorNameString"
            };
            String output = executeCommand(commands);

            // 从注册表输出中提取信息
            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.contains("ProcessorNameString")) {
                    String[] parts = line.split("REG_SZ");
                    if (parts.length > 1) {
                        return parts[1].trim().hashCode() + ""; // 生成伪序列号
                    }
                }
            }
            return "";
        } catch (Exception e) {
            System.err.println("Method 4 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Linux系统CPU序列号获取
     */
    private static String getLinuxCPUSerial() {
        String result = "";

        try {
            // 方法1: 读取 /proc/cpuinfo
            result = tryLinuxMethod1();
            if (!result.isEmpty()) return result;

            // 方法2: dmidecode命令
            result = tryLinuxMethod2();
            if (!result.isEmpty()) return result;

            // 方法3: lscpu命令
            result = tryLinuxMethod3();
            if (!result.isEmpty()) return result;

        } catch (Exception e) {
            System.err.println("Linux methods failed: " + e.getMessage());
        }

        return "";
    }

    /**
     * 读取/proc/cpuinfo
     */
    private static String tryLinuxMethod1() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("/proc/cpuinfo")));
            String[] lines = content.split("\n");

            for (String line : lines) {
                if (line.startsWith("serial")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        return parts[1].trim();
                    }
                }
                // 有些系统用processor id
                if (line.startsWith("processor") && line.contains("id")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        return parts[1].trim();
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * dmidecode命令
     */
    private static String tryLinuxMethod2() {
        try {
            String[] commands = {"dmidecode", "-t", "processor"};
            String output = executeCommand(commands);

            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.contains("ID:") || line.contains("Serial Number:")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        String serial = parts[1].trim();
                        if (!serial.isEmpty() && !serial.equals("Not Specified")) {
                            return serial;
                        }
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * lscpu命令
     */
    private static String tryLinuxMethod3() {
        try {
            String[] commands = {"lscpu"};
            String output = executeCommand(commands);

            // 从lscpu输出中提取CPU信息，生成唯一标识
            Pattern pattern = Pattern.compile("Model name:\\s*(.+)");
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                return String.valueOf(matcher.group(1).trim().hashCode());
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Mac系统CPU序列号获取
     */
    private static String getMacCPUSerial() {
        String result = "";

        // 方法1: 获取硬件序列号
        result = tryMacMethod1();
        if (!result.isEmpty()) return result;

        // 方法2: 获取CPU信息
        result = tryMacMethod2();
        if (!result.isEmpty()) return result;

        // 方法3: ioreg获取平台序列号
        result = tryMacMethod3();
        if (!result.isEmpty()) return result;

        return "";
    }

    /**
     * Mac方法1: system_profiler获取硬件序列号
     */
    private static String tryMacMethod1() {
        try {
            String[] commands = {"system_profiler", "SPHardwareDataType"};
            String output = executeCommand(commands);

            String[] lines = output.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.contains("Serial Number (system):")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        String serial = parts[1].trim();
                        if (!serial.isEmpty() && !serial.equals("Not Available")) {
                            return serial;
                        }
                    }
                }
            }
            return "";
        } catch (Exception e) {
            System.err.println("Mac method 1 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Mac方法2: 获取CPU信息并生成唯一标识
     */
    private static String tryMacMethod2() {
        try {
            String[] commands = {"sysctl", "-a"};
            Process process = Runtime.getRuntime().exec(commands);

            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

                String line;
                String cpuBrand = "";
                String cpuModel = "";

                while ((line = reader.readLine()) != null) {
                    if (line.contains("machdep.cpu.brand_string")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            cpuBrand = parts[1].trim();
                        }
                    } else if (line.contains("machdep.cpu.model")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            cpuModel = parts[1].trim();
                        }
                    }
                }

                if (!cpuBrand.isEmpty() || !cpuModel.isEmpty()) {
                    result.append(cpuBrand).append("_").append(cpuModel);
                    return String.valueOf(Math.abs(result.toString().hashCode()));
                }
            }

            process.waitFor();
            return "";
        } catch (Exception e) {
            System.err.println("Mac method 2 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Mac方法3: ioreg获取平台序列号
     */
    private static String tryMacMethod3() {
        try {
            // 使用sh来执行管道命令
            String[] commands = {
                "/bin/sh", "-c",
                "ioreg -l | grep IOPlatformSerialNumber | head -1"
            };

            Process process = Runtime.getRuntime().exec(commands);

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

                String line = reader.readLine();
                if (line != null && line.contains("=")) {
                    // 解析格式: "IOPlatformSerialNumber" = "C02ABC123DEF"
                    int equalIndex = line.indexOf("=");
                    if (equalIndex > 0) {
                        String value = line.substring(equalIndex + 1).trim();
                        // 移除引号
                        value = value.replaceAll("\"", "").trim();
                        if (!value.isEmpty() && !value.equals("Not Available")) {
                            return value;
                        }
                    }
                }
            }

            process.waitFor();
            return "";
        } catch (Exception e) {
            System.err.println("Mac method 3 failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * 通用方法 - 当无法获取真实序列号时，生成基于硬件信息的唯一标识
     */
    private static String getGenericCPUInfo() {
        try {
            StringBuilder sb = new StringBuilder();

            // 收集系统属性
            sb.append(System.getProperty("os.name", ""));
            sb.append(System.getProperty("os.arch", ""));
            sb.append(System.getProperty("os.version", ""));
            sb.append(Runtime.getRuntime().availableProcessors());

            // 尝试获取更多硬件信息
            try {
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
                sb.append(System.getProperty("java.vm.name", ""));
            } catch (Exception e) {
                // 忽略
            }

            // 生成基于这些信息的哈希值
            return String.valueOf(Math.abs(sb.toString().hashCode()));

        } catch (Exception e) {
            return "UNKNOWN_CPU_" + System.currentTimeMillis();
        }
    }

    /**
     * 执行系统命令的通用方法
     */
    private static String executeCommand(String[] commands) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }

        // 等待进程完成
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }

        return result.toString().trim();
    }

    /**
     * Windows专用的命令执行和解析
     */
    private static String executeWindowsCommand(String[] commands) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() &&
                    !line.contains("ProcessorId") &&
                    !line.contains("---") &&
                    line.length() > 5) {
                    result.append(line);
                    break; // 只要第一个有效结果
                }
            }
        }

        // 等待进程完成
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }

        return result.toString().trim();
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        try {
            String cpuSerial = getCPUSerial();
            System.out.println("CPU Serial: " + cpuSerial);
            System.out.println("OS: " + System.getProperty("os.name"));
            System.out.println("Arch: " + System.getProperty("os.arch"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 改进的原始方法 - 增加错误处理和兼容性
     */
    public static String getImprovedCPUSerial() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();

            if (!osName.contains("windows")) {
                return getCPUSerial(); // 使用上面的跨平台方法
            }

            // Windows系统改进版
            String[] commands = {
                "powershell.exe",
                "-NoProfile",
                "-NonInteractive",
                "-Command",
                "try { Get-CimInstance Win32_Processor -ErrorAction Stop | Select-Object -ExpandProperty ProcessorId } catch { Get-WmiObject Win32_Processor | Select-Object -ExpandProperty ProcessorId }"
            };

            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8"))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() &&
                        !line.contains("ProcessorId") &&
                        line.length() > 5) {
                        result.append(line);
                        break; // 只要第一个有效结果
                    }
                }
            }

            // 设置超时
            if (!process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new RuntimeException("Command timeout");
            }

            String output = result.toString().trim();
            if (!output.isEmpty()) {
                return output;
            }

            // 如果PowerShell失败，降级到备用方案
            return getCPUSerial();

        } catch (Exception e) {
            System.err.println("Error getting CPU serial: " + e.getMessage());
            return getCPUSerial(); // 使用备用方案
        }
    }
}