package com.joinx.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author
 * @Date
 * @Description
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<Object> {
   private Object result;
   
   public Object getResult() {
      return this.result;
   }
   // msg是服务端传递来的远程调用的结果
   @Override
   protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
      this.result = msg;
   }
   
   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      cause.printStackTrace();
      ctx.close();
   }
}
