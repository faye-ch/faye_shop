package com.leyou.Goods.Client;

import com.leyou.item.api.CatagoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface CategoryClient extends CatagoryApi {
}
