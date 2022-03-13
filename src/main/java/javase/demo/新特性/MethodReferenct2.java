package javase.demo.新特性;

import java.util.function.Consumer;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/19 11:48
 */
public class MethodReferenct2 {
    public void sayHello(Consumer<String> f){
        f.accept("aa");
    }

    public static void main(String argv[]){
        MethodReferenct2 main=new MethodReferenct2();
        main.sayHello(System.out::print);
       // main.sayHello((e)->System.out.print(e));
    }
}
