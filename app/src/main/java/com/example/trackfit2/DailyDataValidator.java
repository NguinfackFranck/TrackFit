/**
 * DailyDataValidator.java
 *
 * Enhanced validation for daily fitness metrics with comprehensive edge case handling.
 * Implements strict validation for steps, calories, and active time with realistic limits.
 *
 * Author: Nguinfack Franck-styve
 */
package com.example.trackfit2;
public class DailyDataValidator {
    // Realistic maximum constants based on world records and medical studies
    private static final int MAX_HUMAN_STEPS_PER_DAY = 250000; // Ultra-marathon record
    private static final int MAX_CALORIES_PER_DAY = 30000; // Extreme endurance athletes
    private static final int MAX_ACTIVE_MINUTES = 1440; // Minutes in a day

    /**
     * Validates daily step count with comprehensive edge case handling.
     *
     * @param steps The step count string to validate
     * @return ValidationResult with detailed error messages
     *
     * Enhanced Validation:
     * 1. Null/empty check
     * 2. Numeric format validation
     * 3. Non-negative check
     * 4. Realistic upper bound (250,000 steps)
     * 5. Leading/trailing whitespace handling
     * 6. Empty string after trim check
     *
     * Edge Cases Handled:
     * - Extremely large values (e.g., 999999999)
     * - Negative values
     * - Decimal numbers
     * - Whitespace-only strings
     */
    public ValidationResult validateSteps(String steps) {
        // Null check
        if (steps == null) {
            return new ValidationResult(false, "Steps cannot be null");
        }

        // Empty string and whitespace check
        String trimmedSteps = steps.trim();
        if (trimmedSteps.isEmpty()) {
            return new ValidationResult(false, "Steps cannot be empty");
        }

        try {
            int stepsValue = Integer.parseInt(trimmedSteps);

            // Negative value check
            if (stepsValue < 0) {
                return new ValidationResult(false, "Steps cannot be negative");
            }

            // Realistic upper bound check
            if (stepsValue > MAX_HUMAN_STEPS_PER_DAY) {
                return new ValidationResult(false,
                        String.format("Steps exceed human capacity (max %d)", MAX_HUMAN_STEPS_PER_DAY));
            }

            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            // Handle decimal numbers and non-numeric strings
            if (trimmedSteps.matches(".*\\..*")) {
                return new ValidationResult(false, "Steps must be whole numbers");
            }
            return new ValidationResult(false, "Steps must be a valid number");
        }
    }

    /**
     * Validates calorie expenditure with enhanced checks.
     *
     * @param calories The calorie string to validate
     * @return ValidationResult with contextual messages
     *
     * New Checks Added:
     * 1. Explicit null check
     * 2. Empty string after trim
     * 3. Decimal number handling
     * 4. Realistic upper bound (30,000 kcal)
     * 5. Leading zeros check
     */
    public ValidationResult validateCalories(String calories) {
        if (calories == null) {
            return new ValidationResult(false, "Calories cannot be null");
        }

        String trimmedCalories = calories.trim();
        if (trimmedCalories.isEmpty()) {
            return new ValidationResult(false, "Calories cannot be empty");
        }

        // Check for leading zeros in numbers > 0
        if (trimmedCalories.length() > 1 && trimmedCalories.startsWith("0")) {
            return new ValidationResult(false, "Remove leading zeros");
        }

        try {
            int caloriesValue = Integer.parseInt(trimmedCalories);

            if (caloriesValue < 0) {
                return new ValidationResult(false, "Calories cannot be negative");
            }

            if (caloriesValue > MAX_CALORIES_PER_DAY) {
                return new ValidationResult(false,
                        String.format("Calories exceed physiological limits (max %d)", MAX_CALORIES_PER_DAY));
            }

            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            if (trimmedCalories.matches(".*\\..*")) {
                return new ValidationResult(false, "Use whole numbers for calories");
            }
            return new ValidationResult(false, "Calories must be a valid number");
        }
    }

    /**
     * Validates active time with comprehensive edge case protection.
     *
     * @param activeTime The active time string in minutes
     * @return ValidationResult with specific guidance
     *
     * Additional Protections:
     * 1. Time format validation (HH:MM allowed)
     * 2. Upper bound of 1440 minutes
     * 3. Decimal number rejection
     * 4. Zero-value special case
     */
    public ValidationResult validateActiveTime(String activeTime) {
        if (activeTime == null) {
            return new ValidationResult(false, "Active time cannot be null");
        }

        String trimmedTime = activeTime.trim();
        if (trimmedTime.isEmpty()) {
            return new ValidationResult(false, "Active time cannot be empty");
        }

        // Handle HH:MM format if needed
        if (trimmedTime.contains(":")) {
            return validateTimeFormat(trimmedTime);
        }

        try {
            int minutes = Integer.parseInt(trimmedTime);

            if (minutes < 0) {
                return new ValidationResult(false, "Time cannot be negative");
            }

            if (minutes > MAX_ACTIVE_MINUTES) {
                return new ValidationResult(false,
                        String.format("Exceeds 24 hours (max %d minutes)", MAX_ACTIVE_MINUTES));
            }

            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            if (trimmedTime.matches(".*\\..*")) {
                return new ValidationResult(false, "Use whole minutes");
            }
            return new ValidationResult(false, "Enter valid minutes (0-1440)");
        }
    }

    /**
     * Helper method to validate HH:MM time format.
     *
     * @param timeString The time string in HH:MM format
     * @return ValidationResult for time format
     */
    private ValidationResult validateTimeFormat(String timeString) {
        try {
            String[] parts = timeString.split(":");
            if (parts.length != 2) {
                return new ValidationResult(false, "Use HH:MM format");
            }

            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            if (hours < 0 || minutes < 0 || minutes >= 60) {
                return new ValidationResult(false, "Invalid time values");
            }

            int totalMinutes = hours * 60 + minutes;
            if (totalMinutes > MAX_ACTIVE_MINUTES) {
                return new ValidationResult(false, " Time Exceeds 24 hours");
            }

            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Use numbers in HH:MM format");
        }
    }
}