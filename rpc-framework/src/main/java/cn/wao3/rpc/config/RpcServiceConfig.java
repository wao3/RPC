package cn.wao3.rpc.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcServiceConfig {
    private String version;
    private String group;
    private Object service;
    private int port;

    private String getServiceName() {
        return service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public String getRpcServiceName() {
        return getServiceName() + group + version;
    }
}
