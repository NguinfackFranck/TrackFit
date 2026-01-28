/**
 * PersonalInfoValidator.java
 *
 * Validates personal health metrics including weight, height, and gender selection.
 * Implements business rules for acceptable ranges and formats of health data.
 *
 * Author: Nguinfack Franck-styve
 * Key Features:
 * - Validates weight in kilograms (20-300 kg range)
 * - Validates height in centimeters (100-300 cm range)
 * - Validates gender selection (must be specified)
 * - Provides clear error messages for invalid inputs
 *
 * Validation Logic:
 * - All methods follow fail-fast validation principle
 * - Input sanitization (trimming whitespace)
 * - Type checking (numeric conversion)
 * - Range validation
 */
package com.example.trackfit2;
public class PersonalInfoValidator {
    public ValidationResult validateWeight(String weight) {
        if (weight == null || weight.trim().isEmpty()) {
            return new ValidationResult(false, "Weight is required");
        }

        try {
            int weightValue = Integer.parseInt(weight);
            if (weightValue < 20 || weightValue > 300) {
                return new ValidationResult(false, "Weight must be between 20 and 300");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Weight must be a number");
        }
    }

    public ValidationResult validateHeight(String height) {
        if (height == null || height.trim().isEmpty()) {
            return new ValidationResult(false, "Height is required");
        }

        try {
            int heightValue = Integer.parseInt(height);
            if (heightValue < 100 || heightValue > 300) {
                return new ValidationResult(false, "Height must be between 100 and 300");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Height must be a number");
        }
    }

    public ValidationResult validateGender(int selectedGenderId) {
        if (selectedGenderId == -1) {
            return new ValidationResult(false, "Please select your gender");
        }
        return new ValidationResult(true, null);
    }
}
