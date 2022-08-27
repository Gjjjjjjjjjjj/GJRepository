package com.hj.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CustomerService extends Remote {
    String getCustomer(String name) throws RemoteException;
    int addCustomer(String name) throws RemoteException;
}
