package org.example;

public class WeatherConsumer {
    public static void main(String[] args) {
        try (WeatherConsumerService service = new WeatherConsumerService()) {
            Runtime.getRuntime().addShutdownHook(new Thread(service::close));

            service.consumeLoop(record -> {
                System.out.printf("Получено: %s => %s%n", record.key(), record.value());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}