package com.douyu.qa.agent.mtpagentrefactor.bootstrap.android;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.douyu.qa.agent.mtpagentrefactor.common.cache.DeviceConsumerCache;
import com.douyu.qa.agent.mtpagentrefactor.rocketmq.Consumer;
import com.douyu.qa.agent.mtpagentrefactor.netty.AgentNettyClientTool;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.request.DeviceStatusRequest;
import com.douyu.qa.agent.mtpagentrefactor.utils.DevicePropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Android设备上下线监听组件
 * @author wunanfang
 */
@Component
@Slf4j
public class AndroidDeviceListener implements AndroidDebugBridge.IDeviceChangeListener {

    @Resource
    private AgentNettyClientTool agentNettyClientTool;


    @Override
    public void deviceConnected(IDevice device) {
        log.info("Android device: " + device.getSerialNumber() + " already online ......");
        // 当程序初始化时设备已经连接的情况下，设备的状态时ONLINE，会直接走这个逻辑
        if (device.getState().equals(IDevice.DeviceState.ONLINE)){
            sendOnlineNettyMsg(device);
        }
    }

    @Override
    public void deviceDisconnected(IDevice device) {
        log.info("Android device: " + device.getSerialNumber() + " already offline......");
        String localHostStr = agentNettyClientTool.getLocalHostStr();
        if (StrUtil.isBlank(localHostStr)){
            log.info("=== IP info access failed! ===");
            return;
        }
        DeviceStatusRequest offlineDeviceInfo = DevicePropertyUtils.getDeviceInfo(device, 0, 0);
        String addressStr = DevicePropertyUtils.getLocalHostFromSocketAddressStr(localHostStr);
        offlineDeviceInfo.setHost(addressStr);
        log.info("message from server when device offline ---> {}", JSON.toJSONString(offlineDeviceInfo));
        agentNettyClientTool.sendProtocolMsg(offlineDeviceInfo);
        // 移除设备消费者
        Consumer consumer = DeviceConsumerCache.getDeviceConsumerCacheMap().get(device.getSerialNumber());
        if (consumer != null){
            consumer.shutdown();
            log.info("device: " + device.getSerialNumber() + " shutdown consumer success");
        }
    }

    @Override
    public void deviceChanged(IDevice device, int changeMask) {
        IDevice.DeviceState state = device.getState();
        // 但是当设备出现拔插的情况下，connected方法获取设备的状态为OFFLINE，获取不到部分设备信息
        // 因此要在deviceChanged方法下判断设备是否处于ONLINE状态，这里会当设备连接时，完成OFFLINE--->ONLINE的转换
        // 因此要在这里进行一个判断和发送！
        if (state == IDevice.DeviceState.ONLINE){
            sendOnlineNettyMsg(device);
        }
    }

    private void sendOnlineNettyMsg(IDevice device) {
        String localHostStr = agentNettyClientTool.getLocalHostStr();
        if (StrUtil.isBlank(localHostStr)){
            log.info("=== IP info access failed! ===");
            return;
        }
        log.info("localHostStr: " + localHostStr);
        DeviceStatusRequest onlineDeviceInfo = DevicePropertyUtils.getDeviceInfo(device, 1, 0);
        String addressStr = DevicePropertyUtils.getLocalHostFromSocketAddressStr(localHostStr);
        onlineDeviceInfo.setHost(addressStr);
        log.info("message from server when device online ---> {}", JSON.toJSONString(onlineDeviceInfo));
        agentNettyClientTool.sendProtocolMsg(onlineDeviceInfo);
        Consumer consumer = new Consumer("device", device.getSerialNumber());
        log.info("device: " + device.getSerialNumber() + " register consumer success");
        DeviceConsumerCache.getDeviceConsumerCacheMap().put(device.getSerialNumber(), consumer);
    }
}
