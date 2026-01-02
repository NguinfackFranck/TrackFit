package com.example.trackfit2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class LoginActivity extends AppCompatActivity {
   RegistrationValidator validator = new RegistrationValidator();
    private EditText  emailInput, passwordInput;
    private ProgressBar loginProgress;
    private Button loginButton;
    private PrefsHelper prefsHelper;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        // View binding
         emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginProgress = findViewById(R.id.loginProgressbar);
         loginButton = findViewById(R.id.loginButton);
        TextView register = findViewById(R.id.registerHint);
        prefsHelper = new PrefsHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validateInputs()){
                            authenticateUser();
                        }
                    }
                }
        );
        register.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
       
    }

    private void authenticateUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        setLoading(true);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = Objects.requireNonNull(authResult.getUser()).getUid();
                    FirebaseFirestore.getInstance().collection("users").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                boolean isComplete = false;
                                if (documentSnapshot.exists() && documentSnapshot.contains("setup_complete")) {
                                    isComplete = Boolean.TRUE.equals(documentSnapshot.getBoolean("setup_complete"));
                                }
                                prefsHelper.setSetupComplete(isComplete);
                    restoreUserData();
                    navigateAfterAuth();})
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                // Fallback if firestore check fails
                                restoreUserData();
                                navigateAfterAuth();
                            });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    // Failure: Determine why it failed
                    String errorMessage;

                    if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                        errorMessage = "No account found with this email.";
                    } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Incorrect password. Please try again.";
                    } else if (e instanceof com.google.firebase.FirebaseNetworkException) {
                        errorMessage = "Network error. Check your connection.";
                    } else {
                        errorMessage = "Authentication failed: " + e.getLocalizedMessage();
                    }

                    // Display the message to the user
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

                });
    }
    private void setLoading(boolean isLoading) {
        loginProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
    }


    private void navigateAfterAuth() {
        if (prefsHelper.isSetupComplete()) {
            // Returning user → go directly to dashboard
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            // New user → complete profile
            startActivity(new Intent(this, PersonnalInformationActivity.class));
        }

    }

    private void restoreUserData() {
        FirebaseSyncService syncService = new FirebaseSyncService(this);
        syncService.restoreUserProfile();
        syncService.restoreGoalsFromFirestore();
        syncService.restoreDailyStatsFromFirestore();
    }

    private boolean validateInputs() {
        ValidationResult emailResult= validator.validateEmail(emailInput.getText().toString().trim());
        ValidationResult passwordResult= validator.validatePassword(passwordInput.getText().toString().trim());
        if (!emailResult.isValid()) {
            emailInput.setError(emailResult.getErrorMessage());
            return false;
        }
        if (!passwordResult.isValid()) {
            passwordInput.setError(passwordResult.getErrorMessage());
            return false;
        }
        return true;
    }
}