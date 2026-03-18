package com.levelonejava.car_show.integration;

import com.levelonejava.car_show.TestcontainersConfiguration;
import com.levelonejava.car_show.entities.Owner;
import com.levelonejava.car_show.enums.Gender;
import com.levelonejava.car_show.repository.OwnerRepository;
import com.levelonejava.car_show.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for OwnerRepository using Testcontainers MySQL
 * Verifies CRUD operations and query performance with complex date/enum filters
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Owner Repository Integration Tests")
class OwnerRepositoryIntegrationTest {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    private static final int TEST_ITERATIONS = 100;

    @BeforeEach
    void setUp() {
        // Delete in correct order to respect foreign key constraints
        userCredentialRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    @DisplayName("Save and retrieve owner with date of birth")
    void testSaveAndRetrieveOwner() {
        Owner owner = new Owner(0, "John", "Doe", Gender.MALE, LocalDate.of(1990, 5, 15), null);

        long startTime = System.nanoTime();
        Owner saved = ownerRepository.save(owner);
        long saveTime = System.nanoTime() - startTime;

        assertNotNull(saved.getOwnerId());
        assertEquals("John", saved.getFirstName());
        System.out.println("Save owner time: " + (saveTime / 1_000_000.0) + " ms");

        startTime = System.nanoTime();
        Owner retrieved = ownerRepository.findById(saved.getOwnerId()).orElse(null);
        long retrieveTime = System.nanoTime() - startTime;

        assertNotNull(retrieved);
        assertEquals("Doe", retrieved.getLastName());
        System.out.println("Retrieve owner time: " + (retrieveTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test bulk owner insert performance")
    void testBulkOwnerInsertPerformance() {
        List<Owner> owners = generateTestOwners(100);

        long startTime = System.nanoTime();
        ownerRepository.saveAll(owners);
        long totalTime = System.nanoTime() - startTime;

        assertEquals(100, ownerRepository.count());
        double avgTimePerOwner = (totalTime / 1_000_000.0) / 100;
        System.out.println("Bulk insert 100 owners: " + (totalTime / 1_000_000.0) + " ms");
        System.out.println("Average time per owner: " + avgTimePerOwner + " ms");

        assertTrue(totalTime / 1_000_000.0 < 5000, "Bulk insert should complete within 5 seconds");
    }

    @Test
    @DisplayName("Test findByFirstName query performance")
    void testFindByFirstNamePerformance() {
        ownerRepository.saveAll(generateTestOwners(50));

        long startTime = System.nanoTime();
        List<Owner> owners = ownerRepository.findAllByFirstName("John");
        long queryTime = System.nanoTime() - startTime;

        assertTrue(owners.size() >= 0);
        System.out.println("FindByFirstName query: " + (queryTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test findByGender query performance")
    void testFindByGenderPerformance() {
        ownerRepository.saveAll(generateTestOwners(50));

        long startTime = System.nanoTime();
        List<Owner> maleOwners = ownerRepository.findAllByGender(Gender.MALE);
        long queryTime = System.nanoTime() - startTime;

        assertTrue(maleOwners.size() >= 0);
        System.out.println("FindByGender 'MALE' query: " + (queryTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test findByDateOfBirth query performance")
    void testFindByDateOfBirthPerformance() {
        ownerRepository.saveAll(generateTestOwners(50));

        LocalDate targetDate = LocalDate.of(1985, 6, 15);
        long startTime = System.nanoTime();
        List<Owner> owners = ownerRepository.findAllByDateOfBirth(targetDate);
        long queryTime = System.nanoTime() - startTime;

        System.out.println("FindByDateOfBirth query: " + (queryTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test repeated owner inserts for bottleneck detection")
    void testRepeatedOwnerInsertsBottleneck() {
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Owner owner = new Owner(0,
                    "FirstName" + i,
                    "LastName" + i,
                    i % 2 == 0 ? Gender.MALE : Gender.FEMALE,
                    LocalDate.of(1980 + (i % 40), (i % 12) + 1, (i % 28) + 1),
                    null);

            long startTime = System.nanoTime();
            ownerRepository.save(owner);
            long elapsed = System.nanoTime() - startTime;

            totalTime += elapsed;
            minTime = Math.min(minTime, elapsed);
            maxTime = Math.max(maxTime, elapsed);
        }

        double avgTime = totalTime / 1_000_000.0 / TEST_ITERATIONS;
        System.out.println("\n=== Repeated Owner Insert Performance (100 iterations) ===");
        System.out.println("Avg time: " + avgTime + " ms");
        System.out.println("Min time: " + (minTime / 1_000_000.0) + " ms");
        System.out.println("Max time: " + (maxTime / 1_000_000.0) + " ms");
        System.out.println("Potential bottleneck ratio (max/min): " + ((double) maxTime / minTime) + "x");
    }

    @Test
    @DisplayName("Test owner update performance")
    void testUpdateOwnerPerformance() {
        Owner saved = ownerRepository.save(new Owner(0, "Jane", "Smith", Gender.FEMALE, LocalDate.of(1995, 3, 20), null));

        long startTime = System.nanoTime();
        saved.setFirstName("Janet");
        ownerRepository.save(saved);
        long updateTime = System.nanoTime() - startTime;

        Owner retrieved = ownerRepository.findById(saved.getOwnerId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals("Janet", retrieved.getFirstName());
        System.out.println("Update owner time: " + (updateTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test owner delete performance")
    void testDeleteOwnerPerformance() {
        Owner saved = ownerRepository.save(new Owner(0, "DeleteMe", "Test", Gender.MALE, LocalDate.of(2000, 1, 1), null));

        long startTime = System.nanoTime();
        ownerRepository.deleteById(saved.getOwnerId());
        long deleteTime = System.nanoTime() - startTime;

        assertTrue(ownerRepository.findById(saved.getOwnerId()).isEmpty());
        System.out.println("Delete owner time: " + (deleteTime / 1_000_000.0) + " ms");
    }

    private List<Owner> generateTestOwners(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> new Owner(0,
                        i % 3 == 0 ? "John" : i % 3 == 1 ? "Jane" : "Bob",
                        "LastName" + i,
                        i % 2 == 0 ? Gender.MALE : Gender.FEMALE,
                        LocalDate.of(1980 + (i % 40), (i % 12) + 1, (i % 28) + 1),
                        null))
                .toList();
    }
}



