package 容器.hashmap全解析;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author www.yanzhongxin.com
 * @date 2020/11/13 19:21
 */
public class HashMapDemo {
    public static void main(String[] args) {
        //testCounCurrentHashmap();
        LinkedHashMap linkedHashMap=new LinkedHashMap();
        linkedHashMap.put("aa","bb");
        System.out.println(tableSizeFor(17));
    }
    static final int tableSizeFor(int cap) {
// 扩容门槛为传入的初始容量往上取最近的2的n次方
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : n + 1;
    }
    public static void teshi1(){
        System.out.println(Integer.parseInt("0001111", 2) & 15);
        System.out.println(Integer.parseInt("0011111", 2) & 15);
        System.out.println(Integer.parseInt("0111111", 2) & 15);
        System.out.println(Integer.parseInt("1111111", 2) & 15);
    }
}

class Student{

    String name;

    @Override
    public boolean equals(Object o) { //不同名字的对象不相等
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(name, student.name);
    }

    public Student(String name) {

        this.name = name;
    }



    @Override
    public int hashCode() {
        return 80;//任何对象的hashcode相等
    }
}
