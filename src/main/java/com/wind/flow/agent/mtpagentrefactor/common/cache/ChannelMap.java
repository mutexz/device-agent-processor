package com.wind.flow.agent.mtpagentrefactor.common.cache;


import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author zhuifeng
 */
public class ChannelMap {
    private static Map<String, Channel> channelCache = new ConcurrentHashMap<>();

    public static Map<String, Channel> getChannelCache(){
        return channelCache;
    }
}
