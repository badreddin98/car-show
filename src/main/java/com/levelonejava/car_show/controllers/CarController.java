package com.levelonejava.car_show.controllers;

import com.levelonejava.car_show.dtos.CarRequest;
import com.levelonejava.car_show.dtos.CarResponse;
import com.levelonejava.car_show.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/car")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping(value = {"/", ""})
    public ResponseEntity<List<CarResponse>> carIndex() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @PostMapping(value = {"/create", "/create/"})
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarRequest carRequest) {
        return ResponseEntity.created(null).body(carService.createCar(carRequest));
    }


    @PutMapping(value = {"{id}/update"})
    public ResponseEntity<CarResponse> updateCarRequest(@Valid @RequestBody CarRequest carRequest) {
        return ResponseEntity.ok(carService.updateCarInformation(carRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable long id) {
        carService.deleteCarById(id);
        return ResponseEntity.ok().build();
    }

}


