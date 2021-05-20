package cn.wao3.rpc.net.netty;

import cn.wao3.rpc.common.RpcConstants;
import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;
import cn.wao3.rpc.common.exception.RpcException;
import cn.wao3.rpc.config.RpcServiceConfig;
import cn.wao3.rpc.net.RpcServer;
import cn.wao3.rpc.net.netty.codec.RpcMessageEncoder;
import cn.wao3.rpc.net.netty.codec.RpcRequestDecoder;
import cn.wao3.rpc.net.netty.handler.RpcRequestHandler;
import cn.wao3.rpc.registry.ServiceProvider;
import cn.wao3.rpc.registry.zk.ZkServiceProvider;
import cn.wao3.rpc.utils.SingletonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author wangao
 * @date 2021-05-20
 */
public class NettyRpcServer implements RpcServer {

    private final RpcServiceConfig rpcServiceConfig;

    public NettyRpcServer(RpcServiceConfig rpcServiceConfig) {
        ServiceProvider serviceProvider = SingletonUtil.getInstance(ZkServiceProvider.class);
        this.rpcServiceConfig = rpcServiceConfig;
        serviceProvider.publishService(rpcServiceConfig);
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            ChannelFuture channelFuture = bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new RpcMessageEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(
                                            RpcConstants.MAX_LENGTH,
                                            RpcConstants.HEADER_BYTES_MAGIC + RpcConstants.HEADER_BYTES_VERSION,
                                            RpcConstants.HEADER_BYTES_LENGTH))
                                    .addLast(new RpcRequestDecoder())
                                    .addLast(new RpcRequestHandler());
                        }
                    }).bind(rpcServiceConfig.getPort())
                    .sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RpcException(RpcExceptionMessageEnums.RPC_SERVER_ERROR);
        }
    }
}
