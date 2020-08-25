package com.banywl.file.transfer.udp;

public class Utils {

    public static final String FRONT_CHART = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";


    /**
     * int 转 byte 数组
     *
     * @param val int 值
     * @return byte 数组
     */
    public static byte[] intToBytes(int val) {

        return new byte[]{
                (byte) (val),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24),
        };
    }

    /**
     * 字节转int
     *
     * @param bytes
     * @return
     */
    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes,0);
    }

    /**
     * 字节转int
     * @param buf 字节数组
     * @param offset 开始位置
     * @return
     */
    public static int bytesToInt(byte[] buf, int offset) {
        return buf[offset] & 0xff
                | (buf[offset + 1] & 0xff) << 8
                | (buf[offset + 2] & 0xff) << 16
                | (buf[offset + 3] & 0xff) << 24;
    }


    /**
     * long 转字节数组
     * @param values
     * @return
     */
    public static byte[] longToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * 字节数组 转 long
     * @param buffer
     * @return
     */
    public static long bytesToLong(byte[] buffer) {
        return bytesToLong(buffer,0);
    }

    /**
     * 字节数组 转 long
     * @param buffer
     * @return
     */
    public static long bytesToLong(byte[] buffer, int offset) {
        long  values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (buffer[offset + i] & 0xff);
        }
        return values;
    }


    /**
     * 字节长度转MB
     * @param len 长度值
     * @return
     */
    public static String storeUnit(long len){

        double[] unit = {1,1024, 1048576, 1073741824};
        String[] suffix = {"B","KB","MB","GB"};

        for (int i = 0;i < unit.length;i++){
            if (len < unit[i]){
                return String.format("%.2f %s",(len / unit[i-1]),suffix[i-1]);
            }
        }
        return len+"B";
    }





}


