package 高并发.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author www.yanzhongxin.com
 * @date 2020/11/19 14:29
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws Exception {

        CountDownLatch countDownLatch=new CountDownLatch(2);
        Mythread thread1=new Mythread("thread1",countDownLatch);
        Mythread thread2=new Mythread("thread2",countDownLatch);
        thread1.start();
        thread2.start();

        countDownLatch.await();
        System.out.println("主线程等待结束");

    }
}
class Mythread extends Thread{
    private CountDownLatch countDownLatch;

    public Mythread(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"我正在干活");
        try {
            Thread.sleep(2000);
        }catch (Exception e){
        }
        if (countDownLatch!=null){
            countDownLatch.countDown();
        }
        System.out.println(Thread.currentThread().getName()+"工作结束");
    }
}
