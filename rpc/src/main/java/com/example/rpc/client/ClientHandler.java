package com.example.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * @author iceWang
 * @date 2020/3/7
 * @description 重点：
 * 实现 Callable 接口
 * </br>
 * <p>
 * 调用顺序：
 * 1. channelActive，为 ChannelHandlerContext 赋值
 * 2. 设置参数
 * 3. 调用 call() 发送数据并进行 wait(）
 * 4。 read() 接受到数据后，将 call() 唤醒
 * 5. call() 继续执行
 */
public class ClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    private ChannelHandlerContext context;
    private String result;
    private String param;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为了在其他的方法中使用 channelHandlerContext
//        System.out.println("与服务器建立了连接！！！");
        context = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 被代理对象调用，负责发送数据给 Server，然后 wait 等待，等到 read 读到数据后，再被唤醒
     *
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(param);
        wait();
        return result;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify();
    }

    public void setParam(String param) {
        this.param = param;
    }

}
