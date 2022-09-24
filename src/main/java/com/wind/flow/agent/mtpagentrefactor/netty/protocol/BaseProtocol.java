package com.wind.flow.agent.mtpagentrefactor.netty.protocol;

import com.wind.flow.agent.mtpagentrefactor.common.constant.ProtocolConstants;

/**
 * @author zhuifeng
 */
public abstract class BaseProtocol {

    private final Integer flag = ProtocolConstants.MARK_NUMBER;

    /**
     * 返回对象类型，通过map解耦
     */
    public abstract Integer getProtocol();
}
