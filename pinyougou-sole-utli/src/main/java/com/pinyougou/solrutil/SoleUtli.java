package com.pinyougou.solrutil;


import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class SoleUtli {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;


    public void importItemData(){


        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();

        criteria.andStatusEqualTo("1");//审核状态

        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        System.out.println("查询数据");
        for (TbItem tbItem : tbItems) {
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);

            tbItem.setSpecMap(map);

        }


        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("添加成功");

    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");

        SoleUtli soleUtli = (SoleUtli) context.getBean("soleUtli");

        soleUtli.importItemData();


    }

}
