package com.levelonejava.car_show.car_utils;

import com.levelonejava.car_show.dtos.CarRequest;
import com.levelonejava.car_show.dtos.CarResponse;
import com.levelonejava.car_show.entities.Car;
import com.levelonejava.car_show.enums.EngineType;
import com.levelonejava.car_show.enums.VehicleType;

public class CarMapper {

    public static Car fromDto(CarRequest request){
        return new Car(
                request.carId(),
                request.make(),
                request.model(),
                EngineType.valueOf(request.engineType()),
                request.doorCount(),
                VehicleType.valueOf(request.vehcileType()),
                null
        );


    }

    public static CarResponse toDto(Car car){
        return new CarResponse(
            car.getCarId(),
            car.getMake(),
            car.getModel(),
            car.getEngineType().toString(),
            car.getVehicleType().toString(),
            car.getDoorCount()

        );
    }


}
