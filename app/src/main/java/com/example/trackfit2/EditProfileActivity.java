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
 * - Persists updated profile data
 * - Provides user feedback
 * - Handles gender selection
 *
 * Data Fields:
 * - Personal Information: name, age
 * - Physical Metrics: height, weight
 * - Demographic: gender
 *
 * Validation Rules:
 * - Name: Non-empty, valid characters
 * - Age: 10-100 years
 * - Height: 100-300 cm
 * - Weight: 20-300 kg
 * - Gender: Must be selected
 */
package com.example.trackfit2;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditProfileActivity extends AppCompatActivity {
    EditProfileValidator validator= new EditProfileValidator();
    private EditText nameEdit, ageEdit, heightEdit, weightEdit;
    private RadioGroup genderEdit;
    private PrefsHelper prefsHelper;
    private Button saveProfile;
    /**
     * Initializes the activity and sets up UI components.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     *
     * Lifecycle Methods:
     * - Configures edge-to-edge display
     * - Initializes view components
     * - Loads existing profile data
     * - Sets up click listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        initializeViews();
        loadProfiledata();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });saveProfile.setOnClickListener(new View.OnClickListener() {
            // On click, validate inputs and save profile.
            @Override
            public void onClick(View view) {
                if (validateInput()){
                    saveProfile();
                }
            }
        });
    }
/**
 * Binds view references from layout XML.
 */
 private void initializeViews() {
        nameEdit= findViewById(R.id.Name_edit);
        ageEdit= findViewById(R.id.Age_edit);
        heightEdit= findViewById(R.id.Height_edit);
        weightEdit= findViewById(R.id.Weight_edit);
        saveProfile= findViewById(R.id.btnSave);
        genderEdit= findViewById(R.id.genderGroup);
        prefsHelper= new PrefsHelper(this);
    }
    /** input Validation
     * Validates all form fields using EditProfileValidator.
     * @return true if all inputs are valid, false otherwise
     * <p>
     * Validation Flow:
     * 1. Name validation
     * 2. Age validation
     * 3. Height validation
     * 4. Weight validation
     * 5. Gender validation
     */
    private boolean validateInput() {
        String name= nameEdit.getText().toString().trim();
        String age= ageEdit.getText().toString().trim();
        String height= heightEdit.getText().toString().trim();
        String weight= weightEdit.getText().toString().trim();
        int genderId = genderEdit.getCheckedRadioButtonId();

        ValidationResult nameResult = validator.validateName(name);
        ValidationResult ageResult = validator.validateAge(age);
        ValidationResult heightResult = validator.validateHeight(height);
        ValidationResult weightResult = validator.validateWeight(weight);
        ValidationResult genderResult = validator.validateGender(genderId);



        return  showValidationError(nameResult) &&
                showValidationError(ageResult) &&
                showValidationError(heightResult) &&
                showValidationError(weightResult) &&
                showValidationError(genderResult); }
    /**
     * Displays validation errors to user.
     *
     * @param result ValidationResult to check
     * @return true if validation passed, false if failed
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
     *
     * Data Sources:
     * - Name and age from PrefsHelper
     * - Height and weight from PrefsHelper
     * - Gender selection from PrefsHelper
     */
    private void loadProfiledata() {
        nameEdit.setText(prefsHelper.getUserName());
        ageEdit.setText(String.valueOf(prefsHelper.getUserAge()));
        heightEdit.setText(String.valueOf(prefsHelper.getUserHeight()));
        weightEdit.setText(String.valueOf(prefsHelper.getUserWeight()));

        switch (prefsHelper.getUserGender()) {
            case "male":
                genderEdit.check(R.id.rbMale);
                break;
            case "female":
                genderEdit.check(R.id.rbFemale);
                break;
            default:
                genderEdit.check(R.id.rbOther);}
        /**
         * Saves validated profile data to persistent storage.
         *
         * Data Processing:
         * - Parses numeric inputs with error handling
         * - Determines gender selection
         * - Persists via PrefsHelper
         * - Provides user feedback
         */
    }private void saveProfile() {
        try {
            String name = nameEdit.getText().toString().trim();
            int age = Integer.parseInt(ageEdit.getText().toString());
            int height = Integer.parseInt(heightEdit.getText().toString());
            float weight = Float.parseFloat(weightEdit.getText().toString());

            String gender = "male";
            if (genderEdit.getCheckedRadioButtonId() == R.id.rbFemale) {
                gender = "female";
            } else if (genderEdit.getCheckedRadioButtonId() == R.id.rbOther) {
                gender = "other";
            }

            prefsHelper.saveUserData(height,weight,gender);
            prefsHelper.saveAgeandName(name,age);
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }

}}