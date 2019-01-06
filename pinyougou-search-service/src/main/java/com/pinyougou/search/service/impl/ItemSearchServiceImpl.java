package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public Map search(Map searchMap) {
        Map map=new HashMap();
        //空格处理
        String keywords= (String)searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));//关键字去掉空格
        //调用高亮
        Map maplist = searchmap(searchMap);

        //分类分组
        List<String> grupList=grubList(searchMap);
        map.put("categoryList",grupList);

        map.putAll(maplist);

       //判断是否使用了选中的品牌
        String categoryName=(String)searchMap.get("category");
        if(!"".equals(categoryName)){//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{//如果没有分类名称，按照第一个查询
            if(grupList.size()>0){
                map.putAll(searchBrandAndSpecList(grupList.get(0)));
            }
        }

                return map;
    }



    //高亮
    private Map searchmap(Map searchMap){

        Map map=new HashMap();
        HighlightQuery query=new SimpleHighlightQuery();
        HighlightOptions highlightOptiond = new HighlightOptions().addField("item_title");//设置gap里昂与

        highlightOptiond.setSimplePrefix("<em style='color:red'>");
        highlightOptiond.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptiond);//设置高亮显示


        //1.0 高亮查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);

        //1.1 条件查询，品牌

        if (!"".equals(searchMap.get("brand"))) {

            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }


        //1.2 条件查询规格

        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map) searchMap.get("spec");
            for(String key:specMap.keySet() ){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.3 条件查询分类

        if (!"".equals(searchMap.get("category"))) {

            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }


        // 1.6 价格区间

        if (!"".equals(searchMap.get("price"))){

           String[] price = ((String) searchMap.get("price")).split("-");

           if (!price[0].equals("0")){

            Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
           }
           if (!price[1].equals("*")){

               Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
               FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
               query.addFilterQuery(filterQuery);

           }

        }

        //分页查询


        //当前页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){

            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");

        //每页记录数
        if (pageSize==null){
            pageSize=20;
        }

        query.setOffset((pageNo-1)*pageSize);//传递启示索引

        query.setRows(pageSize);//传递每页显示记录数



        //排序
       String sortValue = (String) searchMap.get("sort");
       String sortFieldValue = (String) searchMap.get("sortField");

       if (sortValue==null&& sortValue.equals("")){
           if (sortValue.equals("ASC")){
               Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortFieldValue);
               query.addSort(sort);
           }
           if (sortValue.equals("DESC")){

               Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortFieldValue);
               query.addSort(sort);
           }

       }






//*************************高亮显示结果******************************************************
        // 查询结果集
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //循环高亮入口集

        for (HighlightEntry<TbItem> h: page.getHighlighted()) {

            TbItem entity = h.getEntity();

            if (h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){

                entity.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
                //设置高亮结果
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());//返回总页数
        map.put("total",page.getTotalElements());//返回总记录数


        return map;
    }


    //分组，分类

    private List<String> grubList(Map searchMap) {

        List<String> list=new ArrayList<String>();

        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));//where
        query.addCriteria(criteria);

        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");//group by
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

        Page<GroupEntry<TbItem>> entries = groupResult.getGroupEntries();

        List<GroupEntry<TbItem>> content = entries.getContent();

        for (GroupEntry<TbItem> tbItemGroupEntry : content) {

            list.add(tbItemGroupEntry.getGroupValue());
        }


        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;


    //调用缓存

     private Map searchBrandAndSpecList(String category){

         Map map=new HashMap();

         Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
         if(typeId!=null){
             //根据模板ID查询品牌列表
             List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
             map.put("brandList", brandList);//返回值添加品牌列表
             //根据模板ID查询规格列表
             List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);

             map.put("specList", specList);
         }
         return map;
     }


     //更新索引库
     @Override
     public void importList(List list) {
         solrTemplate.saveBeans(list);//导入到索引库
         solrTemplate.commit();
     }

     //删除商品索引
    @Override
    public void deleteByGoodsIds(List goodsIdList) {

        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }


}



