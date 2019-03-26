package com.xetlab.javatest.question1;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteBufferTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("退下！Mr Nobody.".getBytes("UTF8"));
        System.out.println("退下！Mr Nobody.".getBytes("UTF8").length);
        System.out.println(buffer.array().length);
        System.out.println(buffer);
        buffer.flip();
        System.out.println(buffer);
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst);
        System.out.println(new String(dst, "UTF8").getBytes("UTF8").length);
    }
}
