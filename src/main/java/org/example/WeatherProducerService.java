package org.example;

import org.apache.kafka.clients.producer.*;

import java.util.Random;

public class WeatherProducerService {
    private final Producer<String, String> producer;
    private final Random rand = new Random();

    private final String[] cities = {"Москва", "Питер", "Магадан", "Чукотка", "Тюмень"};
    private final String[] conditions = {"солнечно", "облачно", "дождь"};

    public WeatherProducerService(Producer<String, String> producer) {
        this.producer = producer;
    }

    public String generateMessage() {
        String city = cities[rand.nextInt(cities.length)];
        int temp = rand.nextInt(36);
        String condition = conditions[rand.nextInt(conditions.length)];
        return city + "," + temp + "," + condition;
    }

    public void sendMessage(String topic, String key, String value) {
        producer.send(new ProducerRecord<>(topic, key, value),
                (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Ошибка отправки: " + exception.getMessage());
                    }
                }
        );
    }
}