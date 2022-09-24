package com.wind.flow.agent.mtpagentrefactor.netty.codec;

import com.alibaba.fastjson.JSON;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import com.wind.flow.agent.mtpagentrefactor.common.constant.ProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wunanfang
 */
@Component
public class ProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 表明不是本客户端发来的包
        if (byteBuf.readInt() != ProtocolConstants.MARK_NUMBER){
            channelHandlerContext.close();
            return;
        }
        int type = byteBuf.readInt();
        int protocolLength = byteBuf.readInt();
        byte[] contentByteArr = new byte[protocolLength];
        byteBuf.readBytes(contentByteArr);

        // 根据type去获取对应的对象
        Class<? extends BaseProtocol> targetClass = TypeObjectEnum.getClassByCode(type);
        BaseProtocol baseProtocol = JSON.parseObject(contentByteArr, targetClass);
        list.add(baseProtocol);
    }
}
