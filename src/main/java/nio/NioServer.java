package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

/**
 * @author iceWang
 * @date 2020/1/10
 * @description 创建NIO服务器
 */
public class NioServer {
    ServerSocketChannel serverSocketChannel;

    Selector selector;

    InetSocketAddress inetSocketAddress;

    public NioServer() {
        try {
            // 生成一个 ServerSocketChannel Selector，并将 ServerSocketChannel 绑定到指定端口
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(6666);
            serverSocketChannel.socket().bind(inetSocketAddress);

            serverSocketChannel.configureBlocking(false);

            // 建立连接，也要通过一个 Channel，因此将 serverSocketChannel 注册到指定selector上
            // 后面给每一个连接生成的 SocketChannel，就是通过 ServerSocketChannel 来生成的
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        try {

            while (true) {
                // 没有事件发生，就干其他事
                if (selector.select(3000) == 0) {
//                    System.out.println("waiting 5s,no connect");
                    continue;
                }

                // 得到有事件发生的事件集合，然后在后面可以通过其反向获取channel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
//                System.out.println("本次获取事件的个数：" + selectionKeys.size());

                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
//                    System.out.println("迭代事件：" + selectionKey.toString());

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
            e.printStackTrace();
        } finally {

        }
    }

    private void whenAccept() throws IOException {
        // 因为此时已经有连接事件进入了，因此虽然 accept() 是阻塞的，但是在这里会直接返回
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("connect success,socketChannel : " + socketChannel.toString());

        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

        //聊天特带
        System.out.println(socketChannel.getRemoteAddress() + "上线");
    }

    private void readData(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        try {

            int read = socketChannel.read(byteBuffer);
            String message = new String(byteBuffer.array());
//            System.out.println("正常读： form client :" + message);
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\t\t" + message);

            // 向其他的客户端转发消息
            sendInfoToOtherClients(socketChannel, message);
        } catch (Exception e) {
            System.out.println(socketChannel.getRemoteAddress() + " 离线了。。。");

            sendInfoToOtherClients(socketChannel, socketChannel.getRemoteAddress() + " 离线了。。。");
            // 将关闭连接的 channel 关闭
            socketChannel.close();

            // 将该键移除出 set
            selectionKey.cancel();
        }


        // 判断客户端是否是退出   切断客户端链接时，会一直出发SocketChannel读就绪事件
//        if (read > 0) {
//
//        } else {
////            System.out.println("client closed");
//
//        }
    }

    private void sendInfoToOtherClients(SocketChannel socketChannel, String message) throws IOException {
//        System.out.println("服务器转发消息。。。");

        for (SelectionKey key : selector.keys()) {
            SelectableChannel sourceChannel = key.channel();
            if (sourceChannel instanceof SocketChannel && sourceChannel != socketChannel) {
                SocketChannel targetChannel = (SocketChannel) sourceChannel;
                ByteBuffer targetByteBuffer = ByteBuffer.wrap(message.getBytes());
                targetChannel.write(targetByteBuffer);
            }
        }
    }

    public static void main(String[] args) {

        NioServer nioServer = new NioServer();

        nioServer.listen();

    }
}
