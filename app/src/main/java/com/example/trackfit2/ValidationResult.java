/**
 * ValidationResult.java
 *Author: Nguinfack Franck-styve
 * Immutable data transfer object that encapsulates the result of a validation operation.
 * Implements the Result Object pattern for clean validation result handling.
 */
 package com.example.trackfit2;

public class ValidationResult {
    private final boolean isValid;
    private final String errorMessage;
/**
 * Constructs a new ValidationResult instance.
 *
 * @param isValid Boolean indicating validation success
 * @param errorMessage Descriptive error message (null if valid)
 *
 * Invariants:
 * - errorMessage must be null when isValid is true
 * - errorMessage should not be null when isValid is false
 */
public ValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }
    /**
     * Returns the validation status.
     *
     * @return true if validation succeeded, false otherwise
     */
    public boolean isValid() {
        return isValid;
    }
    /**
     * Returns the error message associated with validation failure.
     *
     * @return String containing error description, or null if valid
     *
     * Contract:
     * - Returns null when isValid() returns true
     * - Returns non-null when isValid() returns false
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
