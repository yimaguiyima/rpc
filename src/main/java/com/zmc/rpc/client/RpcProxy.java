package com.zmc.rpc.client;

import com.zmc.rpc.common.RpcRequest;
import com.zmc.rpc.common.RpcResponse;
import com.zmc.rpc.registry.ServiceDiscovery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcProxy {

   private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T getService(Class<?> class1) {
        return (T)Proxy.newProxyInstance(class1.getClassLoader(), new Class<?>[]{class1}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //proxy.getClass().getInterfaces()[0]);
                //创建RpcRequest，封装被代理类的属性
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                //拿到声明这个方法的业务接口名称
                request.setClassName(method.getDeclaringClass()
                        .getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                //创建Netty实现的RpcClient，链接服务端
                RpcClient client = null;
                if(serviceDiscovery != null){
                    String serverAddress = serviceDiscovery.discover();
                    System.out.println(serverAddress);
                    String[] array = serverAddress.split(":");
                    String host = array[0] ;
                    int port = Integer.parseInt(array[1]);
                    client = new RpcClient(host, port);
                }else{
                    client = new RpcClient("localhost", 8000);
                }
                System.out.println(request.toString());
                //		通过netty向服务端发送请求
                RpcResponse response = client.send(request);
                System.out.println("response is zmc");

                //返回信息
                if (response.isError()) {
                    //throw response.isError();
                    System.out.println("response is error");
                } else {
                    return response.getResult();
                }
                return response.getResult();
            }
        });
    }
}
