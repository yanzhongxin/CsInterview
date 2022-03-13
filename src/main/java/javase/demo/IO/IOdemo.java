package javase.demo.IO;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/6 10:24
 */
public class IOdemo {
    public static void main(String[] args)  throws Exception{
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new
                PipedOutputStream(pipedInputStream);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { //输出流线程向通过管道写数据
                    Thread.sleep(10000);
                    pipedOutputStream.write("hello input".getBytes());
                    System.out.println("输出流");
                    pipedOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("输出 停止");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    System.out.println("输入流开始了");
                    byte []arr = new byte[128]; //输入流线程从pipe管道中读取数据，实现线程通信
                    while (pipedInputStream.read(arr) != -1) {
                        System.out.println(Arrays.toString(arr));
                    }
                    pipedInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
