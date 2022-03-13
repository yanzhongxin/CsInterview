package javase.demo.新特性;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/14 19:53
 */
public class Demo {
    private static void test(){

    }
    public static void main(String[] args) {

        testLambda();



    }
    public static void testLambda(){
        int a=10;
        int b=20;
        MyFun sum=(e1,e2)->{
            return (e1+e2);
        };
        test(a,b,sum);
    }
    public static void testInnerClass(){
        int a=10;
        int b=20;
        test(a, b, new MyFun() {
            @Override
            public Integer count(Integer a, Integer b) {

                return  a*b;
            }
        });
    }
    public static void test(int a,int b,MyFun myFun){
        System.out.println(myFun.count(a,b));
    }

}
@FunctionalInterface
interface MyFun {
    Integer count(Integer a, Integer b);
}



