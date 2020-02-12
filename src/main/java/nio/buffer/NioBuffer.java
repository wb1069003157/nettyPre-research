package nio.buffer;

import java.nio.IntBuffer;

/**
 * @author iceWang
 * @date 2020/1/10
 * @description
 */
public class NioBuffer {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i = 1; i <= intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }
        // 翻转，读写切换
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

        intBuffer.position(0);
        for (int i = 1; i <= intBuffer.capacity(); i++) {
            intBuffer.put(i * i);
        }
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
