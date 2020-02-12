package nio;

import java.nio.IntBuffer;

/**
 * @author iceWang
 * @date 2020/1/10
 * @description
 */
public class NioBuffer {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        intBuffer.put(1);
        intBuffer.put(2);
        intBuffer.put(3);
        intBuffer.put(4);
        intBuffer.put(5);

        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

        intBuffer.position(0);



        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * i);
        }

        /*
         *  翻转，读写切换
         *  public final Buffer flip() {
         *         limit = position;
         *         position = 0;
         *         mark = -1;
         *         return this;
         *     }
         */
        intBuffer.flip();

        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

    }
}
