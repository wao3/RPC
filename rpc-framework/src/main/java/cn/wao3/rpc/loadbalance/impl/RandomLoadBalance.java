package cn.wao3.rpc.loadbalance.impl;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author wangao
 * @date 2021-05-23
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String select(List<String> hostList, RpcRequest request) {
        int index = new Random().nextInt(hostList.size());
        return hostList.get(index);
    }
}
