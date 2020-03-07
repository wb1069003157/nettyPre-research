package com.example.tcp.resolution;

/**
 * @author iceWang
 * @date 2020/3/6
 * @description
 */
public class MessageProtocal {
    private int length;
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
