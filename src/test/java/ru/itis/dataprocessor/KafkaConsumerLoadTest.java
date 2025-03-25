package ru.itis.dataprocessor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@EmbeddedKafka(partitions = 50, topics = "gov-data")
@Import(KafkaConfig.class) // Импортируем конфигурацию
class KafkaConsumerLoadTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerLoadTest.class);
    private final CountDownLatch latch = new CountDownLatch(10000);
    private long startTime;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void sendMessage() {
        for (int i = 0; i < 10000; i++) {
            kafkaTemplate.send("gov-data", "key-" + i, "value-" + i);
        }
    }

    @KafkaListener(
            topics = "gov-data",
            groupId = "processor-group",
            concurrency = "50",
            containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void listenBatch(List<ConsumerRecord<String, String>> records) {
        records.forEach(record -> latch.countDown());
    }

    @Test
    void testKafkaConsumerLatency() throws InterruptedException {
        startTime = System.currentTimeMillis();
        boolean completed = latch.await(120, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;
        if (completed) {
            log.info("\uD83D\uDD25 10000 сообщений обработаны за {}ms", duration);
        } else {
            log.error("❌ Не все сообщения были обработаны за отведенное время");
        }
    }
}