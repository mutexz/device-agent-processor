package com.wind.flow.agent.mtpagentrefactor.netty.protocol;

import com.wind.flow.agent.mtpagentrefactor.netty.protocol.request.*;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.response.AgentResponse;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.response.HeartbeatResponse;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.response.TaskStatusResponse;
import lombok.Getter;

/**
 * @author zhuifeng
 */
@Getter
public enum TypeObjectEnum {
    /**
     * agent请求
     */
    AGENT_REQUEST(1, AgentRequest.class),
    /**
     * agent返回
     */
    AGENT_RESPONSE(2, AgentResponse.class),

    /**
     * 设备状态传输
     */
    DEVICE_STATUS_REQUEST(3, DeviceStatusRequest.class),

    /**
     * 心跳包请求
     */
    HEARTBEAT_REQUEST(4, HeartbeatRequest.class),

    /**
     * 心跳包回复
     */
    HEARTBEAT_RESPONSE(5,HeartbeatResponse.class),

    /**
     * 任务刷新状态请求
     */
    TASK_STATUS_REQUEST(6,TaskStatusRequest.class),

    /**
     * 任务刷新状态回复
     */
    TASK_STATUS_RESPONSE(7, TaskStatusResponse.class),

    /**
     * App启动上传数据类
     */
    APP_START_ANDROID(8, AppStartAndroidRequest.class),

    ROOM_START_ANDROID(9, RoomStartRequest.class);

    private Integer code;
    private Class<? extends BaseProtocol> clazz;

    /**
     * 添加限定的父类
     */
    TypeObjectEnum(Integer code, Class<? extends BaseProtocol> clazz){
        this.code = code;
        this.clazz = clazz;
    }

    public static Class<? extends BaseProtocol> getClassByCode(Integer code){
        if (code == null){
            return null;
        }
        for (TypeObjectEnum item : TypeObjectEnum.values()){
            if (code.equals(item.code)){
                return item.clazz;
            }
        }
        return null;
    }

}
