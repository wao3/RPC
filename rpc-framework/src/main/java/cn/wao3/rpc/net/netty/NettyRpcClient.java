package cn.wao3.rpc.net.netty;

import cn.wao3.rpc.common.RpcConstants;
import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;
import cn.wao3.rpc.common.exception.RpcException;
import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.net.RequestSender;
import cn.wao3.rpc.net.netty.codec.RpcMessageEncoder;
import cn.wao3.rpc.net.netty.codec.RpcResponseDecoder;
import cn.wao3.rpc.net.netty.handler.RpcResponseHandler;
import cn.wao3.rpc.net.netty.util.AsyncRequestUtil;
import cn.wao3.rpc.registry.ServiceRegistry;
import cn.wao3.rpc.registry.zk.ZkServiceRegistry;
import cn.wao3.rpc.serialize.ProtostuffSerializer;
import cn.wao3.rpc.utils.SingletonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author wangao
 * @date 2021-05-20
 */
@Slf4j
public class NettyRpcClient implements RequestSender {

    private final ServiceRegistry serviceRegistry;
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public NettyRpcClient() {
        serviceRegistry = SingletonUtil.getInstance(ZkServiceRegistry.class);
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        Channel channel = getChannel(request);
        CompletableFuture<RpcResponse<Object>> requestFuture = AsyncRequestUtil.newTask(request.getRequestId());
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                future.channel().close();
                requestFuture.completeExceptionally(future.cause());
            }
        });

        try {
            return requestFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RpcException(RpcExceptionMessageEnums.RPC_REQUEST_FAILED);
        }
    }

    private Channel getChannel(RpcRequest request) {
        if (channelMap.containsKey(request.getRpcServiceName())) {
            return channelMap.get(request.getRpcServiceName());
        }
        InetSocketAddress address = serviceRegistry.discover(request);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            Channel channel = bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new RpcMessageEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(
                                    RpcConstants.MAX_LENGTH,
                                    RpcConstants.HEADER_BYTES_MAGIC + RpcConstants.HEADER_BYTES_VERSION,
                                    RpcConstants.HEADER_BYTES_LENGTH))
                                    .addLast(new RpcResponseDecoder())
                                    .addLast(new RpcResponseHandler());
                        }
                    }).connect(address.getAddress(), address.getPort())
                    .sync()
                    .channel();
            log.info("连接服务器成功");
            channelMap.putIfAbsent(request.getRpcServiceName(), channel);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channelMap.get(request.getRpcServiceName());
    }
}
