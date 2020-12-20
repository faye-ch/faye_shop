package com.leyou.item.cotroller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("category")
public class CatagoryController {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 功能描述:根据父节点id 查询子节点
     * @Param: [pid]
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.leyou.item.pojo.Category>>
     * @Author: CHWN
     * @Date: 2020/4/18 23:34
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoersByPid(@RequestParam(name = "pid",defaultValue = "0")Long pid)
    {

            if(pid==null||pid<0)
            {
                //400
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories = categoryService.queryCategoryByPid(pid);
            if(CollectionUtils.isEmpty(categories))
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(categories);
        }

      @GetMapping("bid/{bid}")
      public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid")Long bid){
            List<Category> list = categoryService.queryByBrandId(bid);
            if (list==null ||list.size()<1)
            {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(list);
      }

      //根据ID集合查询分类
    @GetMapping
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids)
    {
        List<String> list = categoryService.queryCnameListByCidList(ids);
        if (list==null ||list.size()<1)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(list);

    }
}
