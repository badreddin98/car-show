package com.levelonejava.car_show.integration;

import com.levelonejava.car_show.TestcontainersConfiguration;
import com.levelonejava.car_show.entities.Car;
import com.levelonejava.car_show.enums.EngineType;
import com.levelonejava.car_show.enums.VehicleType;
import com.levelonejava.car_show.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CarRepository using Testcontainers MySQL
 * Verifies CRUD operations and query performance
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Car Repository Integration Tests")
class CarRepositoryIntegrationTest {

    @Autowired
    private CarRepository carRepository;

    private static final int TEST_ITERATIONS = 100;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
    }

    @Test
    @DisplayName("Save single car and verify retrieval")
    void testSaveAndRetrieveCar() {
        Car car = new Car(0, "Toyota", "Corolla", EngineType.V6_ENGINE, (byte) 4, VehicleType.SEDAN, null);

        long startTime = System.nanoTime();
        Car saved = carRepository.save(car);
        long saveTime = System.nanoTime() - startTime;

        assertNotNull(saved.getCarId());
        assertEquals("Toyota", saved.getMake());
        System.out.println("Save time: " + (saveTime / 1_000_000.0) + " ms");

        startTime = System.nanoTime();
        Car retrieved = carRepository.findById(saved.getCarId()).orElse(null);
        long retrieveTime = System.nanoTime() - startTime;

        assertNotNull(retrieved);
        assertEquals("Corolla", retrieved.getModel());
        System.out.println("Retrieve time: " + (retrieveTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test bulk insert performance")
    void testBulkInsertPerformance() {
        List<Car> cars = generateTestCars(100);

        long startTime = System.nanoTime();
        carRepository.saveAll(cars);
        long totalTime = System.nanoTime() - startTime;

        assertEquals(100, carRepository.count());
        double avgTimePerCar = (totalTime / 1_000_000.0) / 100;
        System.out.println("Bulk insert 100 cars: " + (totalTime / 1_000_000.0) + " ms");
        System.out.println("Average time per car: " + avgTimePerCar + " ms");

        assertTrue(totalTime / 1_000_000.0 < 5000, "Bulk insert should complete within 5 seconds");
    }

    @Test
    @DisplayName("Test findAll performance")
    void testFindAllPerformance() {
        carRepository.saveAll(generateTestCars(50));

        long startTime = System.nanoTime();
        List<Car> cars = carRepository.findAll();
        long queryTime = System.nanoTime() - startTime;

        assertEquals(50, cars.size());
        System.out.println("FindAll 50 cars: " + (queryTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test findByMake performance")
    void testFindByMakePerformance() {
        carRepository.saveAll(generateTestCars(50));

        long startTime = System.nanoTime();
        List<Car> makeCars = carRepository.findAllByMake("Make0");
        long queryTime = System.nanoTime() - startTime;

        assertFalse(makeCars.isEmpty(), "Should find cars with Make0");
        System.out.println("FindByMake 'Make0': " + (queryTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test update performance")
    void testUpdatePerformance() {
        Car saved = carRepository.save(new Car(0, "Honda", "Civic", EngineType.V8_ENGINE, (byte) 2, VehicleType.SEDAN, null));

        long startTime = System.nanoTime();
        saved.setMake("Updated Make");
        carRepository.save(saved);
        long updateTime = System.nanoTime() - startTime;

        Car retrieved = carRepository.findById(saved.getCarId()).orElse(null);
        assertEquals("Updated Make", retrieved.getMake());
        System.out.println("Update time: " + (updateTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test delete performance")
    void testDeletePerformance() {
        Car saved = carRepository.save(new Car(0, "Ford", "Mustang", EngineType.V8_ENGINE, (byte) 2, VehicleType.SEDAN, null));

        long startTime = System.nanoTime();
        carRepository.deleteById(saved.getCarId());
        long deleteTime = System.nanoTime() - startTime;

        assertTrue(carRepository.findById(saved.getCarId()).isEmpty());
        System.out.println("Delete time: " + (deleteTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test repeated inserts to detect bottlenecks")
    void testRepeatedInsertsBottleneck() {
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Car car = new Car(0, "Make" + i, "Model" + i, EngineType.V6_ENGINE, (byte) 4, VehicleType.SEDAN, null);
            long startTime = System.nanoTime();
            carRepository.save(car);
            long elapsed = System.nanoTime() - startTime;

            totalTime += elapsed;
            minTime = Math.min(minTime, elapsed);
            maxTime = Math.max(maxTime, elapsed);
        }

        double avgTime = totalTime / 1_000_000.0 / TEST_ITERATIONS;
        System.out.println("\n=== Repeated Insert Performance (100 iterations) ===");
        System.out.println("Avg time: " + avgTime + " ms");
        System.out.println("Min time: " + (minTime / 1_000_000.0) + " ms");
        System.out.println("Max time: " + (maxTime / 1_000_000.0) + " ms");
        System.out.println("Potential bottleneck if max >> avg: " + ((double) maxTime / minTime) + "x");
    }

    private List<Car> generateTestCars(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> new Car(0,
                        "Make" + (i % 5),
                        "Model" + i,
                        i % 2 == 0 ? EngineType.V6_ENGINE : EngineType.V8_ENGINE,
                        (byte) ((i % 5) + 2),
                        VehicleType.values()[i % VehicleType.values().length],
                        null))
                .toList();
    }
}


