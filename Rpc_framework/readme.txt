框架核心：
  服务端启动的时候，提供默认的服务端口号 。如：9090
  服务端启动的时候，必须指定Zookeeper所在位置。 也就是zk的ip和端口。
  服务端启动的时候，提供要发布的服务对象，就是Remote接口的实现。
  服务端启动的时候，RMI的URI地址，由固定的逻辑拼接，不是随机自定义。
    如：Remote接口的实现是com.hj.service.impl.UserServiceImpl。接口是UserService
        URI自动拼接为： rmi://ip:port/UserService
  服务端启动的时候，服务端的IP由启动代码指定，或自动获取。

  客户端启动的时候，必须指定Zookeeper所在位置。
  客户端启动的时候，必须提供要创建代理的接口类型。
    如： com.hj.service.UserService
  自动连接zk，拼接一个有规则的节点名称， 如： /hj/com.hj.service.UserService
    节点中保存的数据data，就是远程服务的RMI地址。 如： rmi://ip:port/UserService
  客户端使用的代理对象，是由规则生成的，不再需要客户端开发者来记忆RMI地址。