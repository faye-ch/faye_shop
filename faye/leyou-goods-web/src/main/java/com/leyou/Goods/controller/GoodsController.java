package com.leyou.Goods.controller;

import com.leyou.Goods.service.GoodHtmlService;
import com.leyou.Goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    //传进 spu id 作为参数，进行查询
    @Autowired
    private GoodHtmlService goodHtmlService;
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long id, Model model)
    {
        //调用 goodsService 查询后台数据
        Map<String, Object> map = goodsService.loadData(id);
        model.addAllAttributes(map);
        //创建静态页面
        goodHtmlService.createHtml(id);
        return "item";
    }
}
