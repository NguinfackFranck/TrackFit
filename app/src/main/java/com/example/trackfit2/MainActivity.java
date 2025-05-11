package com.example.trackfit2;
/**
 * MainActivity.java
 * Initial activity for TrackFit2 application that serves as the entry point
 * and determines whether to show the welcome screen or navigate directly
 * to the dashboard based on user setup status.
 * Author: Nguinfack Franck-styve
 * Key Functionality:
 * - Checks if user setup is complete using PrefsHelper
 * - Displays welcome screen for new users
 * - Handles navigation to registration or dashboard
 * - Implements edge-to-edge display for modern Android UI
 * Dependencies:
 * - Android SDK 35
 * - androidx libraries (core, appcompat, activity)
 * - Project-specific PrefsHelper utility class
 */

 import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/**
 * Main entry point activity that handles initial application routing.
 * Implements conditional navigation based on user's setup status.
 */
public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private PrefsHelper prefsHelper;

    /**
     * Initializes activity and determines navigation path.
     *
     * @param savedInstanceState Bundle containing previous state (if available)
     * Logic Flow:
     * 1. Enables edge-to-edge display
     * 2. Checks setup completion status
     * 3. Either navigates to dashboard or shows welcome screen
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        prefsHelper = new PrefsHelper(this);
        // Check if user has completed initial setup
        if (prefsHelper.isSetupComplete()) {
            navigateToDashboard();
            return;
        }
        setupWelcomeScreen();
    }
    /**
     * Configures the welcome screen UI components and event handlers.
     * Implementation Details:
     * - Configures button click listener for navigation
     * - Uses ViewCompat for backward compatibility
     */
    private void setupWelcomeScreen() {
        startButton = findViewById(R.id.getStartedButton);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Navigation to Register activity for start button
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();// Remove this activity from back stack
        });
    }/**
     * Transitions user to the main dashboard activity.
     * Navigation Behavior:
     * - Creates explicit intent for DashboardActivity
     * - Finishes current activity to prevent back navigation
     */

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();// Prevent returning to this launch screen
    }
}