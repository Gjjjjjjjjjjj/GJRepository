package com.hj.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

//����һ������ӿ�
public interface UserService extends Remote {
    //�����û�����ѯ�û�������һ��JSON��ʽ���ַ��������������û�����
    String getUser(String name) throws RemoteException;
}