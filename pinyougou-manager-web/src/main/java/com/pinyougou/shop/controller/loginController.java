package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class loginController {

    @RequestMapping("/name.do")
    public Map name(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map getname=new HashMap();

        getname.put("loginname",name);

        return getname;

    }


}
