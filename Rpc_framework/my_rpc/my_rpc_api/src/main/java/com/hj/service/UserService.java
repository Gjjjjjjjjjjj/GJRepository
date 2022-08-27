package com.hj.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

//定义一个服务接口
public interface UserService extends Remote {
    //根据用户名查询用户。返回一个JSON格式的字符串，用于描述用户对象。
    String getUser(String name) throws RemoteException;
}