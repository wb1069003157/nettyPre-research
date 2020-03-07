package com.example.rpc.server;

/**
 * @author iceWang
 * @date 2020/3/7
 * @description
 */
public class ServerBootstrap {
    public static void main(String[] args) {
        Server.startServer("127.0.0.1",7000);
    }
}
