/**
 * PersonalInfoValidatorTest.java
 *
 * Unit tests for the PersonalInfoValidator class.
 * This suite verifies the validation logic for weight, height, and gender inputs.
 *
 * Author: Nguinfack Franck-styve
 */

package com.example.trackfit2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonalInfoValidatorTest {

    private PersonalInfoValidator validator;

    /**
     * Initialize a fresh instance before each test.
     */
    @Before
    public void setUp() {
        validator = new PersonalInfoValidator();
    }

    // ------------------ Weight Validation ------------------

    /**
     * Test: Valid numeric weight should pass validation.
     */
    @Test
    public void validateWeight_withValidInput_returnsValid() {
        ValidationResult result = validator.validateWeight("70");
        assertTrue(result.isValid());
    }

    /**
     * Test: Empty weight string should return an error.
     */
    @Test
    public void validateWeight_withEmptyString_returnsInvalid() {
        ValidationResult result = validator.validateWeight("");
        assertFalse(result.isValid());
        assertEquals("Weight is required", result.getErrorMessage());
    }

    /**
     * Test: Weight below the minimum threshold (20kg) should return an error.
     */
    @Test
    public void validateWeight_withBelowMin_returnsInvalid() {
        ValidationResult result = validator.validateWeight("10");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("between 20 and 300"));
    }

    // ------------------ Height Validation ------------------

    /**
     * Test: Non-numeric height input should return a format error.
     */
    @Test
    public void validateHeight_withNonNumeric_returnsInvalid() {
        ValidationResult result = validator.validateHeight("six feet");
        assertFalse(result.isValid());
        assertEquals("Height must be a number", result.getErrorMessage());
    }

    // ------------------ Gender Validation ------------------

    /**
     * Test: No gender selected (-1) should return an error.
     */
    @Test
    public void validateGender_withNoSelection_returnsInvalid() {
        ValidationResult result = validator.validateGender(-1);
        assertFalse(result.isValid());
        assertEquals("Please select a gender", result.getErrorMessage());
    }

    /**
     * Test: Valid gender selection (positive ID) should pass.
     */
    @Test
    public void validateGender_withSelection_returnsValid() {
        ValidationResult result = validator.validateGender(1); // Any valid ID
        assertTrue(result.isValid());
    }
}
