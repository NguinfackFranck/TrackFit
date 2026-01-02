package com.example.trackfit2;

/**
 * PersonalInformationActivity.java
 *
 * Activity for collecting and validating user physical metrics during initial setup.
 * Implements the Form Entry design pattern with comprehensive validation.
 *
 * Author: Nguinfack Franck-Styve
 *
 * Purpose:
 * This activity serves as the second step in the user onboarding process,
 * following the RegisterActivity which collects demographic information.
 * It completes the user profile by gathering physical metrics necessary
 * for personalized fitness tracking.
 *
 * Key Functionality:
 * - Collects and validates user physical information (height, weight, gender)
 * - Persists user data locally via SharedPreferences wrapper (PrefsHelper)
 * - Synchronizes complete user profile to Firebase Firestore
 * - Implements clean error reporting pattern with Toast notifications
 * - Maintains separation of validation logic via PersonalInfoValidator
 *
 * Activity Flow:
 * 1. User enters height, weight, and selects gender
 * 2. Inputs are validated in real-time
 * 3. Valid data is saved locally and to cloud
 * 4. User is redirected to DashboardActivity
 *
 * Data Integration:
 * - Combines physical metrics from this activity with demographic data
 *   (name, age) collected in RegisterActivity
 * - Creates complete user profile for cloud synchronization
 *
 * Validation Rules (via PersonalInfoValidator):
 * - Weight: Numeric value within valid range (20-300 kg)
 * - Height: Numeric value within valid range (100-300 cm)
 * - Gender: Radio button selection required
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PersonnalInformationActivity extends AppCompatActivity {

    /**
     * Validator instance for physical information input validation.
     * Encapsulates validation logic for separation of concerns.
     */
    private PersonalInfoValidator validator = new PersonalInfoValidator();

    /**
     * UI Components for collecting physical metrics.
     */
    private EditText heightInput, weightInput;
    private RadioGroup genderInput;
    private Button submitbtn;

    /**
     * Helper class for managing SharedPreferences persistence.
     * Provides abstraction layer for user data storage.
     */
    private PrefsHelper prefsHelper;

    /**
     * Initializes the activity and sets up UI components.
     *
     * This method is called when the activity is first created.
     * It performs the following tasks:
     * 1. Enables edge-to-edge display for modern Android UI
     * 2. Inflates the activity layout
     * 3. Binds view references from XML
     * 4. Initializes PrefsHelper for data persistence
     * 5. Sets up window insets for edge-to-edge compatibility
     * 6. Configures click listener for submit button
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     *                           If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for modern Android UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personnal_information);

        // Initialize view components by binding to XML elements
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        genderInput = findViewById(R.id.genderGroup);
        submitbtn = findViewById(R.id.submitButton);

        // Initialize data persistence helper
        prefsHelper = new PrefsHelper(this);

        // Set up window insets for edge-to-edge compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /**
         * Submit button click handler.
         * Validates input data and proceeds with registration if validation passes.
         */
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputData()) {
                    registerData();
                }
            }
        });
    }

    /**
     * Validates all form inputs using the PersonalInfoValidator.
     *
     * This method implements comprehensive validation with immediate user feedback.
     * Validation is performed in the following sequence:
     * 1. Weight validation: Checks if input is numeric and within acceptable range
     * 2. Height validation: Checks if input is numeric and within acceptable range
     * 3. Gender validation: Ensures a radio button selection has been made
     *
     * Error Handling:
     * - Invalid inputs trigger Toast notifications with specific error messages
     * - Validation stops at the first encountered error (short-circuit evaluation)
     * - User can correct errors and resubmit without losing other valid inputs
     *
     * @return Boolean indicating overall validation success
     *         true: All inputs are valid
     *         false: One or more inputs failed validation
     */
    private boolean validateInputData() {
        // Extract raw input values from form fields
        String weight = weightInput.getText().toString().trim();
        String height = heightInput.getText().toString().trim();
        int genderId = genderInput.getCheckedRadioButtonId();

        // Perform individual validations
        ValidationResult weightResult = validator.validateWeight(weight);
        ValidationResult heightResult = validator.validateHeight(height);
        ValidationResult genderResult = validator.validateGender(genderId);

        // Check weight validation
        if (!weightResult.isValid()) {
            Toast.makeText(this, weightResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check height validation
        if (!heightResult.isValid()) {
            Toast.makeText(this, heightResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check gender selection
        if (!genderResult.isValid()) {
            Toast.makeText(this, genderResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        // All validations passed
        return true;
    }

    /**
     * Persists validated user data and advances to the dashboard.
     *
     * This method orchestrates the complete data flow for profile completion:
     * 1. Parses input values into appropriate data types
     * 2. Extracts gender selection from radio buttons
     * 3. Persists physical metrics locally via PrefsHelper
     * 4. Marks setup completion flag in shared preferences
     * 5. Retrieves previously stored demographic data (name, age)
     * 6. Synchronizes complete user profile to Firebase Firestore
     * 7. Navigates to DashboardActivity with cleared back stack
     *
     * Data Integration:
     * - Combines physical metrics (height, weight, gender) from this activity
     *   with demographic data (name, age) collected in RegisterActivity
     * - Creates a complete user profile document in Firestore
     *
     * Cloud Synchronization:
     * - Uses FirebaseSyncService to handle cloud persistence
     * - Implements offline-first approach with eventual consistency
     * - Profile data includes setup_complete flag for user state tracking
     *
     * Navigation:
     * - Clears activity stack to prevent back navigation to setup screens
     * - Dashboard becomes the new root activity
     */
    private void registerData() {
        // Parse numeric inputs (validation ensures these will succeed)
        Float weight = Float.parseFloat(weightInput.getText().toString().trim());
        int height = Integer.parseInt(heightInput.getText().toString().trim());

        // Extract gender selection from radio buttons
        int selectedGenderId = genderInput.getCheckedRadioButtonId();
        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String gender = selectedGenderRadioButton.getText().toString();

        // Persist physical metrics locally
        prefsHelper.saveUserData(height, weight, gender);
        prefsHelper.setSetupComplete(true);

        // Initialize cloud synchronization service
        FirebaseSyncService syncService = new FirebaseSyncService(this);

        // Retrieve previously stored demographic data
        String name = prefsHelper.getUserName();
        int age = prefsHelper.getUserAge();

        // Synchronize complete profile to Firebase Firestore
        syncService.saveUserProfile(name, age, height, weight, gender);

        // Navigate to DashboardActivity
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Prevent back navigation to setup screens
    }
}