package com.example.tcp.question;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 自定义一个 Handler，继承 Handler 规范，来实现
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        //将 buffer 转成字符串
        String message = new String(buffer, Charset.forName("utf-8"));
        System.out.println("服务器接收到数据 " + message);
        System.out.println("服务器接收到消息量=" + (++this.count));
        //服务器回送数据给客户端, 回送一个随机 id ,
        ByteBuf responseByteBuf = Unpooled.copiedBuffer(
                UUID.randomUUID().toString() + " ", Charset.forName("utf-8"));
        ctx.writeAndFlush(responseByteBuf);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
