package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojogroup.Cart;

import java.util.List;

@Service

public class CartServiceImpl implements CartService {
    @Reference
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {



        return null;
    }
}
