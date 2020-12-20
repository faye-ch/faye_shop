package com.leyou.item.cotroller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid") Long cid)
    {
        List<SpecGroup> list = specificationService.querySpecGroupByCid(cid);
        if(CollectionUtils.isEmpty(list))
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamByGid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "generic",required = false)Boolean generic,
            @RequestParam(value = "searching",required = false)Boolean searching
            )
    {
        List<SpecParam> list = specificationService.querySpecParamById(gid,cid,generic,searching);
        if(CollectionUtils.isEmpty(list))
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("group/parm/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupWithParm(@PathVariable("cid") Long cid)
    {
        List<SpecGroup> specGroups =  specificationService.queryGroupWithParm(cid);
        if(CollectionUtils.isEmpty(specGroups))
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);
    }

 }
