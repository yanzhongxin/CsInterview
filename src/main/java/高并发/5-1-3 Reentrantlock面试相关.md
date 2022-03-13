阿里：非常重要，区别ReentrantLock和synchronized比较区别，使用场景
[面试官：谈谈synchronized与ReentrantLock的区别？](https://zhuanlan.zhihu.com/p/126085068 "区别")
[美团技术点评AQS讲解](https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html "美团技术原文地址")

[美团技术【基本功】不可不说的Java“锁”事](https://mp.weixin.qq.com/s?__biz=MjM5NjQ5MTI5OA==&mid=2651749434&idx=3&sn=5ffa63ad47fe166f2f1a9f604ed10091&chksm=bd12a5778a652c61509d9e718ab086ff27ad8768586ea9b38c3dcf9e017a8e49bcae3df9bcc8&scene=38#wechat_redirect "算法")

[Java之AQS框架底层原理分析](https://www.nowcoder.com/discuss/755693?type=post&order=recall&pos=&page=0&ncTraceId=&channel=-1&source_id=search_post_nctrack&subType=2&gio_id=3E42838500270B4FB6AE8B4F8263DFA2-1643696351794 "Java之AQS框架底层原理分析")
# 非公平锁

## 非公平锁上锁过程。tryAcquire(aqs抽象方法，非公平锁自己实现规则)
非公平锁执行lock加锁方法时候，上来不管锁的状态state,上来就是cas修改锁状态，修改成功的话，成功获取到锁。
如果修改失败（别的线程cas成功）的话，尝试tryAcquire加锁才查看当前锁的状态state,如果锁是空闲state=0，这时候又直接cas加锁。
加锁成功直接返回，加锁失败给我排队去。如果锁的状态state不等于0的话，说明已经有人持有锁，接着判断如果持有这把锁
的线程是否是当前线程，如果是，说明是重入锁，修改state数值，如果不是说明别的线程已经占有锁，自己
乖乖的去排队。（排队入队规则由AQS定义，公平非公平锁都是同样规则）

## 入队规则（AQS提供的公共方法addWaiter(Node mode)）
新建一个节点Node，如果队列已经被初始化，通过cas入队一次，如果入队成功直接返回Node,
如果入队失败（说明入队过程中有人也在cas，并且成功入队），则通过for(;;)死循环cas保证入队。如果队列没有初始化
则首先初始化队列首节点（空节点）接着通过for(;;)死循环cas保证入队。返回最终入队节点。

## 新入队的节点看是否要要阻塞 acquireQueued(刚入队的节点)

总体设计思路（只有队列中第一个等待的线程有资格多次cas自旋，多次cas失败第一个节点也要park，第一个等待节点之后的线程直接park睡）
上一步刚入对的节点Node，第一步判断是不是第一个等待的节点，如果是尝试tryAcquire加锁，加锁成功直接返回。
如果加锁失败，说明头节点隐含的线程正在忙着使用锁，因此需要判断是不是要阻塞Node对应的线程(防止无限cas)，头节点的waitStatus
状态时0，并且头节点隐含指向的线程正在忙着使用锁，因此Node节点把上一个节点waitStatus设置为-1，表示正忙，接着当前节点Node就是第一个
有资格获取锁的线程节点，因此在尝试一次上述过程，如果第二次tryAcquire cas加锁失败，此时头节点的waitStatus=-1，因此当前节点需要park阻塞（因此
当前节点尝试了两次tryAcquire都失败了，没必要让他无限cas自旋加锁，浪费cpu执行时间）
回到第一步情况，如果刚入对的节点Node不是第一个等待的节点，并且当前节点的前一个结点的waitStatus状态时0，其实上一个节点已经阻塞了，只是没办法
自己设置自己的状态时-1，因此后一个节点设置前一个结点的状态是-1，表示上一个节点正在睡park。


## 锁的释放（不区分公平非公平）
首先减少state数值重入锁，state=state-1，如果减少重入次数后state仍然大于0，说明还是有线程
持有这把锁。如果减少重入次数后state=0，说明锁此时是空闲状态，如果队列中头节点的waitStatus=-1，说明队列中
第一个等待的节点把头节点状态置为-1，说明队列中第一个节点阻塞了，因此需要唤醒队列中第一个等待的节点，
LockSupport.unpark(s.thread);


# 公平锁

## 加锁过程
公平锁执行lock加锁方法时候，尝试tryAcquire加锁才查看当前锁的状态state,如果锁是空闲state=0，由于要保证公平，需要看一下
自己是否需要排队hasQueuedPredecessors，需要排队的话，乖乖的入队，否则尝试一次cas加锁，加锁成功直接返回，
加锁失败给我排队去。如果锁的状态state不等于0的话，说明已经有人持有锁，接着判断如果持有这把锁
的线程是否是当前线程，如果是，说明是重入锁，修改state数值，如果不是说明别的线程已经占有锁，自己
乖乖的去排队。（排队入队规则由AQS定义，公平非公平锁都是同样规则）


## 锁空闲state=0是，判断是不是要排队
公平锁当锁的状态等于0空闲时候，判断自己是不是要排队（hasQueuedPredecessors）
return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());

排队情况：
1. 队列正在被其他线程初始化过程中ing,初始化头节点
2. 队列中有排队线程，并且第一个等待线程不是自己

不要排队情况：
1. 队列没初始化
2. 队列中只有一个头节点（空节点）  
3. 队列有人排队，并且第一个排队节点是字节（重入）







