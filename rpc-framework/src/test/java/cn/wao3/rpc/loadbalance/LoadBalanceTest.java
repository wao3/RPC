package cn.wao3.rpc.loadbalance;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import cn.wao3.rpc.loadbalance.impl.RandomLoadBalance;
import cn.wao3.rpc.loadbalance.impl.RoundLoadBalance;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangao
 * @date 2021-05-23
 */
public class LoadBalanceTest {

    private final List<String> hostList = Arrays.asList("1", "2", "3");

    public RpcRequest makeRpcRequest(String id) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(id);
        rpcRequest.setInterfaceName("testInterface" + id);
        rpcRequest.setParams(new Object[]{1, "a", id});
        rpcRequest.setMethodName("testMethod");
        return rpcRequest;
    }
    
    @Test
    public void randomLoadBalanceTest() {
        LoadBalance loadBalance = new RandomLoadBalance();
        for (int i = 0; i < 10; i++) {
            String service = loadBalance.selectService(hostList, makeRpcRequest("1"));
            Assert.assertTrue("1".equals(service) || "2".equals(service) || "3".equals(service));
            System.out.println(service);
        }
    }

    @Test
    public void consistentHashLoadBalanceTest() {
        LoadBalance loadBalance = new ConsistentHashLoadBalance();

        String service1 = loadBalance.selectService(hostList, makeRpcRequest("1"));
        System.out.println(service1);
        for (int i = 0; i < 10; i++) {
            String service = loadBalance.selectService(hostList, makeRpcRequest("1"));
            Assert.assertEquals(service1, service);
        }

        String service2 = loadBalance.selectService(hostList, makeRpcRequest("2"));
        System.out.println(service2);
        for (int i = 0; i < 10; i++) {
            String service = loadBalance.selectService(hostList, makeRpcRequest("2"));
            Assert.assertEquals(service2, service);
        }

        String service3 = loadBalance.selectService(hostList, makeRpcRequest("3"));
        System.out.println(service3);
        for (int i = 0; i < 10; i++) {
            String service = loadBalance.selectService(hostList, makeRpcRequest("3"));
            Assert.assertEquals(service3, service);
        }
    }

    @Test
    public void roundLoadBalanceTest() {
        LoadBalance loadBalance = new RoundLoadBalance();
        for (int i = 0; i < 10; i++) {
            String service = loadBalance.selectService(hostList, makeRpcRequest("1"));
            String expectService = hostList.get(i % hostList.size());
            Assert.assertEquals(expectService, service);
        }
    }
}
