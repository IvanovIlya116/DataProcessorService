package ru.itis.dataprocessor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.itis.dataprocessor.dto.PersonDto;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final DataProcessingService dataProcessingService;
    private final KafkaProducerService kafkaProducerService;
    private final NotificationProducer notificationProducer;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "raw_data", groupId = "processor-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            PersonDto person = objectMapper.readValue(record.value(), PersonDto.class);
            long receivedTime = System.currentTimeMillis();
            System.out.println("📥 Получено: " + record.value() + " | Задержка: " + (receivedTime - record.timestamp()) + "ms");

            PersonDto processedData = dataProcessingService.process(person);
            String message = objectMapper.writeValueAsString(processedData);
            kafkaProducerService.send(message);

            notificationProducer.sendNotification("✅ DataProcessor: обработка прошла успешно");
        } catch (Exception e) {
            notificationProducer.sendNotification("❌ DataProcessor: ошибка обработки — " + e.getMessage());
            System.err.println("❌ Ошибка обработки: " + e.getMessage());
        }
    }
}