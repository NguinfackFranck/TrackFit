/**
 * EditProfileActivity.java
 *
 * Activity for editing and updating user profile information with comprehensive validation.
 * Implements form handling, data validation, and profile persistence.
 *
 * Author: Nguinfack Franck-Styve
 *
 * Key Responsibilities:
 * - Displays current profile information
 * - Validates user input using EditProfileValidator
 * - Persists updated profile data locally and to cloud
 * - Provides user feedback through toasts and error messages
 * - Handles gender selection through radio buttons
 * - Implements data erasure functionality with confirmation
 *
 * Data Fields:
 * - Personal Information: name, age
 * - Physical Metrics: height, weight
 * - Demographic: gender (Male/Female/Other)
 *
 * Validation Rules (via EditProfileValidator):
 * - Name: Non-empty, valid characters
 * - Age: 10-100 years
 * - Height: 100-300 cm
 * - Weight: 20-300 kg
 * - Gender: Must be selected
 *
 * Design Patterns:
 * - Form Validation Pattern: Centralized validation logic
 * - Repository Pattern: Data persistence through PrefsHelper and FirebaseSyncService
 * - Observer Pattern: UI event handling through click listeners
 */
package com.example.trackfit2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditProfileActivity extends AppCompatActivity {

    /**
     * Validator instance for form input validation.
     * Implements separation of concerns by extracting validation logic.
     */
    EditProfileValidator validator = new EditProfileValidator();

    /**
     * UI Components for profile editing.
     */
    private EditText nameEdit, ageEdit, heightEdit, weightEdit;
    private RadioGroup genderEdit;
    private Button saveProfile;

    /**
     * Helper class for managing SharedPreferences persistence.
     * Provides abstraction layer for user data storage.
     */
    private PrefsHelper prefsHelper;

    /**
     * Initializes the activity and sets up UI components.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     *
     * Lifecycle Methods:
     * - Configures edge-to-edge display
     * - Initializes view components
     * - Loads existing profile data
     * - Sets up click listeners for save, logout, and data erasure
     *
     * Data Flow:
     * 1. onCreate() initializes UI and loads existing data
     * 2. User makes edits to form fields
     * 3. validateInput() checks all inputs
     * 4. saveProfile() persists changes locally and to cloud
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for modern Android UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Initialize view components
        Button btnEraseAll = findViewById(R.id.btnEraseAll);
        Button btnLogout = findViewById(R.id.btnLogout);

        initializeViews();
        loadProfiledata();

        // Set up window insets for edge-to-edge compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /**
         * Logout button click handler.
         * Navigates to LogoutActivity for user session termination.
         */
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, LogoutActivity.class);
            startActivity(intent);
        });

        /**
         * Data erasure button click handler.
         * Shows confirmation dialog before performing irreversible deletion.
         */
        btnEraseAll.setOnClickListener(v -> showEraseDataConfirmation());

        /**
         * Save profile button click handler.
         * Validates inputs and saves profile if validation passes.
         */
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    saveProfile();
                }
            }
        });
    }

    /**
     * Displays confirmation dialog for data erasure.
     * Implements defensive design pattern to prevent accidental data loss.
     *
     * Dialog Components:
     * - Title: Clear warning message
     * - Message: Detailed explanation of consequences
     * - Positive Button: Confirms deletion with "Erase Everything"
     * - Negative Button: Cancels operation with "Cancel"
     * - Icon: Warning icon for visual emphasis
     */
    private void showEraseDataConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Erase All Data?")
                .setMessage("This will permanently delete your profile, fitness history, and goals from this device and the cloud. This action cannot be undone.")
                .setPositiveButton("Erase Everything", (dialog, which) -> performFullDataWipe())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Performs complete data deletion across all storage layers.
     * Implements cascading deletion pattern: Cloud → Local → Cache
     *
     * Deletion Sequence:
     * 1. Cloud Data (Firestore) - Requires internet connection
     * 2. Local SQLite Database - Fitness history and goals
     * 3. SharedPreferences - User profile and settings
     * 4. Firebase Authentication - User session termination
     * 5. Navigation - Redirects to MainActivity with cleared task stack
     *
     * Error Handling:
     * - Each step is independent; failure in one doesn't stop others
     * - Toast provides user feedback on completion
     */
    private void performFullDataWipe() {
        FirebaseSyncService syncService = new FirebaseSyncService(this);

        // 1. Wipe Cloud Data first (requires Internet)
        syncService.deleteUserCloudData(() -> {
            // 2. Wipe Local SQLite database
            FitnessDatabaseHelper dbHelper = new FitnessDatabaseHelper(this);
            dbHelper.clearAllData();
            dbHelper.close(); // Important: Release database resources

            // 3. Wipe SharedPreferences
            prefsHelper.clearUserData();
            prefsHelper.setSetupComplete(false);

            // 4. Sign out from Firebase Auth
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            // 5. Redirect to Welcome/Main screen with cleared back stack
            Toast.makeText(this, "All data has been wiped.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Binds view references from layout XML.
     * Implements View Binding pattern to connect Java objects with XML elements.
     *
     * Views Initialized:
     * - Text fields: Name, Age, Height, Weight
     * - RadioGroup: Gender selection
     * - Button: Save profile
     * - Helper: PrefsHelper for data persistence
     */
    private void initializeViews() {
        nameEdit = findViewById(R.id.Name_edit);
        ageEdit = findViewById(R.id.Age_edit);
        heightEdit = findViewById(R.id.Height_edit);
        weightEdit = findViewById(R.id.Weight_edit);
        saveProfile = findViewById(R.id.btnSave);
        genderEdit = findViewById(R.id.genderGroup);
        prefsHelper = new PrefsHelper(this);
    }

    /**
     * Validates all form fields using EditProfileValidator.
     * Implements comprehensive validation pattern with user feedback.
     *
     * @return true if all inputs are valid, false otherwise
     *
     * Validation Flow:
     * 1. Extract raw input values from form fields
     * 2. Validate each field using appropriate validator method
     * 3. Display error messages for invalid inputs
     * 4. Return overall validation status
     *
     * Validation Methods Used:
     * - validateName(): Checks name format and length
     * - validateAge(): Validates age range (10-100)
     * - validateHeight(): Validates height range (100-300 cm)
     * - validateWeight(): Validates weight range (20-300 kg)
     * - validateGender(): Ensures gender selection is made
     */
    private boolean validateInput() {
        String name = nameEdit.getText().toString().trim();
        String age = ageEdit.getText().toString().trim();
        String height = heightEdit.getText().toString().trim();
        String weight = weightEdit.getText().toString().trim();
        int genderId = genderEdit.getCheckedRadioButtonId();

        // Perform individual validations
        ValidationResult nameResult = validator.validateName(name);
        ValidationResult ageResult = validator.validateAge(age);
        ValidationResult heightResult = validator.validateHeight(height);
        ValidationResult weightResult = validator.validateWeight(weight);
        ValidationResult genderResult = validator.validateGender(genderId);

        // Aggregate validation results with short-circuit evaluation
        return showValidationError(nameResult) &&
                showValidationError(ageResult) &&
                showValidationError(heightResult) &&
                showValidationError(weightResult) &&
                showValidationError(genderResult);
    }

    /**
     * Displays validation errors to user via Toast notifications.
     * Implements feedback pattern for user input validation.
     *
     * @param result ValidationResult containing validity status and error message
     * @return true if validation passed, false if failed
     *
     * Error Display Logic:
     * - Invalid result: Shows error message via Toast
     * - Valid result: Returns true without user notification
     */
    private boolean showValidationError(ValidationResult result) {
        if (!result.isValid()) {
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Loads existing profile data into form fields.
     * Implements data hydration pattern for edit forms.
     *
     * Data Sources:
     * - Name and age from PrefsHelper
     * - Height and weight from PrefsHelper
     * - Gender selection from PrefsHelper
     *
     * Gender Mapping Logic:
     * - "male" → R.id.rbMale
     * - "female" → R.id.rbFemale
     * - default → R.id.rbOther
     */
    private void loadProfiledata() {
        // Load text field values
        nameEdit.setText(prefsHelper.getUserName());
        ageEdit.setText(String.valueOf(prefsHelper.getUserAge()));
        heightEdit.setText(String.valueOf(prefsHelper.getUserHeight()));
        weightEdit.setText(String.valueOf(prefsHelper.getUserWeight()));

        // Set gender radio button based on stored value
        switch (prefsHelper.getUserGender()) {
            case "male":
                genderEdit.check(R.id.rbMale);
                break;
            case "female":
                genderEdit.check(R.id.rbFemale);
                break;
            default:
                genderEdit.check(R.id.rbOther);
        }
    }

    /**
     * Saves validated profile data to persistent storage.
     * Implements dual persistence pattern: local (SharedPreferences) + cloud (Firestore)
     *
     * Data Processing Steps:
     * 1. Parse numeric inputs with error handling for NumberFormatException
     * 2. Determine gender selection from radio buttons
     * 3. Persist locally via PrefsHelper
     * 4. Sync to cloud via FirebaseSyncService
     * 5. Provide user feedback and close activity
     *
     * Error Handling:
     * - Catches NumberFormatException for invalid numeric inputs
     * - Displays user-friendly error message
     * - Maintains activity state for correction
     */
    private void saveProfile() {
        try {
            // Parse input values
            String name = nameEdit.getText().toString().trim();
            int age = Integer.parseInt(ageEdit.getText().toString());
            int height = Integer.parseInt(heightEdit.getText().toString());
            float weight = Float.parseFloat(weightEdit.getText().toString());

            // Determine gender selection
            String gender = "Male"; // Default value
            if (genderEdit.getCheckedRadioButtonId() == R.id.rbFemale) {
                gender = "Female";
            } else if (genderEdit.getCheckedRadioButtonId() == R.id.rbOther) {
                gender = "Other";
            }

            // Persist data locally
            prefsHelper.saveUserData(height, weight, gender);
            prefsHelper.saveAgeandName(name, age);

            // Sync to cloud
            FirebaseSyncService syncService = new FirebaseSyncService(this);
            syncService.saveUserProfile(name, age, height, weight, gender);

            // Provide feedback and return to previous screen
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            // Handle invalid numeric input
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}