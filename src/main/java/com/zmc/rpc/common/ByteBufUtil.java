package com.zmc.rpc.common;

import io.netty.buffer.ByteBuf;

public class ByteBufUtil {
    public static byte[] readByteBuffer(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
