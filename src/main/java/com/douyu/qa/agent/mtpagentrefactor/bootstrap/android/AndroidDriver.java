package com.douyu.qa.agent.mtpagentrefactor.bootstrap.android;

import cn.hutool.core.util.StrUtil;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.douyu.qa.agent.mtpagentrefactor.bootstrap.DeviceBootstrap;
import com.douyu.qa.agent.mtpagentrefactor.utils.PathUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author wunanfang
 */

@ConditionalOnProperty(value = "module.android.enable", havingValue = "true")
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@DependsOn({"agentNettyClient"})
public class AndroidDriver implements ApplicationListener<ContextRefreshedEvent>, DeviceBootstrap {

    public static AndroidDebugBridge androidDebugBridge = null;


    @Resource
    private AndroidDeviceListener androidDeviceListener;


    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        log.info("start Android module......");
        init();
    }

    @Override
    public void init(){
        String systemAdbPath = PathUtils.getSystemAdbPath();
        if (StrUtil.isBlank(systemAdbPath)){
            log.info("access Android ADB path failed，Android module shutdown");
            return;
        }
        // 添加设备上下线监听器
        AndroidDebugBridge.addDeviceChangeListener(androidDeviceListener);
        try {
            AndroidDebugBridge.init(false);
            androidDebugBridge = AndroidDebugBridge.createBridge(systemAdbPath, true, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            if (androidDebugBridge == null){
                log.error("initialize ADB failed，Android module shutdown");
                return;
            }
            log.info("ADB start success，listen device ......");
        }catch (IllegalStateException e){
            log.error("ADB already initialized: " + e.getMessage());
        }
    }

    /**
     * 获取当前在线的设备数组
     */
    public static IDevice[] getCurrentOnlineDevices(){
        if (androidDebugBridge != null){
            return androidDebugBridge.getDevices();
        }
        return null;
    }

    /**
     * 通过udid获取实际在线的设备
     */
    public static IDevice getDeviceByUdId(String udId){
        IDevice[] currentOnlineDevices = AndroidDriver.getCurrentOnlineDevices();
        if (currentOnlineDevices == null || currentOnlineDevices.length == 0){
            return null;
        }
        IDevice iDevice = null;
        for (IDevice device : currentOnlineDevices){
            // 序列号相等，且在线的状态
            if (device.getSerialNumber().equals(udId) && IDevice.DeviceState.ONLINE.equals(device.getState())){
                iDevice = device;
                break;
            }
        }
        if (iDevice == null){
            log.info("device 【" + udId + "】 not connected");
        }
        return iDevice;
    }



}
