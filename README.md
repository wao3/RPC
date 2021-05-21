# 一个小型 RPC 框架。

- 网络IO使用 Netty 框架。
- 使用 Zookeeper 作为注册中心，提供服务注册与服务发现功能。
- 使用随机负载均衡策略。
- 支持服务版本号，提供一个接口多个版本实现功能。
- 使用 protostuff 作为序列化和反序列化

# 使用方式：

启动 zookeeper：
```bash
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.5
```

定义接口：
```java
public interface AdderService {
    int add(int a, int b);
}
```

服务端定义实现并启动服务器：
```java
public class Server {
    public static void main(String[] args) {
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(new AddServiceImpl());
        RpcServer rpcServer = new NettyRpcServer(rpcServiceConfig);
        rpcServer.start();
    }
}

class AddServiceImpl implements AdderService{
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
```

客户端调用 RPC 服务：
```java
public class Client {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy(new NettyRpcClient(), new RpcConfig());
        AdderService adderService = rpcClientProxy.getService(AdderService.class);
        int result = adderService.add(1, 2);
        System.out.println(result); // 将会输出结果 3
    }
}
```
# Licence
MIT Licence