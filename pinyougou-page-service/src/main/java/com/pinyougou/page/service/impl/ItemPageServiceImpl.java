package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;



    @Override
    public boolean genItemHtml(Long goodsId) {

        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        try {
            Template template = configuration.getTemplate("item.ftl");
//Template template = configuration.getTemplate("item.ftl");
            Map dataMap=new HashMap();

            //商品胴
            TbGoods goods = tbGoodsMapper.selectByPrimaryKey(goodsId);

            dataMap.put("goods",goods);


            //商品详情
            TbGoodsDesc goodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

            dataMap.put("goodsDesc",goodsDesc);

            //商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();

            dataMap.put("itemCat1",itemCat1);

            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();

            dataMap.put("itemCat2",itemCat2);

            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataMap.put("itemCat3",itemCat3);
            //加载sku

            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//状态为有效

            criteria.andGoodsIdEqualTo(goodsId);//指定SPU ID
            example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认

            List<TbItem> items = tbItemMapper.selectByExample(example);

            dataMap.put("itemList",items);


            //拼接字符
            FileWriter writer = new FileWriter(pagedir+goodsId+".html");

            template.process(dataMap,writer);

            writer.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
        for (Long goodsId : goodsIds) {

                new File(pagedir+goodsId+".html").delete();

            }  return true;
        }catch (Exception e) {

                e.printStackTrace();
                return false;
            }



    }
}
