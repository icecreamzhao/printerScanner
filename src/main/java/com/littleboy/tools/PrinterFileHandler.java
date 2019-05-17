package com.littleboy.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class PrinterFileHandler {
    public static void handler() {
        try {
            // Instantiate with specified ` group name.
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("print");

            // Specify name server addresses.
            consumer.setNamesrvAddr("123.56.217.55:9876");

            // Subscribe one more more topics to consume.
            consumer.subscribe("PrintFileIsComing", "*");
            // Register callback to execute on arrival of messages fetched from brokers.
            consumer.registerMessageListener(new MessageListenerConcurrently() {

                @Override
                public ConsumeConcurrentlyStatus consumeMessage(
                        List<MessageExt> msgs,
                        ConsumeConcurrentlyContext context) {
                    for (MessageExt messageExt : msgs) {
                        String result = new String(messageExt.getBody());
                        try {
                            System.out.println(result);
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode rootNode = mapper.readTree(result);
                            String fileUrl = rootNode.path("fileUrl").asText();
                            System.out.println(fileUrl);
                            HttpUtil.sendPost(fileUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            //Launch the consumer instance.
            consumer.start();

            System.out.printf("Consumer Started.%n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushCode(String code) {
        try {
            // 消息生产者
            DefaultMQProducer producer = new DefaultMQProducer("print");
            producer.setInstanceName(UUID.randomUUID().toString());
            // Specify name server addresses.
            producer.setVipChannelEnabled(false);
            producer.setNamesrvAddr("123.56.217.55:9876");
            //Launch the instance.
            producer.start();
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("printerCode" /* Topic */,
                    "TagA" /* Tag */,
                    (code).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg, 10000);
            System.out.printf("%s%n", sendResult);

            //Shut down once the producer instance is not longer in use.
            producer.shutdown();
        } catch (UnsupportedEncodingException | MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
