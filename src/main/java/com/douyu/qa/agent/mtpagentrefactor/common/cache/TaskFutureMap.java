package com.douyu.qa.agent.mtpagentrefactor.common.cache;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 任务和Future的集合类
 * @author wunanfang
 */
public class TaskFutureMap {
    private static Map<String, Future<?>> taskFutureMap = new ConcurrentHashMap<>();

    public static Map<String, Future<?>> getTaskFutureMap(){
        return taskFutureMap;
    }
}
