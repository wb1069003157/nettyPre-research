package nio.zerocopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.Socket;

/**
 * @author iceWang
 * @date 2020/1/13
 * @description bio 客户端
 */
public class OldIoClient {
    private static final Logger logger = LoggerFactory.getLogger(OldIoClient.class);

    public static void main(String[] args) {
        File file = new File("1.jar");
        try (Socket socket = new Socket(InetAddress.getByName("localhost"), 6666);
             InputStream inputStream = new FileInputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            byte[] bytes = new byte[4096];
            long readCount;
            long total = 0;

            long start = System.currentTimeMillis();

            while ((readCount = inputStream.read(bytes)) >= 0) {
                total += readCount;
                dataOutputStream.write(bytes);
            }
            logger.info("send success ,total byte : {},consumer time : {}", total, System.currentTimeMillis() - start);
        } catch (IOException e) {

        }
    }
}
