package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.Client.BrandClient;
import com.leyou.search.Client.CategoryClient;
import com.leyou.search.Client.GoodsClient;
import com.leyou.search.Client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.reponsitory.GoodReponsitory;
import javafx.scene.chart.CategoryAxis;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private GoodReponsitory goodReponsitory;

    //总体思想：
    //根据spu的信息,调用各种FeginClient 接口获取相应的数据，封装成 goods 对象
    public Goods buildGoods(Spu spu) throws Exception {

        // 1.创建goods对象
        Goods goods = new Goods();

        // 2.查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // 3.查询分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 4.查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        //为什么采用 List<Map<String,Object>> 数据结构？
        //Object 类型能存储多种类型 如name是字符型 price 是Long型
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 4.1遍历skus，获取价格集合
        skus.forEach(sku ->{
            prices.add(sku.getPrice());
            //4.2将优化的 sku 装进集合
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });


        //5.完成存储 Specs(规格参数集合)，模型->map<规格参数名,规格参数值> -> map<内存,64G>
        //规格参数：内存:512G -> 参数名:参数值 分别存储在规格参数表，和Spu详情表
        // 查询出所有的搜索规格参数名，只需要根据分类3id 和 是否需要搜索两个参数进行查询
        List<SpecParam> params = this.specificationClient.querySpecParamByGid(null, spu.getCid3(), null, true);
        // 5.1 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 5.2 获取通用的规格参数值，因为规格参数在数据库中用json格式存储的，需要MAPPER 对象进行反序列化
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        //5.3 获取特殊的规格参数值,特殊的规格参数值是可选的:
        //{"4":["白色","金色","玫瑰金"], "12":["3GB"], "13":["16GB"]}
        //4就是颜色(规格参数名的Id),所以特殊的规格参数值有可能是集合，多个的，可选的
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {});
        // 5.4 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 使用 paramId 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    //如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                //此时的Object为List<Object>
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        //将 skuMapList 转换成 json 格式的字符串 : [{"price":999.0,"name":"华为nove3","id":1998}]
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    //搜索业务
    //SearchRequst 搜索的请求对象封装
    public PageResult<Goods> search(SearchRequest request) {

        //如果搜索对象的关键词为空，则直接返回
        if(StringUtils.isBlank(request.getKey()))
        {
            return null;
        }

        //自定义搜索
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        //构建布尔查询对象 , 在下面编写一个方法，处理构建过滤查询对象
        BoolQueryBuilder queryBuilder = buildBoolQueryBuilder(request);
        nativeSearchQueryBuilder.withQuery(queryBuilder);

        //通过过滤器设置返回的结果字段 只需要 id suks subTitle
        //过滤器的使用 ：FatchSourceFilter(需要的字段，排除的字段)
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"},null
        ));

        //分页
        int page = request.getPage();
        int size = request.getSize();

        //分页下标从 0 开始
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page-1,size));

        //集合名称
        String categoryesName = "categoryes";
        String brandsName = "brands";
         //对分类进行聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryesName).field("cid3"));
        //对品牌进行聚合，设置聚合名称，要进行聚合的字段
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandsName).field("brandId"));

        //查询  //强转为包含聚合的结果集
        AggregatedPage<Goods> goodsPage =(AggregatedPage) this.goodReponsitory.search(nativeSearchQueryBuilder.build());



        //解析聚合结果集
        List<Map<String,Object>> categoryes = CategoryesAggregation(goodsPage.getAggregation(categoryesName));
        List<Brand> brands = BrandsAggregation(goodsPage.getAggregation(brandsName));

        List<Map<String,Object>> specs=null;
        //如果聚集出来的分类为 1 个，则进行规格参数的聚合
        //如查询华为mate30 聚合的分类为手机，则可以显示规格参数
        //如果查询小米 ，小米的产品很广泛 分类不止一个，如手机，电视剧等 ，这么多的分类规格参数不统一
        if(categoryes.size()==1)
        {
            //集合规格参数，需要传入的值
            //1.cid 根据分类的 id 进行聚合
            //2.query 搜索的查询条件,在搜索的记录中进行集合
            //调用规格参数聚合方法
           specs = getParamAggResult((Long)categoryes.get(0).get("id"),queryBuilder);
        }

        //封装集合
        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(),brands,categoryes,specs);



    }

    //构建布尔查询器方法
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        //创建布尔查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //添加基本的查询 all字段,关键词的交集
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        //添加过滤条件，如果没有选择过滤项目，则直接返回
        if(CollectionUtils.isEmpty(request.getFilter()))
        {
            return boolQueryBuilder;
        }

        //遍历过滤项
        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {
            //获取过滤项的 key
            String key = entry.getKey();
            //如果key == 品牌,则赋值为 brandId 对应 goods的字段
            if(StringUtils.equals("品牌",key))
            {
                key="brandId";
            }
            else if (StringUtils.equals("分类",key)){
                key="cid3";
            }
            else{
                key="specs."+key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }


    //规格参数聚合器
    private List<Map<String,Object>> getParamAggResult(Long id, QueryBuilder basicQuery) {

        // 创建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(basicQuery);
        // 查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.querySpecParamByGid(null, id, null, true);
        // 添加聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        // 只需要聚合结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        // 执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodReponsitory.search(queryBuilder.build());

        // 定义一个集合，收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();
        // 解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            // 放入规格参数名
            map.put("k", entry.getKey());
            // 收集规格参数值
            List<Object> options = new ArrayList<>();
            // 解析每个聚合
            StringTerms terms = (StringTerms)entry.getValue();
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("options", options);
            paramMapList.add(map);
        }

        return paramMapList;
    }

//    private List<Map<String,Object>> getSpecsAggreate(Long id, MatchQueryBuilder queryBuilder) {
////        1.自定义查询对象构建
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
////        2.添加用户搜索查询条件
//        nativeSearchQueryBuilder.withQuery(queryBuilder);
////        3.查询要聚合的规格参数
//        List<SpecParam> params = this.specificationClient.querySpecParamByGid(null, id, null, true);
////        4.添加规格参数的聚合
//        params.forEach(specParam -> {
//            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("specs."+specParam.getName()+".keyword"));
//        });
//
////        5.添加结果过滤
//        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
//
////        6.执行集合查询，获取集合的结果集
//        AggregatedPage<Goods> goodsPage =(AggregatedPage) this.goodReponsitory.search(nativeSearchQueryBuilder.build());
//        List<Map<String,Object>> paramMapList = new ArrayList<>();
////        7.解析集合结果集，key-集合的名称(规格参数名) value-聚合对象
//        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
//        for(Map.Entry<String,Aggregation> entry:aggregationMap.entrySet())
//        {
//            Map<String,Object> map = new HashMap<>();
//            map.put("k",entry.getKey());
//            List<Object> options = new ArrayList<>();
//            StringTerms terms = (StringTerms)entry.getValue();
//            terms.getBuckets().forEach(bucket -> {
//                options.add(bucket.getKeyAsString());
//            });
//            map.put("options",options);
//            paramMapList.add(map);
//        }
//        return paramMapList;
////        //7.1初始化 map{k:规格参数名 options：聚合的规格参数值}
//        //7.2获取聚合
//        //7.3获取桶聚合
//
//    }

    //品牌集合器
    private List<Brand> BrandsAggregation(Aggregation aggregation) {
        LongTerms terms = (LongTerms)aggregation;
        List<Brand> brands = new ArrayList<>();
        //每个桶装 的是品牌的id
        terms.getBuckets().forEach(bucket -> {
            long id = bucket.getKeyAsNumber().longValue();
            //根据id查询品牌
            Brand brand = this.brandClient.queryBrandById(id);
            brands.add(brand);
        });
        return brands;
    }

    //分类聚合器
    private List<Map<String,Object>> CategoryesAggregation(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //拿出聚合桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        //遍历聚合桶
        return buckets.stream().map(bucket -> {
          Map<String,Object> map = new HashMap<>();
            //取出聚合的分类 id
            long id = bucket.getKeyAsNumber().longValue();
            //根据分类 id 查询分类名称
            List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(id));
            map.put("id",id);
            map.put("name",names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    public void save(Long id) throws Exception{
        Spu spu = goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        goodReponsitory.save(goods);
    }

    public void delete(Long id) {
        goodReponsitory.deleteById(id);
    }
}