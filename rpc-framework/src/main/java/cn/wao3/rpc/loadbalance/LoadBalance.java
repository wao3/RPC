package cn.wao3.rpc.loadbalance;

import cn.wao3.rpc.dto.RpcRequest;

import java.util.List;

public interface LoadBalance {
    String selectService(List<String> hostList, RpcRequest request);
}
