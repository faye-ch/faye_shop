package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParmMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParmMapper specParmMapper;

    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return specGroupMapper.select(specGroup);
    }

    public List<SpecParam> querySpecParamById(Long gid,Long cid,Boolean generic,Boolean searching) {

        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return specParmMapper.select(specParam);
    }

    public List<SpecGroup> queryGroupWithParm(Long cid) {
        List<SpecGroup> specGroups = this.querySpecGroupByCid(cid);
        specGroups.forEach(specGroup -> {
            List<SpecParam> params = this.querySpecParamById(specGroup.getId(), null, null, null);
            specGroup.setParams(params);
        });

        return specGroups;
    }
}
