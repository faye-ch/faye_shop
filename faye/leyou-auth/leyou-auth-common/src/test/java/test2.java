import java.lang.Integer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class test2 {

    public static void main(String[] args)throws Exception{
        Class aClass = Class.forName("person");
        Field[] fields = aClass.getFields();
        for (Field field : fields) {
            System.out.println(field);
        }
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            System.out.println(method);
        }
        Constructor[] constructors = aClass.getConstructors();
        
    }
}
