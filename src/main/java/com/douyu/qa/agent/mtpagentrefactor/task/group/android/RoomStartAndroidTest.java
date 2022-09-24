package com.douyu.qa.agent.mtpagentrefactor.task.group.android;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.NullOutputReceiver;
import com.douyu.qa.agent.mtpagentrefactor.bootstrap.android.AndroidDriver;
import com.douyu.qa.agent.mtpagentrefactor.common.cache.TaskFutureMap;
import com.douyu.qa.agent.mtpagentrefactor.common.constant.AppConstants;
import com.douyu.qa.agent.mtpagentrefactor.common.entity.MessageEntity;
import com.douyu.qa.agent.mtpagentrefactor.netty.AgentNettyClientTool;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.request.RoomStartRequest;
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
 * 直播间启动起播
 * @author wunanfang
 */
@Slf4j
@Getter
@Setter
@ToString
public class RoomStartAndroidTest extends BaseRunner {

    private IDevice currentDevice;
    private File horizonFile;
    private File beautyLiveFile;

    private AgentNettyClientTool agentNettyClientTool = SpringUtils.getBean(AgentNettyClientTool.class);


    @Override
    public void init(String business, String taskId, String msgId, MessageEntity messageEntity) {
        super.init(business, taskId, msgId, messageEntity);
        String deviceId = messageEntity.getDeviceId();
        if (StrUtil.isBlank(deviceId)){
            result = false;
            errorLog.append("device not found ");
            return;
        }
        currentDevice = AndroidDriver.getDeviceByUdId(deviceId);
        String horizonFilePath = this.reportDir + File.separator + "Horizon_Live_" + messageEntity.getDeviceId() + ".txt";
        horizonFile = new File(horizonFilePath);
        String beautyLiveFilePath = this.reportDir + File.separator + "Beauty_Live_" + messageEntity.getDeviceId() + ".txt";
        beautyLiveFile = new File(beautyLiveFilePath);
        if (horizonFile.exists()){
            horizonFile.delete();
        }
        if (beautyLiveFile.exists()){
            beautyLiveFile.delete();
        }
        try {
            horizonFile.createNewFile();
            beautyLiveFile.createNewFile();
            log.info("file created success!");
        } catch (IOException e) {
            log.error("file create exception: " + e.getMessage());
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
        // 第一步 执行起播
        Future<?> horizonFuture = ThreadUtil.execAsync(new LogServiceRunnable(AppConstants.ROOM_START_LOG, currentDevice, horizonFile));
        IShellOutputReceiver shellOutputReceiver = new NullOutputReceiver();
        try {
            currentDevice.executeShellCommand(AppConstants.INTO_PLAYER_TENCENT, shellOutputReceiver, 3600, TimeUnit.SECONDS);
            if (!horizonFuture.isDone()){
                horizonFuture.cancel(true);
            }
        } catch (Exception e) {
            log.error("horizonFuture exception: " + e.getMessage());
        }
//        try {
//            // 休眠十秒后继续执行颜值直播间任务
//            log.info("task rest for 10s");
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            log.error("task rest exception: " + e.getMessage());
//        }
//        Future<?> beautyFuture = ThreadUtil.execAsync(new LogServiceRunnable(AppConstants.ROOM_START_LOG, currentDevice, horizonFile));
////        IShellOutputReceiver shellOutputReceiver = new NullOutputReceiver();
//        try {
//            currentDevice.executeShellCommand(AppConstants.INTO_MOBILE_PLAYER, shellOutputReceiver, 3600, TimeUnit.SECONDS);
//            if (!beautyFuture.isDone()){
//                beautyFuture.cancel(false);
//            }
//        } catch (Exception e) {
//            log.error("horizonFuture exception: " + e.getMessage());
//        }
    }

    @Override
    public void after() {
        String regex = "\\{.*}";
        extractInfoFromFile(horizonFile, regex, 2);
        extractInfoFromFile(beautyLiveFile, regex, 1);
        log.info("room start task finished!");

        super.after();
    }

    @Override
    public String invoke() throws Exception {
        return null;
    }

    private void extractInfoFromFile(File file, String regex, int type){
        if (file == null || !file.exists() || FileUtil.isEmpty(file)){
            log.error("room start file empty!");
            return;
        }
        List<String> stringList = FileUtil.readLines(file, StandardCharsets.UTF_8);
        if (CollectionUtils.isEmpty(stringList)){
            log.error("room start file empty!");
            return;
        }
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        for (String content : stringList){
            if (StringUtils.isBlank(content) || !content.contains("ac:rml_fs_c")){
                continue;
            }
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String result= matcher.group();
                RoomStartRequest request = JSON.parseObject(result, RoomStartRequest.class);
                request.setS_type(Long.valueOf(String.valueOf(type)));
                request.setTask_id(messageEntity.getTaskId());
                request.setDevice_id(messageEntity.getDeviceId());
                request.setPlatform("Android");
                log.info("room start test upload!");
                agentNettyClientTool.sendProtocolMsg(request);
            }
        }
    }

    public static void main(String[] args) {
        String filePath = "D:\\mtp_workspace\\Horizon_Live.txt";
        File file = new File(filePath);
        List<String> stringList = FileUtil.readLines(file, StandardCharsets.UTF_8);
        String regex = "\\{.*}";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        for (String item : stringList){
            Matcher matcher = pattern.matcher(item);
            if (matcher.find()) {
                String result= matcher.group();
                System.out.println(result);
                JSONObject jsonObject = JSON.parseObject(result);
                System.out.println("json: " + jsonObject);
            }
        }
    }
}
