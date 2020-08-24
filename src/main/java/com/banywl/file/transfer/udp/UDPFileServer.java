package com.banywl.file.transfer.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 文件接收服务器
 */
public class UDPFileServer {

    /**
     * socket 缓冲区
     */
    private DatagramSocket socket;
    /**
     * 文件包数据大小
     */
    private int packetSize;
    /**
     * 名称长度值占用4字节
     * 文件长度值占用8字节
     */
    private byte[] fileInfo = new byte[12];
    /**
     * packet 缓冲区
     */
    private byte[] packetBuf;
    /**
     * 文件名长度值用4字节缓冲区接收 int 值
     */
    private DatagramPacket fileNameLenPacket;
    /**
     * 文件长度值使用8字节缓冲区接收 long 值
     */
    private DatagramPacket fileLenPacket;
    /**
     * 文件数据封包使用512字节缓冲区分段接收
     */
    private DatagramPacket filePacket;
    /**
     * 接收到的文件名
     */
    private String fileName;
    /**
     * 接收到的文件内容
     */
    private byte[] fileData;


    /**
     * 初始化文件接收UDP服务
     *
     * @param port 监听端口
     * @param packetSize 封包大小
     * @throws SocketException
     */
    public UDPFileServer(int port, int packetSize) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.packetSize = packetSize;
        this.packetBuf = new byte[packetSize];
        fileNameLenPacket = new DatagramPacket(fileInfo,  4);
        fileLenPacket = new DatagramPacket(fileInfo,  4,8);
        filePacket = new DatagramPacket(packetBuf,  this.packetSize);
    }

    /**
     * 接收文件，
     * udp 包顺序: 1、文件名长度值 2、文件长度值 3、文件名 4、文件内容
     *
     * @return 文件内容字节数组
     * @throws IOException
     */
    public void receiveFile() throws IOException {
        // 读取文件名长度
        this.socket.receive(fileNameLenPacket);
        // 读取文件长度
        this.socket.receive(fileLenPacket);
        // 取回文件名
        byte[] fileNameBuf = new byte[Utils.bytesToInt(this.fileInfo)];
        DatagramPacket fnPacket = new DatagramPacket(fileNameBuf, fileNameBuf.length);
        this.socket.receive(fnPacket);
        this.fileName = new String(fileNameBuf);
        // 建立文件缓冲区,读取文件内容到缓冲区
        int fileLen = (int) Utils.bytesToLong(this.fileInfo,4);
        this.fileData = new byte[fileLen];
        int writePos = 0;
        while (writePos != fileLen) {
            // 取回文件内容
            this.socket.receive(filePacket);
            if (writePos + this.packetSize < fileLen) {
                System.arraycopy(packetBuf, 0, this.fileData, writePos, this.packetSize);
                writePos += this.packetSize;
            } else {
                int rsize = fileLen - writePos;
                System.arraycopy(packetBuf, 0, this.fileData, writePos, rsize);
                writePos += rsize;
            }
        }
        System.out.println("接收完成："+this.fileName);
    }

    public void close() {
        this.socket.close();
    }


    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }


}
