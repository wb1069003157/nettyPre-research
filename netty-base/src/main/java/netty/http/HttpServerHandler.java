package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 自定义一个 Handler，继承 SimpleChannelInboundHandler 规范，来实现
 * 1. SimpleChannelInboundHandler 继承了以前使用的那个  ChannelInboundHandlerAdapter
 * 2。 HttpObject 因为做的是一个 Http 服务，因此在这里直接加入参数，会自动封装好
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    public static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (request.uri().contains("favicon.ico")) {
                logger.info("不做响应！！！");
                return;
            }
            logger.info(msg.getClass() + ctx.channel().remoteAddress().toString());
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);

            DefaultFullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
        }

    }
}
