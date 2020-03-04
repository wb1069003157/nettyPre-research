package nio.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author iceWang
 * @date 2020/1/3
 * @description bio 服务器，传统io耗时
 */
public class OldIoServer {
    public static Logger logger = LoggerFactory.getLogger(OldIoServer.class);

    public static void main(String[] args) throws IOException {
        // 创建线程池，如果有连接，就创建一个线程
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 创建 ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        logger.info("BIO 服务器启动！！！");

        while (true) {
            logger.info("thread info0 id : {} \tname:{}", Thread.currentThread().getId(), Thread.currentThread().getName());
            logger.info("等待客户端连接中。。。");
            // 方法会阻塞在这里，直到有客户端连接
            final Socket socket = serverSocket.accept();
            logger.info("有客户端连接成功");

            // 创建一个线程来进行请求处理
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handler(socket);
                }
            });
        }
    }

    /**
     * 在接受一个请求起来线程后，即使不处理，该线程也会卡在这里，造成资源浪费
     *
     * @param socket
     */
    public static void handler(Socket socket) {
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             OutputStream outputStream = socket.getOutputStream()) {
            byte[] bytes = new byte[4096];
            while (true) {
                int read = dataInputStream.read(bytes, 0, bytes.length);
                if (read == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.info("处理读写错误，错误原因:{0}", e);
        }
    }
}
