package com.pinyougou.content.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.content.service.GoodsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private TbBrandMapper tbBrandMapper;

	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		//添加审核状态
		goods.getGoods().setAuditStatus("0");

		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());


		if ("1".equals(goods.getGoods().getIsEnableSpec())){




		for (TbItem item : goods.getItems()) {

			String title=goods.getGoods().getGoodsName();

	        Map<String,Object> specMap= JSON.parseObject(item.getSpec());

	        for (String key:specMap.keySet()){

	        	title+=" "+specMap.get(key);
			}

			item.setTitle(title);

			setItemValus(goods,item);

			tbItemMapper.insert(item);
		}

		}else{
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(goods,item);
			tbItemMapper.insert(item);
		}

	}

	//封装固有属性
	private void setItemValus(Goods goods,TbItem item){

		item.setGoodsId(goods.getGoods().getId());

		item.setSellerId(goods.getGoods().getSellerId());

		item.setCategoryid(goods.getGoods().getCategory3Id());

		item.setCreateTime(new Date());

		item.setUpdateTime(new Date());

		//品牌名称

		TbBrand tbBrand=tbBrandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());

		item.setBrand(tbBrand.getName());

		//分类名称

		TbItemCat itemCat=itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());

		item.setCategory(itemCat.getName());

		//商家名称

		TbSeller tbSeller=tbSellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());

		item.setSeller(tbSeller.getNickName());

		//图片地址

		List<Map> imagelist=JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);


		if (imagelist.size()>0){

			item.setImage((String) imagelist.get(0).get("url"));

		}

	}



	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		goodsMapper.updateByPrimaryKey(goods.getGoods());

		tbItemMapper.deleteByPrimaryKey(goods.getGoods().getId());




	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);

			goods.setIsDelete("1");

			goodsMapper.updateByPrimaryKey(goods);

		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();

		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteIsNull();
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);

			goods.setAuditStatus(status);

			goodsMapper.updateByPrimaryKey(goods);
		}

	}


	//添加索引库

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status){
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdNotIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);
		return tbItemMapper.selectByExample(example);
	}



}
