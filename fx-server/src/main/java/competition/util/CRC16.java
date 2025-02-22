package competition.util;

import java.nio.charset.StandardCharsets;

public class CRC16 {
    // CRC-CCITT 的多项式 (X^16 + X^12 + X^5 + 1) 对应的多项式是 0x1021
    private static final int CRC16_POLYNOMIAL = 0x1021;
    private static final int CRC16_INITIAL_VALUE = 0;

    /**
     * 计算CRC16校验值 (CRC-CCITT)
     *
     * @param data 数据字节数组
     * @return 计算后的CRC16校验值
     */
    public static String calculateCRC16(byte[] data) {
        int crc = CRC16_INITIAL_VALUE; // 初始化值为0xFFFF

        for (byte b : data) {
            crc ^= (b << 8) & 0xFF00; // 将当前字节左移8位，与当前CRC值异或
            for (int i = 0; i < 8; i++) { // 每个字节处理8次，即每个位
                if ((crc & 0x8000) != 0) { // 如果最高位是1
                    crc = (crc << 1) ^ CRC16_POLYNOMIAL; // 左移并与多项式异或
                } else {
                    crc <<= 1; // 否则仅左移
                }
            }
        }
        return String.format("%04X",(crc & 0xFFFF)); // 返回16位结果
    }

    public static void main(String[] args) {
        // 测试数据
        byte[] testData = "#,0020,SMOD,0,".getBytes(StandardCharsets.US_ASCII); // 示例数据，可以根据实际情况修改
        String crc = calculateCRC16(testData);
        System.out.printf(crc); // 以16进制格式输出CRC16校验值
    }
}
