package com.wind.flow.agent.mtpagentrefactor.common.cache;

import com.wind.flow.agent.mtpagentrefactor.rocketmq.Consumer;

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
