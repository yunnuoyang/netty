package com.joinx.sever;

public class RpcServerStarter {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.publish("com.joinx.service");
        server.start();
    }
}
