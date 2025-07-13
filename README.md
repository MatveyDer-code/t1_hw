# Weather Kafka App

Простой учебный проект на Java с использованием Apache Kafka.  
Приложение состоит из двух микросервисов:

- **WeatherProducer** — генерирует случайные погодные данные и отправляет их в Kafka-топик `weather-topic`;
- **WeatherConsumer** — получает сообщения из Kafka и выводит их в консоль.

## Стек

- Java 17
- Apache Kafka 4.0
- Docker + Docker Compose
- JUnit 5 + Mockito (для тестирования)

## Запуск

### 1. Соберите образы:

```bash
docker compose up --build
