package com.leyou.item.api;



import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface BrandApi {


    //根据品牌id 查询 品牌
    @GetMapping("brand/{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);
}


