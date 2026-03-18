package com.levelonejava.car_show.services;

import com.levelonejava.car_show.dtos.CarRequest;
import com.levelonejava.car_show.dtos.CarResponse;
import com.levelonejava.car_show.entities.Car;
import com.levelonejava.car_show.enums.EngineType;
import com.levelonejava.car_show.enums.VehicleType;
import com.levelonejava.car_show.exception.InvalidCarIdException;
import com.levelonejava.car_show.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarSerivceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarSerivceImpl carService;

    @Test
    void createCar_shouldReturnCarResponse() {
        CarRequest request = new CarRequest(0L, "Toyota", "Corolla", "V6_ENGINE", "SEDAN", (byte)4);
        Car car = new Car(1L, "Toyota", "Corolla", EngineType.V6_ENGINE, (byte)4, VehicleType.SEDAN, null);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarResponse resp = carService.createCar(request);

        assertNotNull(resp);
        assertEquals(1L, resp.carId());
        assertEquals("Toyota", resp.make());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void getAllCars_returnsList() {
        Car car1 = new Car(1L, "A", "M", EngineType.V6_ENGINE, (byte)4, VehicleType.SEDAN, null);
        Car car2 = new Car(2L, "B", "N", EngineType.V8_ENGINE, (byte)2, VehicleType.RV, null);
        when(carRepository.findAll()).thenReturn(List.of(car1, car2));

        List<CarResponse> list = carService.getAllCars();

        assertEquals(2, list.size());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void updateCarInformation_existing_returnsUpdated() {
        CarRequest request = new CarRequest(1L, "NewMake", "NewModel", "V8_ENGINE", "SUV", (byte)4);
        Car newCar = new Car(1L, "NewMake", "NewModel", EngineType.V8_ENGINE, (byte)4, VehicleType.SUV, null);

        when(carRepository.existsById(1L)).thenReturn(true);
        when(carRepository.save(any(Car.class))).thenReturn(newCar);

        var updated = carService.updateCarInformation(request);

        assertNotNull(updated);
        assertEquals(1L, updated.carId());
        assertEquals("NewMake", updated.make());
    }

    @Test
    void deleteCarById_notExists_throws() {
        when(carRepository.existsById(99L)).thenReturn(false);

        assertThrows(InvalidCarIdException.class, () -> carService.deleteCarById(99L));
        verify(carRepository, never()).deleteById(anyLong());
    }
}

