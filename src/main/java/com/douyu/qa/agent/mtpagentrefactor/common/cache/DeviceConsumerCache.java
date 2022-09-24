package com.douyu.qa.agent.mtpagentrefactor.common.cache;

import com.douyu.qa.agent.mtpagentrefactor.rocketmq.Consumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuifeng
 */
public class DeviceConsumerCache {
    public static Map<String, Consumer> deviceConsumerCacheMap = new ConcurrentHashMap<>();

    public static Map<String, Consumer> getDeviceConsumerCacheMap() {
        return deviceConsumerCacheMap;
    }
}
