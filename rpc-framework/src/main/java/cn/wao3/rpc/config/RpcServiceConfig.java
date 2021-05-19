package cn.wao3.rpc.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RpcServiceConfig extends RpcConfig{
    private Object service;
    private int port;

    private String getServiceName() {
        return service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public String getRpcServiceName() {
        return getServiceName() + getGroup() + getVersion();
    }
}
