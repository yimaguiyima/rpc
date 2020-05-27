package com.zmc.rpc.registry;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ServiceRegistry.class);
    private ZkClient zkClient = null;
    public ServiceRegistry(String connectString) {
        zkClient = new ZkClient(connectString,Constant.ZK_SESSION_TIMEOUT);
        System.out.println("ServiceRegistry");
    }

    public void register(String data) {
        if(data != null){
            if(zkClient != null){
                createNode(zkClient,data);
            }
        }
    }

    //create node
    private void createNode(ZkClient zk, String data) {
        try {
            if(!zk.exists(Constant.ZK_DATA_PATH)){
                zk.createPersistent(Constant.ZK_DATA_PATH);
                System.out.println("zmc sub");
            }
            zk.writeData(Constant.ZK_DATA_PATH,data);
            System.out.println(zk.readData(Constant.ZK_DATA_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
