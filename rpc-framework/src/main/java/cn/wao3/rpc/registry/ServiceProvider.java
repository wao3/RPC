package cn.wao3.rpc.registry;

import cn.wao3.rpc.config.RpcServiceConfig;

public interface ServiceProvider {

    void publishService(RpcServiceConfig config);

    Object getService(String rpcServiceName);
}
