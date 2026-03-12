package com.levelonejava.car_show.dtos;

import jakarta.validation.constraints.*;

public record CarRequest(
        long carId,
        @NotBlank(message = "Make cannot be blank")
        @Size(min = 2, max = 50, message = "Make must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Make must contain only letters and spaces")
        String make,
        
        @NotBlank(message = "Model cannot be blank")
        @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-]+$", message = "Model must contain only letters, numbers, spaces, and hyphens")
        String model,
        
        @NotBlank(message = "Engine type cannot be blank")
        @Pattern(regexp = "^(V6_ENGINE|V8_ENGINE)$", message = "Engine type must be either V6_ENGINE or V8_ENGINE")
        String engineType,
        
        @NotBlank(message = "Vehicle type cannot be blank")
        @Pattern(regexp = "^(SEDAN|TRUCK|SUV|RV)$", message = "Vehicle type must be SEDAN, TRUCK, SUV, or RV")
        String vehcileType,
        
        @Min(value = 2, message = "Door count must be at least 2")
        @Max(value = 6, message = "Door count cannot exceed 6")
        byte doorCount
) {
    public CarRequest(String make, String model, String engineType, String vehcileType, byte doorCount) {
        this(0, make, model, engineType, vehcileType, doorCount);
    }
}
