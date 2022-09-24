package com.douyu.qa.agent.mtpagentrefactor.utils;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 路径查找工具类
 * @author wunanfang
 */
@Slf4j
public class PathUtils {

    /**
     * 查找系统ADB路径
     */
    public static String getSystemAdbPath(){
        String path = System.getenv("ANDROID_HOME");
        if (StrUtil.isNotBlank(path)){
            path += File.separator + "platform-tools" + File.separator + "adb";
        } else {
            log.info("access ANDROID_HOME file path");
            return null;
        }
        return path;
    }

    public static String getCurrentPathDir(){
        try {
            return new File(".").getCanonicalPath();
        } catch (IOException e) {
            log.error("access root path failed ....");
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(getCurrentPathDir());
    }

}
