package com.example.tcp.resolution;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class NettyClient {
    public static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group) //设置线程组
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ClientEncoder())
                                    .addLast(new NettyClientHandler()); //加入自己的处理器
                        }
                    });
            logger.info("============客户端配置参数完成，准备连接服务端============");

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6669).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("客户端错误，错误原因：{0}", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}

