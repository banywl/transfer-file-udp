package com.banywl.file.transfer.udp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class Application {

    private static final LinkedBlockingQueue<File> FILE_QUEUE = new LinkedBlockingQueue<File>();

    /**
     * -server 18888 10240
     * -client 255.255.255.255 18888 10240 path/to/file
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseDir = System.getProperty("user.dir");
        System.out.println(baseDir);
        if (args.length == 0) {
            System.out.println("参数错误!");
            System.exit(0);
        }

        if ("-server".equals(args[0])) {
            int port = Integer.parseInt(args[1]);
            try {
                UDPFileServer server = new UDPFileServer(port);
                System.out.println("服务端已启动");
                while (true) {
                    System.out.println("等待中...");
                    server.receiveFile();
                    File file = new File(baseDir + File.separator + "receive" + File.separator + server.getFileName());
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(server.getFileData());
                    fo.close();
                    System.out.println("文件已写入到目录:" + file.getAbsolutePath());
                    System.out.println("====================================================");
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("-client".equals(args[0])) {

            String type = args[1];
            String ip = args[2];
            int port = Integer.parseInt(args[3]);
            String pathname = args[4];

            if ("-dir".equals(type)) {
                System.out.println("扫描目录:" + pathname);
                listFile(pathname);
            } else if ("-file".equals(type)) {
                pathname = "";
                for (int i = 4; i < args.length; i++) {
                    String arg = args[i];
                    try {
                        File file = new File(args[i]);
                        FILE_QUEUE.put(file);
                        System.out.println("文件:" + file.getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("参数错误!");
                System.exit(0);
            }
            System.out.println("文件总数:" + FILE_QUEUE.size());
            System.out.println("====================================================");
            try {
                UDPFileClient client = new UDPFileClient(ip, port);
                File file = FILE_QUEUE.poll();
                while (file != null) {
                    String filename = file.getAbsolutePath().replace(pathname, "");
                    client.sendFile(pathname, filename);
                    file = FILE_QUEUE.poll();
                }
                client.close();
                System.out.println("文件发送结束，客户端退出");
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            System.out.println("参数错误!");
            System.exit(0);
        }

    }

    /**
     * 递归获取目录下的文件名
     *
     * @param pathname
     */
    public static void listFile(String pathname) {
        File dir = new File(pathname);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                listFile(file.getAbsolutePath());
            } else {
                System.out.println("找到文件:" + file.getName());
                try {
                    FILE_QUEUE.put(file);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
