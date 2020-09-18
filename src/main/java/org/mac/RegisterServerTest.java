package org.mac;

import java.util.UUID;

/**
 * 代表了服务注册中心的这么一个东西
 * 
 * @author zhonghuashishan
 *
 */
public class RegisterServerTest {
	
	public static void main(String[] args) throws Exception {
		RegisterServerController controller = new RegisterServerController();
		
		String serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
		
		// 模拟发起一个服务注册的请求
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setHostName("inventory-service-01");
		registerRequest.setIp("192.168.31.208");  
		registerRequest.setPort(9000); 
		registerRequest.setServiceInstanceId(serviceInstanceId);    
		registerRequest.setServiceName("inventory-service");  

		controller.register(registerRequest);
		
		// 模拟进行一次心跳，完成续约
		HeartBreakRequest heartbeatRequest = new HeartBreakRequest(serviceInstanceId);
		heartbeatRequest.setServiceName("inventory-service");

		controller.heartBeat(heartbeatRequest);
		
		// 开启一个后台线程，检测微服务的存活状态
		ServiceAliveMonitor serviceAliveMonitor = new ServiceAliveMonitor();
		serviceAliveMonitor.start();

		while(true) {
			Thread.sleep(30 * 1000);
		}
	}

}
