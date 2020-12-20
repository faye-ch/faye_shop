package com.leyou.search.pojo;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;
import java.util.Map;

//扩展 pageResult 添加多两个字段，品牌集合，分类集合
public class SearchResult extends PageResult{

    private List<Brand> brands;
    private List<Map<String,Object>> categoryes;
    //  List [ <cpu,[4核,6核,8核]>,<摄像头,[1200万,2400万,5000万]>]
    private List<Map<String,Object>> specs;

    public SearchResult(Long total, Integer totalPage, List<Goods> items,List<Brand> brands, List<Map<String, Object>> categoryes,List<Map<String,Object>> specs) {

        super(total,totalPage,items);
        this.brands = brands;
        this.categoryes = categoryes;
        this.specs=specs;
    }


    public SearchResult() {
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getCategoryes() {
        return categoryes;
    }

    public void setCategoryes(List<Map<String, Object>> categoryes) {
        this.categoryes = categoryes;
    }
}
