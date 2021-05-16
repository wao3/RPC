package cn.wao3.rpc.registry.zk;

import cn.wao3.rpc.registry.ServiceRegistry;

import java.net.InetSocketAddress;

public class ZkServiceRegistry implements ServiceRegistry {
    @Override
    public void register(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // inetSocketAddress.toString() 本身就带有斜杠前缀，如"/127.0.0.1:8080"
        ZkUtils.createPersistentNode(rpcServiceName + inetSocketAddress);
    }
}
