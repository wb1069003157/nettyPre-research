package nio.zerocopy;

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
    public static void main(String[] args) throws IOException {


        // 创建线程池，如果有连接，就创建一个线程
        ExecutorService executorService = Executors.newCachedThreadPool();

        // create ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("server startup!!!");

        while (true) {
            System.out.println("thread info0 id : " + Thread.currentThread().getId() + " \tname: " + Thread.currentThread().getName());

            // waiting for client
            System.out.println("waiting for client");
            // The method blocks until a connection is made.
            final Socket socket = serverSocket.accept();
            System.out.println("client is connected");

            // create a thread for communicated
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handler(socket);
                }
            });
        }
    }

    // communicate method
    // 在接受一个请求起来线程后，即使不处理，该线程也会卡在这里，造成资源浪费
    public static void handler(Socket socket) {

        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             OutputStream outputStream = socket.getOutputStream()) {
            byte[] bytes = new byte[4096];

            while (true) {
                int read = dataInputStream.read(bytes, 0, bytes.length);
                if (read == -1){
                    break;
                }
            }

        } catch (IOException e) {

            System.out.println(e);
        }

    }
}
