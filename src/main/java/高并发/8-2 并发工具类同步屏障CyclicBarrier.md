CyclicBarrier的字面意思是可循环使用的屏障（因为该屏障点在释放等待线程后可以重用，所以称它为循环的屏障点）。当一个线程执行CyclicBarrier.await方法时，就会到达一个屏障并且被阻塞，直到最后一个线程到达屏障（执行CyclicBarrier.await）时，屏障才会
开门，所有被屏障拦截的线程才会继续运行。构造器中可以设置一个CyclicBarrier包含的屏障个数new CyclicBarrier(2);

**一句话：线程遇到屏障阻塞，如果是遇到最后一个屏障，则所有的屏障打开。**
## 使用例子
```java_holder_method_tree
public class CyclicBarrierTest {
    //一个CyclicBarrier中设置两个屏障。
    static CyclicBarrier c = new CyclicBarrier(2);
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();//达到内存屏障，消耗一个内存屏障。如果是最后一个则所有屏障解除
                } catch (Exception e) {
                }
                System.out.println(1);
            }
        }).start();
        try {
            c.await();//达到内存屏障，消耗一个内存屏障。如果是最后一个则所有屏障解除
        } catch (Exception e) {
        }
        System.out.println(2);
    }
}
结果是1，2或者2，1。因为内存屏障解除后，两个线程的调度是由cpu决定的。
```
如果设置了三个内存屏障，只是用了两个内存屏障则这两个使用内存屏障的线程都会阻塞。
CyclicBarrier还提供一个更高级的构造函数CyclicBarrier（int parties，Runnable barrier-
Action），保证在线程到达屏障时候，先执行行barrierAction，在一组线程中的最后一个线程到达屏障点之后（但在释放所有线程之前），该命令只在所有线程到达屏障点之后运行一次，
并且该命令由最后一个进入屏障点的线程执行。使用场景：线程1,线程2干完活之后，都遇到屏障barrier，
后阻塞，先执行runnable中的代码使用线程1线程2的数据后，屏障打开，线程1，线程2接着执行。
```java_holder_method_tree
package 高并发.demo;

import java.util.concurrent.CyclicBarrier;

/**
 * @author www.yanzhongxin.com
 * @date 2020/11/19 16:32
 */
public class CyclicBarrierTest {
    static CyclicBarrier c = new CyclicBarrier(2, new A());
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();
                } catch (Exception e) {
                }
                System.out.println(1);
            }
        }).start();
        try {
            c.await();
        } catch (Exception e) {
        }
        System.out.println(2);
    }
    static class A implements Runnable {
        @Override
        public void run() {
            System.out.println(3);
        }
    }
}
输出：3，1，2或者3，2，1
```

## CyclicBarrier源码解析
基于ReentrantLock独占锁和Condition条件队列控制实现的。其实就是判断当前线程是不是执行最后一个屏障，如果不是则直接挂到condition的队列中，
如果是遇到了最后一个内存屏障则，如果有runnable接口则先执行接口中方法，后唤醒所有等待
在condition上的线程。
### 数据结构
CyclicBarrier中声明了如下一些属性及变量：
```java_holder_method_tree
private final ReentrantLock lock = new ReentrantLock();
private final Condition trip = lock.newCondition();
private final int parties;
private final Runnable barrierCommand;
private Generation generation = new Generation();
private int count;
```

1. lock用于保护屏障入口的锁；
2. trip遇到屏障都在这个condition上等待；
3. parties参与等待的线程数；
4. barrierCommand当所有线程到达屏障点之后，首先执行的命令即runnable接口；
5. count实际中仍在等待的线程数，每当有一个线程到达屏障点，count值就会减一；当一次新的运算开始后，count的值被重置为parties。

### 构造方法
```java_holder_method_tree
    //创建一个CyclicBarrier实例，parties指定参与相互等待的线程数，
    //barrierAction指定当所有线程到达屏障点之后，首先执行的操作，该操作由最后一个进入屏障点的线程执行。
    public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }
    //创建一个CyclicBarrier实例，parties指定参与相互等待的线程数
    public CyclicBarrier(int parties) {
        this(parties, null);
    }
```
### await方法
这个等待的await方法，其实是使用ReentrantLock和Condition控制实现的。
其实就是判断当前线程是不是执行最后一个屏障，如果不是则直接挂到condition的队列中，
如果是遇到了最后一个内存屏障则，如果有runnable接口则先执行接口中方法，后唤醒所有等待
在condition上的线程。
```java_holder_method_tree
/该方法被调用时表示当前线程已经到达屏障点，当前线程阻塞进入休眠状态
    //直到所有线程都到达屏障点，当前线程才会被唤醒
    public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return dowait(false, 0L);
        } catch (TimeoutException toe) {
            throw new Error(toe); // cannot happen;
        }
    }

    //该方法被调用时表示当前线程已经到达屏障点，当前线程阻塞进入休眠状态
    //在timeout指定的超时时间内，等待其他参与线程到达屏障点
    //如果超出指定的等待时间，则抛出TimeoutException异常，如果该时间小于等于零，则此方法根本不会等待
    public int await(long timeout, TimeUnit unit)
        throws InterruptedException,
               BrokenBarrierException,
               TimeoutException {
        return dowait(true, unit.toNanos(timeout));
    }

    private int dowait(boolean timed, long nanos)
        throws InterruptedException, BrokenBarrierException,
               TimeoutException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final Generation g = generation;
            if (g.broken)
                throw new BrokenBarrierException();
            if (Thread.interrupted()) {
                breakBarrier();
                throw new InterruptedException();
            }

           int index = --count;//实际还需要等待的线程数量，每到达一个屏障点，count减一
           if (index == 0) {  // 说明当前线程是执行到了最后一个屏障点
               boolean ranAction = false;
               try {
                   final Runnable command = barrierCommand;
                   if (command != null)//判断是否有需要执行的runable接口
                       command.run();
                   ranAction = true;
                   //当所有参与的线程都到达屏障点，立即去唤醒所有处于休眠状态的线程，恢复执行
                   nextGeneration();
                   return 0;
               } finally {
                   if (!ranAction)
                       breakBarrier();//调用condition的trip.signalAll()，唤醒所有等待在condition上的线程。
               }
           }

            // loop until tripped, broken, interrupted, or timed out
            for (;;) {
                try {
                    if (!timed)
                        //如果不是最后一个屏障，则让当前执行的线程阻塞，处于休眠状态
                        trip.await();
                    else if (nanos > 0L)
                        //让当前执行的线程阻塞，在超时时间内处于休眠状态
                        nanos = trip.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw ie;
                    } else {
                        // We're about to finish waiting even if we had not
                        // been interrupted, so this interrupt is deemed to
                        // "belong" to subsequent execution.
                        Thread.currentThread().interrupt();
                    }
                }

                if (g.broken)
                    throw new BrokenBarrierException();

                if (g != generation)
                    return index;

                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    //唤醒所有处于休眠状态的线程，恢复执行
    //重置count值为parties
    //重置中断状态为false
    private void nextGeneration() {
        // signal completion of last generation
        trip.signalAll();
        // set up next generation
        count = parties;
        generation = new Generation();
    }

    //唤醒所有处于休眠状态的线程，恢复执行
    //重置count值为parties
    //重置中断状态为true
    private void breakBarrier() {
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }
```

### reset方法
```java_holder_method_tree
//将屏障重置为其初始状态。
    public void reset() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            //唤醒所有等待的线程继续执行，并设置屏障中断状态为true
            breakBarrier();   // break the current generation
            //唤醒所有等待的线程继续执行，并设置屏障中断状态为false
            nextGeneration(); // start a new generation
        } finally {
            lock.unlock();
        }
    }
```

## 他人cyclicbarrier源码博客
[cyclicbarrier源码解析](https://segmentfault.com/a/1190000016518256 "afaf")