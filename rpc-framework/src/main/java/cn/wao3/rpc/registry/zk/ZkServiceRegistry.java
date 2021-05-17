package cn.wao3.rpc.registry.zk;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // inetSocketAddress.toString() 本身就带有斜杠前缀，如"/127.0.0.1:8080"
        ZkUtils.createPersistentNode(rpcServiceName + inetSocketAddress);
    }

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
