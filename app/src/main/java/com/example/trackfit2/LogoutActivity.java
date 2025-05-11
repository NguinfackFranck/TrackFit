/**
 * LogoutActivity.java
 *
 * Handles user logout functionality with confirmation dialog and data cleanup.
 * Implements secure session termination following Android best practices.
 *
 * Author: Nguinfack Franck-styve
 *
 * Key Responsibilities:
 * - Presents logout confirmation dialog
 * - Clears user credentials and preferences
 * - Purges sensitive database content
 * - Terminates current session securely
 * - Redirects to authentication screen
 *
 * Design Patterns:
 * - Confirmation Dialog Pattern
 * - Cleanup-on-Exit Pattern
 */
package com.example.trackfit2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogoutActivity extends AppCompatActivity {
Button logoutButton;
    /**
     * Initializes the activity and sets up UI components.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     * - Initializes logout button listener
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);
        logoutButton= findViewById(R.id.buttonLogout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        })// logout on click
        ;logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmation();
            }
        });
    }
// Confirmation of logout
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.logout_yes), (dialog, which) -> performLogout())
                .setNegativeButton(getString(R.string.logout_no), (dialog, which) -> dialog.dismiss())
                .show();


    }
/**
 * Executes the logout process.
 *
 * Security Operations:
 * 1. Clears user preferences via PrefsHelper
 * 2. Marks setup as incomplete
 * 3. Clears database contents
 * 4. Closes database connection
 * 5. Redirects to MainActivity
 * 6. Finishes current activity
 *
 * Data Sanitization:
 * - All user credentials removed
 */

    private void performLogout() {
        // Clear user preferences
        PrefsHelper prefsHelper = new PrefsHelper(this);
        prefsHelper.clearUserData();
        prefsHelper.setSetupComplete(false);
        // Wipe database contents
        FitnessDatabaseHelper dbHelper = new FitnessDatabaseHelper(this);
        dbHelper.clearAllData();
        dbHelper.close();
        // Redirect to login screen
   Intent intent= new Intent(this, MainActivity.class);
   startActivity(intent);
   finish();//Prevent back navigation
    }
}