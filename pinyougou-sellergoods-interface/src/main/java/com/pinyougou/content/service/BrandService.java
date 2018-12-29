package com.pinyougou.content.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    List<TbBrand> findAll();

    PageResult getPageResult(int page , int size);

    void add(TbBrand tbBrand);

    TbBrand findOne(long id);

    void update(TbBrand tbBrand);

    void delete(long[] id);

    PageResult getPageResult(TbBrand brand, int page , int size);

    /**
     * 下拉框数据回显
     */

    public List<Map> selectOptionList();

}
