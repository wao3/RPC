package cn.wao3.rpc.registry;

import cn.wao3.rpc.dto.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress discover(RpcRequest request);
}
