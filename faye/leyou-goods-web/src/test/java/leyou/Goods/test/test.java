package leyou.Goods.test;


import com.leyou.Goods.Client.GoodsClient;
import com.leyou.LeyouGoodsWebApplication;
import com.leyou.item.pojo.Spu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = LeyouGoodsWebApplication.class)
@RunWith(SpringRunner.class)
public class test {

    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void test()
    {
        Spu spu = goodsClient.querySpuById(2l);
        System.out.println(spu.getTitle());
    }
}
