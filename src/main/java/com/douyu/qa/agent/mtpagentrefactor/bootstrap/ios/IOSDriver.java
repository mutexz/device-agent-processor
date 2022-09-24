package com.douyu.qa.agent.mtpagentrefactor.bootstrap.ios;

import com.douyu.qa.agent.mtpagentrefactor.bootstrap.DeviceBootstrap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * iOS设备驱动类
 * @author wunanfang
 */

@ConditionalOnProperty(value = "module.ios.enable", havingValue = "true")
@Component
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class IOSDriver implements ApplicationListener<ContextRefreshedEvent>, DeviceBootstrap {


    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {

    }

    @Override
    public void init() {

    }
}
