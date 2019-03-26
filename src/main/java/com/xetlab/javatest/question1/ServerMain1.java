package com.xetlab.javatest.question1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class ServerMain1 {

    private static final Logger logger = LoggerFactory.getLogger(ServerMain1.class);

    public static void main(String[] args) {
        logger.info("0.主线程启动");
        try {
            //服务端初始化，在9999端口监听
            ServerSocket serverSocket = new ServerSocket(9999);
            while (true) {
                //等待客户端连接，如果没有连接就阻塞当前线程
                Socket clientSocket = serverSocket.accept();
                logger.info("1.客户端 {}:{} 已连接", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());

                //向客户端发消息
                logger.info("2.向客户端发欢迎消息");
                clientSocket.getOutputStream().write("你好，请报上名来！".getBytes("UTF8"));
                clientSocket.getOutputStream().flush();

                //从客户端读取消息
                StringBuffer msgBuf = new StringBuffer();
                byte[] byteBuf = new byte[1024];
                clientSocket.getInputStream().read(byteBuf);
                msgBuf.append(new String(byteBuf, "UTF8"));
                logger.info("5.收到客户端消息：{}", msgBuf);

                //向客户端发消息
                logger.info("6.向客户端发退出消息");
                clientSocket.getOutputStream().write(String.format("退下，%s！", msgBuf.toString()).getBytes(Charset.forName("UTF8")));
                clientSocket.getOutputStream().flush();
            }
        } catch (IOException e) {
            logger.error("server error", e);
            System.exit(1);
        }
    }
}
