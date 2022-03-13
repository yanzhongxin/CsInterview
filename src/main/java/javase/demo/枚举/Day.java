package javase.demo.枚举;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/1 21:11
 */
public enum Day {
    MONDAY {
        @Override
        void say() {
            System.out.println("MONDAY");
        }
    }
    , TUESDAY {
        @Override
        void say() {
            System.out.println("TUESDAY");
        }
    }, FRIDAY("work"){
        @Override
        void say() {
            System.out.println("FRIDAY");
        }
    }, SUNDAY("free"){
        @Override
        void say() {
            System.out.println("SUNDAY");
        }
    };
    String work;
//没有构造参数时，每个实例可以看做常量。
//使用构造参数时，每个实例都会变得不一样，可以看做不同的类型，所以编译后会生成
    //实例个数对应的class。
    private Day(String work) {
        this.work = work;
    }
    private Day() {

    }
    //枚举实例必须实现枚举类中的抽象方法
    abstract void say ();
}
