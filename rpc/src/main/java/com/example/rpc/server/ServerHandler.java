package com.example.rpc.server;

import com.example.rpc.common.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author iceWang
 * @date 2020/3/7
 * @description
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取客户端发送的消息，并调用本地的服务
        System.out.println("c:" + msg);
        // 这里可以定义一个规范，使得客户端满足一定的条件，才可以调用本地接口
        String toString = msg.toString();
        if (toString.startsWith("HelloService#hello#")) {
            String result = new HelloServiceImpl().sayHello(toString.substring(toString.lastIndexOf("#") + 1));
            System.out.println(result);
            ctx.writeAndFlush(result);
        } else {
            System.out.println("你没有权限调用接口");
            ctx.writeAndFlush("你没有权限调用接口");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
