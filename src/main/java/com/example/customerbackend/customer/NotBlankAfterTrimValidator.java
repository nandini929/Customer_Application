package com.example.customerbackend.customer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankAfterTrimValidator implements ConstraintValidator<NotBlankAfterTrim, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !value.trim().isEmpty();
    }
}
