package com.example.tcp.resolution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author iceWang
 * @date 2020/3/6
 * @description
 */
public class ServerDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("ServerDecoder.decode() 被调用！！！");
        int length = in.readInt();

        if (length != 0) {

            byte[] content = new byte[length];
            in.readBytes(content);

            MessageProtocal messageProtocal = new MessageProtocal();
            messageProtocal.setLength(length);
            messageProtocal.setContent(content);

            out.add(messageProtocal);
        }
    }
}
