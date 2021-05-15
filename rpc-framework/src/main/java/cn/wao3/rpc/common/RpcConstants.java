package cn.wao3.rpc.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Message header:
 * +----------------------------------+
 * | MAGIC_NUMBER | VERSION |  LENGTH |
 * |      4       |    1    |    3    |
 * +----------------------------------+
 *
 * @author wangao
 * @date 2021-05-15
 */
public class RpcConstants {
    public static final int HEADER_MAGIC_NUMBER = 0x59242364;
    public static final int HEADER_BYTES_MAGIC = 4;
    public static final int HEADER_BYTES_VERSION = 1;
    public static final int HEADER_BYTES_LENGTH = 3;

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final int MAX_LENGTH = Integer.MAX_VALUE >> 7;
}
