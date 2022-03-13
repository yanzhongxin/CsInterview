package javase.demo.新特性;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/16 10:50
 */
public class J8sample {
    public static void main(String[] args) {

        Runnable runnable=()-> System.out.println("xixi");
        new Thread(runnable).start();

        new Thread(()->{
            System.out.println("hh");
        }).start();
    }
}
