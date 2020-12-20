package com.leyou.item.service.Impl;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 功能描述:根据父节点id 查询子节点
     * @Param: [pid]
     * @Return: java.util.List<com.leyou.item.pojo.Category>
     * @Author: CHWN
     * @Date: 2020/4/18 23:34
     */
    @Override
    public List<Category> queryCategoryByPid(Long pid) {

        Category category = new Category();
        category.setParentId(pid);

        return categoryMapper.select(category);

    }

    @Override
    public List<Category> queryByBrandId(Long bid) {
        return categoryMapper.queryByBrandId(bid);
    }


    public List<String> queryCnameListByCidList(List<Long> longs) {
        List<Category> categories = categoryMapper.selectByIdList(longs);
        List<String> names = new ArrayList<>();
        categories.forEach(category -> {
            names.add(category.getName());
        });
        return names;
    }
}
