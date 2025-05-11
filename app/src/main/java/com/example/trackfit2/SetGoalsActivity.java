/**
 * SetGoalsActivity.java
 *
 * Activity for setting and managing fitness goals with comprehensive validation.
 * Uses DailyDataValidator for consistent input validation across the application.
 * Author: Nguinfack Franck-styve
 * Key Responsibilities:
 * - Loads existing goals from database
 * - Validates user input using DailyDataValidator
 * - Persists valid goals to database
 * - Provides user feedback via Toast messages
 * - Navigates to SaveDailyDataActivity on success
 * Validation Rules:
 * - Uses DailyDataValidator for:
 *   - Steps: 0-250,000
 *   - Calories: 0-30,000
 *   - Active Time: 0-1440 minutes
 */
 package com.example.trackfit2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetGoalsActivity extends AppCompatActivity {
    ;
    private FitnessDatabaseHelper dataBase;
    EditText activeTimeInput, caloriesInput, stepsInput;
   Button submitGoal;

    private final DailyDataValidator validator = new DailyDataValidator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_goals);

        initializeViews();
        setupDatabase();
        configureSystemInsets();
        setupButtonListener();
        loadGoalsData();
    }
    /**
     * Initializes view references from layout
     */
    private void initializeViews() {
        stepsInput = findViewById(R.id.stepsInput);
        caloriesInput = findViewById(R.id.caloriesInput);
        activeTimeInput = findViewById(R.id.activeTimeInput);
        submitGoal = findViewById(R.id.submitGoalsButton);
    }
    /**
     * Initializes database helper instance
     */
    private void setupDatabase() {
        dataBase = new FitnessDatabaseHelper(this);
    }

    private void configureSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupButtonListener() {
        submitGoal.setOnClickListener(view -> {
            if (validateGoals()) {
                saveGoals();
            }
        });
    }
    /**
     * Validates all input fields using DailyDataValidator
     * @return true if all inputs are valid
     */
    private boolean validateGoals() {
        String steps = stepsInput.getText().toString().trim();
        String calories = caloriesInput.getText().toString().trim();
        String activeTime = activeTimeInput.getText().toString().trim();

        ValidationResult stepsResult = validator.validateSteps(steps);
        if (!stepsResult.isValid()) {
            showError(stepsResult.getErrorMessage());
            return false;
        }

        ValidationResult caloriesResult = validator.validateCalories(calories);
        if (!caloriesResult.isValid()) {
            showError(caloriesResult.getErrorMessage());
            return false;
        }

        ValidationResult activeTimeResult = validator.validateActiveTime(activeTime);
        if (!activeTimeResult.isValid()) {
            showError(activeTimeResult.getErrorMessage());
            return false;
        }

        return true;
    }
    /**
     * Loads existing goals from database into UI
     */
    private void loadGoalsData() {
        stepsInput.setText(String.valueOf(dataBase.getStepsGoal()));
        caloriesInput.setText(String.valueOf(dataBase.getCaloricGoal()));
        activeTimeInput.setText(String.valueOf(dataBase.getActiveTimeGoal()));
    }
    /**
     * Saves validated goals to database
     */
    private void saveGoals() {
        try {
            int steps = Integer.parseInt(stepsInput.getText().toString().trim());
            int calories = Integer.parseInt(caloriesInput.getText().toString().trim());
            int activeTime = Integer.parseInt(activeTimeInput.getText().toString().trim());

            dataBase.saveGoals(steps, calories, activeTime);
            showSuccess("Goals updated successfully");
            navigateToSaveDailyData();
        } catch (NumberFormatException e) {
            showError("Invalid number format");
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
     // navigate to SaveDailyDataActivity
    private void navigateToSaveDailyData() {
        Intent intent = new Intent(this, SaveDailyDataActivity.class);
        startActivity(intent);
    }
}