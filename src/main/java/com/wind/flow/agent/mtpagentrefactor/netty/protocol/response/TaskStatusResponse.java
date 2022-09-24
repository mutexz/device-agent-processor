package com.wind.flow.agent.mtpagentrefactor.netty.protocol.response;

import com.wind.flow.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wunanfang
 */
@Slf4j
@Getter
@Setter
@ToString
public class TaskStatusResponse extends BaseProtocol {


    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.TASK_STATUS_RESPONSE.getCode();
    }
}
