package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {



        try {
            TextMessage text=(TextMessage) message;
            String text1 = text.getText();
            List<TbItem> items = JSON.parseArray(text1, TbItem.class);

            itemSearchService.importList(items);

        } catch (JMSException e) {
            e.printStackTrace();
        }



    }
}
