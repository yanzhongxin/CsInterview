package javase.demo.新特性;

import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/19 11:04
 */
public class MethodReferenceDemo {
    public static void main(String[] args) {
        test01();
    }
    public static void test01(){
        PrintStream ps = System.out;
        //Consumer<String> con1 = (s) -> ps.println(s);
        //con1.accept("aaa");

        Consumer<String> con2 = ps::println;
        con2.accept("bbb");
    }

    public static void test02(){
        PrintStream ps = System.out;
        Consumer<String> con1 = (s) -> ps.println(s);
        con1.accept("aaa");


    }
    public static void test03(){
        PrintStream ps = System.out;
        ps.getClass();
        return;

    }
}
