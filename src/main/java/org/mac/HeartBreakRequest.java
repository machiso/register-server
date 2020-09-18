package org.mac;

/**
 * 发送心跳请求
 */
public class HeartBreakRequest {
    /**
     * 服务实例id
     */
    private String serviceInstanceId;

    /**
     * 服务名
     */
    private String serviceName;

    public HeartBreakRequest(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
