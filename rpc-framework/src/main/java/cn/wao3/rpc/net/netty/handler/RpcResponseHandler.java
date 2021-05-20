package cn.wao3.rpc.net.netty.handler;

import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.net.netty.util.AsyncRequestUtil;
import cn.wao3.rpc.utils.SingletonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangao
 * @date 2021-05-20
 */
public class RpcResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof RpcResponse)) {
            return;
        }
        RpcResponse<Object> response = (RpcResponse<Object>) msg;
        AsyncRequestUtil.complete(response);
    }
}
