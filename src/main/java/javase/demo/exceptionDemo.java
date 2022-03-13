package javase.demo;

/**
 * @author www.yanzhongxin.com
 * @date 2021/2/28 12:41
 */
public class exceptionDemo {
    static {
        System.out.println("static");
    }
    public static void main(String[] args) {
        System.out.println(bar());
    }
    public static int foo()
    {
        try {
        int a = 5 / 0;
    } catch (Exception e){
        return 8;
    } finally{
        return 9;
    }
    }
    @SuppressWarnings("finally")
    public static int bar()
    {
        try {
            int a=5/0;
        }finally {
            return 100;
        }

    }
}
