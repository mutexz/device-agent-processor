package com.douyu.qa.agent.mtpagentrefactor.task;

import com.douyu.qa.agent.mtpagentrefactor.common.entity.MessageEntity;

/**
 * @author wunanfang
 */
public interface Runner {

    /**
     * 初始化任务
     */
    void init(String business, String taskId, String msgId, MessageEntity messageEntity);

    /**
     * 执行前的准备工作
     */
    void before();

    /**
     * 执行任务
     */
    void run();

    /**
     * 执行完的任务
     */
    void after();

}
