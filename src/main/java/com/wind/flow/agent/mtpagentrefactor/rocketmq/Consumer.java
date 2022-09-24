package com.wind.flow.agent.mtpagentrefactor.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态注册消费者类，这样可以根据业务，灵活采取topic来注册消费者
 * @author wunanfang
 */
public class Consumer {

	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

	private DefaultMQPushConsumer consumer;

	private String topic;

    public Consumer(String type, String id) {
		MessageModel model = MessageModel.CLUSTERING;
    	switch(type){
			case "device":
				topic = "mtp_device_" + id;
				break;
            case "interface":
                topic ="mtp_interface_test";
                break;
			case "terminate":
				topic = "mtp_task_" + id;
				model = MessageModel.BROADCASTING;
				break;
			case "device_process":
				// 这里用BROADCASTING是因为确保进程在所有的执行器上都被kill掉
				topic = "mtp_device_task";
				logger.info("终止任务命令！！！");
				model = MessageModel.BROADCASTING;
				break;
			case "device_process_kill":
				topic = "mtp_device_task_kill";
				model = MessageModel.BROADCASTING;
				break;
			default:
				break;
		}
    	try {
    		consumer = new DefaultMQPushConsumer("mtp-agent-refactor-" + id);
			consumer.setNamesrvAddr("dytest.dz11.com:9876");
			consumer.subscribe(topic, "*");
			consumer.setInstanceName(id);
			consumer.setMessageModel(model);
			consumer.registerMessageListener(new MqConsumerMsgListenerProcessor());
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			//每次只拉取一条消息
			consumer.setPullBatchSize(1);
			//一次最多只消费一条
			consumer.setConsumeMessageBatchMaxSize(1);
			consumer.setClientCallbackExecutorThreads(1);
			consumer.setConsumeThreadMax(1);
			consumer.setConsumeThreadMin(1);
			consumer.setMaxReconsumeTimes(2);
			//单次消费不超过8小时，否则消费失败
			consumer.setConsumeTimeout(8 * 60);
			consumer.start();
    		logger.info(String.format("---------- Consumer:[%s] started, [%s] subscribed ----------", id, topic));
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public void shutdown() {
    	consumer.shutdown();
    	logger.info("---------- Consumer:[" + topic + "] stoped ----------");
    }

}
