package com.backendwork.backendApp.config;

import com.backendwork.backendApp.events.TicketEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    // ===============================
    // PRODUCER
    // ===============================

    @Bean
    public ProducerFactory<String, TicketEvent> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JacksonJsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, TicketEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    // ===============================
    // CONSUMER
    // ===============================

    @Bean
    public ConsumerFactory<String, TicketEvent> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "ticket-service-group"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );

        config.put(
                "spring.json.trusted.packages",
                "com.backendwork.backendApp.events"
        );

        config.put(
                "spring.json.value.default.type",
                "com.backendwork.backendApp.events.TicketEvent"
        );

        return new DefaultKafkaConsumerFactory<>(config);
    }


    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TicketEvent>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TicketEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }


    // ===============================
    // TOPIC
    // ===============================

    @Bean
    public NewTopic ticketEventsTopic() {

        return new NewTopic(
                "ticket-events",
                3,
                (short) 1
        );
    }
}