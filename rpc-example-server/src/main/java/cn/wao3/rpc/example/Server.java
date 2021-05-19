package cn.wao3.rpc.example;

import cn.wao3.rpc.config.RpcServiceConfig;
import cn.wao3.rpc.net.RpcServer;
import cn.wao3.rpc.net.socket.SocketRpcServer;

/**
 * @author wangao
 * @date 2021-05-19
 */
public class Server {
    public static void main(String[] args) {
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(new AddServiceImpl());
        rpcServiceConfig.setPort(8080);
        rpcServiceConfig.setGroup("testGroup");
        rpcServiceConfig.setVersion("1");
        RpcServer rpcServer = new SocketRpcServer(rpcServiceConfig);
        rpcServer.start();
    }
}
