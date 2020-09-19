package org.mac;

/**
 * 服务心跳计数组件，server会计算每分钟client发送过来的心跳次数，达不到一定比例会认为是网络问题
 * 此时不会进行服务实例的摘除
 */
public class HeartbeatMessuredRate {

    //最近一分钟的心跳次数
    private long latestMinuteHeartbeatRate = 0l;

    //最近一分钟的时间戳
    private long latestMinuteTimestamp = System.currentTimeMillis();

    //单例对象
    private static HeartbeatMessuredRate heartbeatMessuredRate = new HeartbeatMessuredRate();

    //单例，私有化构造函数
    private HeartbeatMessuredRate() {
    }

    //获取单例对象
    public static HeartbeatMessuredRate getInstance(){
        return heartbeatMessuredRate;
    }

    //增加最近一分钟的心跳次数
    public synchronized void increment(){
        //判断距离上一次是否已经超过一分钟
        if (isOverOneMinute()){
            this.latestMinuteTimestamp = System.currentTimeMillis();
            this.latestMinuteHeartbeatRate = 0l;
        }
        //增加心跳次数
        latestMinuteHeartbeatRate++;
    }

    /**
     * 判断是否超过一分钟
     * @return
     */
    private boolean isOverOneMinute() {
        return (System.currentTimeMillis()-latestMinuteHeartbeatRate)>60*1000l;
    }
}
