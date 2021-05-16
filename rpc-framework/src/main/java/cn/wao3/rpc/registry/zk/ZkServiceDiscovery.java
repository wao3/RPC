package cn.wao3.rpc.registry.zk;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    @Override
    public InetSocketAddress discover(RpcRequest rpcRequest) {
        List<String> hostList = ZkUtils.getChildrenNodes(rpcRequest.getRpcServiceName());
        String host = loadBalance(hostList);
        log.info("服务地址：{}", host);
        String[] hostSplit = host.split(":");
        String ip = hostSplit[0];
        int port = Integer.parseInt(hostSplit[1]);
        return new InetSocketAddress(ip, port);
    }

    private String loadBalance(List<String> hostList) {
        if (hostList == null || hostList.isEmpty()) {
            return null;
        }
        int index = new Random().nextInt(hostList.size());
        return hostList.get(index);
    }
}
