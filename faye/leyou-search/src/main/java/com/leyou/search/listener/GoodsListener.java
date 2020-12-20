package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
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
    private SearchService searchService;

    //更新和保存
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.SAVE.QUEUE",durable = "true"),
            exchange = @Exchange(
                    value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}))
    public void listenSave(Long id) throws Exception
    {
        if (id==null)
        {
            return;
        }
        searchService.save(id);
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.DELETE.INDEX.QUEUE",durable = "true"),
            exchange = @Exchange(
                    value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void listenDelete(Long id)
    {
        if(id==null) {
            return;
        }
        searchService.delete(id);
    }


}
