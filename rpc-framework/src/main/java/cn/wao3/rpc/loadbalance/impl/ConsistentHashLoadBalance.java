package cn.wao3.rpc.loadbalance.impl;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangao
 * @date 2021-05-23
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final Map<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected String select(List<String> hostList, RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        int hostListHashCode = hostList.hashCode();
        if (selector == null || selector.hostListHashCode != hostListHashCode) {
            selector = new ConsistentHashSelector(hostList, hostListHashCode);
            selectors.put(rpcServiceName, selector);
        }
        return selector.select(rpcServiceName + "#" + request.getMethodName() + Arrays.stream(request.getParams()).reduce("", (a, b) -> a + "," + b));
    }

    private static class ConsistentHashSelector {
        private final TreeMap<Integer, String> hashCycle = new TreeMap<>();
        private final int hostListHashCode;

        public ConsistentHashSelector(List<String> hostList, int virtualNodeSize, int hostListHashCode) {
            this.hostListHashCode = hostListHashCode;
            for (String host : hostList) {
                for (int i = 0; i < virtualNodeSize; i++) {
                    String virtualNode = host + "#" + i;
                    hashCycle.put(hash(virtualNode), host);
                }
            }
        }

        public ConsistentHashSelector(List<String> hostList, int hostListHashCode) {
            this(hostList, 100, hostListHashCode);
        }

        public static int hash(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
            md.update(bytes);
            byte[] digest = md.digest();

            return ((int) digest[0] << 24)
                    | ((int) digest[1] << 16)
                    | ((int) digest[2] << 8)
                    | ((int) digest[3]);
        }

        public String select(String selectKey) {
            Map.Entry<Integer, String> entry = hashCycle.tailMap(hash(selectKey), true).firstEntry();
            if (entry == null) {
                entry = hashCycle.firstEntry();
            }
            if (entry == null) {
                return null;
            }
            return entry.getValue();
        }
    }
}
