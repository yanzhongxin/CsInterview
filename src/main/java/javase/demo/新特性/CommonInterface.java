package javase.demo.新特性;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/19 19:38
 */
//一个有多个默认方法和静态方法的接口CommonInterface
public interface CommonInterface {
    //抽象方法
    public void doSomthing();
    //默认方法
    default void defaultMehtod() {
        System.out.println("CommonInterface's default method　is running...");
    }
    //默认方法可以有多个
    default void anotherDefaultMehtod() {
        System.out.println("CommonInterface's second default method is running...");
    }
    //静态方法
    static void staticMethod() {
        System.out.println("CommonInterface's static method is running...");
    }
    //静态方法也可以有多个
    static void anotherStaticMethod() {
        System.out.println("CommonInterface's second static method is running...");
    }
}


