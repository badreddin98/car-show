package com.levelonejava.car_show.dtos;

public record CarResponse(
        long carId,
        String make,
        String model,
        String engineType,
        String vehicleType,
        byte doorCount
) {
}
