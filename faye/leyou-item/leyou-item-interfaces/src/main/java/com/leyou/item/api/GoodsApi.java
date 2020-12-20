package com.leyou.item.api;


import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );



    /*
     * 功能描述:根据 SpuId 查询 Spudetail
     * @Param: [sid]
     * @Return: org.springframework.http.ResponseEntity<com.leyou.item.pojo.SpuDetail>
     * @Author: CHWN
     * @Date: 2020/4/24 23:51
     */
    @GetMapping("spu/detail/{sid}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("sid") Long sid);


    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long id);

    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    @GetMapping("sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId")Long skuId);


}
