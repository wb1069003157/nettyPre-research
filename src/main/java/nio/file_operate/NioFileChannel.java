package nio.file_operate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author iceWang
 * @date 2020/1/10
 * @description 使用 ByteBuffer 和 FileChannel，将数据写到文件中，如果不存在就创建
 * 使用 ByteBuffer 和 FileChannel，将数据从文件（假定存在）读到内存中
 * 文件拷贝
 */
public class NioFileChannel {

    private static Logger logger = LoggerFactory.getLogger(NioFileChannel.class);

    public static final int BYTE_BUFFER_LENGTH = 1;

    public static void main(String[] args) {
        // 写入文件
        String data = "hello iceWang";
        dataToFile(data, "file01.txt");

        // 文件读取
        dataFromFile("file01.txt");

        // 文件拷贝
        copyFileUseBuffer("file01.txt", "file02.txt");
        copyFileUseChannelTransfer("file01.txt", "file03.txt");
    }

    // 将两个channel通过byteBuffer进行转移
    private static void copyFileUseBuffer(String sourceFilePath, String targetFilePath) {
        File source = new File(sourceFilePath);
        File target = new File(targetFilePath);
        // 获取文件输入输出流
        // 从输入输出流中获取输入输出 channel

        try (FileInputStream fileInputStream = new FileInputStream(source);
             FileOutputStream fileOutputStream = new FileOutputStream(target);
             FileChannel fileInputStreamChannel = fileInputStream.getChannel();
             FileChannel fileOutputStreamChannel = fileOutputStream.getChannel()) {

            // 分配缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_LENGTH);
            // 将输入流中的数据写到缓冲区
            // 这里需要循环读取，如果是大文件，不能直接建立一个很大的内存空间，直接全部放进去，并且还可能放不进去
            while (true) {
                byteBuffer.clear();

                int read = fileInputStreamChannel.read(byteBuffer);
                if (read == -1) {
                    break;
                }
                // 翻转缓冲区
                byteBuffer.flip();

                // 将翻转后可以对外写的缓存区的内容写到输出流，从而形成文件
                fileOutputStreamChannel.write(byteBuffer);
            }
        } catch (Exception e) {
            logger.error("文件复制错误，错误原因 ：{0}", e);
        }
    }

    // 直接用channel的复制完成文件复制
    private static void copyFileUseChannelTransfer(String sourceFilePath, String targetFilePath) {
        File source = new File(sourceFilePath);
        File target = new File(targetFilePath);
        // 获取文件输入输出流
        // 从输入输出流中获取输入输出 channel
        try (FileInputStream fileInputStream = new FileInputStream(source);
             FileOutputStream fileOutputStream = new FileOutputStream(target);
             FileChannel fileInputStreamChannel = fileInputStream.getChannel();
             FileChannel fileOutputStreamChannel = fileOutputStream.getChannel()) {

            // 直接将输入channel复制到输出channel
            fileOutputStreamChannel.transferFrom(fileInputStreamChannel, fileInputStreamChannel.position(), fileInputStreamChannel.size());

        } catch (Exception e) {
            logger.error("文件复制错误，错误原因 ：{0}", e);
        }
    }

    // 文件 -> 内存
    private static void dataFromFile(String filePath) {
        File file = new File(filePath);
        // 从输入流中获取 channel
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileChannel channel = fileInputStream.getChannel()) {

            // 分配缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_LENGTH);
            StringBuilder result = new StringBuilder();
            while (true) {
                byteBuffer.clear();
                // 将 channel数据写到buffer中
                int read = channel.read(byteBuffer);
                // 因为byteBuffer大小原因，因此需要用一个中间字符串接受一下
                result.append(new String(byteBuffer.array()));
                if (read == -1) {
                    break;
                }
            }

            logger.info("从文本读取结果：{}", result);
        } catch (Exception e) {
            logger.error("文件读取错误，错误原因 ：{0}", e);
        }
    }

    // 数据 -> 文件
    private static void dataToFile(String data, String filePath) {
        // 构建输出流  ->   从输出流中获取 channel
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             FileChannel fileChannel = fileOutputStream.getChannel()) {

            // 设置缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_LENGTH);

            // 将需要读写的数据放到缓冲区
            int i = 0;
            int length = data.getBytes().length;
            // 一次就可以读完
            if (BYTE_BUFFER_LENGTH > data.getBytes().length) {
                byteBuffer.put(data.getBytes(), i, data.getBytes().length);
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
            } else {
                // 一次读不完  需要循环读取
                for (int temp = 0; temp < data.getBytes().length; temp += BYTE_BUFFER_LENGTH) {
                    byteBuffer.clear();
                    byteBuffer.put(data.getBytes(), temp, BYTE_BUFFER_LENGTH);
                    // 翻转缓冲区，可以对外读
                    // 这里的 flip() 是重点，其可以将Buffer的属性重置，可以对外写
                    byteBuffer.flip();
                    // 将缓冲区内的数据写到 channel中
                    fileChannel.write(byteBuffer);
                }
            }
        } catch (Exception e) {
            logger.error("文件写入错误，错误原因 ：{0}", e);
        }
    }
}
