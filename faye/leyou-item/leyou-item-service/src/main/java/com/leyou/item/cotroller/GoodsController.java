package com.leyou.item.cotroller;


import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import com.rabbitmq.client.AMQP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    ){
        PageResult<SpuBo> result = goodsService.querySpuByPage(key,saleable,page,rows);
        if(result == null || CollectionUtils.isEmpty(result.getItems()))
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);

    }

    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo)
    {
        System.out.println(spuBo);
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * 功能描述:根据 SpuId 查询 Spudetail
     * @Param: [sid]
     * @Return: org.springframework.http.ResponseEntity<com.leyou.item.pojo.SpuDetail>
     * @Author: CHWN
     * @Date: 2020/4/24 23:51
     */
    @GetMapping("spu/detail/{sid}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("sid") Long sid){
        SpuDetail spuDetail = goodsService.querySpuDetailBySpuId(sid);
        if(spuDetail==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long id)
    {
        List<Sku> skus = goodsService.querySkusBySpuId(id);
        if (CollectionUtils.isEmpty(skus))
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }
    @PutMapping("goods")
    public ResponseEntity<Void> updateSpuBySpuId(@RequestBody SpuBo spuBo)
    {
        goodsService.updateSpuBySpuId(spuBo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id)
    {
        Spu spu = goodsService.querySpuById(id);

        if (spu==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);

    }
    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId")Long skuId){
        Sku sku = goodsService.querySkuBySkuId(skuId);
        if (sku==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sku);

    }

}
