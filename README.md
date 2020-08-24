# java 使用 UDP 发送文件

## 环境

jdk 1.8

## 使用说明

使用 Maven 打包成jar文件可直接运行使用

***服务端(接收文件)启动命令如下***

> java -jar transfer.jar -server 端口号 文件传输大小

***客户端(发送文件)启动命令如下***

> java -jar transfer.jar -client 地址 端口号 文件传输大小 要传输的文件

!!! Caution 注意: 服务端设置的文件传输大小需要与客户端设置的文件传输大小保持一致

## 使用示例(linux环境当前目录文件夹)

```bash
# 启动服务端，文件传输大小设置为1M
cd target
java -jar transfer.jar -server 18888 10240
# 启动客户端并发送 test.mp4 文件
java -jar transfer.jar -client 127.0.0.1 18888 10240 /path/to/test.mp4
```

## 遇到的问题

**Error: Unable to access jarfile**

1.检查目录下是否存在 transfer.jar 文件

2.windows 环境下 transfer.jar 需要指定绝对路径


