/**
 * DailyDataValidatorTest.java
 *
 * Unit tests for DailyDataValidator using JUnit4.
 * Covers edge cases and validation logic for steps, calories, and active time.
 *
 * Author: Nguinfack Franck-styve
 */

package com.example.trackfit2;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DailyDataValidatorTest {

    private DailyDataValidator validator;

    @Before
    public void setUp() {
        validator = new DailyDataValidator();
    }

    // ----------------- Test: Steps -----------------

    @Test
    public void testValidateSteps_valid() {
        ValidationResult result = validator.validateSteps("10000");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateSteps_null() {
        ValidationResult result = validator.validateSteps(null);
        assertFalse(result.isValid());
        assertEquals("Steps cannot be null", result.getErrorMessage());
    }

    @Test
    public void testValidateSteps_empty() {
        ValidationResult result = validator.validateSteps("   ");
        assertFalse(result.isValid());
        assertEquals("Steps cannot be empty", result.getErrorMessage());
    }

    @Test
    public void testValidateSteps_negative() {
        ValidationResult result = validator.validateSteps("-500");
        assertFalse(result.isValid());
        assertEquals("Steps cannot be negative", result.getErrorMessage());
    }

    @Test
    public void testValidateSteps_exceedsMax() {
        ValidationResult result = validator.validateSteps("999999");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Steps exceed human capacity"));
    }

    @Test
    public void testValidateSteps_decimal() {
        ValidationResult result = validator.validateSteps("1000.5");
        assertFalse(result.isValid());
        assertEquals("Steps must be whole numbers", result.getErrorMessage());
    }

    @Test
    public void testValidateSteps_nonNumeric() {
        ValidationResult result = validator.validateSteps("abc");
        assertFalse(result.isValid());
        assertEquals("Steps must be a valid number", result.getErrorMessage());
    }

    // ----------------- Test: Calories -----------------

    @Test
    public void testValidateCalories_valid() {
        ValidationResult result = validator.validateCalories("2000");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateCalories_null() {
        ValidationResult result = validator.validateCalories(null);
        assertFalse(result.isValid());
        assertEquals("Calories cannot be null", result.getErrorMessage());
    }

    @Test
    public void testValidateCalories_empty() {
        ValidationResult result = validator.validateCalories("   ");
        assertFalse(result.isValid());
        assertEquals("Calories cannot be empty", result.getErrorMessage());
    }

    @Test
    public void testValidateCalories_negative() {
        ValidationResult result = validator.validateCalories("-100");
        assertFalse(result.isValid());
        assertEquals("Calories cannot be negative", result.getErrorMessage());
    }

    @Test
    public void testValidateCalories_exceedsMax() {
        ValidationResult result = validator.validateCalories("40000");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Calories exceed physiological limits"));
    }

    @Test
    public void testValidateCalories_decimal() {
        ValidationResult result = validator.validateCalories("123.45");
        assertFalse(result.isValid());
        assertEquals("Use whole numbers for calories", result.getErrorMessage());
    }

    @Test
    public void testValidateCalories_leadingZeros() {
        ValidationResult result = validator.validateCalories("0123");
        assertFalse(result.isValid());
        assertEquals("Remove leading zeros", result.getErrorMessage());
    }

    @Test
    public void testValidateCalories_nonNumeric() {
        ValidationResult result = validator.validateCalories("xyz");
        assertFalse(result.isValid());
        assertEquals("Calories must be a valid number", result.getErrorMessage());
    }

    // ----------------- Test: Active Time -----------------

    @Test
    public void testValidateActiveTime_valid() {
        ValidationResult result = validator.validateActiveTime("60");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateActiveTime_null() {
        ValidationResult result = validator.validateActiveTime(null);
        assertFalse(result.isValid());
        assertEquals("Active time cannot be null", result.getErrorMessage());
    }

    @Test
    public void testValidateActiveTime_empty() {
        ValidationResult result = validator.validateActiveTime("  ");
        assertFalse(result.isValid());
        assertEquals("Active time cannot be empty", result.getErrorMessage());
    }

    @Test
    public void testValidateActiveTime_negative() {
        ValidationResult result = validator.validateActiveTime("-60");
        assertFalse(result.isValid());
        assertEquals("Time cannot be negative", result.getErrorMessage());
    }

    @Test
    public void testValidateActiveTime_decimal() {
        ValidationResult result = validator.validateActiveTime("120.5");
        assertFalse(result.isValid());
        assertEquals("Use whole minutes", result.getErrorMessage());
    }

    @Test
    public void testValidateActiveTime_exceedsMax() {
        ValidationResult result = validator.validateActiveTime("1500");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Exceeds 24 hours"));
    }

    @Test
    public void testValidateActiveTime_HHMM_valid() {
        ValidationResult result = validator.validateActiveTime("1:30");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateActiveTime_HHMM_invalidFormat() {
        ValidationResult result = validator.validateActiveTime("1:70");
        assertFalse(result.isValid());
        assertEquals("Invalid time values", result.getErrorMessage());
    }

    @Test
    public void testValidateActiveTime_HHMM_nonNumeric() {
        ValidationResult result = validator.validateActiveTime("a:b");
        assertFalse(result.isValid());
        assertEquals("Use numbers in HH:MM format", result.getErrorMessage());
    }
}