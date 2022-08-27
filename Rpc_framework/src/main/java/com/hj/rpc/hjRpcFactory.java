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

//框架入口
public class hjRpcFactory {
    //用于保存配置信息
    private static final Properties config = new Properties();
    //连接对象
    private static final zkConnection connection;
    //注册器对象
    private static final hjRpcRegistry registry;
    //用于读取初始化的配置对象
    private static final Properties services = new Properties();
    /**
     * 初始化过程
     * 固定逻辑，在classpath下，提供配置文件，命名为，hj.properties
     * 配置文件结构固化：
     *  registry.ip=服务器IP地址，默认为localhost
     *  registry.port=服务端端口号，默认为9090
     *  zk.server=Zookeeper访问地址，默认为localhost:2181
     *  zk.sessionTimeout=Zookeeper连接会话超时，默认为10000
     */
    static {
        try {
            //获取classpath类路径下的配置文件输入流
            InputStream input = hjRpcFactory.class.getClassLoader().getResourceAsStream("hj.properties");
            //读取配置文件，初始化对象
            config.load(input);
            //获取服务端ip
            String serverIp = config.getProperty("registry.ip") == null ? "localhost" : config.getProperty("registry.ip");
            //获取服务端端口号
            int serverPort = config.getProperty("registry.port") == null ?
                    9090 : Integer.parseInt(config.getProperty("registry.port"));
            //获取zookeeper服务器地址
            String zkServer = config.getProperty("zk.server") == null ? "localhost:2181" : config.getProperty("zk.server");
            //获取zookeeper连接会话超时时长
            int zkSessionTimeout = config.getProperty("zk.sessionTimeout") == null ?
                    10000 : Integer.parseInt(config.getProperty("zk.sessionTimeout"));
            //创建连接对象
            connection = new zkConnection(zkServer,zkSessionTimeout);
            //创建注册器对象
            registry = new hjRpcRegistry();
            //初始化注册器对象属性
            registry.setIp(serverIp);
            registry.setConnection(connection);
            registry.setPort(serverPort);
            //创建一个RMI的注册器
            LocateRegistry.createRegistry(serverPort);
            //初始化zk中的父节点/hj/rpc
            List<String> children = connection.getConnection().getChildren("/",false);
            //不存在子节点/hj
            if(!children.contains("hj")){
                //创建节点/hj
                connection.getConnection().create("/hj",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            List<String> hjChildren = connection.getConnection().getChildren("/hj",false);
            if(!hjChildren.contains("rpc")){
                connection.getConnection().create("/hj/rpc",null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
            //判断在classpath下，是否有一个配置文件，命名为：hj-services.properties
            //如果有这个配置，则自动初始化，没有忽略后续逻辑
            //配置文件的格式是：接口全命名=实现类全命名
            InputStream servicesInout = hjRpcFactory.class.getClassLoader().getResourceAsStream("hj-services.properties");
            if(servicesInout != null){
                //有配置，初始化
                services.load(servicesInout);
                //遍历集合services
                for (Object key: services.keySet()) {
                    //通过key查询value
                    Object value = services.get(key);
                    //key是接口的全命名，value是实现类的全命名
                    Class<Remote> serviceInterface = (Class<Remote>) Class.forName(key.toString());
                    Remote serviceObject = (Remote) Class.forName(value.toString()).newInstance();
                    //有个接口的类对象和服务的对象，注册
                    registry.registerService(serviceInterface,serviceObject);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            //当初始化代码块发生异常问题，抛出错误，中断虚拟机
            throw new ExceptionInInitializerError(e);
        }
    }

    //提供一个快速注册服务和创建客户端代理对象的静态工具方法
    public static void registerSercice(Class<? extends Remote> serviceInterface,Remote remote) throws IOException, Exception, KeeperException {
        registry.registerService(serviceInterface,remote);
    }

    //提供一个快速获取代理对象的静态工具方法
    public static <T extends Remote> T getServiceProxy(Class<T> serviceInterface) throws IOException, InterruptedException, KeeperException, NotBoundException, NotBoundException, IOException, KeeperException {
        return registry.getServiceProxy(serviceInterface);
    }
}
