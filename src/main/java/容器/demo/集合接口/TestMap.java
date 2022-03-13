package 容器.demo.集合接口;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author www.yanzhongxin.com
 * @date 2021/4/4 11:31
 */
public class TestMap {
    public static void main(String[] args) {
        testComparable();
    }
    public static void testComparable(){
        Map<People,String> map=new TreeMap<>(new Comparator<People>() {
            @Override
            public int compare(People o1, People o2) {
                return Integer.compare(o1.getAge(),o2.getAge());
            }
        });
        map.put(new People("张飞",12),"战国");
        map.put(new People("刘备",22),"战国");
        map.put(new People("关羽",2),"战国");





    }
}
class People implements Comparable{
    private int age;
    private String name;
    public int getAge() {
        return age;
    }
    public People( String name,int age) {
        this.age = age;
        this.name = name;
    }
    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.age,((People)o).getAge());
    }
}
