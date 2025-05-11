/**
 * RegistrationValidator.java
 * Author: Nguinfack Franck-styve
 * Implements validation logic for user registration data following domain-specific rules.
 * Utilizes the Strategy Pattern for validation rules and implements defensive programming.
 * Validation Scope:
 * - Name: Presence and  validation
 * - Age: Numeric range validation (10-100 years)
 * Validation Principles:
 * - Fail-fast validation
 * - Contextual error messages
 * - Type safety enforcement
 */
 package com.example.trackfit2;

public class RegistrationValidator {
    // Validate if name is empty or null
    public ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Insert name");
        }
        return new ValidationResult(true, null);
    }
   //validate if name is empty, null and within the boundary
    public ValidationResult validateAge(String age) {
        if (age == null || age.trim().isEmpty()) {
            return new ValidationResult(false, "Insert age");
        }
          // Boundary check
        try {
            int ageNumber = Integer.parseInt(age);
            if (ageNumber < 10 || ageNumber > 100) {
                return new ValidationResult(false, "Insert a valid age (10-100)");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Age must be a number");
        }
    }
}
