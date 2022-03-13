package javase.demo.注解;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * @author www.yanzhongxin.com
 * @date 2021/3/5 19:01
 */

public class DemoTest {
    //使用我们的自定义注解
    @MyAnnocation(description = "yanzhongxin", length = 12)
    private String username;
     public static void main(String[] args) throws Exception {
        // 获取类模板
        Class c = DemoTest.class;
        // 获取所有字段
        for(Field f : c.getDeclaredFields()){
            // 判断这个字段是否有MyField注解
            if(f.isAnnotationPresent(MyAnnocation.class)){//这里才会
                MyAnnocation annotation = f.getAnnotation(MyAnnocation.class);
                System.out.println("字段:[" + f.getName() +
                        "], 描述:[" + annotation.description() + "], 长度:[" +
                        annotation.length() +"]"
                );
                System.out.println(annotation);
            }
        }

    }
}
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnocation {
    String description();
    int length();
}
