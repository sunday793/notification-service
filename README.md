# Notification Service (Event-Driven)

An asynchronous notification service built with **Spring Boot** and **Apache Kafka**. This project demonstrates a production-ready approach to message processing, including idempotency and fault tolerance.

## Tech Stack

* **Java 21**
* **Spring Boot 4.x** (Spring Web, Spring Data JPA, Spring for Apache Kafka)
* **Apache Kafka** (running in KRaft mode)
* **PostgreSQL**
* **Docker & Docker Compose**

## Key Features

* **Asynchronous Processing:** The REST API returns immediately while the actual processing happens in the background via Kafka.
* **Idempotent Consumer:** Prevents duplicate notifications. Each event has a unique `eventId`, which is checked against a PostgreSQL database before processing.
* **Retry Mechanism:** If processing fails (simulated by throwing an exception), the message is retried with exponential backoff (2 seconds, then 4 seconds).
* **Dead Letter Queue (DLQ):** After 3 failed attempts, the message is moved to a Dead Letter Topic (`notification-topic-dlt`) so it doesn't block the queue and can be manually investigated.
* **Unit & Integration Tests:** The core logic is covered by JUnit 5, Mockito, and Spring Boot Test to ensure reliability.

## How to Run

1. Ensure you have **Docker** installed and running.
2. Start the infrastructure (Kafka & PostgreSQL):
   ```bash
   docker compose up -d
   ```
3. Run the Spring Boot application (`NotificationServiceApplication`).
4. The application will start on `http://localhost:8080`.

## How to Test

### 1. Successful processing

Send a `POST` request to `http://localhost:8080/api/notifications`:

```json
{
  "recipient": "test@example.com",
  "message": "Hello Kafka!"
}
```

**Result:** The API returns a generated `eventId`, and the consumer processes it, saving the ID to the database.

### 2. Idempotency (Duplicate Prevention)

Send the **same request again**, but explicitly include the `eventId` you received in the previous step:

```json
{
  "eventId": "YOUR-PREVIOUS-UUID",
  "recipient": "test@example.com",
  "message": "Hello Kafka!"
}
```

**Result:** In the application logs, you will see `Duplicate event detected! Skipping...`.

### 3. Retry and DLQ

Send a request with the word `fail` in the message:

```json
{
  "recipient": "test@example.com",
  "message": "This will fail!"
}
```

**Result:** In the logs, you will see the consumer failing, retrying after 2s, failing again, retrying after 4s, and finally routing the message to the Dead Letter Queue (`DLQ: Message permanently failed...`).

## Project Structure

```text
src/main/java/com/example/notification_service/
├── controller/       # REST API endpoints
├── model/            # Data models (NotificationEvent, ProcessedEvent)
├── repository/       # Spring Data JPA repositories
└── service/          # Kafka Consumer with Retry & DLQ logic
```