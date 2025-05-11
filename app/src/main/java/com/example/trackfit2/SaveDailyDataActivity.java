/**
 * SaveDailyDataActivity.java
 *
 * Activity for saving daily fitness metrics with robust validation and error handling.
 * Implements proper data persistence and user feedback mechanisms.
 *
 * Author: Nguinfack Franck-styve
 *
 * Key Features:
 * - Edge-to-edge UI implementation
 * - Comprehensive input validation
 * - Database persistence
 * - User feedback system
 * - Input sanitization
 */
 package com.example.trackfit2;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SaveDailyDataActivity extends AppCompatActivity {
    // Validator instance
    private DailyDataValidator validator = new DailyDataValidator();
    // View components
EditText dailyStepsInput, dailyCaloriesInput, dailyActiveTimeInput;
Button saveDailyInput;
    // Database helper
    FitnessDatabaseHelper fitnessDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_daily_data);
        /**
         * Binds view references from layout
         */
        dailyStepsInput = findViewById(R.id.dailyStepsInput);
        dailyCaloriesInput = findViewById(R.id.daillyCaloriesInput);
        dailyActiveTimeInput = findViewById(R.id.dailyActiveTimeInput);
        saveDailyInput = findViewById(R.id.saveDailyInput);
        fitnessDatabaseHelper = new FitnessDatabaseHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });// Save data on click
        saveDailyInput.setOnClickListener(view -> {

            if (validateInput()) {
                savaDailyData();
            }
        });
    }
    /**
     * Saves validated data to database
     *
     * Implementation Details:
     * - Parses input values
     * - Persists to database
     * - Provides user feedback
     * - Clears form on success
     */
    private void savaDailyData() {
     try {
         int dailySteps= Integer.parseInt(dailyStepsInput.getText().toString().trim());
        int dailyCalories= Integer.parseInt(dailyCaloriesInput.getText().toString().trim());
        int dailActiveTime=Integer.parseInt(dailyActiveTimeInput.getText().toString().trim());
        fitnessDatabaseHelper.saveDailyData(dailySteps,dailyCalories,dailActiveTime);
        showToast("Data Saved Successfully!");
        clearInputs();
    }catch (NumberFormatException e) {
        showToast("Invalid number format");
    } catch (Exception e) {
        showToast("Failed to save data");
    }}

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void clearInputs() {
        dailyStepsInput.setText("");
        dailyCaloriesInput.setText("");
        dailyActiveTimeInput.setText("");
    }
    /**
     * Validates all input fields
     *
     * @return true if all inputs are valid
     *
     * Validation Flow:
     * 1. Steps validation
     * 2. Calories validation
     * 3. Active time validation
     */
    private boolean validateInput() {
        String steps = dailyStepsInput.getText().toString().trim();
        String calories = dailyCaloriesInput.getText().toString().trim();
        String activeTime = dailyActiveTimeInput.getText().toString().trim();

        ValidationResult stepsResult = validator.validateSteps(steps);
        ValidationResult caloriesResult = validator.validateCalories(calories);
        ValidationResult activeTimeResult = validator.validateActiveTime(activeTime);

        if (!stepsResult.isValid()) {
            showError(stepsResult.getErrorMessage());
            return false;
        }

        if (!caloriesResult.isValid()) {
            showError(caloriesResult.getErrorMessage());
            return false;
        }

        if (!activeTimeResult.isValid()) {
            showError(activeTimeResult.getErrorMessage());
            return false;
        }

        return true;
    }
    /**
     * Displays error message with field focus
     * @param message Error message to display
     */
    private void showError(String message) {
        showToast(message);
    }}