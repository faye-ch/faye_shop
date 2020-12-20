package com.leyou.Goods.listener;

import com.leyou.Goods.service.GoodHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private GoodHtmlService goodHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.SAVE.QUEUE ",durable = "true"),
            exchange = @Exchange(
                    value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
            ))
    public void listenCreate(Long id) throws Exception{
        if(id == null){
            return;
        }
        //创建页面
        goodHtmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.DELETE.QUEUE",durable = "true"),
            exchange = @Exchange(
                    value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void listenDelete(Long id){
        if(id==null)
        {
            return;
        }
        //删除页面
        goodHtmlService.deleteHtml(id);
    }

}
