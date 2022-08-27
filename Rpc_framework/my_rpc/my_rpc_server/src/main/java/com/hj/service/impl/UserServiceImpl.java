package com.hj.service.impl;

import com.hj.service.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//用户服务实现类
public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    public UserServiceImpl() throws RemoteException{}
    public String getUser(String name) throws RemoteException {
        System.out.println("要查询的用户是：" + name);
        return "{\"name\":\""+name+"\",\"age\":20,\"gender\",\"男\"}";
    }
}
