package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class WeatherProducer {
    public static void main(String[] args) throws InterruptedException {
        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
        WeatherProducerService service = new WeatherProducerService(kafkaProducer);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::close));

        while (true) {
            String message = service.generateMessage();
            String city = message.split(",")[0];
            service.sendMessage("weather-topic", city, message);
            System.out.println("Отправлено: " + message);
            Thread.sleep(2000);
        }
    }
}