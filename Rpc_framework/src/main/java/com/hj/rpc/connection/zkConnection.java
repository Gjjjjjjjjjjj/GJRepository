package com.hj.rpc.connection;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

//提供zookeeper连接的自定义类型
public class zkConnection {
    //保存zk的地址，格式是ip:port, 如：192.168.10.202:2181
    private String zkServer;
    //保存会话的超时时间
    private int sessionTimeout;

    public zkConnection(){
        this.zkServer = "localhost:2181";
        this.sessionTimeout = 100000;
    }

    public zkConnection(String zkServer, int sessionTimeout) {
        this.zkServer = zkServer;
        this.sessionTimeout = sessionTimeout;
    }

    public ZooKeeper getConnection() throws IOException {
        return new ZooKeeper(zkServer, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("zookeeper监听");
            }
        });
    }
}
