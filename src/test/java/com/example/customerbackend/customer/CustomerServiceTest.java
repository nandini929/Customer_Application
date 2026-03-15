package com.example.customerbackend.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Nested
    @DisplayName("createCustomer")
    class CreateCustomer {

        @Test
        @DisplayName("trims names and saves customer")
        void trimsAndSaves() {
            CustomerRequest request = new CustomerRequest();
            request.setFirstName("  Jane  ");
            request.setLastName("  Doe  ");
            request.setDateOfBirth(LocalDate.of(1990, 5, 15));

            Customer saved = new Customer("Jane", "Doe", LocalDate.of(1990, 5, 15));
            when(customerRepository.save(any(Customer.class))).thenReturn(saved);

            CustomerResponse response = customerService.createCustomer(request);

            ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
            verify(customerRepository).save(captor.capture());
            Customer captured = captor.getValue();
            assertThat(captured.getFirstName()).isEqualTo("Jane");
            assertThat(captured.getLastName()).isEqualTo("Doe");
            assertThat(captured.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));

            assertThat(response.getFirstName()).isEqualTo("Jane");
            assertThat(response.getLastName()).isEqualTo("Doe");
            assertThat(response.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
        }

        @Test
        @DisplayName("maps saved entity to response")
        void mapsToResponse() {
            CustomerRequest request = new CustomerRequest();
            request.setFirstName("John");
            request.setLastName("Smith");
            request.setDateOfBirth(LocalDate.of(1985, 1, 1));

            Customer saved = new Customer("John", "Smith", LocalDate.of(1985, 1, 1));
            when(customerRepository.save(any(Customer.class))).thenReturn(saved);

            CustomerResponse response = customerService.createCustomer(request);

            assertThat(response.getFirstName()).isEqualTo("John");
            assertThat(response.getLastName()).isEqualTo("Smith");
            assertThat(response.getDateOfBirth()).isEqualTo(LocalDate.of(1985, 1, 1));
        }
    }

    @Nested
    @DisplayName("getAllCustomers")
    class GetAllCustomers {

        @Test
        @DisplayName("returns empty list when repository is empty")
        void returnsEmptyWhenNone() {
            when(customerRepository.findAll()).thenReturn(List.of());

            List<CustomerResponse> result = customerService.getAllCustomers();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("maps all entities to responses")
        void mapsAllToResponses() {
            Customer c1 = new Customer("A", "One", LocalDate.of(2000, 1, 1));
            Customer c2 = new Customer("B", "Two", LocalDate.of(2000, 2, 1));
            when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

            List<CustomerResponse> result = customerService.getAllCustomers();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFirstName()).isEqualTo("A");
            assertThat(result.get(1).getFirstName()).isEqualTo("B");
        }
    }
}
