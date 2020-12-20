package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import com.leyou.item.service.Impl.CategoryServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class GoodsService {


    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private Logger logger;

    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //1.判断可选参数是否为空
        //2.搜索条件
        if(StringUtils.isNotBlank(key))
        {
            criteria.andLike("title","%"+key+"%");
        }
        //3.添加上下架条件
        if(saleable!=null)
        {
            criteria.andEqualTo("saleable",saleable);
        }
        //4.添加分页
        PageHelper.startPage(page,rows);
        //5.执行查询，获取spu结合
        List<Spu> spus = spuMapper.selectByExample(example);
        //封装分页集合 以便获取总条数
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //6.spu集合转化成spubo集合
        List<SpuBo> spuBos = new ArrayList<>();
        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            //6.1添加查询品牌名称
            spuBo.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            //6.2查询分类名称
            List<String> cnames = categoryService.queryCnameListByCidList(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            spuBo.setCname(StringUtils.join(cnames,"/"));
            //6.3将spu 复制到 spubo 中
            // copy共同属性的值到新的对象
            BeanUtils.copyProperties(spu,spuBo);
            //6.4将spuBo 对象装进集合
            spuBos.add(spuBo);
        });

        //7.返回pageResult<spuBo>
        return new PageResult<SpuBo>(pageInfo.getTotal(),spuBos);

    }

    //新增商品
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //1.保存Spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);
        //2.保存SpuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);
        //调用发送消息方法，通知大家商品已经新增
        sendMsg("insert",spuBo.getId());

    }

    //发送消息到 mq
    //id ： spuId
    //type: insert update ...
    private void sendMsg(String type,Long id) {
        try{
            amqpTemplate.convertAndSend("item."+type,id);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //新增sku 并 新增库存
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            //3.保存Sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            //4.保存Stock 库存
            Stock stock = new Stock();
            stock.setStock(sku.getStock());
            stock.setSkuId(sku.getId());
            stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail querySpuDetailBySpuId(Long sid) {
        return  spuDetailMapper.selectByPrimaryKey(sid);
    }

    //根据 SpuId 查询spu下面的sku集合
    public List<Sku> querySkusBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        //添加相应的库存
        skus.forEach(sku1 -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        });
        return skus;
    }

    //修改spu
    public void updateSpuBySpuId(SpuBo spuBo) {
        //查询以前是否有的Sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skus)){
            skus.forEach(s->{
                //删除stock表中的库存
                stockMapper.deleteByPrimaryKey(s.getId());
            });
            //删除Sku 根据Spu id
            Sku sku1 = new Sku();
            sku1.setSpuId(spuBo.getId());
            skuMapper.delete(sku1);
        }

        //新增Sku以及stock
        this.saveSkuAndStock(spuBo);

        //新增Spu
        //防止非法注入，影响数据原始性
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        //是否上下架，此业务不是在更新商品中操作
        spuBo.setSaleable(null);
        //是否删除商品，此业务不是在更新商品中操作
        spuBo.setValid(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);

        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //修改完 商品就发送消息通知大家
        sendMsg("update",spuBo.getId());

    }

    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    public Sku querySkuBySkuId(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }
}
