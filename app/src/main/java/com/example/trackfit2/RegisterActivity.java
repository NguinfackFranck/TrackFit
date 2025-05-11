package com.example.trackfit2;
/**
 * RegisterActivity.java
 * Author: Nguinfack Franck-styve
 * Handles new user registration process including input validation and profile creation.
 * Implements the Form Entry design pattern with real-time validation.
 * Key Functionality:
 * - Collects and validates user demographic information
 * - Persists user data using SharedPreferences wrapper
 * - Implements clean error reporting pattern
 * - Maintains separation of validation logic
 */

 import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    RegistrationValidator validator = new RegistrationValidator();
    // View components
    public EditText nameInput, ageInput;
    private Button confirm;
    // Persistence handler
    public PrefsHelper prefsHelper;
    /**
     * Initializes registration form and sets up validation.
     * UI Configuration:
     * - nameInput (R.id.nameInput)
     * - ageInput (R.id.ageInput)
     * - confirmButton (R.id.confirmButton)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        // View binding
        nameInput= findViewById(R.id.nameInput);
        ageInput= findViewById(R.id.ageInput);
        confirm= findViewById(R.id.confirmButton);
        prefsHelper = new PrefsHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    registerUser();
                }
            }
        });
    }
/**
 * Validates form inputs using separate validator component.
 *
 * @return Boolean indicating validation success
 *
 * Validation Rules:
 * - Name: Non-empty, reasonable length
 * - Age: Numeric, within valid range
 *
 * Error Handling:
 * - Sets error messages directly on views
 */

    public Boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        ValidationResult nameResult = validator.validateName(name);
        ValidationResult ageResult = validator.validateAge(age);

        if (!nameResult.isValid()) {
            nameInput.setError(nameResult.getErrorMessage());
            return false;
        }

        if (!ageResult.isValid()) {
            ageInput.setError(ageResult.getErrorMessage());
            return false;
        }


        return true;
    }
    /**
     * Persists user data and advances to next screen.
     *
     * Data Flow:
     * 1. Extracts values from form inputs
     * 2. Saves via PrefsHelper
     * 3. Logs registration event
     * 4. Navigates to personal information screen
     */
    public void registerUser(){
        String name = nameInput.getText().toString().trim();
        int age = Integer.parseInt(ageInput.getText().toString().trim());
        prefsHelper.saveAgeandName(name, age);
        Log.d("REGISTER", "User registered: Name=" + name + ", Age=" + age);
        Log.d("REGISTER", "Starting DashboardActivity");
        Intent intent = new Intent(this, PersonnalInformationActivity.class);
        startActivity(intent);
        finish();
    }}
