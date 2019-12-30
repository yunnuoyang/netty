package com.joinx.dto;

/**
 * @Author
 * @Date
 * @Description
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 客户端发送给服务端的服务调用信息
 * 实现序列化接口传输信息
 */
@Data
public class Invocation implements Serializable {
   /**
    * 接口名，即服务名称
    */
   private String className;
   /**
    * 远程调用的方法名
    */
   private String methodName;
   /**
    * 方法参数类型
    */
   private Class<?>[] paramTypes;
   /**
    * 方法参数值
    */
   private Object[] paramValues;
}
