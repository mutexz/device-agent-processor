package com.wind.flow.agent.mtpagentrefactor.rocketmq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author wunanfang
 */

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "rocketmq.consumer")
@Slf4j
public class MqConsumerConfig {

    @Resource
    private MqConsumerMsgListenerProcessor mqConsumerMsgListenerProcessor;

    private String groupName;
    private String nameSrvAddr;
    private String topic;
    private Integer consumerThreadMin;
    private Integer consumerThreadMax;
    private Integer consumerMessageBatchMaxSize;


    @Bean
    @ConditionalOnProperty(prefix = "rocketmq.consumer", value = "isOnOff", havingValue = "true")
    public DefaultMQPushConsumer defaultMQPushConsumer() {
        log.info("=== MQ start listen ===");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(nameSrvAddr);
        consumer.setConsumeThreadMin(consumerThreadMin);
        consumer.setConsumeThreadMax(consumerThreadMax);
        consumer.setConsumeMessageBatchMaxSize(consumerMessageBatchMaxSize);
        consumer.setMessageListener(mqConsumerMsgListenerProcessor);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        try{
            String[] topicTagArr = StringUtils.split(topic, ";");
            if (topicTagArr == null || topicTagArr.length == 0){
                log.error("topic and tag not set");
            } else {
                for (String item : topicTagArr){
                    String[] split = StringUtils.split(item, "~");
                    consumer.subscribe(split[0], split[1]);
                }
                consumer.start();
                log.info("=== MQ consumer subscribe Topic:{} success ===", topic);
            }
        }catch (Exception e){
            log.error("MQ consumer start exception: " + e.getMessage());
        }
        return consumer;
    }

}
