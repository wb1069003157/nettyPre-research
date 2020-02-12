package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author iceWang
 * @date 2020/1/11
 * @description nio客户端
 */
public class NioClient {
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();

            socketChannel.configureBlocking(false);

            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);

            if (!socketChannel.connect(inetSocketAddress)) {
                while (!socketChannel.finishConnect()) {
                    System.out.println("connect waiting for time,can do other thing now");
                }
            }

            String temp = "hello iceWang1111111111111111111";
            System.out.println(temp);

            // 可以直接根据字节数据的大小生成ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.wrap(temp.getBytes());

            socketChannel.write(byteBuffer);

//            System.in.read();

            socketChannel.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
