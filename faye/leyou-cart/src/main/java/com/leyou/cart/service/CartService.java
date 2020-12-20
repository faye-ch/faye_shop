package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    //前缀
    private static final String KEY_PREKEY = "leyou:cart:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;
    public void addCart(Cart cart) {

        //1.使用过滤器中的线程域 获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //2.查询购物车的记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(KEY_PREKEY + userInfo.getId());

        //3.判断当前商品是否在购物车当中
        String key = cart.getSkuId().toString();
        //获取数量
        Integer num = cart.getNum();
        if(hashOperations.hasKey(key)){
            //购物车已有此商品
            String cartJson = hashOperations.get(key).toString();
            //反序列化为对象 ,使用传进来的那个 cart 进行接收
            cart = JsonUtils.parse(cartJson, Cart.class);
            //更新数量
            cart.setNum(num+cart.getNum());
        }
        else{
            //购物车中没有此商品
            //根据 skuId 查询 sku
            Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());

            //填充 cart
            cart.setUserId(userInfo.getId());
            cart.setSkuId(sku.getId());
            cart.setTitle(sku.getTitle());
            cart.setImage(StringUtils.isBlank(sku.getImages())? "": StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
        }

        hashOperations.put(key,JsonUtils.serialize(cart));

    }

    //查询购物车 返回浏览器的数据是 java 对象
    public List<Cart> queryCarts() {
        //1.获取用户的信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        if (!redisTemplate.hasKey(KEY_PREKEY+userInfo.getId())){
            return null;
        }
        //2.从redis 中获取用户的购物车
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(KEY_PREKEY + userInfo.getId());

        //3.获取 map 的全部的value 值
        List<Object> JsonCarts = hashOperations.values();

        if(CollectionUtils.isEmpty(JsonCarts)){
            return null;
        }

        //4.反序列化为 Cart 对象
        return JsonCarts.stream().map(JsonCart -> JsonUtils.parse(JsonCart.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {

        //1.查询用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //2.判断用户是否有购物车
        if (!redisTemplate.hasKey(KEY_PREKEY+userInfo.getId()))
        {
            return;
        }

        //获取购物车记录
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(KEY_PREKEY + userInfo.getId());

        //记录未被覆盖的 cart 的数量值
        Integer num = cart.getNum();
        //获取具体的 sku
        String JsonCart = boundHashOps.get(cart.getSkuId().toString()).toString();

        //序列化为 Java 对象 ，使用cart接收
        cart = JsonUtils.parse(JsonCart,Cart.class);

        //重新设置数量
        cart.setNum(num);

        //反序列化未JSON 字符串 put 进redis中
        boundHashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));

    }
}