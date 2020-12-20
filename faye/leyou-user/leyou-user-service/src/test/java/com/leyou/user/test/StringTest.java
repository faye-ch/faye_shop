package com.leyou.user.test;

import com.leyou.LeyouUserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouUserApplication.class)
public class StringTest {

    @Test
    public void StringTest()
    {
        String code = "199824";
        String code1 = "199824";
        if (code.equals(code1)){
            System.out.println(true);
        }
    }

}
