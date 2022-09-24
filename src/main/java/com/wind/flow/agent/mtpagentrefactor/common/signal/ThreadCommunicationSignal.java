package com.wind.flow.agent.mtpagentrefactor.common.signal;

/**
 * @author wunanfang
 * 多线程之间共享对象进行通信 标记类
 */
public class ThreadCommunicationSignal {

    private boolean taskFinishSignal = false;

    public synchronized void setTaskFinishSignal(boolean taskFinishSignal){
        this.taskFinishSignal = taskFinishSignal;
    }

    public synchronized boolean isTaskFinishSignal(){
        return this.taskFinishSignal;
    }
}
