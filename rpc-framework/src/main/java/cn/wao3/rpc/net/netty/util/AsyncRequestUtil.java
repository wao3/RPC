package cn.wao3.rpc.net.netty.util;

import cn.wao3.rpc.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author wangao
 * @date 2021-05-20
 */
public class AsyncRequestUtil {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> PENDING_REQUEST = new ConcurrentHashMap<>();

    public static void put(String requestId, CompletableFuture<RpcResponse<Object>> request) {
        PENDING_REQUEST.put(requestId, request);
    }

    public static CompletableFuture<RpcResponse<Object>> newTask(String requestId) {
        PENDING_REQUEST.put(requestId, new CompletableFuture<>());
        return PENDING_REQUEST.get(requestId);
    }

    public static void complete(RpcResponse<Object> response) {
        CompletableFuture<RpcResponse<Object>> request = PENDING_REQUEST.get(response.getRequestId());
        if (request == null) {
            return;
        }
        request.complete(response);
        PENDING_REQUEST.remove(response.getRequestId());
    }
}
