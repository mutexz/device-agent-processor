package com.douyu.qa.agent.mtpagentrefactor.task.group.android.appstart;

import com.douyu.qa.agent.mtpagentrefactor.common.signal.ThreadCommunicationSignal;

/**
 * @author wunanfang
 */
public class PerformanceTaskRunnable implements Runnable{

    private ThreadCommunicationSignal threadCommunicationSignal;

    public PerformanceTaskRunnable(ThreadCommunicationSignal threadCommunicationSignal){
        this.threadCommunicationSignal = threadCommunicationSignal;
    }

    @Override
    public void run() {

    }
}
