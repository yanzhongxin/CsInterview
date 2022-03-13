package 高并发.demo;



import java.util.concurrent.*;

/**
 * @author www.yanzhongxin.com
 * @date 2020/11/20 19:32
 */
public class FeatureTaskDemo {
    public static void main(String[] args) throws Exception{
        Executors.newSingleThreadExecutor();
    }
    public static void testFutureTask() throws Exception{
        FutureTask<String> futureTask=new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {

                return "this is callable";
            }

    });
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<?> submit = executorService.submit(futureTask);
        System.out.println("result="+((FutureTask)submit).get());
        executorService.shutdown();


    }
    public static void testFutureTaskStatus(){
        FutureTask<String> futureTask=new FutureTask<String>(new Runnable() {
            @Override
            public void run() {
                System.out.println("future task"+Thread.currentThread().getName());

            }
        },"abc");
        futureTask.run();
        System.out.println("future task"+Thread.currentThread().getName());


    }
}
