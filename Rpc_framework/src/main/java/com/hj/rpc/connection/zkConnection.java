package com.hj.rpc.connection;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

//�ṩzookeeper���ӵ��Զ�������
public class zkConnection {
    //����zk�ĵ�ַ����ʽ��ip:port, �磺192.168.10.202:2181
    private String zkServer;
    //����Ự�ĳ�ʱʱ��
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
                System.out.println("zookeeper����");
            }
        });
    }
}
