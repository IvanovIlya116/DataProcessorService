package ru.itis.dataprocessor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itis.dataprocessor.dto.PersonDto;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final DataProcessingService dataProcessingService;
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PersonDto person = objectMapper.readValue(record.value(), PersonDto.class);
        long receivedTime = System.currentTimeMillis();
        System.out.println("📥 Получено: " + record.value() + " | Задержка: " + (receivedTime - record.timestamp()) + "ms");
        PersonDto processedData = dataProcessingService.process(person);

        // Отправляем обработанные данные в StorageService
        webClient.post()
                .uri("/storage/save")
                .bodyValue(processedData)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        System.out.println("Отправлено в StorageService");
    }
}

