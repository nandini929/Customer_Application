package com.example.customerbackend.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CustomerRepository live database integration")
class CustomerRepositoryLiveTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("persists and reads customers against real database")
    void saveAndFindAllWorksWithDatabase() {
        Customer alice = new Customer("Alice", "Brown", LocalDate.of(1995, 6, 10));
        Customer bob = new Customer("Bob", "Green", LocalDate.of(1988, 12, 1));

        customerRepository.save(alice);
        customerRepository.save(bob);

        List<Customer> all = customerRepository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all)
                .extracting(Customer::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");

        assertThat(all)
                .allSatisfy(c -> {
                    assertThat(c.getId()).as("id should be generated").isNotNull();
                });
    }
}

