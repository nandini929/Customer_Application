package com.example.customerbackend.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Customer API performance")
class CustomerControllerPerformanceTest {

    private static final int WARMUP_REQUESTS = 5;
    private static final int MEASURED_REQUESTS = 20;
    private static final Duration MAX_AVG_RESPONSE_TIME = Duration.ofMillis(500);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("GET /api/customers average latency stays under threshold")
    void getCustomersAverageLatencyUnderThreshold() {
        customerRepository.deleteAll();

        IntStream.range(0, 100).forEach(i -> {
            Customer c = new Customer("First" + i, "Last" + i, LocalDate.of(1990, 1, 1).plusDays(i));
            customerRepository.save(c);
        });

        IntStream.range(0, WARMUP_REQUESTS).forEach(i ->
                restTemplate.getForEntity("/api/customers", String.class)
        );

        long totalNanos = 0;

        for (int i = 0; i < MEASURED_REQUESTS; i++) {
            long start = System.nanoTime();
            ResponseEntity<String> response = restTemplate.getForEntity("/api/customers", String.class);
            long end = System.nanoTime();

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

            totalNanos += (end - start);
        }

        long avgNanos = totalNanos / MEASURED_REQUESTS;
        Duration avgDuration = Duration.ofNanos(avgNanos);

        assertThat(avgDuration)
                .as("Average GET /api/customers response time should be under %s, but was %s",
                        MAX_AVG_RESPONSE_TIME, avgDuration)
                .isLessThanOrEqualTo(MAX_AVG_RESPONSE_TIME);
    }
}

