package cn.wao3.rpc.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    void register(String rpcServiceName, InetSocketAddress inetSocketAddress);
}