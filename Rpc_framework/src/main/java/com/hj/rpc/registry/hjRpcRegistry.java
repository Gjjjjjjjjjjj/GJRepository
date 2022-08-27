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

//ע��������
//ͨ��zk���Ӷ��󣬺ʹ����Remote�ӿ�ʵ�ֶ������RMI��ַ��ƴ�ӣ��ͱ��棨������zk�У�
//ȱ��LocateRegistry����ȱ�ٵ�ǰ���������Ը�ֵ���̣��������̣�ȱ��zkconnection�Ĵ�������
public class hjRpcRegistry {
    //���Ӷ���
    private zkConnection connection;
    private String ip;
    private int port;
    /**
     * ע�����ķ���
     * 1��ƴ��RMI�ĵ�ַURI
     * 2���ѷ��ʵ�ַURI�洢��zookeeper��
     * @param serviceInterface-����ӿ���Ķ�����com.hj.service.UserService.class
     *                        �ӿڱ�����Remote�ӿڵ��ӽӿ�
     * @param remote-f����ʵ�����͵Ķ����磺new com.hj.service.impl.UserServiceImpl
     *              ʵ�����ͣ�����ʵ��serviceInterface,����Remote�ӿ�ֱ�ӻ���ʵ����
     * @throws Exception �׳��쳣����ע��ʧ��
     */

    public void registerService(Class<? extends Remote> serviceInterface, Remote remote) throws Exception {
        //rmi = rmi://ip:port/com.hj.service.UserService
        String rmi = "rmi://" + ip + ":" + port + "/" + serviceInterface.getName();
        //ƴ��һ���й����zk�洢�ڵ�����
        String path = "/hj/rpc" + serviceInterface.getName();
        //����ڵ���ھ�ɾ���ؽ�
        List<String> children = connection.getConnection().getChildren("/hj/rpc", false);
        if(children.contains(serviceInterface.getName())){
            //�ڵ������Ҫɾ��
            Stat stat = new Stat();
            connection.getConnection().getData(path, false, stat);
            connection.getConnection().delete(path, stat.getCversion());
        }
        connection.getConnection().create(path, rmi.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //�ѷ��������RMI��Registry��ע��
        Naming.rebind(rmi, remote);
    }

    /**
     * ���ݷ���ӿ����ͣ�����zk,��ȡRMI��Զ�̴������
     * 1,ƴ��һ��zk�еĽڵ�����
     * 2������zk����ѯ�ڵ��д洢������
     * 3�����ݲ�ѯ�Ľ��������һ���������
     * @return
     */
    public <T extends Remote> T getServiceProxy(Class<T> serviceInterface) throws IOException, KeeperException, InterruptedException, NotBoundException {
        //ƴ��zk�еĽڵ�����
        String path = "/hj/rpc" + serviceInterface.getName();
        //��ѯ�洢�ڽڵ��е�����
        byte[] data = connection.getConnection().getData(path, false, null);
        //�Ѳ�ѯ�����ֽ����顣�����RMI�ķ��ʵ�ַ
        String rmi = new String(data);
        //�����������
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
