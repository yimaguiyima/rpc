package com.zmc.rpc.registry;

import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

public class ZKDistributeLockTest {
    public static void main(String[] args) {
        // 开启3个线程模拟分布式环境，分布式环境下每个进程都是一个单独的zkClient
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread t1 = new Thread(new TestThread(countDownLatch));
        Thread t2 = new Thread(new TestThread(countDownLatch));
        Thread t3 = new Thread(new TestThread(countDownLatch));
        t1.start();
        t2.start();
        t3.start();
        System.out.println("1秒后开始竞争资源" + System.currentTimeMillis());
        try {
            Thread.sleep(1000);
            countDownLatch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    static class TestThread implements Runnable{

        private static Integer CNT = 0;
        CountDownLatch countDownLatch;
        ZkClient zkClient;
        public TestThread(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
        }

        public void connect(){
            zkClient = new ZkClient("192.168.1.15:2181",5000);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            connect();
            while(true){
                try {
                    System.out.println(threadName + " 开始竞争锁...");
                    //创建临时节点
                    zkClient.createEphemeral("/dl", "test");
                    System.out.println(threadName + " 获得锁！！！");
                    // 获得锁后修改共享变量
                    CNT ++;
                    Thread.sleep(2000);
                    System.out.println(threadName + " 释放了锁..." + CNT);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                zkClient.delete("/dl");
            }
        }
    }
}
