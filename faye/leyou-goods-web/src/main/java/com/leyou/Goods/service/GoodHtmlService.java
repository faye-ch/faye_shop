package com.leyou.Goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

//创建 html 模板服务
@Service
public class GoodHtmlService {

    //注入模板引擎
    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;
    public void createHtml(Long spuId)
    {
        //获取页面数据
        Map<String, Object> map = goodsService.loadData(spuId);
        //创建thynmeleaf 上下文对象
        Context context = new Context();
        //把数据放进上下文对象
        context.setVariables(map);

        PrintWriter printWriter = null;
        try{
            //创建输出流，
            File file = new File("C:\\Users\\CHWN\\Desktop\\nginx-1.14.0\\html\\item\\"+spuId+".html");

            printWriter = new PrintWriter(file);
            //执行页面静态化
            engine.process("item",context,printWriter);
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }finally {
            if (printWriter!=null)
            {
                printWriter.close();
            }
        }
    }

    public void deleteHtml(Long spuId){
        File file = new File("C:\\Users\\CHWN\\Desktop\\nginx-1.14.0\\html\\item\\"+spuId+".html");
        file.deleteOnExit();
    }
}
