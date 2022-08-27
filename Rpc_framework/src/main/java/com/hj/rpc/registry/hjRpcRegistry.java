package com.hj.rpc.registry;

import com.hj.rpc.connection.zkConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.util.List;

//注册器工具
//通过zk连接对象，和传入的Remote接口实现对象，完成RMI地址的拼接，和保存（保存在zk中）
//缺少LocateRegistry对象，缺少当前类型中属性赋值过程，整体流程，缺少zkconnection的创建过程
public class hjRpcRegistry {
    //连接对象
    private zkConnection connection;
    private String ip;
    private int port;
    /**
     * 注册服务的方法
     * 1，拼接RMI的地址URI
     * 2，把访问地址URI存储在zookeeper中
     * @param serviceInterface-服务接口类的对象，如com.hj.service.UserService.class
     *                        接口必须是Remote接口的子接口
     * @param remote-f服务实现类型的对象如：new com.hj.service.impl.UserServiceImpl
     *              实现类型，必须实现serviceInterface,且是Remote接口直接或间接实现类
     * @throws Exception 抛出异常代表注册失败
     */

    public void registerService(Class<? extends Remote> serviceInterface, Remote remote) throws Exception {
        //rmi = rmi://ip:port/com.hj.service.UserService
        String rmi = "rmi://" + ip + ":" + port + "/" + serviceInterface.getName();
        //拼接一个有规则的zk存储节点命名
        String path = "/hj/rpc" + serviceInterface.getName();
        //如果节点存在就删除重建
        List<String> children = connection.getConnection().getChildren("/hj/rpc", false);
        if(children.contains(serviceInterface.getName())){
            //节点存在需要删除
            Stat stat = new Stat();
            connection.getConnection().getData(path, false, stat);
            connection.getConnection().delete(path, stat.getCversion());
        }
        connection.getConnection().create(path, rmi.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //把服务对象，在RMI的Registry中注册
        Naming.rebind(rmi, remote);
    }

    /**
     * 根据服务接口类型，访问zk,获取RMI的远程代理对象
     * 1,拼接一个zk中的节点名称
     * 2，访问zk，查询节点中存储的数据
     * 3，根据查询的结果，创建一个代理对象
     * @return
     */
    public <T extends Remote> T getServiceProxy(Class<T> serviceInterface) throws IOException, KeeperException, InterruptedException, NotBoundException {
        //拼接zk中的节点名称
        String path = "/hj/rpc" + serviceInterface.getName();
        //查询存储在节点中的数据
        byte[] data = connection.getConnection().getData(path, false, null);
        //把查询到的字节数组。翻译成RMI的访问地址
        String rmi = new String(data);
        //创建代理对象
        Object obj = Naming.lookup(rmi);
        return (T) obj;
    }

    public zkConnection getConnection() {
        return connection;
    }

    public void setConnection(zkConnection connection) {
        this.connection = connection;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
