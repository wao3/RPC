package cn.wao3.rpc.net.netty.codec;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;
import cn.wao3.rpc.net.ByteUtil;
import cn.wao3.rpc.serialize.ProtostuffSerializer;
import cn.wao3.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wangao
 * @date 2021-05-20
 */
public class RpcResponseDecoder extends ByteToMessageDecoder {

    private final static Serializer serializer = new ProtostuffSerializer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(serializer.deserialize(ByteUtil.readBody(byteBuf), RpcResponse.class));
    }
}
