package com.douyu.qa.agent.mtpagentrefactor.netty.protocol.request;

import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhuifeng
 */
@Getter
@Setter
@ToString
public class DeviceStatusRequest extends BaseProtocol {

    private String deviceId;

    private String host;

    private Integer status;

    private String brand;

    private String model;

    private String platform;

    private String type;

    private String osVersion;

    private Integer busyState;


    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.DEVICE_STATUS_REQUEST.getCode();
    }
}
