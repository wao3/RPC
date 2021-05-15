package cn.wao3.rpc.dto;

import cn.wao3.rpc.common.enums.RpcResponseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse<T> implements Serializable {
    private String requestId;
    private Integer code;
    private String message;
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        var success = RpcResponseStatusEnum.SUCCESS;
        var response = new RpcResponse<T>();
        response.setRequestId(requestId);
        response.setCode(success.getCode());
        response.setMessage(success.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseStatusEnum statusEnum, String requestId) {
        var response = new RpcResponse<T>();
        response.setRequestId(requestId);
        response.setCode(statusEnum.getCode());
        response.setMessage(statusEnum.getMessage());
        return response;
    }
}
