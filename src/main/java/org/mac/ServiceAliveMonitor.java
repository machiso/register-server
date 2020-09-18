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
                    /**
                     * 循环遍历，查看每个instance存活情况
                     */
                    for (String serviceName :registryMap.keySet()){
                        Map<String, ServiceInstance> instanceMap = registryMap.get(serviceName);

                        for (ServiceInstance instance:instanceMap.values()){
                            if (!instance.isAlive()){
                                registry.remove(serviceName,instance.getServiceInstanceId());
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
