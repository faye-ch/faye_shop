package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> queryCategoryByPid(Long pid);

    List<Category> queryByBrandId(Long bid);

    List<String> queryCnameListByCidList(List<Long> longs);
}
