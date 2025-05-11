package com.example.trackfit2;
/**
 * RegisterActivity.java
 * Author: Nguinfack Franck-styve
 * Handles new user registration process including input validation and profile creation.
 * Implements the Form Entry design pattern with real-time validation.
 * Key Functionality:
 * - Collects and validates user physical information
 * - Persists user data using SharedPreferences wrapper
 * - Implements clean error reporting pattern
 * - Maintains separation of validation logic
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
    private PersonalInfoValidator validator = new PersonalInfoValidator();
    // View components
private EditText heightInput,weightInput;
    // Persistence handler
private PrefsHelper prefsHelper;
private RadioGroup genderInput;
private Button submitbtn;
    /**
     * Initializes registration form and sets up validation.
     * UI Configuration:
     * - weightInput (R.id.weightInput)
     * - heightInput (R.id.heightInput)*
     * - genderInput (R.id.genderGroup)
     * - submitbtn (R.id.submitButton)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personnal_information);
        // View binding
        weightInput= findViewById(R.id.weightInput);
        heightInput= findViewById(R.id.heightInput);
        genderInput= findViewById(R.id.genderGroup);
        submitbtn= findViewById(R.id.submitButton);

        prefsHelper= new PrefsHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (validateInputData()) {
                    registerData();
                }
            }
        });
    }/**
     * Validates form inputs using separate validator component.
     *
     * @return Boolean indicating validation success
     *
     * Validation Rules:
     * - Weight: Numeric, within valid range
     * - Height: Numeric, within valid range
     * - Gender: check a button
     *
     * Error Handling:
     * - Sets error messages directly on views
     */


    private boolean validateInputData() {
        String weight= weightInput.getText().toString().trim();
        String height= heightInput.getText().toString().trim();
        int genderId = genderInput.getCheckedRadioButtonId();
        ValidationResult weightResult = validator.validateWeight(weight);
        ValidationResult heightResult = validator.validateHeight(height);
        ValidationResult genderResult = validator.validateGender(genderId);

        if (!weightResult.isValid()) {
            Toast.makeText(this, weightResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!heightResult.isValid()) {
            Toast.makeText(this, heightResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!genderResult.isValid()) {
            Toast.makeText(this, genderResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }



   return true; }
    /**
     * Persists user data and advances to next screen.
     *
     * Data Flow:
     * 1. Extracts values from form inputs
     * 2. Saves via PrefsHelper
     * 3. Logs registration event
     * 4. Mark setup complete
     * 4. Navigates to Dashboard screen
     */
    private void registerData(){

       Float weight= Float.parseFloat(weightInput.getText().toString().trim());
        int height= Integer.parseInt(heightInput.getText().toString().trim());
        int selectedGenderId = genderInput.getCheckedRadioButtonId();
        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String gender = selectedGenderRadioButton.getText().toString();
        prefsHelper.saveUserData(height,weight, gender);
        prefsHelper.setSetupComplete(true);
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}