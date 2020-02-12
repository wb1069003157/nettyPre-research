package nio;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class NioChatClient {

    private Selector selector;
    private SocketChannel socketChannel;
    private String username;


    public NioChatClient() {
        try {
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);
            socketChannel = SocketChannel.open(inetSocketAddress);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            username = socketChannel.getLocalAddress().toString().substring(1);

        } catch (Exception e) {

        }
    }

    public void sendInfo(String info) {
        info = username + " : " + info;

        try {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\t\t"+ info);
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInfo() {
        try {
            int readChannel = selector.select();
            if (readChannel > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        int read = socketChannel.read(byteBuffer);
                        if (read > 0) {
                            String message = new String(byteBuffer.array());
//                            System.out.println("正常读： form client :" + message);
                            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\t\t"+ message);
                        } else {
                            System.out.println("client closed");

                            // 将关闭连接的 channel 关闭
                            socketChannel.close();

                            // 将该键移除出 set
                            selectionKey.cancel();
                        }
                    }
                }
                iterator.remove();
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final NioChatClient nioChatClient = new NioChatClient();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    nioChatClient.readInfo();
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
            nioChatClient.sendInfo(message);
        }
    }
}
