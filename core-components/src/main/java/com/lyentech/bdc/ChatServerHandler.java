package com.lyentech.bdc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 自定义一个 Handler，继承 Handler 规范，来实现
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    public static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    // 定义一个Channel组，管理所有的Channel
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("{}  加入聊天", channel.remoteAddress());

        channelGroup.writeAndFlush(channel.remoteAddress() + "加入聊天");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("{}  离开聊天", channel.remoteAddress());

        // 会导致当前channel自动退出channelGroup
        channelGroup.writeAndFlush(channel.remoteAddress() + "离开聊天");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("{}  上线了", channel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("{}  下线了", channel.remoteAddress());
    }

    /**
     * 当通道有读取事件时触发
     *
     * @param ctx ctx:上下文对象, 里面含有管道 pipeline , 通道 channel, 地址
     * @param msg 就是客户端发送的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        logger.info("\t\t {} \t\t", LocalDateTime.now());
        logger.info("{} : {}", channel.remoteAddress(), msg);
        String message = "\\t\\t" + LocalDateTime.now() + "\\t\\t\\n" + channel.remoteAddress() + " : " + msg;
        channelGroup.forEach(flag -> {
            if (flag != channel) {
                flag.writeAndFlush(message);
            }
        });

    }

    /**
     * 处理异常，一般关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
