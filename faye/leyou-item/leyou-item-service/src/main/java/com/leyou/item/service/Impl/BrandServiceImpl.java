package com.leyou.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandServcie;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements IBrandServcie {

    @Autowired
    private BrandMapper brandMapper;
/*
 * 功能描述: 查询品牌集合封装到分页对象中
 * @Param: [key, page, rows, sortBy, desc]
 * @Return: com.leyou.common.pojo.PageResult<com.leyou.item.pojo.Brand>
 * @Author: CHWN
 * @Date: 2020/4/20 19:11
 */
    @Override
    public PageResult<Brand> queryBrandPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        //用什么api查询？
        //要进行模糊查询 不能使用select
        //example用于添加条件，相当where后面的部分.
        //Example为我们创建的实例 Example.createCriteria（）为我们创建了条件容器
        //Criteria ：条件容器
        //Exampel.setOrderByClause 这个是排序不属于查询条件

        //创建Example对象
        Example example = new Example(Brand.class);
        //根据example对象创建条件容器
        Example.Criteria criteria = example.createCriteria();
        //判断有没有输入搜索条件
        if(StringUtils.isNotBlank(key))
        {
            //将条件加入到条件容器
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }

        //使用 pageHelper 插件进行物理分页
        PageHelper.startPage(page,rows);
        //判断有没有选择用什么属性去排序
        if(StringUtils.isNotBlank(sortBy))
        {
            //排序 sql ：order by id(sortBy) desc
            //由于desc 的值为 boolean值 所以使用三元表达式
            example.setOrderByClause(sortBy+" "+(desc? "desc":"asc"));
        }

        //接收查询结果
        List<Brand> brands = brandMapper.selectByExample(example);
        //将查询结果封装到 pageInfo ：只需要传送一个list进pageInfo 就会自动帮你计算好 总条数等信息
        PageInfo pageInfo = new PageInfo(brands);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> ids) {
        //先增加品牌，因为有了品牌id 才能增加中间表
        brandMapper.insertSelective(brand);

        //通用mapper 只能操作一张表，操作另一张表需要自己添加方法
        //遍历插入中间表
        ids.forEach(id->{
            brandMapper.insertBrandIdAndCategoryId(id,brand.getId());
        });
    }

    /*
     * 功能描述:更新
     * @Param: [brand]
     * @Return: void
     * @Author: CHWN
     * @Date: 2020/4/22 10:41
     */
    @Override
    public void upateBrand(Brand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void deleteBrand(Long bid) {
        brandMapper.deleteByPrimaryKey(bid);
    }

    /*
     * 功能描述:根据Spu id 更新 Spu Sku SpuDetail Stock 四张表
     * @Param: [cid]
     * @Return: java.util.List<com.leyou.item.pojo.Brand>
     * @Author: CHWN
     * @Date: 2020/4/25 11:04
     */
    @Override
    public List<Brand> queryBrandByCid(Long cid) {

        return brandMapper.selectBrandByCid(cid);
    }

    //根据id 查询品牌
    @Override
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
