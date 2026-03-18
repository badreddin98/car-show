package com.levelonejava.car_show.integration;

import com.levelonejava.car_show.TestcontainersConfiguration;
import com.levelonejava.car_show.dtos.UserRequest;
import com.levelonejava.car_show.entities.UserCredential;
import com.levelonejava.car_show.repository.UserCredentialRepository;
import com.levelonejava.car_show.services.UserCredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserCredentialRepository and UserCredentialService
 * Tests user creation, password encoding, and query performance
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("UserCredential Integration Tests")
class UserCredentialIntegrationTest {

    @Autowired
    private UserCredentialRepository userRepository;

    @Autowired
    private UserCredentialService userService;

    private static final int TEST_ITERATIONS = 50;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create user via service and verify password encoding")
    void testCreateUserViaService() {
        UserRequest request = new UserRequest("test@example.com", "password123");

        long startTime = System.nanoTime();
        String result = userService.createUser(request);
        long createTime = System.nanoTime() - startTime;

        assertEquals("User has been created", result);
        System.out.println("Create user time (with password encoding): " + (createTime / 1_000_000.0) + " ms");

        Optional<UserCredential> saved = userRepository.findByEmail("test@example.com");
        assertTrue(saved.isPresent());
        assertNotEquals("password123", saved.get().getPassword(), "Password should be encoded, not plain text");
    }

    @Test
    @DisplayName("Test findByEmail query performance")
    void testFindByEmailPerformance() {
        // Create some test users
        for (int i = 0; i < 20; i++) {
            UserRequest req = new UserRequest("user" + i + "@example.com", "pass" + i);
            userService.createUser(req);
        }

        long startTime = System.nanoTime();
        Optional<UserCredential> user = userRepository.findByEmail("user10@example.com");
        long queryTime = System.nanoTime() - startTime;

        assertTrue(user.isPresent());
        System.out.println("FindByEmail query time: " + (queryTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test bulk user creation performance")
    void testBulkUserCreationPerformance() {
        long startTime = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            UserRequest req = new UserRequest("bulk" + i + "@example.com", "password" + i);
            userService.createUser(req);
        }
        long totalTime = System.nanoTime() - startTime;

        assertEquals(50, userRepository.count());
        double avgTime = (totalTime / 1_000_000.0) / 50;
        System.out.println("Bulk create 50 users: " + (totalTime / 1_000_000.0) + " ms");
        System.out.println("Average time per user (with password encoding): " + avgTime + " ms");

        assertTrue(totalTime / 1_000_000.0 < 10000, "Bulk creation should complete within 10 seconds");
    }

    @Test
    @DisplayName("Test repeated user creation for bottleneck detection")
    void testRepeatedUserCreationBottleneck() {
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            UserRequest req = new UserRequest("perf" + i + "@example.com", "password" + i);

            long startTime = System.nanoTime();
            userService.createUser(req);
            long elapsed = System.nanoTime() - startTime;

            totalTime += elapsed;
            minTime = Math.min(minTime, elapsed);
            maxTime = Math.max(maxTime, elapsed);
        }

        double avgTime = totalTime / 1_000_000.0 / TEST_ITERATIONS;
        System.out.println("\n=== Repeated User Creation Performance (50 iterations) ===");
        System.out.println("Avg time: " + avgTime + " ms");
        System.out.println("Min time: " + (minTime / 1_000_000.0) + " ms");
        System.out.println("Max time: " + (maxTime / 1_000_000.0) + " ms");
        System.out.println("Potential bottleneck ratio (max/min): " + ((double) maxTime / minTime) + "x");
        System.out.println("Note: Time includes password encoding which is intentionally slow for security");
    }

    @Test
    @DisplayName("Test user update performance")
    void testUpdateUserPerformance() {
        UserRequest req = new UserRequest("update@example.com", "oldpass");
        userService.createUser(req);

        Optional<UserCredential> user = userRepository.findByEmail("update@example.com");
        assertTrue(user.isPresent());

        long startTime = System.nanoTime();
        UserCredential userCred = user.get();
        userCred.setRole("ADMIN");
        userRepository.save(userCred);
        long updateTime = System.nanoTime() - startTime;

        System.out.println("Update user time: " + (updateTime / 1_000_000.0) + " ms");
    }

    @Test
    @DisplayName("Test password encoding performance")
    void testPasswordEncodingImpact() {
        // Measure just the user creation overhead
        UserRequest req = new UserRequest("encoding@example.com", "securepwd");

        long startTime = System.nanoTime();
        String result = userService.createUser(req);
        long totalTime = System.nanoTime() - startTime;

        assertEquals("User has been created", result);
        System.out.println("\nPassword encoding impact:");
        System.out.println("Total creation time: " + (totalTime / 1_000_000.0) + " ms");
        System.out.println("Note: Most of this time is spent on secure password encoding (bcrypt/scrypt)");
    }

    @Test
    @DisplayName("Test user findAll performance")
    void testFindAllUsersPerformance() {
        for (int i = 0; i < 30; i++) {
            UserRequest req = new UserRequest("all" + i + "@example.com", "password");
            userService.createUser(req);
        }

        long startTime = System.nanoTime();
        var users = userRepository.findAll();
        long queryTime = System.nanoTime() - startTime;

        assertEquals(30, users.size());
        System.out.println("FindAll 30 users: " + (queryTime / 1_000_000.0) + " ms");
    }
}

