package bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author iceWang
 * @date 2020/1/3
 * @description
 */
public class BioServer {
    public static void main(String[] args) throws IOException {

        // 创建线程池，如果有连接，就创建一个线程  ps:这里手动指定参数创建线程池会更好，但这里因为不是重点，因此就采用默认的线程池来实现。
        ExecutorService executorService = Executors.newCachedThreadPool();

        // create ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务端建立，监听！！！");

        while (true) {
            System.out.println("thread info0 id : " + Thread.currentThread().getId() + " \tname: " + Thread.currentThread().getName());

            // waiting for client
            System.out.println("等待客户端连接");
            // The method blocks until a connection is made.
            final Socket socket = serverSocket.accept();
            System.out.println("客户端连接成功");

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

        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("thread info1 id : " + Thread.currentThread().getId() + " \tname: " + Thread.currentThread().getName());

            String expression;
            String result;
            while (true) {
                System.out.println("thread info2 id : " + Thread.currentThread().getId() + " \tname: " + Thread.currentThread().getName());
                //BufferedReader.readLine()会读满缓冲区或者在读到文件末尾（遇到："/r"、"/n"、"/r/n"）才返回
                System.out.println("等待读取数据");
                if ((expression = bufferedReader.readLine()) == null) {
                    break;
                }
                System.out.println("result: " + expression);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            System.out.println("关闭和client连接！！！");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
