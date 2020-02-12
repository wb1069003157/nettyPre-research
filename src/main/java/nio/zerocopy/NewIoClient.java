package nio.zerocopy;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class NewIoClient {

    public static void main(String[] args) {
        try {

            Selector selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);
            SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);


            long start = System.currentTimeMillis();
            File file = new File("1.tgz");
            // 从输入流中获取 channel
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileChannel channel = fileInputStream.getChannel()) {

                // Linux下可以直接传输完成
                // Windows下一次只能传8M，需要进行分段传输
                long byteCount = 0;
                long transferToCount = 0;
                long count = channel.size() / 8388608;
                log.info("需要传输次数 ： {}", count);
                for (long i = 0; i <= count; i++) {
                    transferToCount = channel.transferTo(i * 8388608, 8388608, socketChannel);
                    log.info("本次传输大小： {}，目前总字节大小：{}", transferToCount, byteCount);
                    byteCount = byteCount + transferToCount;
                }

//                transferToCount = channel.transferTo(0, channel.size(), socketChannel);
                log.info("send success ,total byte : {},consumer time : {}", byteCount, System.currentTimeMillis() - start);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
