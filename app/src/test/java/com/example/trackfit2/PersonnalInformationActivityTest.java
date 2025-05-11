
/**
 * PersonnalInformationActivityTest.java
 *
 * Unit tests for the PersonalInfoValidator class.
 * These tests verify the correctness and robustness of validation logic
 * for personal information inputs: weight, height, and gender.
 *
 * Each test ensures edge cases such as null, empty, invalid formats, and bounds are handled.
 *
 * Author: Nguinfack Franck-styve
 */

package com.example.trackfit2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonnalInformationActivityTest {

    private PersonalInfoValidator validator;

    /**
     * Initializes a new validator instance before each test.
     */
    @Before
    public void setUp() {
        validator = new PersonalInfoValidator();
    }

    // ----------- Weight Validation -----------

    /**
     * Test: Null weight input should return an error.
     */
    @Test
    public void validateWeight_Null_ReturnsError() {
        ValidationResult result = validator.validateWeight(null);
        assertFalse(result.isValid());
        assertEquals("Weight is required", result.getErrorMessage());
    }

    /**
     * Test: Empty weight string should return an error.
     */
    @Test
    public void validateWeight_Empty_ReturnsError() {
        ValidationResult result = validator.validateWeight("");
        assertFalse(result.isValid());
        assertEquals("Weight is required", result.getErrorMessage());
    }

    /**
     * Test: Non-numeric weight input should return a format error.
     */
    @Test
    public void validateWeight_NonNumeric_ReturnsError() {
        ValidationResult result = validator.validateWeight("abc");
        assertFalse(result.isValid());
        assertEquals("Weight must be a number", result.getErrorMessage());
    }

    /**
     * Test: Weight below the minimum threshold (20kg) should return an error.
     */
    @Test
    public void validateWeight_TooLow_ReturnsError() {
        ValidationResult result = validator.validateWeight("19");
        assertFalse(result.isValid());
        assertEquals("Weight must be between 20 and 300", result.getErrorMessage());
    }

    /**
     * Test: Weight above the maximum threshold (300kg) should return an error.
     */
    @Test
    public void validateWeight_TooHigh_ReturnsError() {
        ValidationResult result = validator.validateWeight("301");
        assertFalse(result.isValid());
        assertEquals("Weight must be between 20 and 300", result.getErrorMessage());
    }

    /**
     * Test: Valid weight input should pass validation.
     */
    @Test
    public void validateWeight_Valid_ReturnsSuccess() {
        ValidationResult result = validator.validateWeight("70");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ----------- Height Validation -----------

    /**
     * Test: Null height input should return an error.
     */
    @Test
    public void validateHeight_Null_ReturnsError() {
        ValidationResult result = validator.validateHeight(null);
        assertFalse(result.isValid());
        assertEquals("Height is required", result.getErrorMessage());
    }

    /**
     * Test: Empty height string should return an error.
     */
    @Test
    public void validateHeight_Empty_ReturnsError() {
        ValidationResult result = validator.validateHeight("");
        assertFalse(result.isValid());
        assertEquals("Height is required", result.getErrorMessage());
    }

    /**
     * Test: Non-numeric height input should return a format error.
     */
    @Test
    public void validateHeight_NonNumeric_ReturnsError() {
        ValidationResult result = validator.validateHeight("abc");
        assertFalse(result.isValid());
        assertEquals("Height must be a number", result.getErrorMessage());
    }

    /**
     * Test: Height below the minimum threshold (100cm) should return an error.
     */
    @Test
    public void validateHeight_TooLow_ReturnsError() {
        ValidationResult result = validator.validateHeight("99");
        assertFalse(result.isValid());
        assertEquals("Height must be between 100 and 300", result.getErrorMessage());
    }

    /**
     * Test: Height above the maximum threshold (300cm) should return an error.
     */
    @Test
    public void validateHeight_TooHigh_ReturnsError() {
        ValidationResult result = validator.validateHeight("301");
        assertFalse(result.isValid());
        assertEquals("Height must be between 100 and 300", result.getErrorMessage());
    }

    /**
     * Test: Valid height input should pass validation.
     */
    @Test
    public void validateHeight_Valid_ReturnsSuccess() {
        ValidationResult result = validator.validateHeight("175");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ----------- Gender Validation -----------

    /**
     * Test: No gender selected (e.g., -1) should return an error.
     */
    @Test
    public void validateGender_NotSelected_ReturnsError() {
        ValidationResult result = validator.validateGender(-1);
        assertFalse(result.isValid());
        assertEquals("Please select a gender", result.getErrorMessage());
    }

    /**
     * Test: Valid gender selection (e.g., radio button ID > 0) should pass.
     */
    @Test
    public void validateGender_Selected_ReturnsSuccess() {
        ValidationResult result = validator.validateGender(1); // any valid ID
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
}
