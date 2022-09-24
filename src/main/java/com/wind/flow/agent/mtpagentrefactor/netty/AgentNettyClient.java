package com.wind.flow.agent.mtpagentrefactor.netty;

import com.wind.flow.agent.mtpagentrefactor.common.cache.ChannelMap;
import com.wind.flow.agent.mtpagentrefactor.netty.codec.ProtocolDecoder;
import com.wind.flow.agent.mtpagentrefactor.netty.codec.ProtocolEncoder;
import com.wind.flow.agent.mtpagentrefactor.netty.handler.AgentResponseHandler;
import com.wind.flow.agent.mtpagentrefactor.netty.handler.HeartbeatResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author zhuifeng
 */

@Component
@Slf4j
@Getter
public class AgentNettyClient {

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Bootstrap clientBootStrap;

    private Channel channel;

    @Value("${netty.host}")
    private String host;

    @Value("${netty.port}")
    private Integer port;


    @Resource
    private ProtocolEncoder protocolEncoder;

    @Resource
    private AgentResponseHandler agentResponseHandler;


    @PostConstruct
    public void connect(){
        ChannelFuture channelFuture = null;
        try {
            clientBootStrap = new Bootstrap();
            clientBootStrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 推荐的做法是先addLast所有的outboundhandler，再addLast所有的inboundhandler
                            ByteBuf buf = Unpooled.copiedBuffer("$".getBytes(StandardCharsets.UTF_8));
                            socketChannel.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(8192, buf))
                                    .addLast(protocolEncoder)
                                    .addLast(new ProtocolDecoder())
                                    .addLast(new HeartbeatResponseHandler(clientBootStrap, host, port))
                                    .addLast(agentResponseHandler);
                        }
                    });
            channelFuture = clientBootStrap.connect(host, port).syncUninterruptibly();
            channel = channelFuture.channel();
            ChannelMap.getChannelCache().put("running", channel);
        } catch (Exception e) {
            log.error("start netty client exception: " + e.getMessage());
        } finally {
            if (null != channelFuture && channelFuture.isSuccess()){
                log.info("start netty client success, connect server {}:{} success", host, port);
            } else {
                log.error("start netty client failed!");
            }
        }
    }

    @PreDestroy
    public void shutDown(){
        if (channel == null){
            return;
        }
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("shutdown netty client exception: " + e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            log.info("netty client shutdown manually");
        }
    }
}
