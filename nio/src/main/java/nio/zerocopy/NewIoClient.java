package nio.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author iceWang
 * @date 2020/1/13
 * @description nio 客户端 ,统计零拷贝技术下，所需耗时
 */
public class NewIoClient {
    private static final Logger logger = LoggerFactory.getLogger(NewIoClient.class);

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);
            SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            long start = System.currentTimeMillis();
            File file = new File("1.jar");
            // 从输入流中获取 channel
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileChannel channel = fileInputStream.getChannel()) {

                // Windows下一次只能传8M，需要进行分段传输
                long transferToCount = 0;
                long byteCount = 0;
                transferToCount = 409600;
                while (true) {
                    transferToCount = channel.transferTo(byteCount, transferToCount, socketChannel);
                    byteCount = byteCount + transferToCount;
                    logger.info("本次传输大小： {}，目前总字节大小：{}", transferToCount, byteCount);
                    if (transferToCount == 0) {
                        break;
                    }
                }

                // Linux下可以直接传输完成
//                long transferToCount = 0;
//                long byteCount = 0;
//                transferToCount = channel.transferTo(0, channel.size(), socketChannel);
//                byteCount = byteCount + transferToCount;
//                logger.info("本次传输大小： {}，目前总字节大小：{}", transferToCount, byteCount);


                logger.info("send success ,total byte : {},consumer time : {}", byteCount, System.currentTimeMillis() - start);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
