package com.wind.flow.agent.mtpagentrefactor.netty.handler;

import com.wind.flow.agent.mtpagentrefactor.common.cache.ChannelMap;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.request.HeartbeatRequest;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.response.HeartbeatResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author wunanfang
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartbeatResponseHandler extends SimpleChannelInboundHandler<HeartbeatResponse> {

    private static final int INITIAL_DELAY = 2;

    private static final int PERIOD = 30;

    private final Bootstrap bootstrap;

    private String host;

    private Integer port;



    public HeartbeatResponseHandler(Bootstrap bootstrap, String host, Integer port) {
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().scheduleAtFixedRate(() -> {
            if (ctx.channel().isActive()){
                HeartbeatRequest request = new HeartbeatRequest();
                request.setMsg("PING");
                log.info("client heartbeat: " + request.getMsg());
                ctx.writeAndFlush(request);
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatResponse msg) throws Exception {
        log.info("receive heartbeat from server: " + msg.getMsg());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("connection exception: " + cause.getMessage());
        ctx.channel().close();
    }

    /**
     * 连接重试
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                log.info("Agent reconnect success");
                if (ChannelMap.getChannelCache().get("running") != null){
                    // 先前的channel要关闭，没有用了
                    ChannelMap.getChannelCache().get("running").close();
                }
                ChannelMap.getChannelCache().put("running", channelFuture.channel());
            } else {
                log.error("Agent reconnect failed , wait 5s and retry");
                Thread.sleep(5000);
                future.channel().pipeline().fireChannelInactive();
            }
        });
//        ChannelFuture channelFuture;
//        synchronized (bootstrap){
//            bootstrap.handler(new ChannelInitializer<Channel>() {
//                final ByteBuf buf = Unpooled.copiedBuffer("$".getBytes(StandardCharsets.UTF_8));
//                @Override
//                protected void initChannel(Channel ch) throws Exception {
//                    ch.pipeline()
//                            .addLast(new DelimiterBasedFrameDecoder(8192, buf))
//                            .addLast(new ProtocolEncoder())
//                            .addLast(new ProtocolDecoder())
//                            .addLast(new HeartbeatResponseHandler(bootstrap, host, port))
//                            .addLast(new AgentResponseHandler());
//                }
//            });
//            channelFuture = bootstrap.connect(host, port);
//        }
//        channelFuture.addListener((ChannelFutureListener) future -> {
//            if (future.isSuccess()){
//                log.info("Agent重连成功");
//            } else {
//                log.error("Agent重连失败, 5s后重连");
//                Thread.sleep(5000);
//                future.channel().pipeline().fireChannelInactive();
//            }
//        });
    }
}
