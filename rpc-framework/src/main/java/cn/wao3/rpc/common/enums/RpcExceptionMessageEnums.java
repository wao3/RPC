package cn.wao3.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RpcExceptionMessageEnums {

    NO_SUCH_SERVICE("没有找到服务"),
    ;

    private final String message;
}
