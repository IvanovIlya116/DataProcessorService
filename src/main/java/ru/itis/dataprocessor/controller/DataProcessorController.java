package ru.itis.dataprocessor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itis.dataprocessor.dto.PersonDto;
import ru.itis.dataprocessor.service.DataProcessingService;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class DataProcessorController {

    private final DataProcessingService dataProcessingService;
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @PostMapping("/data")
    public ResponseEntity<String> processData(@RequestBody PersonDto person) {
        PersonDto processedData = dataProcessingService.process(person);

        // Отправляем обработанные данные в StorageService
        webClient.post()
                .uri("/storage/save")
                .bodyValue(processedData)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        return ResponseEntity.ok("Data processed and sent to StorageService.");
    }
}