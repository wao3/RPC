package cn.wao3.rpc.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcConfig {
    private String group;
    private String version;
}
