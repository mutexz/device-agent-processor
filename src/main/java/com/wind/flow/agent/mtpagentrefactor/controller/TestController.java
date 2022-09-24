package com.wind.flow.agent.mtpagentrefactor.controller;

import com.wind.flow.agent.mtpagentrefactor.netty.AgentNettyClient;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.request.AgentRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wunanfang
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private AgentNettyClient agentNettyClient;

    @GetMapping("/send")
    public String sendMsg(){
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setHost("localhost");
        agentNettyClient.getChannel().writeAndFlush(agentRequest);
        return "true";
    }
}
