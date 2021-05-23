package cn.wao3.rpc.loadbalance;

import cn.wao3.rpc.dto.RpcRequest;

import java.util.List;

/**
 * @author wangao
 * @date 2021-05-23
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectService(List<String> hostList, RpcRequest request) {
        if (hostList == null || hostList.size() == 0) {
            return null;
        }
        if (hostList.size() == 1) {
            return hostList.get(0);
        }
        return select(hostList, request);
    }

    protected abstract String select(List<String> hostList, RpcRequest request);
}
