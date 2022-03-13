package 容器.demo.集合接口;

import java.util.*;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/31 20:53
 */
public class TestSet {
    public static void main(String[] args) {
        testComparable();
    }
    public static void testComparable(){
        Comparator comparator=new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof Person && o2 instanceof  Person){
                    Person p1=(Person)o1;
                    Person p2=(Person)o2;
                    //按照年龄从小到大排序
                    return Integer.compare(p1.getAge(),p2.getAge());
                }else {
                    throw new RuntimeException("类型错误");
                }
            }
        };
        
        Set set=new TreeSet(comparator);
        set.add(new Person(12,"tom"));
        set.add(new Person(22,"jerry"));
        set.add(new Person(1,"bob"));
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }
    public static void testTreeSet(){
        Set set=new TreeSet();
        set.add(new Person(12,"tom"));
        set.add(new Person(22,"jerry"));
        set.add(new Person(1,"bob"));
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
    public static void testMap(){
        Map map=new HashMap();
        map.put(new Person(1,12,"Tom")," I am Tom");
        System.out.println(map.get(new Person(1,12,"Tom")));
    }
    public static void test(){
        Set set=new HashSet();
        set.add(123);
        set.add("ABC");
        set.add(new Person(1,12,"Tom"));
        set.add(new Person(1,12,"Tom"));
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
class Person implements Comparable{
    private int id;
    private int age;
    private String name;

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Person(int id, int age, String name) {
        this.id = id;
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    //按照姓名从小到大排序
    @Override
    public int compareTo(Object o) {
        if (o instanceof Person){
            Person p=(Person)o;
            int res= this.name.compareTo(p.name);
            if (res!=0) {//第一级排序按照姓名从小到大
                return res;
            }else {//二级排序，姓名相同按照年龄排序
                return Integer.compare(this.age,((Person) o).age);
            }
        }else {
            throw  new  RuntimeException("类型错误");
        }
    }
}
