package org.mac;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务心跳计数组件，server会计算每分钟client发送过来的心跳次数，达不到一定比例会认为是网络问题
 * 此时不会进行服务实例的摘除
 */
public class HeartbeatCounter {

    //最近一分钟的心跳次数
    //使用automiclong来进行优化
    private AtomicLong latestMinuteHeartbeatRate = new AtomicLong();

    //最近一分钟的时间戳
    private long latestMinuteTimestamp = System.currentTimeMillis();

    //单例对象
    private static HeartbeatCounter heartbeatMessuredRate = new HeartbeatCounter();

    //单例，私有化构造函数
    private HeartbeatCounter() {
        Daemon daemon = new Daemon();
        daemon.setDaemon(true);
        daemon.start();
    }

    //获取单例对象
    public static HeartbeatCounter getInstance(){
        return heartbeatMessuredRate;
    }

    //增加最近一分钟的心跳次数
    public void increment(){
        //增加心跳次数
        latestMinuteHeartbeatRate.getAndIncrement();
    }

    /**
     * 判断是否超过一分钟
     * @return
     */
    private boolean isOverOneMinute() {
        return (System.currentTimeMillis()-latestMinuteTimestamp)>60*1000l;
    }

    /**
     * 获取每分钟的心跳次数
     * @return
     */
    public long get() {
        return latestMinuteHeartbeatRate.get();
    }

    /**
     * Daemon进程
     */
    class Daemon extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    if(isOverOneMinute()) {
                        latestMinuteHeartbeatRate = new AtomicLong(0L);
                        latestMinuteTimestamp = System.currentTimeMillis();
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
