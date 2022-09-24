package com.douyu.qa.agent.mtpagentrefactor.netty.handler;

import cn.hutool.core.util.StrUtil;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.request.AgentRequest;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.response.AgentResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.douyu.qa.agent.mtpagentrefactor.utils.DevicePropertyUtils.getLocalHostFromSocketAddressStr;

/**
 * @author wunanfang
 * agent启动注册层
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class AgentResponseHandler extends SimpleChannelInboundHandler<AgentResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String socketAddressStr = ctx.channel().localAddress().toString();
        if (StrUtil.isBlank(socketAddressStr)){
            log.info("AgentResponse====access IP info failed, channel closing ====");
            ctx.channel().close();
        }
        log.info("local Agent channel active: " + socketAddressStr);
        String hostStr = getLocalHostFromSocketAddressStr(socketAddressStr);
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setHost(hostStr);
        agentRequest.setPlatform(System.getProperty("os.name"));

        // 这个表明启动连接成功服务的第一次，只会走到这个handler为止就结束，后续的inboundhandler即使
        // 调用channelActive方法也不会生效。
        ctx.channel().writeAndFlush(agentRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse agentResponse) throws Exception {
        if (agentResponse.getSuccess()){
            log.info("server response--> " + agentResponse.getMessage());
        } else {
            log.info("server response--> " + agentResponse.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("connection shutdown");
    }
}
