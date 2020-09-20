package org.mac;

import java.util.List;
import java.util.Map;

/**
 * 模拟register-server的controller接口
 * 提供对外接口，register-client调用
 */
public class RegisterServerController {

    private Registry registry = Registry.registry();
    //心跳次数实例
    private SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
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

            //服务注册
            registry.register(instance);
            //期望心跳次数+2
            selfProtectionPolicy.register();

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

            //增加心跳次数
            HeartbeatCounter heartbeatMessuredRate = HeartbeatCounter.getInstance();
            heartbeatMessuredRate.increment();

            response.setStatus(HeartBreakResponse.SUCCESS);
        }catch (Exception e){
            response.setStatus(HeartBreakResponse.FAIL);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * client拉取全量注册表
     */
    public Map<String, Map<String, ServiceInstance>> fetchServiceRegistry(){
        return registry.getRegistry();
    }

    /**
     * client拉取增量注册表
     */
    public List<Registry.RecentlyChangedInstance> fetchDeltaInstance(){
        return registry.fetchDeltaInstance();
    }


    /**
     * register-client下线
     */
    public void shutdown(String serviceName,String instanceId){
        //注册表摘除实例
        registry.remove(serviceName,instanceId);
        //每分钟心跳次数减2
        selfProtectionPolicy.shutdown();
    }
}
