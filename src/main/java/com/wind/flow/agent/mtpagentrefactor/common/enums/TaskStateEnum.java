package com.wind.flow.agent.mtpagentrefactor.common.enums;

/**
 * @author wunanfang
 * 任务执行状态属性
 */

public enum TaskStateEnum {
    /**
     * 任务等待中
     */
    waiting,
    /**
     * 任务执行中
     */
    executing,
    /**
     * 任务执行失败
     */
    fail,
    /**
     * 任务执行成功
     */
    success
}
