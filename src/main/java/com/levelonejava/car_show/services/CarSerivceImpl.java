package com.levelonejava.car_show.services;

import com.levelonejava.car_show.dtos.CarRequest;
import com.levelonejava.car_show.dtos.CarResponse;
import com.levelonejava.car_show.entities.Car;
import com.levelonejava.car_show.exception.InvalidCarIdException;
import com.levelonejava.car_show.repository.CarRepository;
import com.levelonejava.car_show.car_utils.CarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarSerivceImpl implements CarService {

    private final CarRepository carRepository;


    @Override
    public CarResponse createCar(CarRequest carRequest) {
        Car car = CarMapper.fromDto(carRequest);
//        Car car = new Car();
//        car.setMake(carRequest.make());
//        car.setModel(carRequest.model());
//        car.setDoorCount(carRequest.doorCount());
//        car.setEngineType(EngineType.valueOf(carRequest.engineType()));
//        car.setVehicleType(VehicleType.valueOf(carRequest.vehcileType()));
        return CarMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(CarMapper::toDto).toList();
    }

    @Override
    public CarResponse getCarById(long id) {
        return CarMapper.toDto(
                carRepository.findById(id)
                        .orElseThrow(
                                () -> new InvalidCarIdException("Car id + " + id + " not found")
                        )
        );
    }

    @Override
    public List<CarResponse> getCarsByMake(String make) {
        return carRepository.findAllByMake(make).stream().map(CarMapper::toDto).toList();
    }

    @Override
    public List<CarResponse> getCarsByModel(String model) {
        return carRepository.findAllByModel(model).stream().map(CarMapper :: toDto).toList();
    }

    @Override
    public List<CarResponse> getCarsByType(String vehicleType) {
        return carRepository
                .findAllByVehicleTypeIgnoreCase(
                    vehicleType
        ).stream()
                .map(CarMapper::toDto).toList();

    }

    @Override
    public CarResponse updateCarInformation(CarRequest carRequest) {
        Car newCarInfo = CarMapper.fromDto(carRequest);
        if (carRepository.existsById(newCarInfo.getCarId())){
            return CarMapper.toDto(carRepository.save(newCarInfo));
        }
        return null;
    }

    @Override
    public void deleteCarById(long id) {
        if (!carRepository.existsById(id)) throw new InvalidCarIdException("Car ID " + id + " not found!");
        carRepository.deleteById(id);

    }
}
