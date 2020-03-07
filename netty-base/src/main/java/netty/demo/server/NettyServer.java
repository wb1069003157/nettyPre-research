package netty.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.demo.handler.NettyServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class NettyServer {
    public static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) {
        // 1. 创建线程组，在创建时可以指定子线程（NioEventLoop)数量，默认 Cpu * 2
        // bossGroup : 处理 accept
        // workerGroup : 处理 read/write
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务端启动对象，配置参数 配置整个 Netty 程序，串联 各个组件
            // ServerBootstrap 是服务端启动引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup) // 为服务端配置Reactor和工作线程
                    .channel(NioServerSocketChannel.class) //使用 NioServerSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 用来给 ServerChannel 添加配置 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 用来给接收到的通道添加配置 设置保持活动连接状态
//                    .handler(null) // 使用 handler 负责的是 BossGroup 的处理
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道测试对象(匿名对象)
                        //给 pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 给我们的 workerGroup 的 EventLoop 对应的管道设置处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            logger.info("=======服务器准备完毕，开始监听========");

            // 绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
            // 启动服务器(并绑定端口)
            ChannelFuture cf = serverBootstrap.bind(6669).sync();

            //对关闭通道进行监听,涉及到 netty 异步模型
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("服务器启动失败，失败原因：{0}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
