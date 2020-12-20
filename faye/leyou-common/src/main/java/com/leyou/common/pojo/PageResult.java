package com.leyou.common.pojo;

import java.util.List;

/*
 * 功能描述:分页结果集合，可供需要分页的调用
 * @Param:
 * @Return:
 * @Author: CHWN
 * @Date: 2020/4/19 23:19
 */
public class PageResult<T> {

    private Long total;//总条数
    private Integer totalPage;//总页数
    private List<T> items;//不应该写成具体的类型,这样可供其他的需要分页的方法使用


    public PageResult(){

    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
