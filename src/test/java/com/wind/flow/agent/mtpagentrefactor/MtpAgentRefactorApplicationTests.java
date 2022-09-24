package com.wind.flow.agent.mtpagentrefactor;

import com.wind.flow.agent.mtpagentrefactor.utils.PropertyValueUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MtpAgentRefactorApplicationTests {

    @Resource
    private PropertyValueUtils propertyValueUtils;

    @Test
    void contextLoads() {
        String propertyValueByKey = propertyValueUtils.getPropertyValueByKey("spring.application.name");
        System.out.println(propertyValueByKey);
    }

}
