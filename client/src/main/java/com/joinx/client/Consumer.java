package com.joinx.client;

import com.joinx.service.OnlyService;

/**
 * @Author
 * @Date
 * @Description
 */
public class Consumer {
   public static void main(String[] args) {
         OnlyService service = RpcProxy.create(OnlyService.class);
         System.out.println(service.hello("开课吧"));
         System.out.println(service.hashCode());
      }
}
