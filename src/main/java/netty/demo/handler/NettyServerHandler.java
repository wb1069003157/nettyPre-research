package netty.demo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 自定义一个 Handler，继承 Handler 规范，来实现
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    public static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);


    /**
     * 当通道有读取事件时触发
     *
     * @param ctx ctx:上下文对象, 里面含有管道 pipeline , 通道 channel, 地址
     * @param msg 就是客户端发送的数据 默认 Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("服务器读取线程 :{}", Thread.currentThread().getName());
        logger.info("server ctx :{}", ctx);
        logger.info("看看 channel 和 pipeline 的关系");
        Channel channel = ctx.channel();
        //本质是一个双向链接, 可以实现套娃 ： ctx.pipeline().channel().pipeline().channel().pipeline();
        ChannelPipeline pipeline = ctx.pipeline();
        // 将 msg 转成一个 ByteBuf
        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer.
        ByteBuf buf = (ByteBuf) msg;
        logger.info("客户端发送消息是: {}", buf.toString(CharsetUtil.UTF_8));
        logger.info("客户端地址: {}", channel.remoteAddress());
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将数据写入到缓存，并刷新        一般讲，我们对这个发送的数据进行编码
        // writeAndFlush：用于将数据写入客户端通道
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵", CharsetUtil.UTF_8));
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
