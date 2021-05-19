package cn.wao3.rpc.registry.zk;

import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;
import cn.wao3.rpc.common.exception.RpcException;
import cn.wao3.rpc.config.RpcServiceConfig;
import cn.wao3.rpc.registry.ServiceProvider;
import cn.wao3.rpc.registry.ServiceRegistry;
import cn.wao3.rpc.utils.SingletonUtil;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZkServiceProvider implements ServiceProvider {

    /**
     * key: rpc service name(interface name + version + group)
     * value: service object
     */
    private final Map<String, Object> serviceMap;
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.serviceRegistry = SingletonUtil.getInstance(ZkServiceRegistry.class);
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        String rpcServiceName = config.getRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceRegistry.register(rpcServiceName, new InetSocketAddress("127.0.0.1", config.getPort()));
        serviceMap.put(rpcServiceName, config.getService());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (service == null) {
            throw new RpcException(RpcExceptionMessageEnums.NO_SUCH_SERVICE);
        }
        return service;
    }
}
