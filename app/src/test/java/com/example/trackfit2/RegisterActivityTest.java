/**
 * RegisterActivityTest.java
 *
 * Unit tests for the RegistrationValidator class.
 * This test suite verifies validation rules for user name and age inputs
 * in the registration flow of the TrackFit2 application.
 *
 * It checks edge cases including null, empty, non-numeric, and boundary values.
 *
 * Author: Nguinfack Franck-styve
 */

package com.example.trackfit2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterActivityTest {

    private RegistrationValidator validator;

    /**
     * Set up a fresh instance of the RegistrationValidator before each test.
     */
    @Before
    public void setUp() {
        validator = new RegistrationValidator();
    }

    // ----------- Name Validation Tests -----------

    /**
     * Test: Null name input should return an error.
     */
    @Test
    public void validateName_NullName_ReturnsError() {
        ValidationResult result = validator.validateName(null);
        assertFalse(result.isValid());
        assertEquals("Insert name", result.getErrorMessage());
    }

    /**
     * Test: Empty string name should return an error.
     */
    @Test
    public void validateName_EmptyName_ReturnsError() {
        ValidationResult result = validator.validateName("");
        assertFalse(result.isValid());
        assertEquals("Insert name", result.getErrorMessage());
    }

    /**
     * Test: Valid name input should be accepted.
     */
    @Test
    public void validateName_ValidName_ReturnsValid() {
        ValidationResult result = validator.validateName("John Doe");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ----------- Age Validation Tests -----------

    /**
     * Test: Null age input should return an error.
     */
    @Test
    public void validateAge_NullAge_ReturnsError() {
        ValidationResult result = validator.validateAge(null);
        assertFalse(result.isValid());
        assertEquals("Insert age", result.getErrorMessage());
    }

    /**
     * Test: Empty age string should return an error.
     */
    @Test
    public void validateAge_EmptyAge_ReturnsError() {
        ValidationResult result = validator.validateAge("");
        assertFalse(result.isValid());
        assertEquals("Insert age", result.getErrorMessage());
    }

    /**
     * Test: Non-numeric age input should return a number format error.
     */
    @Test
    public void validateAge_NonNumericAge_ReturnsError() {
        ValidationResult result = validator.validateAge("abc");
        assertFalse(result.isValid());
        assertEquals("Age must be a number", result.getErrorMessage());
    }

    /**
     * Test: Age below the minimum (10) should return an error.
     */
    @Test
    public void validateAge_AgeBelowMinimum_ReturnsError() {
        ValidationResult result = validator.validateAge("9");
        assertFalse(result.isValid());
        assertEquals("Insert a valid age (10-100)", result.getErrorMessage());
    }

    /**
     * Test: Age above the maximum (100) should return an error.
     */
    @Test
    public void validateAge_AgeAboveMaximum_ReturnsError() {
        ValidationResult result = validator.validateAge("101");
        assertFalse(result.isValid());
        assertEquals("Insert a valid age (10-100)", result.getErrorMessage());
    }

    /**
     * Test: Minimum valid age (10) should pass validation.
     */
    @Test
    public void validateAge_MinimumAge_ReturnsValid() {
        ValidationResult result = validator.validateAge("10");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    /**
     * Test: Maximum valid age (100) should pass validation.
     */
    @Test
    public void validateAge_MaximumAge_ReturnsValid() {
        ValidationResult result = validator.validateAge("100");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    /**
     * Test: Typical valid age (e.g., 25) should pass validation.
     */
    @Test
    public void validateAge_ValidAge_ReturnsValid() {
        ValidationResult result = validator.validateAge("25");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
}
