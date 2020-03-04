package netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description 自定义一个 Handler，继承 Handler 规范，来实现
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    public static final Logger logger = LoggerFactory.getLogger(HttpServerInitializer.class);

    /**
     * 前面使用的都是匿名构造的方法，这里手动建一个初始化类，然后引入本身自带的 Handler
     * <p>
     * HttpServerCodec ： Netty 提供的编解码器
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.info("Handler 初始化");
        ch.pipeline().addLast(new HttpServerCodec())
                .addLast(new StringDecoder(StandardCharsets.UTF_8))
                .addLast(new StringEncoder(StandardCharsets.UTF_8))
                .addLast(new HttpServerHandler());
    }
}
