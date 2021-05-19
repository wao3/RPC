package cn.wao3.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RpcExceptionMessageEnums {

    NO_SUCH_SERVICE("没有找到服务"),
    TOO_MUCH_DATA("请求数据过大"),
    RPC_REQUEST_FAILED("RPC 请求失败"),
    RPC_DATA_ERROR("RPC 数据包出错"),
    RPC_SERVER_ERROR("RPC 服务器出错"),
    ;

    private final String message;
}
