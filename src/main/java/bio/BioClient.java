package bio;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author iceWang
 * @date 2020/1/3
 * @description
 */
public class BioClient {
    public static void main(String[] args) {
        // 发送
        try (Socket socket = new Socket(InetAddress.getByName("localhost"), 6666);
             OutputStream outputStream = socket.getOutputStream()) {
            String data = "0A,3,863921030884418,23.178061,0,113.225998,0,2020/01/18 17:28:30,051,4,FF,";
            data = getData(data) + "\n";
            outputStream.write(data.getBytes());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static String getData(String source) {
        String verify = getVerify(source);
        return ">" + source + verify + "<";
    }

    static String getVerify(String source) {
        char c = source.charAt(0);
        for (int i = 1; i < source.length(); i++) {
            c = (char) (c ^ source.charAt(i));
        }
        String string = Integer.toHexString(c);
        if (string.length() == 1) {
            string = "0" + string;
        }
        return string.toUpperCase();
    }
}
