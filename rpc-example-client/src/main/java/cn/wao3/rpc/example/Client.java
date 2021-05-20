package cn.wao3.rpc.example;

import cn.wao3.rpc.config.RpcConfig;
import cn.wao3.rpc.net.RequestSender;
import cn.wao3.rpc.net.RpcClientProxy;
import cn.wao3.rpc.net.netty.NettyRpcClient;
import cn.wao3.rpc.net.socket.SocketRpcClient;

/**
 * @author wangao
 * @date 2021-05-19
 */
public class Client {
    public static void main(String[] args) {
        RpcConfig rpcConfig = new RpcConfig();
        rpcConfig.setGroup("testGroup");
        rpcConfig.setVersion("1");
        RequestSender requestSender = new NettyRpcClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(requestSender, rpcConfig);
        AdderService adderService = rpcClientProxy.getService(AdderService.class);
        System.out.println(adderService.add(1, 2));
    }
}
