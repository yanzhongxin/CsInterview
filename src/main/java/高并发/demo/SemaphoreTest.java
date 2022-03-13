package 高并发.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author www.yanzhongxin.com
 * @date 2020/11/19 18:30
 */
public class SemaphoreTest {

    private static Semaphore s = new Semaphore(10);
    public static void main(String[] args) throws Exception {

                        s.acquire();

                        s.release();

    }
}