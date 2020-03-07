package com.example.rpc.client;

import com.example.rpc.common.HelloService;

/**
 * @author iceWang
 * @date 2020/3/7
 * @description
 */
public class ClientBootstrap {
    private static final String providerName = "HelloService#hello#";

    public static void main(String[] args) {
        Client client = new Client();
        HelloService service = (HelloService) client.getBean(HelloService.class, providerName);

        //通过代理对象调用服务提供者的方法(服务)
        String res = service.sayHello("你好 dubbo~");
        System.out.println("调用的结果 res= " + res);

    }
}
