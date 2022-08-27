package com.hj;

import com.hj.rpc.hjRpcFactory;
import com.hj.service.UserService;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.rmi.NotBoundException;

//���Ի����Զ���RPC��ܵĿͻ���
public class TestRpcClient {
    public static void main(String[] args) {
        //ͨ���Զ����ܣ�����zk����ȡ�ӿڵĶ�̬�������
        try {
            UserService userService = hjRpcFactory.getServiceProxy(UserService.class);
            System.out.println(userService.getClass().getName());
            String result = userService.getUser("����Ա");
            System.out.println("Զ�̷��񷵻ز�ѯ�����" + result);
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
