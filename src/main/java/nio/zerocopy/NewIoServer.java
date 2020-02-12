package nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author iceWang
 * @date 2020/1/3
 * @description nio 服务器,统计零拷贝下所需耗时
 */
public class NewIoServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(6666));
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();

            int readCount = 0;

            while (-1 != readCount) {
                try {
                    readCount = socketChannel.read(byteBuffer);
                } catch (Exception e) {
                    break;
                }
                byteBuffer.rewind();
            }
        }

//        while (true) {
//            if (selector.select() > 0) {
//                Set<SelectionKey> selectionKeys = selector.selectedKeys();
//
//                Iterator<SelectionKey> iterator = selectionKeys.iterator();
//
//                while (iterator.hasNext()) {
//                    SelectionKey selectionKey = iterator.next();
//
//                    if (selectionKey.isReadable()) {
//                        SocketChannel channel = (SocketChannel) selectionKey.channel();
//
//
//                        int readCount = 0;
//                        while (readCount != -1) {
//                            readCount = channel.read(byteBuffer);
//                        }
//                        byteBuffer.rewind();
//                        String message = new String(byteBuffer.array());
//                        System.out.println("读取完成！！！");
//                    }
//                    iterator.remove();
//                }
//            }
//        }
    }
}
