package com.example.tcp.resolution;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocal> {
    public static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocal msg) throws Exception {
        //将 buffer 转成字符串
        System.out.println("服务器接收到数据 " + msg.getLength());
        System.out.println("服务器接收到数据 " + new String(msg.getContent(), CharsetUtil.UTF_8));
        System.out.println("服务器接收到消息量=" + (++this.count));
        //服务器回送数据给客户端, 回送一个随机 id ,
//        ByteBuf responseByteBuf = Unpooled.copiedBuffer(
//                UUID.randomUUID().toString() + " ", Charset.forName("utf-8"));
//        ctx.writeAndFlush(responseByteBuf);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
