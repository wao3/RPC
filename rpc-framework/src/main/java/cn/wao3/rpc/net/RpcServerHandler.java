package cn.wao3.rpc.net;

import cn.wao3.rpc.common.exception.RpcException;
import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.registry.ServiceProvider;
import cn.wao3.rpc.registry.zk.ZkServiceProvider;
import cn.wao3.rpc.utils.SingletonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcServerHandler {
    private static final ServiceProvider serviceProvider = SingletonUtil.getInstance(ZkServiceProvider.class);

    public static RpcResponse handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private static RpcResponse invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParams());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return RpcResponse.success(result, rpcRequest.getRequestId());
    }
}
