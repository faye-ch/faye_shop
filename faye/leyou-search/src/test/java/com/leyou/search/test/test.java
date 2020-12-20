package com.leyou.search.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.search.Client.BrandClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {

    @Autowired
    private BrandClient brandClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testBrand()
    {
        Brand brand = brandClient.queryBrandById(1528l);
        System.out.println(brand.getName());
        System.out.println(brand.getName());
        System.out.println(brand.getName());
        System.out.println(brand.getName());
    }

    @Test
    public void timeTestr(){
        System.out.println(new Date());
    }

    //将List<Map<String,Object>> 转为 json 格式
    @Test
    public void JsonStringTest()throws Exception{
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("id",1998);
        map.put("price",999.00);
        map.put("name","华为nove3");
        skuMapList.add(map);

        String jsonString = MAPPER.writeValueAsString(skuMapList);
        System.out.println(jsonString);
    }

    //将 json 格式转换为 Map<String,List<Object>>结构
    @Test
    public void JsonToListMap()throws Exception{
        //{"4":["白色","金色","玫瑰金"],"12":["3GB"],"13":["16GB"]}
        String json = "{\"4\":[\"白色\",\"金色\",\"玫瑰金\"],\"12\":[\"3GB\"],\"13\":[\"16GB\"]}";
        Map<String,List<Object>> MapList = MAPPER.readValue(json,new TypeReference<Map<String,List<Object>>>(){});
        System.out.println(MapList.toString());
    }
}
