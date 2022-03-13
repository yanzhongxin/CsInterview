# 控制并发线程数的Semaphore
Semaphore（信号量）是用来控制同时访问特定资源的线程数量，它通过协调各个线程，以
保证合理的使用公共资源。主要用于流量控制,比如数据库连接，几十个线程并发去读取
内存数据后保存到数据库，此时数据库只有10个连接。

```java_holder_method_tree
public class SemaphoreTest {
    private static final int THREAD_COUNT = 30;
    private static ExecutorServicethreadPool = Executors
            .newFixedThreadPool(THREAD_COUNT);//30个线程
    private static Semaphore s = new Semaphore(10);//10个信号量
    public static void main(String[] args) {
        for (inti = 0; i< THREAD_COUNT; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        s.acquire();//类似os中的信号量p操作
                       //获取数据库连接，保存数据。
                        System.out.println("save data");
                        s.release();//类似os中的信号量v操作
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        threadPool.shutdown();
    }
}
```
Semaphore还提供一些其他方法，具体如下。
- intavailablePermits()：返回此信号量中当前可用的许可证数。
- intgetQueueLength()：返回正在等待获取许可证的线程数。
- booleanhasQueuedThreads()：是否有线程正在等待获取许可证。
- void reducePermits（int reduction）：减少reduction个许可证，是个protected方法。
- Collection getQueuedThreads()：返回所有等待获取许可证的线程集合，是个protected方
法。

# Semaphore源码解析
![Semaphore](./imgs/第八章/Semaphore类结构)

Semaphore底层实现机制还是AQS，把permits个数设置到AQS的state中，作为许可线程个数。
由于Semaphore是控制并发数量的，因此当并发数量超过permits时，必定需要阻塞，当
需要唤醒一些阻塞的线程时候，必定涉及到公平/非公平策略。因此Semaphore内部有两个
锁一个是公平锁一个非公平锁。
```java_holder_method_tree
abstract static class Sync extends AbstractQueuedSynchronizer
static final class NonfairSync extends Sync 
static final class FairSync extends Sync 
```

## Semaphore构造器
```java_holder_method_tree
public Semaphore(int permits) {
        sync = new NonfairSync(permits);//默认非公平锁
    }
//非公平锁调用父类构造方法，把permits并发许可个数设置到AQS的state变量处
Sync(int permits) {
       setState(permits);//AQS的state
}
```
## acquire “上锁” 源码分析
简单理解：尝试上锁修改state个数，如果资源充足state>0则 自旋+cas 修改state（state-1>=0）修改成功直接返回执行acquire
方法后面的代码。如果资源不足（ state-1<0 ）利用LockSupport.park加入到AQS的阻塞队列。
```java_holder_method_tree
public void acquire() throws InterruptedException {
	// 尝试获取一个锁
    sync.acquireSharedInterruptibly(1);
}

// 这是抽象类 AQS 的方法
public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    // 如果小于0，就获取锁失败了。加入到AQS 等待队列中。
    // 如果大于0，就直接执行下面的逻辑了。不用进行阻塞等待。
    if (tryAcquireShared(arg) < 0)
        doAcquireSharedInterruptibly(arg);
}
// 这是抽象父类 Sync 的方法，默认是非公平的
protected int tryAcquireShared(int acquires) {
    return nonfairTryAcquireShared(acquires);
}
// 非公平锁的获取的方法。就是判断修改state数值
final int nonfairTryAcquireShared(int acquires) {
	// 死循环
    for (;;) {
    	// 获取锁的状态
        int available = getState();
        int remaining = available - acquires;
        // state 变量是否还足够当前获取的
        // 如果小于 0，获取锁就失败了。
        // 如果大于 0，就循环尝试使用 CAS 将 state 变量更新成减去输入参数之后的。
        if (remaining < 0 ||
            compareAndSetState(available, remaining))
            return remaining;
    }
}
private void doAcquireSharedInterruptibly(int arg)
    throws InterruptedException {
    // 添加一个节点 AQS 队列尾部
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
    	// 死循环
        for (;;) {
        	// 找到新节点的上一个节点
            final Node p = node.predecessor();
            // 如果这个节点是 head，就尝试获取锁
            if (p == head) {
            	// 继续尝试获取锁，这个方法是子类实现的
                int r = tryAcquireShared(arg);
                // 如果大于0，说明拿到锁了。
                if (r >= 0) {
                	// 将 node 设置为 head 节点
                	// 如果大于0，就说明还有机会获取锁，那就唤醒后面的线程，称之为传播
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
            }
            // 如果他的上一个节点不是 head，就不能获取锁
            // 对节点进行检查和更新状态，如果线程应该阻塞，返回 true。
            if (shouldParkAfterFailedAcquire(p, node) &&
            	// 阻塞 park，并返回是否中断，中断则抛出异常
                parkAndCheckInterrupt())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
        	// 取消节点
            cancelAcquire(node);
    }
}

```
1. 创建一个分享类型的 node 节点包装当前线程追加到 AQS 队列的尾部。
2. 如果这个节点的上一个节点是 head ，就是尝试获取锁，获取锁的方法就是子类重写的方法。如果获取成功了，就将刚刚的那个节点设置成 head。
3. 如果没抢到锁，就阻塞等待
主要也是两步
1. 修改“锁标志”state，利用自旋for+cas修改state+1
2. 调用LockSupport.unpark方法唤醒阻塞在AQS队列中的节点
## release “解锁”源码分析

```java_holder_method_tree
public void release() {
    sync.releaseShared(1);
}

public final boolean releaseShared(int arg) {
	// 死循环释放成功
    if (tryReleaseShared(arg)) {
    	// 唤醒 AQS 等待对列中的节点，从 head 开始	
        doReleaseShared();
        return true;
    }
    return false;
}
// 所谓的解锁就是：自旋for+cas修改state+1
protected final boolean tryReleaseShared(int releases) {
    for (;;) {
        int current = getState();
        // 对 state 变量 + 1
        int next = current + releases;
        if (next < current) // overflow
            throw new Error("Maximum permit count exceeded");
        if (compareAndSetState(current, next))
            return true;
    }
}
//调用LockSupport.unpark方法唤醒阻塞在AQS队列中的节点
private void doReleaseShared() {
    for (;;) {
        Node h = head;
        if (h != null && h != tail) {
            int ws = h.waitStatus;
            if (ws == Node.SIGNAL) {
            	// 设置 head 的等待状态为 0 ，并唤醒 head 上的线程
                if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                    continue;            // loop to recheck cases
                unparkSuccessor(h);
            }
            // 成功设置成 0 之后，将 head 状态设置成传播状态
            else if (ws == 0 &&
                     !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                continue;                // loop on failed CAS
        }
        if (h == head)                   // loop if head changed
            break;
    }
}

```

## Semaphore总结
Semaphore 就是一个“共享锁”，通过设置 state（就是permit允许的线程个数） 变量来实现对这个变量的共享。
当调用 acquire 方法的时候，state 变量就减去一，当调用 release 方法的时候，state 变量就加一。
当 state 变量为 0 的时候，别的线程就不能进入代码块了，就会在 AQS 中阻塞等待。

# 他人博客
[Java并发系列[6]----Semaphore源码分析](https://www.cnblogs.com/liuyun1995/p/8474026.html "faf")
