package cn.wao3.rpc.net;

import cn.wao3.rpc.common.RpcConstants;
import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;
import cn.wao3.rpc.common.exception.RpcException;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangao
 * @date 2021-05-19
 */
@Slf4j
public class ByteUtil {
    public static byte[] wrapBody(byte[] body) {
        byte[] magicNumber = RpcConstants.HEADER_MAGIC_NUMBER;
        byte[] version = new byte[]{ 0x1 };
        int length = body.length;
        if (length > RpcConstants.MAX_LENGTH) {
            throw new RpcException(RpcExceptionMessageEnums.TOO_MUCH_DATA);
        }
        byte[] lengthBytes = new byte[]{ (byte)(length >> 16), (byte)(length >> 8), (byte)length};
        byte[] result = new byte[length + RpcConstants.HEADER_TOTAL_LENGTH];
        System.arraycopy(magicNumber, 0, result, 0, RpcConstants.HEADER_BYTES_MAGIC);
        System.arraycopy(version, 0, result, RpcConstants.HEADER_BYTES_MAGIC, RpcConstants.HEADER_BYTES_VERSION);
        System.arraycopy(lengthBytes, 0, result, RpcConstants.HEADER_BYTES_MAGIC + RpcConstants.HEADER_BYTES_VERSION, RpcConstants.HEADER_BYTES_LENGTH);
        System.arraycopy(body, 0, result, RpcConstants.HEADER_TOTAL_LENGTH, length);
        return result;
    }

    public static int checkHeader(byte[] header) {
        if (header.length != RpcConstants.HEADER_TOTAL_LENGTH) {
            throw new RpcException(RpcExceptionMessageEnums.RPC_DATA_ERROR);
        }
        byte[] magicNumber = RpcConstants.HEADER_MAGIC_NUMBER;
        log.debug("header: {}, magicNumber: {}", header, magicNumber);
        for (int i = 0; i < magicNumber.length; i++) {
            if (magicNumber[i] != header[i]) {
                throw new RpcException(RpcExceptionMessageEnums.RPC_DATA_ERROR);
            }
        }
        // 暂时忽略版本号
        int version = header[4];
        // 返回数据包长度
        return (Byte.toUnsignedInt(header[5]) << 16)
                + (Byte.toUnsignedInt(header[6]) << 8)
                + (Byte.toUnsignedInt(header[7]));
    }

    public static int getBodyLength(InputStream inputStream) throws IOException {
        int headerLength = RpcConstants.HEADER_TOTAL_LENGTH;
        byte[] header = new byte[headerLength];
        int read = inputStream.read(header);
        if (read != headerLength) {
            throw new IOException();
        }
        return ByteUtil.checkHeader(header);
    }

    public static byte[] readBody(InputStream inputStream, int bodyLength) throws IOException {
        byte[] result = new byte[bodyLength];
        int read = inputStream.read(result);
        if (read != bodyLength) {
            throw new IOException();
        }
        return result;
    }

    public static byte[] readBody(InputStream inputStream) throws IOException {
        int bodyLength = getBodyLength(inputStream);
        byte[] result = new byte[bodyLength];
        int read = inputStream.read(result);
        if (read != bodyLength) {
            throw new IOException();
        }
        return result;
    }

    public static byte[] readBody(ByteBuf byteBuf) {
        if (RpcConstants.MAGIC_NUMBER != byteBuf.readInt()) {
            throw new RpcException(RpcExceptionMessageEnums.RPC_DATA_ERROR);
        }
        byte version = byteBuf.readByte();
        int length = ByteUtil.readLength(byteBuf);
        byte[] body = new byte[length];
        byteBuf.readBytes(body);
        return body;
    }

    public static int readLength(ByteBuf byteBuf) {
        byte[] length = new byte[3];
        byteBuf.readBytes(length);
        return (Byte.toUnsignedInt(length[0]) << 16)
                + (Byte.toUnsignedInt(length[1]) << 8)
                + (Byte.toUnsignedInt(length[2]));
    }
}
