package com.leyou.Goods.service;

import com.leyou.Goods.Client.BrandClient;
import com.leyou.Goods.Client.CategoryClient;
import com.leyou.Goods.Client.GoodsClient;
import com.leyou.Goods.Client.SpecificationClient;
import com.leyou.item.api.CatagoryApi;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// goodsService 负责调用 item-service 提供的API接口,从后台查询数据
@Service
public class GoodsService {

    //注入相关的 Client
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    //传入 SpuId 作为参数
    //要封装的数据
    //1.spu
    //2.SpuDetail
    //3.品牌
    //4.skus
    //5.三级分类
    //6.规格参数组名以及规格参数名
    //7.特殊的规格参数
    public Map<String,Object> loadData(Long spuId)
    {
        Map<String,Object> BigMap = new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //查询商品详情 SpuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuId);
        
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        
        //查询 spu 下面的 所以sku
        List<Sku> skus = goodsClient.querySkusBySpuId(spuId);
        
        //查询三级分类 使用 List<Map<String,Object>> 来封装
        List<Map<String,Object>> categores = new ArrayList<>();
        //使用Arrays.asList(多个参数)
        List<Long> CidList = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            //传进分类ids 查询出来分类的名字
        List<String> names = categoryClient.queryNameByIds(CidList);
        for (int i = 0; i < CidList.size(); i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",CidList.get(i));
            map.put("name",names.get(i));
            categores.add(map);
        }

        //查询 参数组名，以及参数名
        List<SpecGroup> specGroups = specificationClient.queryGroupWithParm(spu.getCid3());

        //查询特殊的规格参数 数据类型：Map<Long,Sstring>
        List<SpecParam> params = specificationClient.querySpecParamByGid(null, spu.getCid3(), false, null);
        Map <Long,String> paramsMap = new HashMap<>();
        params.forEach(specParam -> {
            paramsMap.put(specParam.getId(),specParam.getName());
        });

        //把查询到的结果全部封装到 BigMap 中
        BigMap.put("Spu",spu);
        BigMap.put("SpuDetail",spuDetail);
        BigMap.put("Skus",skus);
        BigMap.put("Brand",brand);
        BigMap.put("Categores",categores);
        BigMap.put("SpecGroups",specGroups);
        BigMap.put("ParamMap",paramsMap);
        return BigMap;
    }
}
