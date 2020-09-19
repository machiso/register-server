package org.mac;

/**
 * 自我保护机制的组件
 */
public class SelfProtectionPolicy {

    private double THRESHOLD = 0.85;

    //单例对象
    private static SelfProtectionPolicy policy = new SelfProtectionPolicy();


    public SelfProtectionPolicy() {
    }

    public static SelfProtectionPolicy getInstance(){
        return policy;
    }

    //每分钟期望的心跳次数
    private long expectedHeartbeatRate = 0l;

    //期望的心跳次数的阈值,85%
    private long expectedHeartbeatThreshold = 0l;

    //服务上线，心跳次数+2
    public synchronized void register(){
        this.expectedHeartbeatRate = expectedHeartbeatRate+2;
        this.expectedHeartbeatThreshold = (long) (this.expectedHeartbeatRate*THRESHOLD);
    }

    //服务下线，心跳次数-2
    public synchronized void shutdown(){
        this.expectedHeartbeatRate = expectedHeartbeatRate - 2;
        this.expectedHeartbeatThreshold = (long) (this.expectedHeartbeatRate*THRESHOLD);
    }

    //判断是否需要进入自我保护机制
    // true 需要进入自我保护机制
    public boolean shouldEnterSelfProtection(){
        HeartbeatMessuredRate heartbeatMessuredRate = HeartbeatMessuredRate.getInstance();
        //每分钟实际的心跳次数 < 期望的心跳次数，则不用进入自我保护机制
        if (heartbeatMessuredRate.get()<expectedHeartbeatThreshold){
            System.out.println("【自我保护机制开启】最近一分钟心跳次数=" + heartbeatMessuredRate.get() + ", 期望心跳次数=" + this.expectedHeartbeatThreshold);
            return true;
        }
        System.out.println("【自我保护机制未开启】最近一分钟心跳次数=" + heartbeatMessuredRate.get() + ", 期望心跳次数=" + this.expectedHeartbeatThreshold);
        return false;
    }
}
