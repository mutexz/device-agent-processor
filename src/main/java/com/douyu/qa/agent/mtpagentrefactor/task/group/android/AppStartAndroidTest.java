package com.douyu.qa.agent.mtpagentrefactor.task.group.android;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.ddmlib.*;
import com.douyu.qa.agent.mtpagentrefactor.bootstrap.android.AndroidDriver;
import com.douyu.qa.agent.mtpagentrefactor.common.constant.AppConstants;
import com.douyu.qa.agent.mtpagentrefactor.common.entity.MessageEntity;
import com.douyu.qa.agent.mtpagentrefactor.common.signal.ThreadCommunicationSignal;
import com.douyu.qa.agent.mtpagentrefactor.netty.AgentNettyClientTool;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.request.AppStartAndroidRequest;
import com.douyu.qa.agent.mtpagentrefactor.task.BaseRunner;
import com.douyu.qa.agent.mtpagentrefactor.task.group.android.appstart.LogServiceRunnable;
import com.douyu.qa.agent.mtpagentrefactor.utils.SpringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 性能测试
 * @author wunanfang
 */
@Slf4j
@Getter
@Setter
@ToString
public class AppStartAndroidTest extends BaseRunner {

    private IDevice currentDevice;
    private File appLaunchFile;
    private AgentNettyClientTool agentNettyClientTool = SpringUtils.getBean(AgentNettyClientTool.class);


    @Override
    public void init(String business, String taskId, String msgId, MessageEntity messageEntity) {
        super.init(business, taskId, msgId, messageEntity);
        // 解析msg，获得对应的MessageEntity对象
        String deviceId = messageEntity.getDeviceId();
        if (StrUtil.isBlank(deviceId)){
            result = false;
            errorLog.append("device not found ");
            return;
        }
        currentDevice = AndroidDriver.getDeviceByUdId(deviceId);
        String appLaunchFilePath = this.reportDir + File.separator + "App_Launch_" + messageEntity.getDeviceId() + ".txt";
        appLaunchFile = new File(appLaunchFilePath);
        try {
            if (appLaunchFile.exists()){
                appLaunchFile.delete();
            }
            boolean newFile = appLaunchFile.createNewFile();
            if (newFile){
                log.info("file create success");
            } else {
                log.info("file already exist");
            }
        } catch (IOException e) {
            log.info("file create exception: " + e.getMessage());
        }
    }

    @Override
    public void before() {
        super.before();
//        initApplication(currentDevice);
        grantPermissions(currentDevice);
        switchEnv(currentDevice);

    }

    @Override
    public void run() {
        Future<?> future = ThreadUtil.execAsync(new LogServiceRunnable(AppConstants.APP_COLD_START_LOG, currentDevice, appLaunchFile));
        IShellOutputReceiver shellOutputReceiver = new NullOutputReceiver();
        try {
            currentDevice.executeShellCommand(AppConstants.APP_COLD_START, shellOutputReceiver, 3600, TimeUnit.SECONDS);
            if (!future.isDone()){
                future.cancel(true);
            }
        } catch (Exception e) {
            log.error("task exception: " + e.getMessage());
        }
    }

    @Override
    public void after() {
        // 提取数据，上传至服务端
        String regex = "\\(.*\\)";
        extractAppStartInfo(appLaunchFile, regex);
        log.info("task" + messageEntity.getTaskId() + "execute finished");

        super.after();
    }

    @Override
    public String invoke() {
        return null;
    }


//    public void initApplication() {
//        String appUrl = messageEntity.getAppInfo().getAppUrl();
//        try {
//            log.info("device: " + currentDevice.getSerialNumber() + " uninstall App");
//            currentDevice.uninstallPackage(messageEntity.getAppInfo().getPackageName());
//            log.info("device: " + currentDevice.getSerialNumber() + "uninstall success");
//        } catch (InstallException e) {
//            log.error("device: " + currentDevice.getSerialNumber() + "uninstall APP exception: " + e.getMessage());
//            errorLog.append("device: ").append(currentDevice.getSerialNumber()).append("uninstall APP exception: ").append(e.getMessage());
//            result = false;
//            return;
//        }
//        String appName = StringUtils.substring(appUrl, appUrl.lastIndexOf("/"));
//        String appPath = reportDir + appName;
//        File appFile = new File(appPath);
//        log.info("=== start download APP ====");
//        File downloadedApp = HttpUtil.downloadFileFromUrl(appUrl, appFile, 300000);
//        if (downloadedApp == null){
//            log.error("device: " + currentDevice.getSerialNumber() + "download APP failed");
//            errorLog.append("device: ").append(currentDevice.getSerialNumber()).append("download APP failed");
//            result = false;
//            return;
//        }
//        InstallReceiver installReceiver = new InstallReceiver();
//        try {
//            currentDevice.installPackage(appPath, false, installReceiver);
//            boolean successfullyCompleted = installReceiver.isSuccessfullyCompleted();
//            log.info(" ==== device：" + currentDevice.getSerialNumber() + " install APP" + (successfullyCompleted ? "success！": "failed！"));
//        } catch (InstallException e) {
//            log.error("device: " + currentDevice.getSerialNumber() + "install APP exception: " + e.getMessage());
//            errorLog.append("device: ").append(currentDevice.getSerialNumber()).append("install APP exception: ").append(e.getMessage());
//            result = false;
//        }
//    }

//    private void grantPermissions(){
//        String[] permissionsList = {
//                "android.permission.READ_PHONE_STATE",
//                "android.permission.WRITE_EXTERNAL_STORAGE",
//                "android.permission.READ_EXTERNAL_STORAGE",
//                "android.permission.ACCESS_COARSE_LOCATION",
//                "android.permission.ACCESS_FINE_LOCATION",
//                "android.permission.READ_CALENDAR",
//                "android.permission.WRITE_CALENDAR",
//                "android.permission.CAMERA",
//                "android.permission.RECORD_AUDIO",
//                "android.permission.SYSTEM_ALERT_WINDOW"
//        };
//        for(String item : permissionsList){
//            String shellPermission = "pm grant ".concat(String.format("%s", messageEntity.getAppInfo().getPackageName()))
//                    .concat(" ")
//                    .concat(item);
//            CollectingOutputReceiver receiver = new CollectingOutputReceiver();
//            try {
//                currentDevice.executeShellCommand(shellPermission, receiver);
//                log.info("execute permission: " + item + " output: " + receiver.getOutput());
//            } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
//                log.error("execute permission: " + item + " exception " + e.getMessage());
//            }
//        }
//    }
//
//    private void switchEnv(){
//        CollectingOutputReceiver receiver = new CollectingOutputReceiver();
//        try {
//            currentDevice.executeShellCommand(AppConstants.SWITCH_ENV_CMD, receiver);
//            log.info("env switch: " + receiver.getOutput());
//        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
//            log.error("env switch exception " + e.getMessage());
//        }
//    }

    private void extractAppStartInfo(File file, String regex){
        if (file == null || !file.exists() || FileUtil.isEmpty(file)){
            log.error("App_Launch.txt empty");
            return;
        }
        List<String> logList = FileUtil.readLines(file, StandardCharsets.UTF_8);
        if (CollectionUtils.isEmpty(logList)){
            log.error("App_Launch.txt empty");
            return;
        }
        AppStartAndroidRequest request;
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        for (String content : logList){
            // 过滤空白行以及非法行
            if (StringUtils.isBlank(content) || !content.contains("total")){
                continue;
            }
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String result= matcher.group();
                String firstContent = StrUtil.split(result, "+").get(1);
                String secondContent = StrUtil.split(firstContent, "ms").get(0);
                int startTime;
                if (StringUtils.isNumeric(secondContent)){
                    // 对于只有毫秒的场景
                    startTime = Integer.parseInt(secondContent);
                }else{
                    // 对于1s50ms这种情况
                    String[] timeSplit = StringUtils.split(secondContent, "s");
                    //解析数据为"1s"的特殊情况
                    if (timeSplit.length == 1){
                        startTime = Integer.parseInt(timeSplit[0]) * 1000;
                    } else {
                        startTime = Integer.parseInt(timeSplit[0]) * 1000 + Integer.parseInt(timeSplit[1]);
                    }
                }
                request = new AppStartAndroidRequest();
                request.setAppStartTime(startTime);
                request.setPlatform("Android");
                request.setTaskId(messageEntity.getTaskId());
                request.setDeviceId(messageEntity.getDeviceId());

                agentNettyClientTool.sendProtocolMsg(request);
            }
        }
    }
}
