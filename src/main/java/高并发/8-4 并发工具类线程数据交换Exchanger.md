# Exchanger概述
Exchanger用于进行线程间的数据交
换。它提供一个同步点，在这个同步点，两个线程可以通过exchanger方法交换数据，如果有
某个线程先执行exchanger后它会一直等待第二个线程执行exchange，之后再及逆行数据交换。
Exchanger也可以用于校对工作，比如我们需
要将纸制银行流水通过人工的方式录入成电子银行流水，为了避免错误，采用AB岗两人进行
录入，录入到Excel之后，系统需要加载这两个Excel
```java_holder_method_tree
 public static void main(String[] args) {
         final Exchanger<String> exgr = new Exchanger<String>();
        ExecutorService threadPool= Executors.newFixedThreadPool(2);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String A = "银行流水A";// A录入银行流水数据
                    String b=exgr.exchange(A);
                    System.out.println("线程A交换前 "+A+"  交换后"+b);
                } catch (InterruptedException e) {
                }
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String B = "银行流水B";// B录入银行流水数据
                    String A = exgr.exchange(B);
                    System.out.println("线程B交换前 "+B+"  交换后"+A);
                } catch (InterruptedException e) {
                }
            }
        });
        threadPool.shutdown();
    }
//线程B交换前 银行流水B  交换后银行流水A
//线程A交换前 银行流水A  交换后银行流水B
```
# Exchanger源码解析
https://www.jianshu.com/p/2840c5c4f368
https://www.zzwzdx.cn/juc-exchanger/
https://www.mmbyte.com/article/51447.html