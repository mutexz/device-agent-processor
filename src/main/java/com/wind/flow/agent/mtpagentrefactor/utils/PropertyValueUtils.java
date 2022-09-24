package com.wind.flow.agent.mtpagentrefactor.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @author nanfang
 */
@Component
@Slf4j
public class PropertyValueUtils implements EmbeddedValueResolverAware {

    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        log.info("initialized PropertyValueUtils......");
        this.stringValueResolver = resolver;
    }

    /**
     * 通过key获取对应value
     */
    public String getPropertyValueByKey(String key){
        return stringValueResolver.resolveStringValue("${" + key + "}");
    }
}
