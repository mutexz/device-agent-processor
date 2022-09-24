package com.douyu.qa.agent.mtpagentrefactor.common.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wunanfang
 */

@Slf4j
@Data
public class AppInfoEntity {

    private String appName;

    private String appVersion;

    private String appVersionCode;

    private String packageName;

    private String launchActivity;

    private String appUrl;

    private String dsymUrl;
}
