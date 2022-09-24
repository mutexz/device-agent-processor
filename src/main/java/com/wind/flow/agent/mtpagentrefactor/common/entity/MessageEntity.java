package com.wind.flow.agent.mtpagentrefactor.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author wunanfang
 */
@Slf4j
@Getter
@Setter
@ToString
public class MessageEntity {

    private Long taskId;

    private String taskType;

    private String deviceId;

    private AppInfoEntity appInfo;

    private AccountInfoEntity accountInfoEntity;

    private String paramObjStr;

    private Date createTime;

    private Long startCount;

    private String source;

    private String reportDir;

    private String deviceType;

    private String platform;
}
