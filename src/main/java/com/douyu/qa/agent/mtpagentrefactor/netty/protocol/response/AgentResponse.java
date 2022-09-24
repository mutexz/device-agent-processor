package com.douyu.qa.agent.mtpagentrefactor.netty.protocol.response;

import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wunanfang
 */
@Getter
@Setter
@ToString
public class AgentResponse extends BaseProtocol {

    private Boolean success;

    private String message;


    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.AGENT_RESPONSE.getCode();
    }
}
