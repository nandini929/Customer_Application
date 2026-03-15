package com.example.customerbackend.customer;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CustomerRequest {

    private static final int MAX_NAME_LENGTH = 100;
    private static final LocalDate MIN_DATE_OF_BIRTH = LocalDate.of(1900, 1, 1);

    @NotBlankAfterTrim(message = "First name is required")
    @Size(max = MAX_NAME_LENGTH, message = "First name must be at most " + MAX_NAME_LENGTH + " characters")
    private String firstName;

    @NotBlankAfterTrim(message = "Last name is required")
    @Size(max = MAX_NAME_LENGTH, message = "Last name must be at most " + MAX_NAME_LENGTH + " characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @AssertTrue(message = "Date of birth must be from 1900 onwards")
    public boolean isDateOfBirthAfter1900() {
        if (dateOfBirth == null) return true;
        return !dateOfBirth.isBefore(MIN_DATE_OF_BIRTH);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

