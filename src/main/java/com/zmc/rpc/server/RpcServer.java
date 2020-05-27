package com.zmc.rpc.server;

import com.zmc.rpc.common.RpcDecoder;
import com.zmc.rpc.common.RpcEncoder;
import com.zmc.rpc.common.RpcRequest;
import com.zmc.rpc.common.RpcResponse;
import com.zmc.rpc.registry.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RpcServer implements ApplicationContextAware,InitializingBean {
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    private static Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();

    public RpcServer(String serverAddress,ServiceRegistry serviceRegistry){
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //init bean
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        if (serviceRegistry != null) {
            serviceRegistry.register(serverAddress);
        }
        System.out.println(serviceRegistry);
        try{
            // 第二步，创建启动类
            ServerBootstrap b = new ServerBootstrap();
            // 第三步，配置各组件
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            RpcEncoder encoder = new RpcEncoder(RpcResponse.class);
                            //注册解码
                            pipeline.addLast(new RpcDecoder(RpcRequest.class));
                            pipeline.addLast(encoder);
                            pipeline.addLast(new RpcHandler(handlerMap));

                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 第四步，开启监听
            ChannelFuture f = b.bind(host,port).sync();
            System.out.println("host , port " + host + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //aware 扫描注解
        Map<String, Object> serviceBeanMap = applicationContext
                .getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                //从业务实现类上的自定义注解中获取到value，从来获取到业务接口的全名
                String interfaceName = serviceBean.getClass()
                        .getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }
}
