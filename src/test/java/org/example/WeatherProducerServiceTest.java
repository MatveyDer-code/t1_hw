package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherProducerServiceTest {

    @Test
    void testGenerateMessageFormat() {
        WeatherProducerService service = new WeatherProducerService(null);
        String message = service.generateMessage();

        String[] parts = message.split(",");

        assertEquals(3, parts.length, "Сообщение должно состоять из трёх частей");

        String city = parts[0];
        int temp = Integer.parseInt(parts[1]);
        String condition = parts[2];

        assertTrue(temp >= 0 && temp < 36, "Температура должна быть в пределах 0–35");
        assertTrue(city.matches("Москва|Питер|Магадан|Чукотка|Тюмень"), "Неожиданный город");
        assertTrue(condition.matches("солнечно|облачно|дождь"), "Неожиданное погодное условие");
    }

    @org.junit.jupiter.api.Test
    void testSendMessageCallsKafkaProducer() {
        Producer<String, String> mockProducer = mock(Producer.class);
        WeatherProducerService service = new WeatherProducerService(mockProducer);

        String topic = "weather-topic";
        String key = "Москва";
        String value = "Москва,25,солнечно";

        service.sendMessage(topic, key, value);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass((Class) ProducerRecord.class);
        verify(mockProducer, times(1)).send(recordCaptor.capture(), any());
        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals(topic, record.topic());
        assertEquals(key, record.key());
        assertEquals(value, record.value());
    }
}