package cn.wao3.rpc.net.netty.codec;

import cn.wao3.rpc.net.ByteUtil;
import cn.wao3.rpc.serialize.ProtostuffSerializer;
import cn.wao3.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wangao
 * @date 2021-05-20
 */
public class RpcMessageEncoder extends MessageToByteEncoder<Object> {

    private final static Serializer serializer = new ProtostuffSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] body = serializer.serialize(o);
        byte[] bytes = ByteUtil.wrapBody(body);
        byteBuf.writeBytes(bytes);
    }
}
