package com.zmc.rpc.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        RpcRequest po = new RpcRequest();
        po.setClassName("zmc");
        po.setMethodName("test");
        byte[] bytes = serialize(po);
        System.out.println(bytes.length);
        RpcRequest newPo = deserialize(bytes,RpcRequest.class);
        System.out.println(newPo);

    }

    public static <T> byte[] serialize(T po) {
        Class<T> cls = (Class<T>)po.getClass();
        Schema<T> schema = getSchema(cls);
        final byte[] bytes = ProtostuffIOUtil.toByteArray(po, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        return bytes;
    }

   private static Map<Class<?>,Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>,Schema<?>>();

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<?> schema = cachedSchema.get(cls);
        if(cls == null){
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls,schema);
        }
        return RuntimeSchema.createFrom(cls);
    }

    public static <T> T deserialize(byte[] bytes,Class<T> genericClass) {
        Schema<T> poSchema = getSchema(genericClass);
        T po = poSchema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, po, poSchema);
        return po;
    }
}
