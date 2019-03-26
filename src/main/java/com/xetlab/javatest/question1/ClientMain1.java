package com.xetlab.javatest.question1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class ClientMain1 {

    private static final Logger logger = LoggerFactory.getLogger(ClientMain1.class);

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            while (true) {
                StringBuffer msgBuf = new StringBuffer();
                byte[] byteBuf = new byte[1024];
                socket.getInputStream().read(byteBuf);
                msgBuf.append(new String(byteBuf, "UTF8"));
                logger.info("3.收到服务端消息：{}", msgBuf);
                try {
                    Thread.sleep(30000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("4.向服务端发送名字消息");
                socket.getOutputStream().write("Mr Nobody.".getBytes("UTF8"));
                socket.getOutputStream().flush();

                msgBuf = new StringBuffer();
                byteBuf = new byte[1024];
                socket.getInputStream().read(byteBuf);
                msgBuf.append(new String(byteBuf, "UTF8"));
                logger.info("7.收到服务端消息：{}", msgBuf);
                if (msgBuf.toString().startsWith("退下")) {
                    socket.close();
                    logger.info("8.客户端退出");
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("client error", e);
            System.exit(1);
        }
    }
}
