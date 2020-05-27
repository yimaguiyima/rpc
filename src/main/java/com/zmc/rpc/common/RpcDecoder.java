package com.zmc.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    // 构造函数传入向反序列化的class
    public RpcDecoder(Class<?> class1) {
        // TODO Auto-generated constructor stub
        this.genericClass = class1;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        /*
        *         if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        * */
        byte[] bytes = ByteBufUtil.readByteBuffer(byteBuf);
        Object object = SerializationUtil.deserialize(bytes,genericClass);
        if(object != null)
            list.add(object);
    }
}
