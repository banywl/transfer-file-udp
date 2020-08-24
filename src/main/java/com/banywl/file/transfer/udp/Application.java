package com.banywl.file.transfer.udp;

import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Application {

    /**
     * -server 18888 10240
     * -client 255.255.255.255 18888 10240 path/to/file
     * @param args
     */
    public static void main(String[] args) {
        String baseDir = System.getProperty("user.dir") ;
        System.out.println(baseDir);
        if (args.length == 0) {
            System.out.println("参数错误!");
            System.exit(0);
        }

        if ("-server".equals(args[0])) {
            System.out.println("启动服务端");
            int port = Integer.parseInt(args[1]);
            int size = args.length == 2 ? 1024 : Integer.parseInt(args[2]);

            try {
                UDPFileServer server = new UDPFileServer(port,size);
                while (true){
                    System.out.println("等待接收");
                    server.receiveFile();
                    System.out.println("收到文件:"+ server.getFileName());
                    File file = new File(baseDir + File.separator+ "receive" + File.separator+server.getFileName());
                    if (!file.getParentFile().exists()){
                        file.getParentFile().mkdir();
                    }
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(server.getFileData());
                    fo.close();
                    System.out.println("是否退出(Y/N)?");
                    Scanner scanner = new Scanner(System.in);
                    if ("y".equalsIgnoreCase(scanner.next())){
                        break;
                    }
                }
                server.close();
                System.out.println("服务器已关闭");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if("-client".equals(args[0])){
            System.out.println("启动客户端");
            String ip = args[1];
            int port = Integer.parseInt(args[2]);
            int size = Integer.parseInt(args[3]);
            String pathname = args[4];
            try {
                UDPFileClient client = new UDPFileClient(ip,port,size);
                client.sendFile(pathname);
                client.close();
                System.out.println("发送完成");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
