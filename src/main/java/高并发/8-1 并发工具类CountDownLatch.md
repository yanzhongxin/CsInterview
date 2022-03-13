# CountDownLatch基本原理
1. CountDownLatch作用：CountDownLatch允许一个或多个线程等待其他线程完成操作。
2. 使用场景：thread1，thread2线程执行某些操作，thread3在某个点必须等待thread1，thread2完成才能执行。
```java_holder_method_tree
class CountDownLatchTest {
    staticCountDownLatch c = new CountDownLatch(2);//构造函数内int等待计数器
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {//thread1
            @Override
            public void run() {
                System.out.println(1);
                c.countDown();
                System.out.println(2);
                c.countDown();
            }
        }).start();
        c.await();//threadMain
        System.out.println(3);
    
```
thread1调用CountDownLatch的countDown方法时，N就会减1，threadMain执行CountDownLatch
的await方法会阻塞当前threadMain线程，直到N变成零后，threadMain从await处返回。
用在多个线程时，只需要把这个CountDownLatch的引用传递到线程里即可。除了一直阻塞等待之外
还有超时等待方法——await（long time，TimeUnit unit），如果指定时间内N没有减到0，时间一到
不会阻塞当前线程。
```java_holder_method_tree
package 高并发.demo;
import java.util.concurrent.CountDownLatch;
/**
 * @author www.yanzhongxin.com
 * @date 2020/11/19 14:29
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch=new CountDownLatch(2);//等待线程个数
        Mythread thread1=new Mythread("thread1",countDownLatch);
        Mythread thread2=new Mythread("thread2",countDownLatch);
        thread1.start();
        thread2.start();
        countDownLatch.await();//countDownLatch.await(1, TimeUnit.SECONDS);当前线程等待1秒，如果计数器没有减少到0，当前线程直接返回，不阻塞
        System.out.println("主线程等待结束");
        //thread1我正在干活
        // thread2我正在干活
        //主线程等待结束

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
            Thread.sleep(1000);
        }catch (Exception e){
        }
        if (countDownLatch!=null){
            countDownLatch.countDown();
        }
    }
}

```

# CountDownLatch源码
CountDownLatch的核心是通过Sync继承AbstractQueuedSynchronizer实现的,利用aqs
的state=count变量作为计数器的数值。

```java_holder_method_tree
private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }
        int getCount() {
            return getState();
        }
		 //重写父类的方法，计数器不等于0返回-1，等于0返回1
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }
		 //重写父类的方法，cas更新计数器count-1数值。
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            //使用CAS机制来减少count数，直到count数为0。
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }
```
## CountDownLatch构造函数
初始化一个aqs把count计数器数值赋值给aqs的state变量
```java_holder_method_tree
public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }
```

## countDown计数器减一方法
主要做两件事。
- 第一、cas更新 AQS的state计数器数值-1。
- 第二、如果计数器count数值减为零，那么LockSupport.unpark唤醒阻塞的线程
```java_holder_method_tree
public void countDown() {
        sync.releaseShared(1);
    }
public final boolean releaseShared(int arg) {
		  //设置count减一，如果count被减为0，则释放队列的所有线程
        if (tryReleaseShared(arg)) {//自旋+cas更新计数器
            doReleaseShared();
            return true;
        }
        return false;
    }
private void doReleaseShared() {
        for (;;) {
            Node h = head;
            //如果头节点不为null，并且头节点不等于tail节点。
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                //如果头节点对应的线程是SIGNAL状态，则意味着“头节点的下一个节点所对应的线程”需要被unpark唤醒。
                if (ws == Node.SIGNAL) {
                	    //一直循环，直到成功设置头结点的状态为空为止
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;            // loop to recheck cases
                    //释放所有后序线程
                    unparkSuccessor(h);
                }
                else if (ws == 0 &&
                         !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue;                // loop on failed CAS
            }
            if (h == head)                   // loop if head changed
                break;
        }
    }
```

## await等待count变成0的方法
判断AQS的state计数器是否等于0，如果不等于0的话，加入到AQS队列中，并且LockSupport.park阻塞。如果等于0则方法返回，
接着执行await方法后面的代码。
```java_holder_method_tree
public void await() throws InterruptedException {
		 //直接调用父类AQS的方法
        sync.acquireSharedInterruptibly(1);
    }
public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        //判断一下线程是否被中断了
        if (Thread.interrupted())
            throw new InterruptedException();
        //count状态不等于0，就调用下面的方法
        if (tryAcquireShared(arg) < 0)//计数器不为零返回-1，阻塞等待，等于0返回1继续执行
            doAcquireSharedInterruptibly(arg);
    }
private void doAcquireSharedInterruptibly(int arg)//计数器不为零返回-1，阻塞等待
        throws InterruptedException {
        //将当前线程添加进入等待队列，从具体实现就可以看出是双向链表实现的队列。
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                //获得node的前一个节点
                final Node p = node.predecessor();
                //如果前一个节点为头结点
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    //如果前一个节点为头结点，且状态为0，则设置当前线程节点为头结点并且繁殖
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        //帮助释放前一个节点
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                //在这里对当前线程节点进行检测中断，然后让当前线程进行阻塞等待
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```