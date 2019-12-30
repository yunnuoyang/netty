package com.joinx.sever;

import com.joinx.dto.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

/**
 * @Author
 * @Date
 * @Description
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<Invocation> {
   private Map<String, Object> registerMap;
   
   public RpcServerHandler(Map<String, Object> registerMap) {
      this.registerMap = registerMap;
   }
   
   @Override
   protected void channelRead0(ChannelHandlerContext ctx, Invocation msg) throws Exception {
      Object result = "没有指定的提供者";
      if (registerMap.containsKey(msg.getClassName())) {
         Object provider = registerMap.get(msg.getClassName());
         result = provider.getClass()
                .getMethod(msg.getMethodName(), msg.getParamTypes())
                .invoke(provider, msg.getParamValues());
      }
      ctx.writeAndFlush(result);
      ctx.close();
   }
   
   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      cause.printStackTrace();
      ctx.close();
   }
}
