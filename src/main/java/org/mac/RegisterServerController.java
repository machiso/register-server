package org.mac;

import java.util.Map;

/**
 * 模拟register-server的controller接口
 * 提供对外接口，register-client调用
 */
public class RegisterServerController {

    private Registry registry = Registry.registry();
    /**
     * 服务注册
     * @param registerRequest
     * @return
     */
    public RegisterResponse register(RegisterRequest registerRequest){
        RegisterResponse response = new RegisterResponse();
        try {
            //创建服务实例
            ServiceInstance instance = new ServiceInstance();
            instance.setServiceName(registerRequest.getServiceName());
            instance.setHostName(registerRequest.getHostName());
            instance.setServiceInstanceId(registerRequest.getServiceInstanceId());
            instance.setIp(registerRequest.getIp());
            instance.setPort(registerRequest.getPort());

            registry.register(instance);

            response.setStatus(RegisterResponse.SUCCESS);
        }catch (Exception e){
            response.setStatus(RegisterResponse.FAIL);
        }
        return response;
    }

    /**
     * 心跳请求
     * @param request
     * @return
     */
    public HeartBreakResponse heartBeat(HeartBreakRequest request){
        HeartBreakResponse response = new HeartBreakResponse();
        try {
            ServiceInstance instance = registry.getServiceInstance(
                    request.getServiceName(),request.getServiceInstanceId());
            //心跳续约
            instance.renew();

            response.setStatus(HeartBreakResponse.SUCCESS);
        }catch (Exception e){
            response.setStatus(HeartBreakResponse.FAIL);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * client拉取服务注册表
     */
    public Map<String, Map<String, ServiceInstance>> fetchServiceRegistry(){
        return registry.getRegistry();
    }

    /**
     * register-client下线
     */
    public void shutdown(String serviceName,String instanceId){
        registry.remove(serviceName,instanceId);
    }
}
