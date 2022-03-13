package javase.demo.泛型;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/1 13:03
 */
class Point< T>{  // 此处可以随便写标识符号，T是type的简称
    public T var ; // var的类型由T指定，即：由外部指定

    public Point(T var) {
        this.var = var;
    }
    public T getVar(){ // 返回值的类型由外部决定
        return var ;
    }
    public void setVar(T var){ // 设置的类型也由外部决定
        this.var = var ;
    }
};
public class GenericsDemo06{
    public static void main(String args[]) throws Exception{
        showColor(Color.Red);

    }
    public static void showColor(Color color) {
        switch (color) {
            case Red:
                System.out.println(color);
                break;
            case Blue:
                System.out.println(color);
                break;
            case Yellow:
                System.out.println(color);
                break;
            case Green:
                System.out.println(color);
                break;
        }
    }
};
enum weekdays
{ Sun,Mon,Tue,Wed,Thu,Fri,Sat };
enum Color {
    Red, Green, Blue, Yellow //每个元素都是public static final修饰的
}