package com.hj.rpc;

import com.hj.rpc.connection.zkConnection;
import com.hj.rpc.registry.hjRpcRegistry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Properties;

//������
public class hjRpcFactory {
    //���ڱ���������Ϣ
    private static final Properties config = new Properties();
    //���Ӷ���
    private static final zkConnection connection;
    //ע��������
    private static final hjRpcRegistry registry;
    //���ڶ�ȡ��ʼ�������ö���
    private static final Properties services = new Properties();
    /**
     * ��ʼ������
     * �̶��߼�����classpath�£��ṩ�����ļ�������Ϊ��hj.properties
     * �����ļ��ṹ�̻���
     *  registry.ip=������IP��ַ��Ĭ��Ϊlocalhost
     *  registry.port=����˶˿ںţ�Ĭ��Ϊ9090
     *  zk.server=Zookeeper���ʵ�ַ��Ĭ��Ϊlocalhost:2181
     *  zk.sessionTimeout=Zookeeper���ӻỰ��ʱ��Ĭ��Ϊ10000
     */
    static {
        try {
            //��ȡclasspath��·���µ������ļ�������
            InputStream input = hjRpcFactory.class.getClassLoader().getResourceAsStream("hj.properties");
            //��ȡ�����ļ�����ʼ������
            config.load(input);
            //��ȡ�����ip
            String serverIp = config.getProperty("registry.ip") == null ? "localhost" : config.getProperty("registry.ip");
            //��ȡ����˶˿ں�
            int serverPort = config.getProperty("registry.port") == null ?
                    9090 : Integer.parseInt(config.getProperty("registry.port"));
            //��ȡzookeeper��������ַ
            String zkServer = config.getProperty("zk.server") == null ? "localhost:2181" : config.getProperty("zk.server");
            //��ȡzookeeper���ӻỰ��ʱʱ��
            int zkSessionTimeout = config.getProperty("zk.sessionTimeout") == null ?
                    10000 : Integer.parseInt(config.getProperty("zk.sessionTimeout"));
            //�������Ӷ���
            connection = new zkConnection(zkServer,zkSessionTimeout);
            //����ע��������
            registry = new hjRpcRegistry();
            //��ʼ��ע������������
            registry.setIp(serverIp);
            registry.setConnection(connection);
            registry.setPort(serverPort);
            //����һ��RMI��ע����
            LocateRegistry.createRegistry(serverPort);
            //��ʼ��zk�еĸ��ڵ�/hj/rpc
            List<String> children = connection.getConnection().getChildren("/",false);
            //�������ӽڵ�/hj
            if(!children.contains("hj")){
                //�����ڵ�/hj
                connection.getConnection().create("/hj",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            List<String> hjChildren = connection.getConnection().getChildren("/hj",false);
            if(!hjChildren.contains("rpc")){
                connection.getConnection().create("/hj/rpc",null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
            //�ж���classpath�£��Ƿ���һ�������ļ�������Ϊ��hj-services.properties
            //�����������ã����Զ���ʼ����û�к��Ժ����߼�
            //�����ļ��ĸ�ʽ�ǣ��ӿ�ȫ����=ʵ����ȫ����
            InputStream servicesInout = hjRpcFactory.class.getClassLoader().getResourceAsStream("hj-services.properties");
            if(servicesInout != null){
                //�����ã���ʼ��
                services.load(servicesInout);
                //��������services
                for (Object key: services.keySet()) {
                    //ͨ��key��ѯvalue
                    Object value = services.get(key);
                    //key�ǽӿڵ�ȫ������value��ʵ�����ȫ����
                    Class<Remote> serviceInterface = (Class<Remote>) Class.forName(key.toString());
                    Remote serviceObject = (Remote) Class.forName(value.toString()).newInstance();
                    //�и��ӿڵ������ͷ���Ķ���ע��
                    registry.registerService(serviceInterface,serviceObject);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            //����ʼ������鷢���쳣���⣬�׳������ж������
            throw new ExceptionInInitializerError(e);
        }
    }

    //�ṩһ������ע�����ʹ����ͻ��˴������ľ�̬���߷���
    public static void registerSercice(Class<? extends Remote> serviceInterface,Remote remote) throws IOException, Exception, KeeperException {
        registry.registerService(serviceInterface,remote);
    }

    //�ṩһ�����ٻ�ȡ�������ľ�̬���߷���
    public static <T extends Remote> T getServiceProxy(Class<T> serviceInterface) throws IOException, InterruptedException, KeeperException, NotBoundException, NotBoundException, IOException, KeeperException {
        return registry.getServiceProxy(serviceInterface);
    }
}
