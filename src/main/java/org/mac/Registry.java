package org.mac;

import java.util.*;

/**
 * 服务实例注册表
 */
public class Registry {

    private static Registry instance = new Registry();

    //3分钟
    private static final long CHANGED_INTERVEL_TIME = 3*60*1000;

    public static final Long RECENTLY_CHANGED_ITEM_CHECK_INTERVAL = 3000L;

    /**
     * 服务注册表
     * key1 服务名称 value为服务所有的实例
     * key2 实例名称 value为对应的一个实例
     */
    private Map<String,Map<String,ServiceInstance>> registryMap= new HashMap<>();

    //最近变化的服务实例的队列
    private LinkedList<RecentlyChangedInstance> recentlyChangedQueue = new LinkedList<>();

    public Registry() {
        RencentlyQueueMonitor monitor = new RencentlyQueueMonitor();
        monitor.setDaemon(true);
        monitor.start();
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
        //添加到最近变化的queue
        RecentlyChangedInstance changedInstance = new RecentlyChangedInstance(instance,System.currentTimeMillis(),ServiceChangedType.REGISTER);
        //加入queue队尾
        recentlyChangedQueue.offer(changedInstance);

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
     * 抓取增量注册表
     * @return
     */
    public List<RecentlyChangedInstance> fetchDeltaInstance() {
        return recentlyChangedQueue;
    }

    /**
     * 删除实例
     * @param serviceName
     * @param serviceInstanceId
     */
    public synchronized void remove(String serviceName, String serviceInstanceId) {
        Map<String, ServiceInstance> instanceMap = registryMap.get(serviceName);
        //添加到最近变化的queue
        ServiceInstance serviceInstance = instanceMap.get(serviceInstanceId);
        RecentlyChangedInstance changedInstance = new RecentlyChangedInstance(serviceInstance,System.currentTimeMillis(),ServiceChangedType.REMOVE);
        //加入queue队尾
        recentlyChangedQueue.offer(changedInstance);
        System.out.println("服务实例【" + serviceInstanceId + "】，从注册表中进行摘除");

        //移除注册表
        instanceMap.remove(serviceInstanceId);
    }

    /**
     * 最近变化的instance
     */
    class RecentlyChangedInstance{
        //服务实例
        private ServiceInstance instance;
        //变化的时间戳
        private long changedTime;
        //变化的操作类型
        private ServiceChangedType operation;

        public RecentlyChangedInstance(ServiceInstance instance, long changedTime, ServiceChangedType operation) {
            this.instance = instance;
            this.changedTime = changedTime;
            this.operation = operation;
        }
    }

    /**
     * 变化的类型，注册 or 删除
     */
    enum ServiceChangedType{
        REGISTER("注册"),
        REMOVE("删除")
        ;

        private String operation;
        ServiceChangedType(String operation){
            this.operation = operation;
        }
    }

    /**
     * 后台线程，监控queue中哪些instance超过3分钟，移除
     */
    class RencentlyQueueMonitor extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    synchronized (instance){
                        RecentlyChangedInstance changedInstance = null;
                        //queue中还有元素，就一直遍历
                        while ((changedInstance = recentlyChangedQueue.peek()) != null){
                            if (System.currentTimeMillis()-changedInstance.changedTime>CHANGED_INTERVEL_TIME){
                                //移除changed queue
                                recentlyChangedQueue.pop();
                            }
                        }
                        Thread.sleep(RECENTLY_CHANGED_ITEM_CHECK_INTERVAL);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
