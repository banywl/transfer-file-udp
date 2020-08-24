package com.banywl.file.transfer.udp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class Application {

    private static final LinkedBlockingQueue<File> FILE_QUEUE = new LinkedBlockingQueue<File>(20);

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
                    File file = new File(baseDir + File.separator+ "receive" + File.separator+server.getFileName());
                    if (!file.getParentFile().exists()){
                        file.getParentFile().mkdir();
                    }
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(server.getFileData());
                    fo.close();
                    System.out.println("文件已写入磁盘:"+file.getAbsolutePath());
                }
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
            System.out.println("扫描目录文件:"+pathname);
            listFile(pathname);
            System.out.println("开始发送文件");
            try {
                UDPFileClient client = new  UDPFileClient(ip,port,size);
                File file = null;
                while ((file = FILE_QUEUE.poll()) != null){
                    String filename = file.getAbsolutePath().replace(pathname,"");
                    client.sendFile(pathname,filename);
                    System.out.println("发送完成："+file.getAbsolutePath());
                }
                client.close();
                System.out.println("文件发送结束，客户端退出");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("参数错误!");
            System.exit(0);
        }

    }

    /**
     * 递归获取目录下的文件名
     * @param pathname
     */
    public static void listFile(String pathname){
        File dir = new File(pathname);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()){
                listFile(file.getAbsolutePath());
            }else{
                try {
                    FILE_QUEUE.put(file);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }



}
