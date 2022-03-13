package javase.demo.枚举;

import java.io.*;


/**
 * @author www.yanzhongxin.com
 * @date 2021/3/2 10:32
 */
public class 序列化测试 {
    public static void main(String[] args) throws Exception {
        jijie j=jijie.SPRING;

        //Write Obj to File
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("tempFile"));
            oos.writeObject(j);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            oos.close();
        }
        jijie newUser=null;
        //Read Obj from File
        File file = new File("tempFile");
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
             newUser = (jijie) ois.readObject();
            System.out.println("new "+newUser);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ois.close();
        }

        System.out.println(j==newUser);
    }
}
 enum jijie {
    SPRING,SUMMER,AUTUMN,WINTER;
}