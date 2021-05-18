package cn.wao3.rpc.net;

import cn.wao3.rpc.dto.RpcRequest;
import cn.wao3.rpc.dto.RpcResponse;

public interface RequestSender {

    RpcResponse<Object> sendRequest(RpcRequest request);
}
