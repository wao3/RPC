package cn.wao3.rpc.net.netty.handler;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.net.RpcServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangao
 * @date 2021-05-20
 */
public class RpcRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest req = (RpcRequest) msg;
        RpcResponse response = RpcServerHandler.handle(req);
        ctx.writeAndFlush(response);
    }
}
