package com.zmc.rpc.server;

import com.zmc.rpc.common.RpcRequest;
import com.zmc.rpc.common.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Map;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            response.setError(throwable);
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Throwable{
        // TODO Auto-generated method stub
        String className = request.getClassName();

        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName(); //读取UTF编码的String字符串

        //读取参数类型
        Class<?>[] parameterTypes = (Class<?>[]) request.getParameterTypes();

        //读取参数
        Object[] arguments = (Object[]) request.getParameters();
        System.out.println( "class name: " + className + " method name:  " + methodName );
        //拿到接口类
        Class<?> forName = Class.forName(className);
        Method method = forName.getMethod(methodName, parameterTypes);

        System.out.println("zmc handMap " + handlerMap.size());
        //反射执行方法
        return method.invoke(serviceBean, arguments);
    }
}
