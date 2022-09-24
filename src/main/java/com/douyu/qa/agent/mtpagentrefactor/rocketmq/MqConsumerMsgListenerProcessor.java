package com.douyu.qa.agent.mtpagentrefactor.rocketmq;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.douyu.qa.agent.mtpagentrefactor.common.cache.TaskFutureMap;
import com.douyu.qa.agent.mtpagentrefactor.common.entity.MessageEntity;
import com.douyu.qa.agent.mtpagentrefactor.common.enums.TaskTypeEnum;
import com.douyu.qa.agent.mtpagentrefactor.task.Runner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author wunanfang
 */
@Component
@Slf4j
public class MqConsumerMsgListenerProcessor implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(msgList)){
            log.info("MQ messageList empty!");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt message = msgList.get(0);
        String tag = message.getTags();
        String taskId = message.getKeys();
        String msgId = message.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX);
        String businessMsg = new String(message.getBody(), StandardCharsets.UTF_8);
        MessageEntity messageEntity = JSON.parseObject(businessMsg, MessageEntity.class);
        if (StringUtils.isBlank(messageEntity.getPlatform())){
            log.error("==== device type not found, consume end! ===");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        log.info(tag + "_" + messageEntity.getPlatform().toLowerCase());
        Class<? extends Runner> targetTaskClass = TaskTypeEnum.getClassByName(tag + "_" + messageEntity.getPlatform().toLowerCase());
        if (targetTaskClass == null){
            log.error("==== task type not found, consume end! ===");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        try {
            Constructor<? extends Runner> constructor = targetTaskClass.getConstructor();
            Runner runner = constructor.newInstance();
            Future<?> future = ThreadUtil.execAsync(() -> {
                log.info("==== start init method =====");
                runner.init(tag, taskId, msgId, messageEntity);
                log.info("=== start before method ====");
                runner.before();
                log.info("=== start run method ===");
                runner.run();
                log.info("=== start after method ===");
                runner.after();
            });
            TaskFutureMap.getTaskFutureMap().put(taskId, future);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("reflect process exception: " + e.getMessage());
        }
        log.info("message consumed success!!");
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
