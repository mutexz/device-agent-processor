package com.wind.flow.agent.mtpagentrefactor.netty.codec;

import com.alibaba.fastjson.JSON;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.wind.flow.agent.mtpagentrefactor.common.constant.ProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author wunanfang
 */
@Component
@ChannelHandler.Sharable
public class ProtocolEncoder extends MessageToByteEncoder<BaseProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseProtocol baseProtocol, ByteBuf byteBuf) throws Exception {
        byte[] bytes = JSON.toJSONBytes(baseProtocol);
        byteBuf.writeInt(ProtocolConstants.MARK_NUMBER);
        byteBuf.writeInt(baseProtocol.getProtocol());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        byteBuf.writeBytes("$".getBytes(StandardCharsets.UTF_8));
    }
}
