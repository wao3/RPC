package cn.wao3.rpc.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcServiceConfig {
    private String version;
    private String group;
    private Object service;

    private String getServiceName() {
        return service.getClass().getInterfaces()[0].getCanonicalName();
    }

    private String getRpcServiceName() {
        return getServiceName() + group + version;
    }
}
