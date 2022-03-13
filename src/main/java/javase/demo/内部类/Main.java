package javase.demo.内部类;

import java.lang.reflect.Constructor;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/4 9:57
 */
public class Main {


        public void go(){
            int m=100;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(m);
                }
            }).start();
        }

    public static void main(String[] args) throws Exception {
        Singleton singleton=Singleton.getInstance();
        Class objClass = Singleton.class;
        //获取类的构造器
        Constructor constructor = objClass.getDeclaredConstructor();
        //把构造器私有权限放开
        constructor.setAccessible(true);
        //反射创建实例
        Singleton reflectSingleton = (Singleton) constructor.newInstance();
        System.out.println(singleton);
        System.out.println(reflectSingleton);
        System.out.println(singleton==reflectSingleton);
    }

}
class Outer {
    private int age = 20;
    public void method() {
        int age2 = 30;
        class Inner {
            public void show() {
                System.out.println(age);
//从内部类中访问方法内变量age2，需要将变量声明为最终类型。
                System.out.println(age2);
            }
        }

        Inner i = new Inner();
        i.show();
    }
}
//内部类实现单例模式
class Singleton {
    /***
     * 类的内部类，也就是静态成员的内部类，该内部类的实例与外部类的实例没有绑定关系
     * 只有被调用的时候才会装载，从而实现了延时加载
     *
     */
    public static class SingletonHolder {
        /**
         * 静态初始化器，JVM保证线程安全
         */
        private static Singleton instance = new Singleton();
    }
    /**
     * 私有化构造方法，不让外部可以new
     */
    private Singleton(){
    }
    public static Singleton getInstance(){
        return SingletonHolder.instance;
    }
}