package javase.demo.新特性;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/19 19:39
 */
public class Client {
    public static void main(String[] args) {
        //默认方法　和　抽象方法需实现类实例化后调用
        CommonInterface commonInterface = new CommonInterfaceImpl();


        //抽象方法重写后调用
        commonInterface.doSomthing();
        //默认方法重写后调用
        commonInterface.defaultMehtod();
        commonInterface.anotherDefaultMehtod();
    }
}

@FunctionalInterface
interface FuncInterface {
    //只有一个抽象方法
    public void reference();
    //interface default method
    default void defaultMehtod() {
        System.out.println("This is a default method~");
    }
    //interface static method
    static void staticMethod() {
        System.out.println("This is a static method~");
    }
}

