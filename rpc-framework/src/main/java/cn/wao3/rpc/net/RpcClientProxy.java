package cn.wao3.rpc.net;

import cn.wao3.rpc.config.RpcConfig;
import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RequestSender requestSender;
    private final RpcConfig rpcConfig;

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .params(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcConfig.getGroup())
                .version(rpcConfig.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = requestSender.sendRequest(rpcRequest);
        return rpcResponse.getData();
    }
}
