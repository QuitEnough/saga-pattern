package by.javaguru.orders.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Value("${orders.events.topic.name}")
    private String ordersEventsTopicName;

    @Value("${product.command.topic.name}")
    private String productCommandsTopicName;

    @Value("${payment.commands.topic.name}")
    private String paymentCommandsTopicName;

    @Value("${orders.commands.topic.name}")
    private String ordersCommandsTopicName;

    private final static Integer TOPIC_REPLICATION_FACTOR=3;
    private final static Integer TOPIC_PARTITION=3;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createOrdersEventsTopic() {
        return TopicBuilder
                .name(ordersEventsTopicName)
                .partitions(TOPIC_PARTITION)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    NewTopic createProductCommandTopic() {
        return TopicBuilder
                .name(productCommandsTopicName)
                .partitions(TOPIC_PARTITION)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    NewTopic createPaymentCommandTopic() {
        return TopicBuilder
                .name(paymentCommandsTopicName)
                .partitions(TOPIC_PARTITION)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    NewTopic createOrdersCommandTopic() {
        return TopicBuilder
                .name(ordersCommandsTopicName)
                .partitions(TOPIC_PARTITION)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

}
