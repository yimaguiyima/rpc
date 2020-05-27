package com.zmc.rpc.server;

import com.zmc.rpc.client.SomeService;

@RpcService(SomeService.class)
public class SomeServiceImpl implements SomeService {
    @Override
    public String heartBeat(String name) {
        System.out.println("接收到客户端" + name + "的心跳，正常连接………………");
        return "心跳成功！";
    }
}
