package com.levelonejava.car_show.dtos;

public record CarRequest(
        long carId,
        String make,
        String model,
        String engineType,
        String vehcileType,
        byte doorCount
) {
    public CarRequest(String make, String model, String engineType, String vehcileType, byte doorCount) {
        this(0, make, model, engineType, vehcileType, doorCount);
    }
}
