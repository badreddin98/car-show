package com.levelonejava.car_show.dtos;

import com.levelonejava.car_show.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record OwnerRequest (
        long carId,
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
        String firstName,
        
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
        String lastName,
        
        @NotNull(message = "Gender cannot be null")
        Gender gender,
        
        @NotNull(message = "Date of birth cannot be null")
        @PastOrPresent(message = "Date of birth must be in the past or present")
        LocalDate dateOfBirth){

    public OwnerRequest(long carId, String firstName, String lastName, Gender gender, LocalDate dateOfBirth){
        this.carId = 0; // Force carId to 0
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}


