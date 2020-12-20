package com.leyou.item.cotroller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private IBrandServcie iBrandServcie;
/*
 * 功能描述:查询品牌
 * @Param: [key, page, rows, sortBy, desc]
 * @Return: org.springframework.http.ResponseEntity<com.leyou.common.pojo.PageResult<com.leyou.item.pojo.Brand>>
 * @Author: CHWN
 * @Date: 2020/4/21 10:02
 */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",required = false)Boolean desc
    )
    {
        PageResult<Brand> result = iBrandServcie.queryBrandPage(key,page,rows,sortBy,desc);
        if(CollectionUtils.isEmpty(result.getItems()))
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    /*
     * 功能描述:新增品牌
     * @Param: [brand, ids]
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: CHWN
     * @Date: 2020/4/21 10:02
     */
    @PostMapping
    //浏览器传输的参数：name:xx image：xx cids：xx letter：xx
    // 使用Brand对象通过表单参数自动绑定接收   ，所以ids需要使用@RequestParam指定接收
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam(value = "cids")List<Long> ids)
    {
        //插入数据
        iBrandServcie.saveBrand(brand,ids);
        //响应200状态码 表示成功
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    //浏览器传输的参数：name:xx image：xx cids：xx letter：xx
    // 使用Brand对象通过表单参数自动绑定接收   ，所以ids需要使用@RequestParam指定接收
    public ResponseEntity<Void> updateBrand(Brand brand)
    {
        //插入数据
        iBrandServcie.upateBrand(brand);
        //响应200状态码 表示成功
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid)
    {
        iBrandServcie.deleteBrand(bid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid)
    {
        List<Brand> brands = iBrandServcie.queryBrandByCid(cid);
        if(brands==null || CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brands);
    }

    //根据品牌id 查询 品牌
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id)
    {
        Brand brand = iBrandServcie.queryBrandById(id);
        if(brand==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}


