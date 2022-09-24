package com.wind.flow.agent.mtpagentrefactor.netty.protocol.request;

import com.wind.flow.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wunanfang
 */
@Getter
@Setter
@ToString
public class AgentRequest extends BaseProtocol {

    private String host;

    private String platform;


    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.AGENT_REQUEST.getCode();
    }
}
