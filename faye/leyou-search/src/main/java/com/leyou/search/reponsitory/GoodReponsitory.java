package com.leyou.search.reponsitory;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodReponsitory extends ElasticsearchRepository<Goods,Long> {
}
