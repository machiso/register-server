package org.mac;

/**
 * 模拟register-server的controller接口
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
}