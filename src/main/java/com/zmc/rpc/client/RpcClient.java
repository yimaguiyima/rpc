package com.zmc.rpc.client;

import com.zmc.rpc.common.RpcDecoder;
import com.zmc.rpc.common.RpcEncoder;
import com.zmc.rpc.common.RpcRequest;
import com.zmc.rpc.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private String host;
    private int port;
    Object obj = new Object();
    private RpcResponse response;

    public RpcClient(String localhost, int i) {
        this.host = localhost;
        this.port = i;
    }

    public RpcResponse send(RpcRequest request) {
        // TODO Auto-generated method stub
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel arg0) throws Exception {
                            // TODO Auto-generated method stub
                            // 注册编码的编码
//					arg0.pipeline().addLast(new RpcDecoder(RpcResponse.class));

                            arg0.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClient.this);//注册handler
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);

            // 链接服务器
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            //将request对象写入outbundle处理后发出（即RpcEncoder编码器）
            channelFuture.channel().writeAndFlush(request).sync();

            synchronized (obj) {
                obj.wait();
            }
            if(response != null){
                channelFuture.channel().closeFuture().sync();
            }
            return response;

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
        synchronized(obj){
            obj.notifyAll();
        }
    }
}
