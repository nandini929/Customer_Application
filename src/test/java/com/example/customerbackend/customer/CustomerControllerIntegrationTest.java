package com.example.customerbackend.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "test", roles = "USER")
@DisplayName("Customer API integration")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/customers creates customer and returns 201")
    void postCreatesCustomer() throws Exception {
        String body = """
                {"firstName":"Alice","lastName":"Brown","dateOfBirth":"1995-06-10"}
                """;

        ResultActions result = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.dateOfBirth").value("1995-06-10"));
    }

    @Test
    @DisplayName("GET /api/customers returns list including created customer")
    void getReturnsCustomers() throws Exception {
        String body = """
                {"firstName":"Bob","lastName":"Green","dateOfBirth":"1988-12-01"}
                """;
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].firstName", hasItem("Bob")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Green")));
    }

    @Test
    @DisplayName("POST with invalid payload returns 400 and validation errors")
    void postInvalidReturns400() throws Exception {
        String body = """
                {"firstName":"","lastName":"","dateOfBirth":"2030-01-01"}
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists());
    }

    @Test
    @DisplayName("POST with missing dateOfBirth returns 400")
    void postMissingDobReturns400() throws Exception {
        String body = """
                {"firstName":"X","lastName":"Y"}
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists());
    }

    @Test
    @DisplayName("POST with whitespace-only names returns 400")
    void postWhitespaceOnlyNamesReturns400() throws Exception {
        String body = """
                {"firstName":"   ","lastName":"  \\t  ","dateOfBirth":"1990-01-01"}
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists());
    }

    @Test
    @DisplayName("POST with name exceeding max length returns 400")
    void postNameTooLongReturns400() throws Exception {
        String longName = "a".repeat(101);
        String body = """
                {"firstName":"%s","lastName":"Doe","dateOfBirth":"1990-01-01"}
                """.formatted(longName);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    @DisplayName("POST with date of birth before 1900 returns 400")
    void postDateBefore1900Returns400() throws Exception {
        String body = """
                {"firstName":"Jane","lastName":"Doe","dateOfBirth":"1899-12-31"}
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST with malformed JSON returns 400")
    void postMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }

    @Test
    @DisplayName("POST with invalid date format returns 400")
    void postInvalidDateFormatReturns400() throws Exception {
        String body = """
                {"firstName":"Jane","lastName":"Doe","dateOfBirth":"not-a-date"}
                """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /api/customers without auth returns 401")
    void getWithoutAuthReturns401() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }
}
