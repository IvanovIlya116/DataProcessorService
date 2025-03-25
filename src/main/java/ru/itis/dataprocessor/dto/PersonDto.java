package ru.itis.dataprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
    private String lastName;
    private String firstName;
    private String middleName;
    private String gender;
    private String birthDate;
    private String birthPlace;
    private String snils;
    private String inn;
    private String passportSeries;
    private String passportNumber;
    private String registrationAddress;
    private String residentialAddress;
    private String workplace;
    private String position;
    private String education;
}