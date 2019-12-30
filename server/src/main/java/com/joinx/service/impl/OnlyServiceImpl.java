package com.joinx.service.impl;

import com.joinx.service.OnlyService;

/**
 * @Author
 * @Date
 * @Description
 */
public class OnlyServiceImpl implements OnlyService {
   @Override
   public String hello(String info) {
     return "欢迎来调用服务"+info;
   }
}
