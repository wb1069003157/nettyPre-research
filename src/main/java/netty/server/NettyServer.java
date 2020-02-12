package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.handler.TaskQueuqNettyServerHandler;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class NettyServer {

    public static void main(String[] args) {

        // 1. 创建线程组，在创建时可以指定子线程（NioEventLoop)数量，默认 Cpu * 2
        // bossGroup : 处理 accept
        // workerGroup : 处理 read/write
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {


            // 创建服务端启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) //使用 NioSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道测试对象(匿名对象)
                        //给 pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 给我们的 workerGroup 的 EventLoop 对应的管道设置处理器
                            ch.pipeline().addLast(new TaskQueuqNettyServerHandler());
                        }
                    });
            System.out.println(".....服务器 is ready...");

            // 绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
            // 启动服务器(并绑定端口)
            ChannelFuture cf = serverBootstrap.bind(6668).sync();

            //对关闭通道进行监听,涉及到 netty 异步模型
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {


        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
