package com.example.tcp.resolution;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocal> {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //使用客户端发送 10 条数据
        String message = "iceWang is handsome,他是真的帅!!!";
        for (int i = 0; i < 10; ++i) {
            byte[] content = message.getBytes(Charset.forName("utf-8"));
            int length = content.length;

            MessageProtocal messageProtocal = new MessageProtocal();
            messageProtocal.setLength(length);
            messageProtocal.setContent(content);

            ctx.writeAndFlush(messageProtocal);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocal msg) throws Exception {
        System.out.println("客户端接收到消息=" + msg.getLength());
        System.out.println("客户端接收到消息=" + new String(msg.getContent()));

        System.out.println("客户端接收消息数量=" + (++this.count));
    }
}
