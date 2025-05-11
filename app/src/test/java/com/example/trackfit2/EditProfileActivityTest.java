/**
 * EditProfileActivityTest.java
 *
 * Unit tests for the EditProfileValidator class.
 * This suite checks the validation rules for user profile fields in the Edit Profile activity.
 * Covers validation for name, age, height, weight, and gender.
 *
 * Author: Nguinfack Franck-styve
 */

package com.example.trackfit2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EditProfileActivityTest {

    private EditProfileValidator validator;

    /**
     * Initializes the EditProfileValidator before each test.
     */
    @Before
    public void setUp() {
        validator = new EditProfileValidator();
    }

    // ----------- Name Validation -----------

    /**
     * Test: Null name input should return an error.
     */
    @Test
    public void validateName_Null_ReturnsError() {
        ValidationResult result = validator.validateName(null);
        assertFalse(result.isValid());
        assertEquals("Name is required", result.getErrorMessage());
    }

    /**
     * Test: Empty name string should return an error.
     */
    @Test
    public void validateName_Empty_ReturnsError() {
        ValidationResult result = validator.validateName("");
        assertFalse(result.isValid());
        assertEquals("Name is required", result.getErrorMessage());
    }

    /**
     * Test: Valid name input should pass validation.
     */
    @Test
    public void validateName_Valid_ReturnsSuccess() {
        ValidationResult result = validator.validateName("John Doe");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ----------- Age Validation -----------

    /**
     * Test: Null age input should return an error.
     */
    @Test
    public void validateAge_Null_ReturnsError() {
        ValidationResult result = validator.validateAge(null);
        assertFalse(result.isValid());
        assertEquals("Age is required", result.getErrorMessage());
    }

    /**
     * Test: Age below minimum limit (10) should return an error.
     */
    @Test
    public void validateAge_TooLow_ReturnsError() {
        ValidationResult result = validator.validateAge("9");
        assertFalse(result.isValid());
        assertEquals("Age must be between 10 and 100", result.getErrorMessage());
    }

    /**
     * Test: Age above maximum limit (100) should return an error.
     */
    @Test
    public void validateAge_TooHigh_ReturnsError() {
        ValidationResult result = validator.validateAge("101");
        assertFalse(result.isValid());
        assertEquals("Age must be between 10 and 100", result.getErrorMessage());
    }

    /**
     * Test: Valid age input should pass validation.
     */
    @Test
    public void validateAge_Valid_ReturnsSuccess() {
        ValidationResult result = validator.validateAge("25");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    // ----------- Height Validation -----------

    /**
     * Test: Height below the valid range should return an error.
     */
    @Test
    public void validateHeight_TooLow_ReturnsError() {
        ValidationResult result = validator.validateHeight("99");
        assertFalse(result.isValid());
        assertEquals("Height must be between 100 and 300", result.getErrorMessage());
    }

    /**
     * Test: Height above the valid range should return an error.
     */
    @Test
    public void validateHeight_TooHigh_ReturnsError() {
        ValidationResult result = validator.validateHeight("301");
        assertFalse(result.isValid());
        assertEquals("Height must be between 100 and 300", result.getErrorMessage());
    }

    // ----------- Weight Validation -----------

    /**
     * Test: Weight below the valid range should return an error.
     */
    @Test
    public void validateWeight_TooLow_ReturnsError() {
        ValidationResult result = validator.validateWeight("19");
        assertFalse(result.isValid());
        assertEquals("Weight must be between 20 and 300", result.getErrorMessage());
    }

    /**
     * Test: Weight above the valid range should return an error.
     */
    @Test
    public void validateWeight_TooHigh_ReturnsError() {
        ValidationResult result = validator.validateWeight("301");
        assertFalse(result.isValid());
        assertEquals("Weight must be between 20 and 300", result.getErrorMessage());
    }

    // ----------- Gender Validation -----------

    /**
     * Test: No gender selected (-1) should return an error.
     */
    @Test
    public void validateGender_NotSelected_ReturnsError() {
        ValidationResult result = validator.validateGender(-1);
        assertFalse(result.isValid());
        assertEquals("Please select a gender", result.getErrorMessage());
    }

    /**
     * Test: A selected gender (positive ID) should pass validation.
     */
    @Test
    public void validateGender_Selected_ReturnsSuccess() {
        ValidationResult result = validator.validateGender(1); // Any valid ID
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
}
