package cn.wao3.rpc.loadbalance.impl;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangao
 * @date 2021-05-23
 */
public class RoundLoadBalance extends AbstractLoadBalance {

    private final Map<String, AtomicInteger> lastIndexMap = new ConcurrentHashMap<>();

    @Override
    protected String select(List<String> hostList, RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        lastIndexMap.putIfAbsent(rpcServiceName, new AtomicInteger(0));
        int lastIndex = lastIndexMap.get(rpcServiceName).getAndUpdate(index -> (index + 1) % hostList.size());
        return hostList.get(lastIndex);
    }
}
