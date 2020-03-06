package com.lyentech.bdc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 来实现当空闲的时候进行处理
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    public static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        switch (event.state()) {
            case READER_IDLE:
                break;
            case WRITER_IDLE:
                break;
            case ALL_IDLE:
                break;
        }
        logger.info("根据不同类型做不同的处理");
        logger.info("可能要关闭对应通道");
    }
}
