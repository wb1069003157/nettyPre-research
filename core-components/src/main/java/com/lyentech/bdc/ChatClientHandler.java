package com.lyentech.bdc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {
    public static final Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    /**
     * 当通道有读取事件时，会触发
     *
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info(msg);
    }

}
