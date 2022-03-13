package 高并发.demo;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author www.yanzhongxin.com
 * @date 2020/11/26 17:46
 */
class User {
    String userName;
    int age;
    public User(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
public class AtomicReferenceDemo {

    public static void main(String[] args) {
        User z3 = new User("z3", 22);
        User l4 = new User("l4", 25);
        System.out.println(z3.hashCode()+ "  "+l4.hashCode());
        // 创建原子引用包装类
        AtomicReference<User> atomicReference = new AtomicReference<>();
        // 现在主物理内存的共享变量，为z3
        atomicReference.set(z3);
        // 比较并交换，如果现在主物理内存的值为z3，那么交换成l4
        System.out.println(atomicReference.compareAndSet(z3, l4) + "\t " + atomicReference.get().toString()+"  "+(atomicReference.get()==l4));
        // 比较并交换，现在主物理内存的值是l4了，但是预期为z3，因此交换失败
        System.out.println(atomicReference.compareAndSet(z3, l4) + "\t " + atomicReference.get().toString()+"  "+atomicReference.get().hashCode());
    }
}
