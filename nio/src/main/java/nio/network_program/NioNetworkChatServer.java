package nio.network_program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

/**
 * @author iceWang
 * @date 2020/1/10
 * @description NIO 服务器
 */
public class NioNetworkChatServer {
    private Logger logger = LoggerFactory.getLogger(NioNetworkChatServer.class);

    ServerSocketChannel serverSocketChannel;
    Selector selector;
    InetSocketAddress inetSocketAddress;

    public NioNetworkChatServer() {
        try {
            // 生成一个 ServerSocketChannel 和 Selector，并将 ServerSocketChannel 绑定到指定端口
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            inetSocketAddress = new InetSocketAddress(6666);
            serverSocketChannel.socket().bind(inetSocketAddress);

            serverSocketChannel.configureBlocking(false);

            // 将 serverSocketChannel(一开始的服务器Channel) 注册到指定selector上
            // 后面给每一个连接生成的 SocketChannel，就是通过 ServerSocketChannel 来生成的
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("启动 Server 成功！");
        } catch (IOException e) {
            logger.error("建立Server失败，失败原因：{0}", e);
        }
    }

    public void listen() {
        try {
            while (true) {
                // 没有事件发生，就干其他事
                if (selector.select(3000) == 0) {
                    continue;
                }

                // 得到有事件发生的事件集合，然后在后面可以通过其反向获取channel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 事件：有新的连接
                    if (selectionKey.isAcceptable()) {
                        whenAccept();
                    }
                    // 事件：读从客户端获取的数据
                    if (selectionKey.isReadable()) {
                        readData(selectionKey);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            logger.error("读写错误：{0}", e);
        } finally {

        }
    }

    private void whenAccept() throws IOException {
        // 因为此时已经有连接事件进入了，因此虽然 accept() 是阻塞的，但是在这里会直接返回
        SocketChannel socketChannel = serverSocketChannel.accept();
        logger.info("connect success,socketChannel : " + socketChannel.toString());

        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

        //将某人上线的消息进行显示
        logger.info(socketChannel.getRemoteAddress() + "上线");
    }

    private void readData(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            socketChannel.read(byteBuffer);
            String message = new String(byteBuffer.array());
            logger.info("{}{}{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "\n\t\t", message);

            // 向其他的客户端转发消息
            sendInfoToOtherClients(socketChannel, message);
        } catch (Exception e) {
            // 在捕获到异常后，就是有客户端发送了断开连接的请求
            logger.info("{},离线了。。。", socketChannel.getRemoteAddress());
            sendInfoToOtherClients(socketChannel, socketChannel.getRemoteAddress() + " 离线了。。。");
            // 将关闭连接的 channel 关闭
            socketChannel.close();
            // 将该键移除出 set
            selectionKey.cancel();
        }
    }

    private void sendInfoToOtherClients(SocketChannel socketChannel, String message) throws IOException {
        for (SelectionKey key : selector.keys()) {
            SelectableChannel sourceChannel = key.channel();
            if (sourceChannel instanceof SocketChannel && sourceChannel != socketChannel) {
                SocketChannel targetChannel = (SocketChannel) sourceChannel;
                // 根据转发过来的字节长度，直接生成目标大小的 Buffer，然后将数据写入到客户端的 channel 中
                ByteBuffer targetByteBuffer = ByteBuffer.wrap(message.getBytes());
                targetChannel.write(targetByteBuffer);
            }
        }
    }

    public static void main(String[] args) {
        NioNetworkChatServer nioNetworkChatServer = new NioNetworkChatServer();
        nioNetworkChatServer.listen();
    }
}
