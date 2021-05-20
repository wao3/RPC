package cn.wao3.rpc.net.socket;

import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;
import cn.wao3.rpc.common.exception.RpcException;
import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.net.ByteUtil;
import cn.wao3.rpc.net.RequestSender;
import cn.wao3.rpc.registry.ServiceRegistry;
import cn.wao3.rpc.registry.zk.ZkServiceRegistry;
import cn.wao3.rpc.serialize.ProtostuffSerializer;
import cn.wao3.rpc.serialize.Serializer;
import cn.wao3.rpc.utils.SingletonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author wangao
 * @date 2021-05-19
 */
public class SocketRpcClient implements RequestSender {

    private final Serializer serializer;
    private final ServiceRegistry serviceRegistry;

    public SocketRpcClient() {
        // 暂时写死序列化器
        serializer = new ProtostuffSerializer();
        serviceRegistry = SingletonUtil.getInstance(ZkServiceRegistry.class);
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        byte[] bytes = serializer.serialize(request);
        byte[] requestBytes = ByteUtil.wrapBody(bytes);
        InetSocketAddress serviceHost = serviceRegistry.discover(request);
        try (Socket socket = new Socket(serviceHost.getAddress(), serviceHost.getPort());
             OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {
            outputStream.write(ByteUtil.wrapBody(bytes));
            outputStream.flush();
            byte[] body = ByteUtil.readBody(inputStream);
            return serializer.deserialize(body, RpcResponse.class);
        } catch (IOException e) {
            throw new RpcException(RpcExceptionMessageEnums.RPC_REQUEST_FAILED);
        }
    }
}
