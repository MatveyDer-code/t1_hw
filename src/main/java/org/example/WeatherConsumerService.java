package org.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class WeatherConsumerService implements AutoCloseable {

    private final Consumer<String, String> consumer;
    private volatile boolean closed = false;

    public WeatherConsumerService() {
        this(createDefaultConsumer());
    }

    public WeatherConsumerService(Consumer<String, String> consumer) {
        this.consumer = consumer;
        this.consumer.subscribe(Collections.singletonList("weather-topic"));
    }

    private static KafkaConsumer<String, String> createDefaultConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("group.id", "weather-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        return new KafkaConsumer<>(props);
    }

    public void consumeLoop(java.util.function.Consumer<ConsumerRecord<String, String>> processRecord) {
        try {
            while (!closed) {
                var records = consumer.poll(Duration.ofMillis(500));
                for (var record : records) {
                    processRecord.accept(record);
                }
            }
        } catch (WakeupException e) {
            if (!closed) throw e;
        } finally {
            consumer.close();
        }
    }

    @Override
    public void close() {
        closed = true;
        consumer.wakeup();
    }
}