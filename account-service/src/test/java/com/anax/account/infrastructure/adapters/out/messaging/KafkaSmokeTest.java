package com.anax.account.infrastructure.adapters.out.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// descartar
@Tag("integration")
@SpringBootTest
public class KafkaSmokeTest {

    static final String TOPIC = "test-events";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private BlockingQueue<String> messages;

    @BeforeEach
    void setUp() {
        messages = new LinkedBlockingQueue<>();
    }

    // Arrange: Listener temporal para capturar mensajes
    @KafkaListener(topics = TOPIC)
    public void listen(String message) {
        messages.add(message);
    }

    @Test
    void kafkaSendReceiveTest() throws InterruptedException {
        String payload = "{\"message\":\"Hola Kafka!\"}";

        // Act: Envia mensaje
        kafkaTemplate.send(TOPIC, payload);
        kafkaTemplate.flush();

        // Assert: Esperar máximo 10 segundos a recibirlo
        String received = messages.poll(10, TimeUnit.SECONDS);

        assertThat(received).isNotNull();
        assertThat(received).isEqualTo(payload);
    }
}
