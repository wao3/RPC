package cn.wao3.rpc.registry;

import cn.wao3.rpc.dto.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    void register(String rpcServiceName, InetSocketAddress inetSocketAddress);

    InetSocketAddress discover(RpcRequest request);
}