package org.mac;

import java.util.Map;

/**
 * 后台线程监控服务实例存活情况
 */
public class ServiceAliveMonitor {

    /**
     * 检查服务实例是否存活的间隔
     */
    private static final Long CHECK_ALIVE_INTERVAL = 6 * 1000L;

    private Registry registry = Registry.registry();

    private Daemon daemon;

    private SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();

    public ServiceAliveMonitor() {
        this.daemon = new Daemon();
        daemon.setDaemon(true);
    }

    public void start() {
        daemon.start();
    }


    /**
     * 负责监控服务存活的后台线程
     */
    private class Daemon extends Thread{
        @Override
        public void run() {

            Map<String,Map<String,ServiceInstance>> registryMap = registry.getRegistry();

            while (true){
                try {
                    //首先判断是否需要进入自我保护机制
                    if (selfProtectionPolicy.shouldEnterSelfProtection()){
                        Thread.sleep(CHECK_ALIVE_INTERVAL);
                        continue;
                    }
                    //循环遍历，查看每个instance存活情况
                    for (String serviceName :registryMap.keySet()){
                        Map<String, ServiceInstance> instanceMap = registryMap.get(serviceName);

                        for (ServiceInstance instance:instanceMap.values()){
                            if (!instance.isAlive()){
                                registry.remove(serviceName,instance.getServiceInstanceId());
                                //服务下线，自我保护机制心跳次数-2
                                selfProtectionPolicy.shutdown();
                            }
                        }
                    }
                    //每隔60s检查一次心跳
                    Thread.sleep(CHECK_ALIVE_INTERVAL);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
