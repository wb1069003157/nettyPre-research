package nio.network_program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author iceWang
 * @date 2020/1/11
 * @description nio客户端
 */
public class NioNetworkChatClient {
    private Logger logger = LoggerFactory.getLogger(NioNetworkChatClient.class);

    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public NioNetworkChatClient() {
        try {
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);
            socketChannel = SocketChannel.open(inetSocketAddress);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            username = socketChannel.getLocalAddress().toString().substring(1);
        } catch (Exception e) {
            logger.error("构建客户端错误，错误原因：{0}", e);
        }
    }

    public void sendInfo(String info) {
        info = username + " : " + info;
        try {
            logger.info("{},{},{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "\n\t\t", info);
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            logger.error("发送数据错误，错误原因：{0}", e);
        }
    }

    /**
     * 1. 获取selector上发生的事件
     * 2. 如果是读事件，则将数据通过 Channel 和 Buffer 进行操作
     * 3. 处理完成后，将该key从待处理keys中删除
     */
    public void readInfo() {
        try {
            int readChannel = selector.select();
            if (readChannel > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        SocketChannel handingSocketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        int read = handingSocketChannel.read(byteBuffer);
                        if (read > 0) {
                            String message = new String(byteBuffer.array());
                            logger.info("{},{},{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "\n\t\t", message);
                        } else {
                            logger.info("client closed");
                            // 将关闭连接的 channel 关闭
                            handingSocketChannel.close();
                            // 将该键移除出 set
                            selectionKey.cancel();
                        }
                    }
                }
                iterator.remove();
            } else {
                logger.info("当前没有 channel 可供使用！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 启动一个线程来定时读取 Server 可能发送的数据，如果没有，就休眠，等待下次读取
     * 2. 启动一个获取控制台输出来进行数据的发送
     *
     * @param args
     */
    public static void main(String[] args) {
        NioNetworkChatClient nioNetworkChatClient = new NioNetworkChatClient();

        // 线程资源必须通过线程池提供，不允许在应用中自行显式创建线程,但跟前面一样，这里因为不是重点，就先这样用着
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    nioNetworkChatClient.readInfo();
                    try {
                        sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            nioNetworkChatClient.sendInfo(message);
        }
    }
}
