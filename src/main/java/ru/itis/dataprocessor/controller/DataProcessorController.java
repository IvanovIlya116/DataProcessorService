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
    private final WebClient.Builder webClientBuilder;

    @PostMapping("/data")
    public ResponseEntity<String> processData(@RequestBody PersonDto person,
                                              @RequestHeader("Authorization") String authHeader) {
        PersonDto processedData = dataProcessingService.process(person);

        webClientBuilder.build().post()
                .uri("http://apigateway-service:8080/storage/save")
                .header("Authorization", authHeader) // Прокидываем Bearer-токен
                .bodyValue(processedData)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        return ResponseEntity.ok("Data processed and sent to StorageService.");
    }
}