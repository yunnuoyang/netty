package com.joinx.sever;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @Author
 * @Date
 * @Description
 */
public class Server {
   // 注册表
   private Map<String, Object> registerMap = new HashMap<>();
   
   // 用于缓存指定包下的实现类的类名`
   // 线程安全的list
    private List<String> classCache = Collections.synchronizedList(new ArrayList<>());
   
   public void publish(String packageName) throws Exception {
   
      // 将指定包中的实现类的实例写入到classCache中
      cacheProviderClass(packageName);
      // 真正的注册
      doRegister();
     
   }
   
   private void cacheProviderClass(String packageName) {
      URL url = this.getClass().getClassLoader()
             .getResource(packageName.replaceAll("\\.","/"));
      if(url==null){
         return ;
      }
      //发布的文件的基础包
      File dir = new File(url.getFile());
      for (File f:dir.listFiles()){
         if (f.isDirectory()) {
            cacheProviderClass(packageName+"."+f.getName());
         }else if(f.getName().endsWith(".class")){
            String fileName = f.getName().replace(".class", "").trim();
            classCache.add(packageName + "." + fileName);
         }
      }
   }
   
   private void doRegister() throws Exception {
      if (classCache.size() == 0) {
         return;
      }
   
      // 遍历缓存中的所有类
      for(String className : classCache) {
         Class<?> clazz = Class.forName(className);
         // 获取接口名
         String interfaceName = clazz.getInterfaces()[0].getName();
         registerMap.put(interfaceName, clazz.newInstance());
      }
   }
   
   // 启动Netty Server
   public void start() throws InterruptedException {
      EventLoopGroup parentGroup = new NioEventLoopGroup();
      EventLoopGroup childGroup = new NioEventLoopGroup();
      try {
         ServerBootstrap bootstrap = new ServerBootstrap();
         bootstrap.group(parentGroup, childGroup)
                // 用于指定，当服务端的请求处理线程全部用完时，
                // 临时存放已经完成了三次握手的请求的队列的最大长度。
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 指定是否启用心跳机制来维护长连接的不被清除
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 指定要创建Channel的类型
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                   @Override
                   protected void initChannel(SocketChannel ch) throws Exception {
                      ChannelPipeline pipeline = ch.pipeline();
                      // 添加编码器
                      pipeline.addLast(new ObjectEncoder());
                      // 添加解码器
                      pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
                             ClassResolvers.cacheDisabled(null)));
                      // 添加自定义处理器
                      pipeline.addLast(new RpcServerHandler(registerMap));
                   }
                });
         ChannelFuture future = bootstrap.bind(8888).sync();
         System.out.println("微服务已经注册完成。。。");
         future.channel().closeFuture().sync();
      } finally {
         parentGroup.shutdownGracefully();
         childGroup.shutdownGracefully();
      }
   }
}
