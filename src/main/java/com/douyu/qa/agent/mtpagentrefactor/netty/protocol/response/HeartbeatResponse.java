package com.douyu.qa.agent.mtpagentrefactor.netty.protocol.response;

import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuifeng
 */
@Slf4j
@Getter
@Setter
@ToString
public class HeartbeatResponse extends BaseProtocol {

    private String msg;

    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.HEARTBEAT_RESPONSE.getCode();
    }
}
