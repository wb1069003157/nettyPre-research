package com.example.tcp.resolution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author iceWang
 * @date 2020/3/6
 * @description
 */
public class ClientEncoder extends MessageToByteEncoder<MessageProtocal> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocal msg, ByteBuf out) throws Exception {
        out.writeLong(msg.getLength());
        out.writeBytes(msg.getContent());
    }
}
