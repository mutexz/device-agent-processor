package com.wind.flow.agent.mtpagentrefactor.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import javax.annotation.Resource;

/**
 * 废弃类，启动已转向@PostConstruct注解方式
 * @author wunanfang
 */


//@Component
//@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Deprecated
public class NettyBootstrap implements ApplicationRunner {

    @Resource
    private AgentNettyClient agentNettyClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        log.info("=== 启动Netty客户端 ===");
//        ChannelFuture channelFuture = agentNettyClient.connect();
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> agentNettyClient.shutDown()));
//        channelFuture.channel().closeFuture().syncUninterruptibly();
    }
}
