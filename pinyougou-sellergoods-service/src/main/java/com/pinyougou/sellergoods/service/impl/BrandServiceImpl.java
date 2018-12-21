package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult getPageResult(int page, int size) {

        PageHelper.startPage(page,size);

        Page<TbBrand> page1 = (Page<TbBrand>) brandMapper.selectByExample(null);

        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(long[] id) {

        for (long l : id) {
            brandMapper.deleteByPrimaryKey(l);
        }

    }

    @Override
    public PageResult getPageResult(TbBrand brand, int page, int size) {
        PageHelper.startPage(page,size);

        TbBrandExample example = new TbBrandExample();

        TbBrandExample.Criteria criteria = example.createCriteria();

        if (brand!=null) {
            if (brand.getName() != null && brand.getName().length() != 0) {

                criteria.andNameLike("%" + brand.getName() + "%");

            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() != 0) {

                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
            }

        }

        Page<TbBrand> page1 = (Page<TbBrand>) brandMapper.selectByExample(example);

        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
