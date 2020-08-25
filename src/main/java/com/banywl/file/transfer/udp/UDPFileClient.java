package com.banywl.file.transfer.udp;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * 文件发送客户端
 */
public class UDPFileClient {

    private InetAddress address;

    private int port;
    /**
     * 包大小
     */
    private final int packetSize = 40960;
    /**
     * 文件包
     */
    private byte[] packetBuf;
    /**
     * 名称长度值占用4字节
     * 文件长度值占用8字节
     */
    private byte[] fileInfoBuf = new byte[12];

    /**
     * 文件名 packet
     */
    private DatagramPacket fileNameLenPacket;
    /**
     * 文件长度 packet
     */
    private DatagramPacket fileLenPacket;
    /**
     * 文件数据 packet
     */
    private DatagramPacket filePacket;

    private DatagramSocket socket = new DatagramSocket();

    /**
     * 初始化文件发送
     * @param hostname 目标主机名称
     * @param port 目标端口
     * @throws UnknownHostException
     * @throws SocketException
     */
    public UDPFileClient(String hostname, int port) throws UnknownHostException, SocketException {
        this.address = InetAddress.getByName(hostname);
        this.port = port;
        this.packetBuf = new byte[this.packetSize];
        // 文件名长度值用4字节
        this.fileNameLenPacket = new DatagramPacket(this.fileInfoBuf,4,this.address,port);
        // 文件长度使用8字节
        this.fileLenPacket = new DatagramPacket(this.fileInfoBuf,4,8,this.address,port);
        // 文件使用512字节分段发送
        this.filePacket = new DatagramPacket(packetBuf,this.packetSize,this.address,port);
    }


    /**
     * udp 包顺序: 1、文件名长度值 2、文件长度值 3、文件名 4、文件内容
     * @param filename 文件绝对路径
     * @throws IOException
     */
    public void sendFile(String filename) throws IOException {
        String name = filename.substring(filename.lastIndexOf("/")-1);
        this.sendFile(filename,name);
    }

    /**
     * udp 包顺序: 1、文件名长度值 2、文件长度值 3、文件名 4、文件内容
     * @param pathname 文件路径
     * @throws IOException
     */
    public void sendFile(String pathname,String filename) throws IOException {
        // 读取系统文件
        File file = new File(pathname + filename);
        byte[] fileBuf = new byte[(int)file.length()];
        byte[] readBuf = new byte[2048];
        int readLen,staPos = 0;
        FileInputStream inputStream =new FileInputStream(file);
        while ((readLen = inputStream.read(readBuf))!=-1){
            System.arraycopy(readBuf,0,fileBuf,staPos,readLen);
            staPos += readLen;
        }
        // 发送文件名长度值和文件长度值
        System.arraycopy(Utils.intToBytes(filename.getBytes().length),0,this.fileInfoBuf,0,4);
        System.arraycopy(Utils.longToBytes(file.length()),0,this.fileInfoBuf,4,8);
        socket.send(fileNameLenPacket);
        socket.send(fileLenPacket);
        // 发送文件名
        DatagramPacket fileNamPacket = new DatagramPacket(filename.getBytes(),filename.getBytes().length,address,port);
        socket.send(fileNamPacket);

        System.out.println("文件名:"+file.getName());
        System.out.println("文件名长度:" + file.getName().getBytes().length);
        System.out.println("文件大小:"+Utils.storeUnit(file.length()));
        System.out.print("当前发送进度:\40\40\40\40");
        LocalDateTime sta = LocalDateTime.now();
        // 发送文件
        int readIndex = 0;
        while (readIndex != fileBuf.length){
            if(readIndex + this.packetSize < fileBuf.length){
                System.arraycopy(fileBuf,readIndex,packetBuf,0,this.packetSize);
                readIndex += this.packetSize;
            }else{
                int rsize = fileBuf.length - readIndex;
                System.arraycopy(fileBuf,readIndex,packetBuf,0,rsize);
                readIndex += rsize;
            }
            socket.send(filePacket);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String progress = String.format("%.2f",((double)readIndex / (double)fileBuf.length));
            System.out.print(Utils.FRONT_CHART.substring(0,progress.length()));
            System.out.print(progress);
        }
        System.out.print("\n");

        Duration duration = Duration.between(sta,LocalDateTime.now());
        System.out.println("发送耗时："+duration.getSeconds());
        System.out.println("====================================================");
    }
    public void close(){
        this.socket.close();
    }




}
