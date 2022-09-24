package com.wind.flow.agent.mtpagentrefactor.utils;

import com.android.ddmlib.IDevice;
import com.wind.flow.agent.mtpagentrefactor.netty.protocol.request.DeviceStatusRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wunanfang
 * 获取设备信息工具类--参考sonic
 */
@Slf4j
public class DevicePropertyUtils {

    public static DeviceStatusRequest getDeviceInfo(IDevice device, Integer onlineStatus, Integer busyStatus){
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId(device.getSerialNumber());
        deviceStatusRequest.setBrand(device.getProperty("ro.product.name"));
        deviceStatusRequest.setModel(device.getProperty(IDevice.PROP_DEVICE_MODEL));
        deviceStatusRequest.setStatus(onlineStatus);
        deviceStatusRequest.setPlatform("Android");
        deviceStatusRequest.setOsVersion(device.getProperty(IDevice.PROP_BUILD_VERSION));
        deviceStatusRequest.setType("phone");
        deviceStatusRequest.setBusyState(busyStatus);
        return deviceStatusRequest;
    }

    /**
     * 从SocketAddress对象字符串中解析本地IP
     * SocketAddress格式：/127.0.0.1:12345
     */
    public static String getLocalHostFromSocketAddressStr(String socketAddressStr){
        int firstIndex = socketAddressStr.indexOf("/");
        int lastIndex = socketAddressStr.indexOf(":");
        return socketAddressStr.substring(firstIndex + 1, lastIndex);
    }

}
