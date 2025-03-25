package ru.itis.dataprocessor.service;

import org.springframework.stereotype.Service;
import ru.itis.dataprocessor.dto.PersonDto;

@Service
public class DataProcessingService {
    public PersonDto process(PersonDto person) {
        // Простая обработка данных (например, удаление пробелов)
        person.setLastName(person.getLastName().trim());
        person.setFirstName(person.getFirstName().trim());
        person.setMiddleName(person.getMiddleName().trim());
        return person;
    }
}