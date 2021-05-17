package cn.wao3.rpc.common.exception;

import cn.wao3.rpc.common.enums.RpcExceptionMessageEnums;

public class RpcException extends RuntimeException {
    public RpcException(RpcExceptionMessageEnums rpcExceptionMessageEnums, String detail) {
        super(rpcExceptionMessageEnums.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcExceptionMessageEnums rpcExceptionMessageEnums) {
        super(rpcExceptionMessageEnums.getMessage());
    }
}
