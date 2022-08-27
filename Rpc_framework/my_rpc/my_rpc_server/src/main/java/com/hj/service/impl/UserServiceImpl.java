package com.hj.service.impl;

import com.hj.service.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//�û�����ʵ����
public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    public UserServiceImpl() throws RemoteException{}
    public String getUser(String name) throws RemoteException {
        System.out.println("Ҫ��ѯ���û��ǣ�" + name);
        return "{\"name\":\""+name+"\",\"age\":20,\"gender\",\"��\"}";
    }
}
