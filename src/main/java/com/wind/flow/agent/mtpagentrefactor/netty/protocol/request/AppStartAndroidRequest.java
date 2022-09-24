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
public class AppStartAndroidRequest extends BaseProtocol {

    private Integer appStartTime;

    private Long taskId;

    private String deviceId;

    private String platform;

    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.APP_START_ANDROID.getCode();
    }
}
