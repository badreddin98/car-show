package com.levelonejava.car_show;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test that verifies the Spring Boot application context loads successfully.
 * This test uses the 'test' profile which configures the application for testing environments.
 */
@SpringBootTest
@ActiveProfiles("test")
class CarShowApplicationTests {

	/**
	 * Tests that the Spring application context loads without errors.
	 * This is a smoke test to verify basic application configuration is valid.
	 */
	@Test
	void contextLoads() {
		// Context should load successfully without any exceptions
	}

}
