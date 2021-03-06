package netty.demo.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 异步任务
 */
public class TaskQueueNettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果这里有一个十分耗时的业务 会导致服务端阻塞 因此需要异步执行  这样 channelReadComplete() 会立即执行
        //          ->  提交该 channel 对应的NIOEventLoop 的 taskQueue 中,
        // 放到前面的先执行，因为任务是存储在一个 TaskQueue 中的，有进出顺序
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(20 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 2", CharsetUtil.UTF_8));
                System.out.println("channel1 code=" + ctx.channel().hashCode());
            } catch (Exception ex) {
                System.out.println("发生异常" + ex.getMessage());
            }
        });

        ctx.channel().eventLoop().execute(() -> {
            try {
                // 队列结构，因此会在上一个任务的基础上继续增加
                Thread.sleep(5 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 3", CharsetUtil.UTF_8));
                System.out.println("channel2 code=" + ctx.channel().hashCode());
            } catch (Exception ex) {
                System.out.println("发生异常" + ex.getMessage());
            }
        });

        // 在这里可以看到在 taskQueue 中放了两个任务
        System.out.println("go on .........");
    }

    /**
     * 当通道读取事件完毕后触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将数据写入到缓存，并刷新        一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵1", CharsetUtil.UTF_8));
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
