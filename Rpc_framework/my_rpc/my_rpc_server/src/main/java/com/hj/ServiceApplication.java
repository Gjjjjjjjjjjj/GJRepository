package com.hj;

import com.hj.rpc.hjRpcFactory;
import com.hj.service.UserService;
import com.hj.service.impl.UserServiceImpl;

import java.rmi.RemoteException;

//·þÎñ¶ËÆô¶¯
public class ServiceApplication {
    public static void main(String[] args) throws Exception {
        Class.forName("com.hj.rpc.hjRpcFactory");
    }
}
