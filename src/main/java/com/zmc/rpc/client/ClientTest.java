package com.zmc.rpc.client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-client.xml")
public class ClientTest {
    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest() {
        // 调用代理的create方法，代理HelloService接口
        SomeService someService = (SomeService)rpcProxy.getService(SomeService.class);

        // 调用代理的方法，执行invoke
        String ret = someService.heartBeat("wilson");
       // System.out.println(ret);
    }
}
