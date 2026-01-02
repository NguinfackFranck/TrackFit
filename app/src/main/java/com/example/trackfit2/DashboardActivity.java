/**
 * DashboardActivity.java
 *
 * Main dashboard activity that displays user fitness progress and provides navigation
 * to other application features. Implements real-time progress tracking and visualization.
 *
 * Author: Nguinfack Franck-styve
 *
 * Key Responsibilities:
 * - Displays current fitness metrics (steps, calories, active time)
 * - Shows progress toward daily goals
 * - Provides navigation to other app features
 * - Maintains real-time data synchronization
 * - Handles user profile display
 *
 * Architectural Components:
 * - View Layer: activity_dashboard.xml
 * - Business Logic: Progress calculation and display
 * - Data Access: FitnessDatabaseHelper, PrefsHelper
 *
 * Key Algorithms:
 * - Progress percentage calculation
 * - Data formatting for display
 * - Safe database access
 */
package com.example.trackfit2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    private ImageView profileImage;
    private ProgressBar stepsProgressBar, caloriesProgressBar, activeTimeProgressBar;

    private FitnessDatabaseHelper dbHelper;
    private PrefsHelper prefsHelper;
    /**
     * Initializes the activity and sets up core components.
     *
     * Lifecycle Methods:
     * - Initializes database helpers
     * - Sets up UI components
     * - Starts background services
     * - Configures navigation
     */

    private TextView stepsProgressText, caloriesProgressText, activeTimeProgressText;
    private TextView stepsPercentageText, caloriesPercentageText, activeTimePercentageText;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new FitnessDatabaseHelper(this);
        prefsHelper = new PrefsHelper(this);

        // Initialize views
        initializeViews();
        setupButtonListeners();

        // Start the midnight reset service
        startService(new Intent(this, MidnightResetService.class));
    }
    /**
     * Refreshes dashboard data when activity resumes.
     *
     * Lifecycle Consideration:
     * - Ensures data is current after returning from other activities
     * - Handles background service updates
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    /**
     * Initializes and configures all view components.
     *
     * UI Components:
     * - Progress bars for each metric
     * - Text views for numeric displays
     * - Welcome message with user name
     * - Navigation buttons
     */
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        stepsProgressText = findViewById(R.id.stepsProgressText);
        caloriesProgressText = findViewById(R.id.caloriesProgressText);
        activeTimeProgressText = findViewById(R.id.activeTimeProgressText);
        stepsPercentageText = findViewById(R.id.stepsPercentageText);
        caloriesPercentageText = findViewById(R.id.caloriesPercentageText);
        activeTimePercentageText = findViewById(R.id.activeTimePercentageText);
        // Progress bars with 100% maximum
        stepsProgressBar = findViewById(R.id.stepsProgressBar);
        caloriesProgressBar = findViewById(R.id.caloriesProgressBar);
        activeTimeProgressBar = findViewById(R.id.activeTimeProgressBar);
        stepsProgressBar.setMax(100);
        caloriesProgressBar.setMax(100);
        activeTimeProgressBar.setMax(100);
        // Profile display
        String userName = prefsHelper.getUserName();
        welcomeText.setText(userName.isEmpty() ? "Hello!" : "Hello, " + userName + "!");
        profileImage= findViewById(R.id.profileImage);
    }

    /** On click, Navigates
     * Navigation Paths:
     * - Profile Image: EditProfileActivity
     * - History Button: HistoryActivity
     * - Edit Profile: EditProfileActivity
     * - Logout: LogoutActivity
     * - Save Data: SaveDailyDataActivity
     */
    private void setupButtonListeners() {
        Button historyButton = findViewById(R.id.historyButton);
        Button editProfileButton = findViewById(R.id.editProfileButton);
        Button saveDataButton = findViewById(R.id.saveDataButton);
        Button setDailyGoals= findViewById(R.id.setGoals);
        profileImage.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        historyButton.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });


        setDailyGoals.setOnClickListener(view -> {
            startActivity(new Intent(this, SetGoalsActivity.class));
        });

        saveDataButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SaveDailyDataActivity.class));
        });
    }
    /**
     * Updates all dashboard metrics with current data.
     *
     * Data Flow:
     * 1. Retrieves today's activity data
     * 2. Fetches current goals
     * 3. Calculates progress percentages
     * 4. Updates all UI components
     */
    private void updateDashboard() {

        int steps = 0, calories = 0, activeTime = 0;
        int stepsGoal = 10000, caloriesGoal = 500, activeTimeGoal = 30;


        try (Cursor todayData = dbHelper.getTodayData()) {
            if (todayData != null && todayData.moveToFirst()) {
                int stepsIndex = todayData.getColumnIndex(FitnessDatabaseHelper.COLUMN_STEPS);
                int caloriesIndex = todayData.getColumnIndex(FitnessDatabaseHelper.COLUMN_CALORIES);
                int activeTimeIndex = todayData.getColumnIndex(FitnessDatabaseHelper.COLUMN_ACTIVE_TIME);

                steps = stepsIndex != -1 ? Math.max(0, todayData.getInt(stepsIndex)) : 0;
                calories = caloriesIndex != -1 ? Math.max(0, todayData.getInt(caloriesIndex)) : 0;
                activeTime = activeTimeIndex != -1 ? Math.max(0, todayData.getInt(activeTimeIndex)) : 0;
            }
        } catch (Exception e) {

        }


        try (Cursor goals = dbHelper.getGoals()) {
            if (goals != null && goals.moveToFirst()) {
                int stepsGoalIndex = goals.getColumnIndex(FitnessDatabaseHelper.COLUMN_GOAL_STEPS);
                int caloriesGoalIndex = goals.getColumnIndex(FitnessDatabaseHelper.COLUMN_GOAL_CALORIES);
                int activeTimeGoalIndex = goals.getColumnIndex(FitnessDatabaseHelper.COLUMN_GOAL_ACTIVE_TIME);

                stepsGoal = stepsGoalIndex != -1 ? Math.max(1, goals.getInt(stepsGoalIndex)) : 10000;
                caloriesGoal = caloriesGoalIndex != -1 ? Math.max(1, goals.getInt(caloriesGoalIndex)) : 500;
                activeTimeGoal = activeTimeGoalIndex != -1 ? Math.max(1, goals.getInt(activeTimeGoalIndex)) : 30;
            }
        } catch (Exception e) {

        }


        updateProgress(steps, stepsGoal, stepsProgressBar, stepsProgressText, stepsPercentageText, "steps");
        updateProgress(calories, caloriesGoal, caloriesProgressBar, caloriesProgressText, caloriesPercentageText, "calories");
        updateProgress(activeTime, activeTimeGoal, activeTimeProgressBar, activeTimeProgressText, activeTimePercentageText, "minutes");
    }
    /**
     * Updates progress display for a specific metric.
     *
     * @param current Current metric value
     * @param goal Target goal value
     * @param progressBar Progress bar to update
     * @param progressText Text view for absolute values
     * @param percentageText Text view for percentage
     * @param unit Measurement unit for display
     */
    private void updateProgress(int current, int goal,ProgressBar progressBar, TextView progressText, TextView percentageText, String unit) {

        current = Math.max(0, current);
        goal = Math.max(1, goal); //

        int percentage = goal > 0 ? (int) ((current * 100f) / goal) : 0;
        percentage = Math.min(100, Math.max(0, percentage));
        progressBar.setProgress(percentage);

        progressText.setText(String.format(Locale.getDefault(), "%s/%s %s",
                formatNumber(current),
                formatNumber(goal),
                unit));
        percentageText.setText(String.format(Locale.getDefault(), "%d%%", percentage));
    }
    /**
     * Formats numbers for display with thousands separators.
     *
     * @param number Value to format
     * @return Formatted string with locale-specific separators
     */
    private String formatNumber(int number) {
        return String.format(Locale.getDefault(), "%,d", Math.max(0, number));
    }
    /**
     * Cleans up resources when activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}