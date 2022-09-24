package com.wind.flow.agent.mtpagentrefactor.task.group.android.appstart;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuifeng
 */
@Slf4j
public class LogServiceRunnable implements Runnable{

//    private ThreadCommunicationSignal threadCommunicationSignal;
    private String command;
    private IDevice device;
    private File file;

    public LogServiceRunnable(String command, IDevice device, File file){
//        this.threadCommunicationSignal = threadCommunicationSignal;
        this.command = command;
        this.device = device;
        this.file = file;
    }
    @Override
    public void run() {
        MultiLineReceiverLocal multiLineReceiverLocal = new MultiLineReceiverLocal(file);
        try {
            device.executeShellCommand(command, multiLineReceiverLocal, 3600, TimeUnit.SECONDS);
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            log.error("log exception: " + e.getMessage());
        }
    }
}
