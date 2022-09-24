package com.wind.flow.agent.mtpagentrefactor.netty;

import com.alibaba.fastjson.JSON;
import com.wind.flow.agent.mtpagentrefactor.common.cache.ChannelMap;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wunanfang
 */
@Slf4j
@Service
@DependsOn({"agentNettyClient"})
@Lazy
public class AgentNettyClientTool {

    @Resource
    private AgentNettyClient agentNettyClient;

    public void sendProtocolMsg(BaseProtocol baseProtocol){
//        if (agentNettyClient.getChannel() != null){
//            log.info("=== 发送消息:{} 至服务端 ===", JSON.toJSONString(baseProtocol));
//            agentNettyClient.getChannel().writeAndFlush(baseProtocol);
//        }
        if (ChannelMap.getChannelCache().get("running") != null){
            log.info("=== send messgae :{} to server ===", JSON.toJSONString(baseProtocol));
            ChannelMap.getChannelCache().get("running").writeAndFlush(baseProtocol);
        }
    }

    public String getLocalHostStr(){
//        if (agentNettyClient.getChannel() != null){
//            return agentNettyClient.getChannel().localAddress().toString();
//        }
//        return null;
        if (ChannelMap.getChannelCache().get("running") != null){
            return ChannelMap.getChannelCache().get("running").localAddress().toString();
        }
        return null;
    }
}
