package com.xetlab.javatest.question1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain3 {

    private static final Logger logger = LoggerFactory.getLogger(ServerMain3.class);

    public static void main(String[] args) {
        logger.info("0.主线程启动");
        try {

            Map<SocketChannel, Queue> msgQueueMap = new ConcurrentHashMap<SocketChannel, Queue>();

            //创建channel管理器，用于注册channel的事件
            Selector selector = Selector.open();

            //服务端初始化，在9999端口监听，保留BIO初始化方式用于参照
            //ServerSocket serverSocket = new ServerSocket(9999);
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置非阻塞
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));

            //注册可accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                //NIO仅有的一个阻塞方法，当有注册的事件产生时，才会返回
                selector.select();
                //产生事件的socket列表
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyItr = readyKeys.iterator();
                while (keyItr.hasNext()) {
                    SelectionKey readyKey = keyItr.next();
                    keyItr.remove();
                    if (readyKey.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) readyKey.channel();
                        //接受客户端
                        SocketChannel clientChannel = serverChannel.accept();
                        String clientId = String.format("%s:%s", clientChannel.socket().getInetAddress().getHostAddress(), clientChannel.socket().getPort());
                        logger.info("1.客户端 {} 已连接", clientId);

                        msgQueueMap.put(clientChannel, new ArrayBlockingQueue(100));
                        logger.info("2.向客户端发欢迎消息");
                        //NIO发消息先放到消息队列里，等可写时再发
                        msgQueueMap.get(clientChannel).add("你好，请报上名来！");

                        //设置非阻塞
                        clientChannel.configureBlocking(false);
                        //注册可读和可写事件
                        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    } else if (readyKey.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) readyKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int bytesRead = clientChannel.read(byteBuffer);
                        if (bytesRead <= 0) {
                            continue;
                        }
                        byteBuffer.flip();
                        byte[] msgByte = new byte[bytesRead];
                        byteBuffer.get(msgByte);
                        final String clientName = new String(msgByte, "UTF8");
                        logger.info("5.收到客户端消息：{}", clientName);
                        msgQueueMap.get(clientChannel).add(String.format("退下！%s", clientName));
                    } else if (readyKey.isWritable()) {
                        SocketChannel clientChannel = (SocketChannel) readyKey.channel();
                        Queue<String> msgQueue = msgQueueMap.get(clientChannel);
                        String msg = msgQueue.poll();
                        if (msg != null) {
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            byteBuffer.put(msg.getBytes("UTF8"));
                            byteBuffer.flip();
                            clientChannel.write(byteBuffer);
                            logger.info("6.向客户端发退出消息");
                        }
                    }
                }

            }
        } catch (IOException e) {
            logger.error("server error", e);
            System.exit(1);
        }
    }

}
