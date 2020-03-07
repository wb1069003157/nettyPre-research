package com.example.rpc.common;

/**
 * @author iceWang
 * @date 2020/3/7
 * @description
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String message) {
        if (message != null) {
            return message + "received!!!";
        } else {
            return "you are dead!!!";
        }
    }
}
