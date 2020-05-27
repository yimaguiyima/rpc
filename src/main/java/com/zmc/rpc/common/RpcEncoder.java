package com.zmc.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    // 构造函数传入向反序列化的class
    public RpcEncoder(Class<?> class1) {
        // TODO Auto-generated constructor stub
        this.genericClass = class1;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        if(genericClass.isInstance(o)){
            byte[] bytes =SerializationUtil.serialize(o);
            //byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            channelHandlerContext.flush();
        }
    }
}
