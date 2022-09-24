package com.wind.flow.agent.mtpagentrefactor.common.enums;

import cn.hutool.core.util.StrUtil;
import com.wind.flow.agent.mtpagentrefactor.task.Runner;
import com.wind.flow.agent.mtpagentrefactor.task.group.android.AppStartAndroidTest;
import com.wind.flow.agent.mtpagentrefactor.task.group.android.RoomStartAndroidTest;

/**
 * @author wunanfang
 * 任务类型枚举
 */
public enum TaskTypeEnum {
    /**
     * Android性能测试
     */
    ANDROID_PERFORMANCE("appstart_android", AppStartAndroidTest.class),
    ROOM_START_ANDROID("roomstart_android", RoomStartAndroidTest.class);

    private String name;
    private Class<? extends Runner> clazz;

    TaskTypeEnum(String name, Class<? extends Runner> clazz){
        this.name = name;
        this.clazz = clazz;
    }

    public static Class<? extends Runner> getClassByName(String name){
        if (StrUtil.isBlank(name)){
            return null;
        }
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()){
            if (name.equals(taskTypeEnum.name)){
                return taskTypeEnum.clazz;
            }
        }
        return null;
    }

}
