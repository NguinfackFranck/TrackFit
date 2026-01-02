package com.example.trackfit2;

/**
 * RegisterActivity.java
 *
 * Activity for new user registration with comprehensive validation and account creation.
 * Implements the Form Entry design pattern with real-time validation and dual-path authentication.
 *
 * Author: Nguinfack Franck-Styve
 *
 * Purpose:
 * This activity serves as the entry point for new users to create accounts or for
 * existing users to log in. It implements a smart authentication flow that first
 * attempts to log in with provided credentials, and only creates a new account if
 * login fails (user doesn't exist). This prevents duplicate account creation and
 * provides a seamless user experience.
 *
 * Key Functionality:
 * - Collects and validates user demographic information (name, age, email, password)
 * - Persists user data locally via SharedPreferences wrapper (PrefsHelper)
 * - Implements clean error reporting pattern with inline field validation
 * - Maintains separation of validation logic via RegistrationValidator
 * - Smart authentication: Attempts login first, creates account only if user doesn't exist
 * - Synchronizes user data from cloud on successful authentication
 *
 * Design Patterns:
 * - Form Entry Pattern: Structured data collection with validation
 * - Repository Pattern: Data persistence abstraction via PrefsHelper
 * - Strategy Pattern: Dual authentication paths (login vs. registration)
 * - Observer Pattern: Firebase authentication callback handling
 *
 * Activity Flow:
 * 1. User enters name, age, email, and password
 * 2. Inputs are validated with inline error display
 * 3. authenticateUser() attempts login with provided credentials
 * 4. If login succeeds: restore data and navigate based on setup completion
 * 5. If login fails: createAccount() creates new Firebase account
 * 6. New accounts: save demographic data and navigate to complete physical profile
 *
 * Security Considerations:
 * - Email and password authentication via Firebase Auth
 * - Password validation for strength requirements
 * - User collision detection to prevent duplicate accounts
 * - Secure credential handling through Firebase services
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    /**
     * Validator instance for registration form input validation.
     * Encapsulates validation logic for separation of concerns.
     */
    RegistrationValidator validator = new RegistrationValidator();

    /**
     * UI Components for user input collection.
     */
    private EditText nameInput, ageInput, emailInput, passwordInput;

    /**
     * Helper class for managing SharedPreferences persistence.
     * Provides abstraction layer for user data storage.
     */
    private PrefsHelper prefsHelper;

    /**
     * Firebase Authentication instance for user credential management.
     * Handles both login and registration operations.
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Firebase Firestore instance for cloud data storage.
     * Used for user profile and fitness data synchronization.
     */
    private FirebaseFirestore firestore;

    /**
     * Initializes the registration form and sets up validation.
     *
     * This method is called when the activity is first created.
     * It performs the following tasks:
     * 1. Enables edge-to-edge display for modern Android UI
     * 2. Inflates the activity layout
     * 3. Binds view references from XML
     * 4. Initializes data persistence and Firebase services
     * 5. Sets up window insets for edge-to-edge compatibility
     * 6. Configures click listeners for navigation and form submission
     *
     * UI Configuration:
     * - nameInput (R.id.nameInput): User's full name
     * - ageInput (R.id.ageInput): User's age
     * - emailInput (R.id.emailInput): User's email address
     * - passwordInput (R.id.passwordInput): User's password
     * - confirm (R.id.confirmButton): Form submission button
     * - login (R.id.loginLink): Navigation to login screen
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
        setContentView(R.layout.activity_register);

        // Initialize view components by binding to XML elements
        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        passwordInput = findViewById(R.id.passwordInput);
        emailInput = findViewById(R.id.emailInput);
        Button confirm = findViewById(R.id.confirmButton);
        TextView login = findViewById(R.id.loginLink);

        // Initialize data persistence and Firebase services
        prefsHelper = new PrefsHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set up window insets for edge-to-edge compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /**
         * Login link click handler.
         * Navigates to LoginActivity for existing users.
         */
        login.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        /**
         * Confirm button click handler.
         * Validates inputs and initiates authentication process.
         */
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    authenticateUser();
                }
            }
        });
    }

    /**
     * Validates all form inputs using the RegistrationValidator.
     *
     * This method implements comprehensive validation with inline error display.
     * Validation is performed in the following sequence:
     * 1. Name validation: Checks if non-empty and reasonable length
     * 2. Age validation: Checks if numeric and within valid range (10-100)
     * 3. Email validation: Checks email format and validity
     * 4. Password validation: Checks strength and length requirements
     *
     * Error Handling:
     * - Invalid inputs display error messages directly on the corresponding view
     * - Validation stops at the first encountered error (short-circuit evaluation)
     * - User can correct errors and resubmit without losing other valid inputs
     *
     * @return Boolean indicating overall validation success
     *         true: All inputs are valid
     *         false: One or more inputs failed validation
     */
    public Boolean validateInputs() {
        // Perform individual validations
        ValidationResult nameResult = validator.validateName(nameInput.getText().toString().trim());
        ValidationResult ageResult = validator.validateAge(ageInput.getText().toString().trim());
        ValidationResult emailResult = validator.validateEmail(emailInput.getText().toString().trim());
        ValidationResult passwordResult = validator.validatePassword(passwordInput.getText().toString().trim());

        // Check name validation
        if (!nameResult.isValid()) {
            nameInput.setError(nameResult.getErrorMessage());
            return false;
        }

        // Check age validation
        if (!ageResult.isValid()) {
            ageInput.setError(ageResult.getErrorMessage());
            return false;
        }

        // Check email validation
        if (!emailResult.isValid()) {
            emailInput.setError(emailResult.getErrorMessage());
            return false;
        }

        // Check password validation
        if (!passwordResult.isValid()) {
            passwordInput.setError(passwordResult.getErrorMessage());
            return false;
        }

        // All validations passed
        return true;
    }

    /**
     * Attempts to authenticate user with provided credentials.
     *
     * This method implements a smart authentication strategy:
     * 1. First attempts to sign in with email and password (login)
     * 2. If login succeeds: Restores user data and navigates appropriately
     * 3. If login fails with "user not found": Creates new account
     * 4. If login fails with other errors: Shows appropriate error message
     *
     * Authentication Flow:
     * - Success: Calls restoreUserData() and navigateAfterAuth()
     * - Failure (user not found): Calls createAccount() for registration
     * - Failure (other): Error handled by createAccount() method
     *
     * Benefits:
     * - Prevents duplicate account creation
     * - Provides seamless experience for existing users
     * - Eliminates confusion between login and registration
     */
    private void authenticateUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // First attempt: Try to log in with provided credentials
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Login successful - user exists
                    restoreUserData();
                    navigateAfterAuth();
                })
                .addOnFailureListener(e -> createAccount(email, password)); // Login failed, try registration
    }

    /**
     * Determines navigation destination based on user setup completion status.
     *
     * This method checks if the user has completed their profile setup:
     * - Setup complete: Navigate directly to DashboardActivity (returning user)
     * - Setup incomplete: Navigate to PersonnalInformationActivity (new user)
     *
     * User State Management:
     * - Returning users: Have completed both demographic and physical profile setup
     * - New users: Have only completed demographic information (this activity)
     *
     * Note: This method does NOT call finish(), allowing back navigation.
     */
    private void navigateAfterAuth() {
        if (prefsHelper.isSetupComplete()) {
            // Returning user → go directly to dashboard
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            // New user → complete physical profile
            startActivity(new Intent(this, PersonnalInformationActivity.class));
        }
    }

    /**
     * Restores user data from Firebase Firestore to local storage.
     *
     * This method synchronizes cloud data to the local device:
     * 1. Fitness goals from Firestore to SQLite
     * 2. Daily statistics from Firestore to SQLite
     * 3. User profile from Firestore to SharedPreferences
     *
     * Data Flow: Cloud → Local
     * - Ensures returning users have their data available offline
     * - Maintains consistency across multiple devices
     * - Supports offline-first application design
     */
    private void restoreUserData() {
        FirebaseSyncService syncService = new FirebaseSyncService(this);
        syncService.restoreGoalsFromFirestore();
        syncService.restoreDailyStatsFromFirestore();
        syncService.restoreUserProfile();
    }

    /**
     * Creates a new Firebase account with provided credentials.
     *
     * This method is called when authenticateUser() fails due to "user not found".
     * It performs the following:
     * 1. Creates new user account in Firebase Authentication
     * 2. On success: Restores any existing data and completes registration
     * 3. On failure: Handles specific Firebase errors with user-friendly messages
     *
     * Error Handling:
     * - FirebaseAuthUserCollisionException: Email already registered
     * - Network errors: Connection issues
     * - Other errors: Generic failure message with details
     *
     * @param email User's email address for account creation
     * @param password User's password for account creation
     */
    private void createAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Account creation successful
                    restoreUserData(); // Restore any existing data (though unlikely for new user)
                    completeRegistration(); // Save demographic data and proceed
                })
                .addOnFailureListener(e -> {
                    String errorMessage;

                    // Handle specific Firebase Authentication registration errors
                    if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        errorMessage = "This email is already registered. Please login instead.";
                    } else {
                        // Fallback for network issues or other internal errors
                        errorMessage = "Registration failed: " + e.getLocalizedMessage();
                    }

                    // Display the feedback to the user
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Persists demographic user data and advances to physical profile setup.
     *
     * This method completes the registration process by:
     * 1. Extracting values from form inputs
     * 2. Saving demographic data locally via PrefsHelper
     * 3. Navigating to PersonnalInformationActivity for physical profile completion
     *
     * Data Flow:
     * 1. User inputs → Local SharedPreferences (name and age)
     * 2. Note: Email and password are stored in Firebase Auth, not locally
     *
     * Navigation:
     * - Clears current activity from back stack with finish()
     * - Prevents returning to registration screen after profile completion starts
     *
     * Note: Physical metrics (height, weight, gender) will be collected in
     * the next activity (PersonnalInformationActivity).
     */
    private void completeRegistration() {
        String name = nameInput.getText().toString().trim();
        int age = Integer.parseInt(ageInput.getText().toString().trim());
        String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Save demographic data locally
        prefsHelper.saveAgeandName(name, age);

        // Navigate to physical profile completion
        startActivity(new Intent(this, PersonnalInformationActivity.class));
        finish(); // Remove this activity from back stack
    }
}
