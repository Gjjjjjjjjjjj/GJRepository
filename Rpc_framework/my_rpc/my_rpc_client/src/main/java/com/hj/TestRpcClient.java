package com.hj;

import com.hj.rpc.hjRpcFactory;
import com.hj.service.UserService;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.rmi.NotBoundException;

//测试基于自定义RPC框架的客户端
public class TestRpcClient {
    public static void main(String[] args) {
        //通过自定义框架，连接zk，获取接口的动态代理对象
        try {
            UserService userService = hjRpcFactory.getServiceProxy(UserService.class);
            System.out.println(userService.getClass().getName());
            String result = userService.getUser("管理员");
            System.out.println("远程服务返回查询结果：" + result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
