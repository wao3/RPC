package cn.wao3.rpc.net.socket;

import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;
import cn.wao3.rpc.common.exception.RpcException;
import cn.wao3.rpc.config.RpcServiceConfig;
import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.net.RpcRequestHandler;
import cn.wao3.rpc.net.RpcServer;
import cn.wao3.rpc.registry.ServiceProvider;
import cn.wao3.rpc.registry.zk.ZkServiceProvider;
import cn.wao3.rpc.serialize.ProtostuffSerializer;
import cn.wao3.rpc.serialize.Serializer;
import cn.wao3.rpc.utils.SingletonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author wangao
 * @date 2021-05-19
 */
public class SocketRpcServer implements RpcServer {

    private final RpcServiceConfig rpcServiceConfig;
    private final Executor threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final ThreadLocal<Serializer> serializer = ThreadLocal.withInitial(ProtostuffSerializer::new);

    public SocketRpcServer(RpcServiceConfig rpcServiceConfig) {
        ServiceProvider serviceProvider = SingletonUtil.getInstance(ZkServiceProvider.class);
        this.rpcServiceConfig = rpcServiceConfig;
        serviceProvider.publishService(rpcServiceConfig);
    }

    @Override
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(rpcServiceConfig.getPort());
            Socket socket = serverSocket.accept();
            handleRequest(socket);
        } catch (IOException e) {
            throw new RpcException(RpcExceptionMessageEnums.RPC_SERVER_ERROR);
        }
    }

    private void handleRequest(Socket socket) {
        threadPool.execute(() -> {
            try(InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()) {
                byte[] body = SocketUtil.readBody(inputStream);
                RpcRequest rpcRequest = serializer.get().deserialize(body, RpcRequest.class);
                RpcResponse rpcResponse = RpcRequestHandler.handle(rpcRequest);
                byte[] resBody = serializer.get().serialize(rpcResponse);
                byte[] resBytes = SocketUtil.packRequest(resBody);
                outputStream.write(resBytes);
                outputStream.flush();
            } catch (IOException e) {
                throw new RpcException(RpcExceptionMessageEnums.RPC_SERVER_ERROR);
            }
        });
    }
}
