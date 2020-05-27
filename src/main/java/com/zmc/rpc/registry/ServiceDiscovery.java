package com.zmc.rpc.registry;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ServiceDiscovery.class);
    private String registryAddress;
    //缓存节点信息
    List<String> serverList = null;
    private CountDownLatch latch = new CountDownLatch(1);
    ;
    public ServiceDiscovery(String registryAddress){
        this.registryAddress = registryAddress;
        //建立连接
        try {
            ZkClient zk = new ZkClient(registryAddress, Constant.ZK_SESSION_TIMEOUT);
            watchNode(zk,Constant.ZK_REGISTRY_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//        public static void main(String[] args) throws InterruptedException {
//        String connectString = "192.168.1.15:2181";
//        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(connectString);
//        System.out.println(serviceDiscovery.discover());
////        ZkClient zk = new ZkClient(connectString,5000);
////        zk.createPersistent(Constant.ZK_DATA_PATH,true);
////        System.out.println();
//    }
    // 建立连接
    public ZkClient connectZookeeper() throws Exception {
        ZkClient zk = new ZkClient(registryAddress, Constant.ZK_SESSION_TIMEOUT);
        return zk;
    }
    private void watchNode(ZkClient zk,String path){
        if(zk != null && !path.isEmpty()){
            List<String> nodeList = zk.subscribeChildChanges(path, new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    //serverList = list;
                    System.out.println(list);
                }
            });

            List<String> dataList = new ArrayList<String>();
            for(String node:nodeList){
                String data = zk.readData(Constant.ZK_REGISTRY_PATH + "/" + node);
                dataList.add(data);
            }
            this.serverList = dataList;
        }
    }

    private void watchNode(final ZooKeeper zk) {
        //获取所有子节点
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                        watchNode(zk);
                    }
                }
            });
            List<String> dataList = new ArrayList<String>();
            for(String node:nodeList){
                byte[] data = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node,false,null);
                dataList.add(data.toString());
            }
            LOGGER.debug("node data: {}", dataList);
            // 将节点信息记录在成员变量
            this.serverList = dataList;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //负载均衡
    public String discover() {
        //根据负载情况，返回 节点
        String data = null;
        int size = serverList.size();
        if(size > 0){
            if(size == 1){
                data = serverList.get(0);
            }else{
                data = serverList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }

}
