/**
 * EditProfileValidator.java
 *
 * Implements validation logic for user profile editing functionality in TrackFit2 application.
 * Follows the Strategy Pattern for validation rules and implements defensive programming.
 *
 * Author: Nguinfack Franck-Styve
 *
 * Validation Scope:
 * - Name: Presence check
 * - Age: Numeric range (10-100 years)
 * - Height: Numeric range (100-300 cm)
 * - Weight: Numeric range (20-300 kg)
 * - Gender: Selection verification
 *
 * Design Patterns:
 * - Strategy Pattern (validation methods)
 * - Null Object Pattern (empty error messages)
 *
 * Error Handling:
 * - Returns ValidationResult objects containing:
 *   - Validation status (boolean)
 *   - Contextual error messages
 * - Handles NumberFormatException for numeric fields
 *
 */
package com.example.trackfit2;
public class EditProfileValidator {
    /**
     * Validates user's name input.
     *
     * @param name The name string to validate
     * @return ValidationResult indicating success/failure
     *
     * Validation Rules:
     * - Non-null
     * - Non-empty after trimming
     * - No length restrictions (handled by UI)
     */
    public ValidationResult validateName(String name) {
    if (name == null || name.trim().isEmpty()) {
        return new ValidationResult(false, "Name is required");
    }
    return new ValidationResult(true, null);
}
    /**
     * Validates age input with comprehensive checks.
     *
     * @param age The age string to validate
     * @return ValidationResult with appropriate status
     *
     * Validation Rules:
     * 1. Presence check
     * 2. Numeric format
     * 3. Range validation (10-100)
     *
     * Edge Cases:
     * - Handles NumberFormatException
     * - Empty strings
     * - Boundary values
     */

    public ValidationResult validateAge(String age) {
        if (age == null || age.trim().isEmpty()) {
            return new ValidationResult(false, "Age is required");
        }

        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 10 || ageValue > 100) {
                return new ValidationResult(false, "Age must be between 10 and 100");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Age must be a number");
        }
    }
    /**
     * Validates height input in centimeters.
     *
     * @param height The height string to validate
     * @return ValidationResult object
     *
     * Scientific Basis:
     * - Minimum 100cm (3'3") for adult height
     * - Maximum 300cm (9'10") based on world records
     */

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
    /**
     * Validates weight input in kilograms.
     *
     * @param weight The weight string to validate
     * @return ValidationResult with status
     *
     * Implementation Notes:
     * - Uses Float for decimal precision
     * - Minimum 20kg based on medical standards
     * - Maximum 300kg based on practical limits
     */

    public ValidationResult validateWeight(String weight) {
        if (weight == null || weight.trim().isEmpty()) {
            return new ValidationResult(false, "Weight is required");
        }

        try {
            float weightValue = Float.parseFloat(weight);
            if (weightValue < 20 || weightValue > 300) {
                return new ValidationResult(false, "Weight must be between 20 and 300");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Weight must be a number");
        }
    }
    /**
     * Validates gender selection.
     *
     * @param selectedGenderId The selected gender ID
     * @return ValidationResult
     *
     * UI Integration:
     * - Expects -1 for unselected state
     * - Any positive value considered valid
     */

    public ValidationResult validateGender(int selectedGenderId) {
        if (selectedGenderId == -1) {
            return new ValidationResult(false, "Please select a gender");
        }
        return new ValidationResult(true, null);
    }
}
