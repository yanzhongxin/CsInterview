package 容器.demo.集合接口;

import java.util.*;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/29 8:50
 */
public class TestArrayList {
    public static void main(String[] args) {
        testVector();
    }
    public static void testVector(){
        Vector vector=new Vector();
        vector.add("a");
        vector.add("b");
        Iterator iterator = vector.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
    public static void  testQueue(){
        Deque queue=new LinkedList();

        // insert:双端队列首尾插入
        queue.addFirst("a");
        queue.offerFirst("b");
        queue.addLast("y");
        queue.offerLast("z");
        //remove双端队列首尾删除
        queue.removeFirst();
        queue.pollFirst();
        queue.removeLast();
        queue.pollLast();
        //Examine双端队列get首尾
        queue.getFirst();
        queue.peekFirst();
        queue.getLast();
        queue.peekLast();
    }
    public static void testLinkedList(){
        LinkedList linkedList=new LinkedList();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.add("d");
        linkedList.add("c");
        Iterator iterator = linkedList.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
    public static void test(){
        ArrayList list=new ArrayList(10);
        list.add("a");
        list.add("b");
        list.add("c");
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
