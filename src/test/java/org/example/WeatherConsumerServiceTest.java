package org.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class WeatherConsumerServiceTest {

    @Test
    void testConsumeLoopProcessesRecords() {
        KafkaConsumer<String, String> mockConsumer = mock(KafkaConsumer.class);
        java.util.function.Consumer<ConsumerRecord<String, String>> processRecord = mock(java.util.function.Consumer.class);

        ConsumerRecord<String, String> record = new ConsumerRecord<>("weather-topic", 0, 0L, "key", "value");

        ConsumerRecords<String, String> records = new ConsumerRecords<>(
                Collections.singletonMap(new TopicPartition("weather-topic", 0),
                        Collections.singletonList(record))
        );

        WeatherConsumerService service = new WeatherConsumerService(mockConsumer);

        when(mockConsumer.poll(any(Duration.class)))
                .thenReturn(records)
                .thenAnswer(invocation -> {
                    service.close();
                    throw new WakeupException();
                });

        service.consumeLoop(processRecord);

        verify(processRecord, times(1)).accept(record);
        verify(mockConsumer, times(1)).close();
    }

    @Test
    void testCloseTriggersWakeup() {
        Consumer<String, String> mockConsumer = mock(Consumer.class);
        WeatherConsumerService service = new WeatherConsumerService(mockConsumer);

        service.close();

        verify(mockConsumer).wakeup();
    }
}