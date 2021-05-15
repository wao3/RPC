package cn.wao3.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcResponseStatusEnum {
    SUCCESS(200, "OK"),
    FAIL(500, "FAILED"),
    ;
    private final Integer code;
    private final String message;
}