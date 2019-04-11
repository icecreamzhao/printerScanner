package com.littleboy.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.IOException;
import java.util.List;

public class PrinterFileHandler {
    public static void handler() {
        try {
            // Instantiate with specified consumer group name.
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("print");

            // Specify name server addresses.
            consumer.setNamesrvAddr("localhost:9876");

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
}
