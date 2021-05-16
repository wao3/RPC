package cn.wao3.rpc.registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
public class ZkUtils {

    public static final String ROOT_PATH = "/wa-rpc";
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static volatile ZkUtils instance;

    public static final Supplier<CuratorFramework> zkClientSupplier;

    static {
        AtomicReference<CuratorFramework> zkClient = new AtomicReference<>();
        zkClientSupplier = () -> {
            if (zkClient.get() != null && zkClient.get().getState() == CuratorFrameworkState.STARTED) {
                return zkClient.get();
            }
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
            zkClient.set(CuratorFrameworkFactory.builder()
                    .connectString(DEFAULT_ZOOKEEPER_ADDRESS)
                    .retryPolicy(retryPolicy)
                    .build());
            zkClient.get().start();
            try {
                // wait 30s until connect to the zookeeper
                if (!zkClient.get().blockUntilConnected(30, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Time out waiting to connect to ZK!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return zkClient.get();
        };
    }

    private ZkUtils() {
    }

    public static void createPersistentNode(String path) {
        path = ROOT_PATH + "/" + path;
        var zkClient = zkClientSupplier.get();
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    public static List<String> getChildrenNodes(String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        var zkClient = zkClientSupplier.get();
        List<String> result = null;
        String servicePath = ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    private static void registerWatcher(String rpcServiceName) throws Exception {
        var zkClient = zkClientSupplier.get();
        String servicePath = ROOT_PATH + "/" + rpcServiceName;
        CuratorCache curatorCache = CuratorCache.build(zkClient, servicePath);
        CuratorCacheBuilder builder = CuratorCache.builder(zkClient, servicePath);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forPathChildrenCache(servicePath, zkClient, pathChildrenCacheListener)
                .build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
    }

    public static void clearRegistry(InetSocketAddress inetSocketAddress) {
        var zkClient = zkClientSupplier.get();
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }
}
