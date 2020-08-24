# java使用UDP批量发送文件

## 环境

jdk 1.8

## 使用说明

使用 Maven 打包成jar文件可直接运行使用

***服务端(接收文件)启动命令如下***

> java -jar transfer.jar -server 端口号 文件传输大小

***客户端(发送文件)启动命令如下***

> java -jar transfer.jar -client 地址 端口号 文件传输大小 文件根目录

!!! Caution 注意: 服务端设置的文件传输大小需要与客户端设置的文件传输大小保持一致

## 使用示例(linux环境当前目录文件夹)

```bash
# 启动服务端，文件传输大小设置为1M
cd target
java -jar transfer.jar -server 18888 10240
# 启动客户端并扫描 /root 目录下的文件进行发送
java -jar transfer.jar -client 127.0.0.1 18888 10240 /root
```

## 遇到的问题

**Error: Unable to access jarfile**

1.检查目录下是否存在 transfer.jar 文件

2.windows 环境下 transfer.jar 需要指定绝对路径

**OutOfMemoryException: java heap space**

程序采用的是UDP对等通信策略

发包解包包顺序如下

第1个包: 文件名长度值 

第2个包: 文件长度值
 
第3个包: 文件名
 
第4-n个包: 文件内容

其中第3个包的长度由第1个包传递

此后在第4个包接收之前会根据第二个包的值向内存申请文件接收缓存

如果发现了OOM要检查发包的顺序，或者第1个包和第二个包的值是否正确



