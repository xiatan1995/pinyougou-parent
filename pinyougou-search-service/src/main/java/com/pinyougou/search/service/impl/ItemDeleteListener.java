package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;


/**
 * 删除序列化
 */
@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;


        try {
            Long[] goodsids = (Long[]) objectMessage.getObject();


            itemSearchService.deleteByGoodsIds(Arrays.asList(goodsids));



        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
