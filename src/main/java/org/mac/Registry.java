package org.mac;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务实例注册表
 */
public class Registry {

    private static Registry instance = new Registry();

    /**
     * key1 服务名称 value为服务所有的实例
     * key2 实例名称 value为对应的一个实例
     */
    private Map<String,Map<String,ServiceInstance>> registryMap= new HashMap<>();

    public Registry() {

    }

    /**
     * 单例获取
     */
    public synchronized static Registry registry(){
        return instance;
    }

    /**
     * 服务注册
     * synchronized 服务注册表并发安全性，一边读，一边写
     * @param instance
     */
    public synchronized void register(ServiceInstance instance) {
        Map<String, ServiceInstance> instanceMap = registryMap.get(instance.getServiceName());
        //如果注册表中没有serviceName，则新建
        if (instanceMap == null){
            instanceMap = new HashMap<>();
            registryMap.put(instance.getServiceName(),instanceMap);
        }

        instanceMap.put(instance.getServiceInstanceId(),instance);

        System.out.println("服务实例【" + instance + "】，完成注册......");
        System.out.println("注册表：" + registryMap);
    }

    /**
     * 通过serviceName serviceInstanceId 获取实例
     * @param serviceName
     * @param serviceInstanceId
     * @return
     */
    public synchronized ServiceInstance getServiceInstance(String serviceName, String serviceInstanceId) {
        if (registryMap.get(serviceName)!=null){
            return registryMap.get(serviceName).get(serviceInstanceId);
        }
        return null;
    }

    /**
     * 获取全部注册表
     * @return
     */
    public synchronized Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registryMap;
    }

    /**
     * 删除实例
     * @param serviceName
     * @param serviceInstanceId
     */
    public synchronized void remove(String serviceName, String serviceInstanceId) {
        System.out.println("服务实例【" + serviceInstanceId + "】，从注册表中进行摘除");
        Map<String, ServiceInstance> instanceMap = registryMap.get(serviceName);
        instanceMap.remove(serviceInstanceId);
    }
}
