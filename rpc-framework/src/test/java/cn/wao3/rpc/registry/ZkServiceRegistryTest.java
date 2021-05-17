package cn.wao3.rpc.registry;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.registry.zk.ZkServiceRegistry;
import cn.wao3.rpc.registry.zk.ZkUtils;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class ZkServiceRegistryTest {

    private final ServiceRegistry serviceRegistry = new ZkServiceRegistry();

    @Test
    public void registerAndDiscover() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName("HelloService");
        rpcRequest.setGroup("Test");
        rpcRequest.setVersion("1");
        List<InetSocketAddress> inetSocketAddresses = Arrays.asList(
                new InetSocketAddress("127.0.0.1", 8888),
                new InetSocketAddress("127.0.0.2", 8888),
                new InetSocketAddress("127.0.0.3", 8888),
                new InetSocketAddress("127.0.0.4", 8888));
        inetSocketAddresses.forEach(inetSocketAddress -> serviceRegistry.register(rpcRequest.getRpcServiceName(), inetSocketAddress));
        for (int i = 0; i < 10; i++) {
            InetSocketAddress discover = serviceRegistry.discover(rpcRequest);
            System.out.println(discover);
        }
        inetSocketAddresses.forEach(ZkUtils::clearRegistry);
    }
}
