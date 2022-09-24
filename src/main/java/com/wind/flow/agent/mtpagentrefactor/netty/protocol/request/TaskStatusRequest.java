package com.wind.flow.agent.mtpagentrefactor.netty.protocol.request;

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
@Setter
@Getter
@ToString
public class TaskStatusRequest extends BaseProtocol {

    private String taskId;

    private String msgId;

    private String result;

    private String errorLog;

    private String agentIp;

    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.TASK_STATUS_REQUEST.getCode();
    }
}
