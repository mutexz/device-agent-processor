package com.wind.flow.agent.mtpagentrefactor.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.android.ddmlib.*;
import com.wind.flow.agent.mtpagentrefactor.common.constant.AppConstants;
import com.wind.flow.agent.mtpagentrefactor.common.entity.MessageEntity;
import com.wind.flow.agent.mtpagentrefactor.common.enums.TaskStateEnum;
import com.wind.flow.agent.mtpagentrefactor.netty.AgentNettyClientTool;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.request.TaskStatusRequest;
import com.wind.flow.agent.mtpagentrefactor.utils.DevicePropertyUtils;
import com.wind.flow.agent.mtpagentrefactor.utils.PathUtils;
import com.wind.flow.agent.mtpagentrefactor.utils.SpringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 任务执行基类，所有的自动化任务都要继承该类，以完成部分基础操作
 * @author wunanfang
 */
@Getter
@Setter
@Slf4j
public abstract class BaseRunner implements Runner{

    public String rootDir;
    public String reportDir;
    public String taskId;
    public String msgId;
    public MessageEntity messageEntity;
    public StringBuilder errorLog = new StringBuilder(" ");
    public boolean result = true;

    public AgentNettyClientTool agentNettyClientTool = SpringUtils.getBean(AgentNettyClientTool.class);

    @Override
    public void init(String business, String taskId, String msgId, MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
        this.taskId = taskId;
        this.msgId = msgId;

        this.rootDir = PathUtils.getCurrentPathDir();
        this.reportDir = rootDir + File.separator + "report" + File.separator + taskId + File.separator;
        // 初始化路径
        File mkdir = FileUtil.mkdir(reportDir);
        if (mkdir == null){
            errorLog.append("create path: ").append(reportDir).append(" failed");
        }
    }

    @Override
    public void before() {
        TaskStatusRequest taskStatusRequest = new TaskStatusRequest();
        taskStatusRequest.setTaskId(taskId);
        taskStatusRequest.setMsgId(msgId);
        taskStatusRequest.setResult(TaskStateEnum.executing.name());
        taskStatusRequest.setErrorLog(errorLog.toString());
        String agentIp = DevicePropertyUtils.getLocalHostFromSocketAddressStr(agentNettyClientTool.getLocalHostStr());
        taskStatusRequest.setAgentIp(agentIp);
        agentNettyClientTool.sendProtocolMsg(taskStatusRequest);
    }

    @Override
    public void run() {

    }

    @Override
    public void after() {
        TaskStatusRequest taskStatusRequest = new TaskStatusRequest();
        taskStatusRequest.setTaskId(taskId);
        taskStatusRequest.setMsgId(msgId);
        taskStatusRequest.setResult(result ? TaskStateEnum.success.name() : TaskStateEnum.fail.name());
        taskStatusRequest.setErrorLog(errorLog.toString());
        String agentIp = DevicePropertyUtils.getLocalHostFromSocketAddressStr(agentNettyClientTool.getLocalHostStr());
        taskStatusRequest.setAgentIp(agentIp);
        agentNettyClientTool.sendProtocolMsg(taskStatusRequest);

//        log.info("close current thread");
//        Future<?> future = TaskFutureMap.getTaskFutureMap().get(String.valueOf(messageEntity.getTaskId()));
//        if (future != null){
//            future.cancel(false);
//        }
//        TaskFutureMap.getTaskFutureMap().remove(String.valueOf(messageEntity.getTaskId()));
    }

    /**
     * 实际调用的抽象方法
     * @return 任务返回值
     */
    public abstract String invoke() throws Exception;

    public void initApplication(IDevice currentDevice){
        String appUrl = messageEntity.getAppInfo().getAppUrl();
        try {
            log.info("device: " + currentDevice.getSerialNumber() + " uninstall App");
            currentDevice.uninstallPackage(messageEntity.getAppInfo().getPackageName());
            log.info("device: " + currentDevice.getSerialNumber() + "uninstall success");
        } catch (InstallException e) {
            log.error("device: " + currentDevice.getSerialNumber() + "uninstall APP exception: " + e.getMessage());
            errorLog.append("device: ").append(currentDevice.getSerialNumber()).append("uninstall APP exception: ").append(e.getMessage());
            result = false;
            return;
        }
        String appName = StringUtils.substring(appUrl, appUrl.lastIndexOf("/"));
        String appPath = reportDir + appName;
        File appFile = new File(appPath);
        log.info("=== start download APP ====");
        File downloadedApp = HttpUtil.downloadFileFromUrl(appUrl, appFile, 300000);
        if (downloadedApp == null){
            log.error("device: " + currentDevice.getSerialNumber() + "download APP failed");
            errorLog.append("device: ").append(currentDevice.getSerialNumber()).append("download APP failed");
            result = false;
            return;
        }
        InstallReceiver installReceiver = new InstallReceiver();
        try {
            currentDevice.installPackage(appPath, false, installReceiver);
            boolean successfullyCompleted = installReceiver.isSuccessfullyCompleted();
            log.info(" ==== device：" + currentDevice.getSerialNumber() + " install APP" + (successfullyCompleted ? "success！": "failed！"));
        } catch (InstallException e) {
            log.error("device: " + currentDevice.getSerialNumber() + "install APP exception: " + e.getMessage());
            errorLog.append("device: ").append(currentDevice.getSerialNumber()).append("install APP exception: ").append(e.getMessage());
            result = false;
        }
    }

    public void grantPermissions(IDevice currentDevice){
        String[] permissionsList = {
                "android.permission.READ_PHONE_STATE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_CALENDAR",
                "android.permission.WRITE_CALENDAR",
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO",
                "android.permission.SYSTEM_ALERT_WINDOW"
        };
        for(String item : permissionsList){
            String shellPermission = "pm grant ".concat(String.format("%s", messageEntity.getAppInfo().getPackageName()))
                    .concat(" ")
                    .concat(item);
            CollectingOutputReceiver receiver = new CollectingOutputReceiver();
            try {
                currentDevice.executeShellCommand(shellPermission, receiver);
                log.info("execute permission: " + item + " output: " + receiver.getOutput());
            } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
                log.error("execute permission: " + item + " exception " + e.getMessage());
            }
        }
    }

    public void switchEnv(IDevice currentDevice){
        CollectingOutputReceiver receiver = new CollectingOutputReceiver();
        try {
            currentDevice.executeShellCommand(AppConstants.SWITCH_ENV_CMD, receiver);
            log.info("env switch: " + receiver.getOutput());
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            log.error("env switch exception " + e.getMessage());
        }
    }

}
