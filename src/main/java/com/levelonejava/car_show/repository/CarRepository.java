package com.levelonejava.car_show.repository;

import com.levelonejava.car_show.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByModel(String model);
    List<Car> findAllByMake(String make);
    List<Car> findAllByVehicleTypeIgnoreCase(String model);

}
