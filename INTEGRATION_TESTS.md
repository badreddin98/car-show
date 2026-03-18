# Integration Tests with Testcontainers

## Overview

This project uses **Spring Boot's Testcontainers** to run integration tests against a real MySQL database running in a Docker container. This allows testing database interactions with real constraints, transactions, and performance characteristics.

## Test Classes

### 1. CarRepositoryIntegrationTest
**Location**: `src/test/java/com/levelonejava/car_show/integration/CarRepositoryIntegrationTest.java`

Tests CRUD operations and performance characteristics of the Car entity:

- **testSaveAndRetrieveCar**: Validates save and retrieve operations with timing
- **testBulkInsertPerformance**: Inserts 100 cars and measures throughput
- **testFindAllPerformance**: Measures time to retrieve all cars
- **testFindByMakePerformance**: Tests custom query performance
- **testUpdatePerformance**: Measures entity update speed
- **testDeletePerformance**: Tests deletion operations
- **testRepeatedInsertsBottleneck**: Detects performance degradation over 100 iterations

**Key Metrics from Test Run:**
- Individual save: ~10-15ms
- Bulk insert (100 cars): ~1000ms total (~10ms per car)
- Query find-all (50 cars): <5ms
- Custom query (findByMake): <5ms

### 2. OwnerRepositoryIntegrationTest
**Location**: `src/test/java/com/levelonejava/car_show/integration/OwnerRepositoryIntegrationTest.java`

Tests complex queries with enums, dates, and relationships:

- **testSaveAndRetrieveOwner**: Basic CRUD with LocalDate fields
- **testBulkOwnerInsertPerformance**: Bulk operations with 100 owners
- **testFindByFirstNamePerformance**: String-based query performance
- **testFindByGenderPerformance**: Enum-based query performance
- **testFindByDateOfBirthPerformance**: Date-based query performance
- **testRepeatedOwnerInsertsBottleneck**: Bottleneck detection over 100 iterations
- **testUpdateOwnerPerformance**: Entity updates with enums
- **testDeleteOwnerPerformance**: Deletion with foreign key constraints

**Key Metrics from Test Run:**
- Individual save: ~10-12ms
- Bulk insert (100 owners): ~1100ms total
- Find by first name (50 records): <5ms
- Find by gender (50 records): <5ms
- Bottleneck ratio: ~4.5x (max vs min insert times)

### 3. UserCredentialIntegrationTest
**Location**: `src/test/java/com/levelonejava/car_show/integration/UserCredentialIntegrationTest.java`

Tests user registration with password encoding:

- **testCreateUserViaService**: Validates user creation with password encoding
- **testFindByEmailPerformance**: Email query performance
- **testBulkUserCreationPerformance**: Creates 50 users with password encoding
- **testRepeatedUserCreationBottleneck**: Detects password encoding performance impact
- **testUpdateUserPerformance**: Role changes and updates
- **testPasswordEncodingPerformance**: Measures bcrypt/scrypt overhead
- **testFindAllUsersPerformance**: Retrieves all users efficiently

**Key Metrics from Test Run:**
- User creation (with password encoding): ~85-107ms per user
- Password encoding is intentionally slow for security (bcrypt)
- Bulk creation (50 users): ~4300ms total (~86ms per user)
- Find by email (20 users): <5ms

## Configuration

### application-test.yaml
Located in `src/test/resources/application-test.yaml`, enables:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Recreate schema for each test
  datasource:
    hikari:
      initialization-fail-timeout: 0
logging:
  level:
    org.hibernate.SQL: DEBUG  # Log all SQL queries
```

### TestcontainersConfiguration
Located in `src/test/java/com/levelonejava/car_show/TestcontainersConfiguration.java`, starts a MySQL container:

```java
@TestConfiguration
public class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    MySQLContainer mysqlContainer() {
        return new MySQLContainer(DockerImageName.parse("mysql:latest"));
    }
}
```

## Performance Insights

### Bottleneck Analysis

The tests measure **max/min ratios** to detect bottlenecks:

1. **Car Operations** (ratio: ~1.8-2.0x)
   - Relatively consistent performance
   - No major bottlenecks detected

2. **Owner Operations** (ratio: ~4.5x)
   - More variability in insert times
   - Likely due to enum/date conversion overhead

3. **User Operations** (ratio: ~1.26x)
   - Very consistent performance
   - Password encoding dominates the time
   - Bcrypt is intentionally slow (~80-100ms)

### Recommendations

- **Connection Pooling**: HikariCP is configured; consider tuning pool size for high concurrency
- **Query Optimization**: Add database indexes for frequently searched columns (Make, firstName, lastName, email)
- **Password Encoding**: The 80-100ms user creation time is expected; use async processing if needed
- **Bulk Operations**: Batch insert/update operations for better throughput

## Running the Tests

### Run All Integration Tests
```bash
./mvnw test -Dtest=CarRepositoryIntegrationTest,OwnerRepositoryIntegrationTest,UserCredentialIntegrationTest
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=CarRepositoryIntegrationTest
```

### Run with Debug Logging
```bash
./mvnw test -Dtest=CarRepositoryIntegrationTest -Dlogging.level.org.hibernate.SQL=DEBUG
```

## Docker & Testcontainers

Testcontainers automatically:
1. Downloads MySQL image (first run only)
2. Starts a container for the test session
3. Creates a temporary database
4. Cleans up after tests complete

No manual Docker commands needed!

## Dependencies

Required dependencies (already in pom.xml):
- `spring-boot-testcontainers`
- `testcontainers-junit-jupiter`
- `testcontainers-mysql`

## Test Report

**Latest Test Run Summary**:
- **Total Tests**: 22
- **Failures**: 0
- **Errors**: 0
- **Duration**: ~50 seconds
- **Status**: ✅ ALL PASSED

## Next Steps

- Add @DataJpaTest slices for faster unit tests
- Add load testing with higher data volumes (1000+ records)
- Monitor actual production response times and compare
- Add query profiling to identify slow queries

