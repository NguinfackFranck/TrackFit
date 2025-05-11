/**
 * PrefsHelper.java
 *
 * Manages application preferences using Android's SharedPreferences API.
 * Implements persistent storage for user profile and application state.
 *
 * Author: Nguinfack Franck-styve
 *
 * Key Responsibilities:
 * - Stores and retrieves user profile data
 * - Manages application setup state
 * - Provides data clearing functionality
 * - Encapsulates SharedPreferences access
 *
 * Design Patterns:
 * - Repository Pattern
 * - Singleton Pattern (via SharedPreferences)
 *
 * Security Considerations:
 * - Uses MODE_PRIVATE for preferences
 * - Provides complete data clearing
 * - No sensitive data logging
 *
 * Data Structure:
 * - All values stored as key-value pairs
 * - Default values provided for all retrievals
 */
 package com.example.trackfit2;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    // Preference file name
    private static final String PREFS_NAME = "UserPrefs";
    // User profile keys
    private static final String KEY_NAME = "user_name";
    private static final String KEY_AGE = "user_age";

    private static final String KEY_HEIGHT = "user_height";
    private static final String KEY_WEIGHT = "user_weight";
    private static final String KEY_GENDER = "user_gender";
    // Application state keys
    private static final String KEY_SETUP_COMPLETE = "setup_complete";
    // SharedPreferences instance
    private final SharedPreferences prefs;

    /**
     * Constructs a new PrefsHelper instance.
     *
     * @param context Application context
     *                <p>
     *                Initialization:
     *                - Creates SharedPreferences instance
     *                - Uses private mode for security
     */

    public PrefsHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    /**
     * Saves basic user profile information.
     *
     * @param name User's name
     * @param age User's age
     *
     * Data Characteristics:
     * - Name stored as String
     * - Age stored as integer
     * - Uses atomic apply() for persistence
     */

    public void saveAgeandName(String name, int age) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putInt(KEY_AGE, age)
                .apply();
    }
    /**
     * Saves additional user profile metrics.
     *
     * @param height User height in cm
     * @param weight User weight in kg
     * @param gender User gender identification
     *
     * Measurement Notes:
     * - Height: Centimeters (integer)
     * - Weight: Kilograms (float)
     * - Gender: String representation
     */
    public void saveUserData(int height, Float weight, String gender) {
        prefs.edit()
                .putInt(KEY_HEIGHT, height)
                .putFloat(KEY_WEIGHT, weight)
                .putString(KEY_GENDER, gender)
                .apply();
    }

    /**
     * Retrieves user's name.
     *
     * @return String containing user's name or empty string if not set
     */
    public String getUserName() {
        return prefs.getString(KEY_NAME, "");
    }
    /**
     * Retrieves user's age.
     *
     * @return Integer representing user's age or 0 if not set
     */
    public int getUserAge() {
        return prefs.getInt(KEY_AGE, 0);
    }
    /**
     * Retrieves user's height.
     *
     * @return Height in centimeters (default: 170cm)
     */
    public int getUserHeight() {
        return prefs.getInt(KEY_HEIGHT, 170);
    }
    /**
     * Retrieves user's weight.
     *
     * @return Weight in kilograms (default: 70kg)
     */

    public float getUserWeight() {
        return prefs.getFloat(KEY_WEIGHT, 70f);
    }
    /**
     * Retrieves user's gender.
     *
     * @return Gender identification string (default: "male")
     */
    public String getUserGender() {
        return prefs.getString(KEY_GENDER, "male");
    }
    /**
     * Sets the application setup completion state.
     *
     * @param complete Boolean indicating setup status
     *
     * Usage:
     * - Set to true after initial setup
     * - Set to false during logout
     */
    public void setSetupComplete(boolean complete) {
        prefs.edit().putBoolean(KEY_SETUP_COMPLETE, complete).apply();
    }
    /**
     * Checks if application setup is complete.
     *
     * @return Boolean indicating setup status (default: false)
     */
    public boolean isSetupComplete() {
        return prefs.getBoolean(KEY_SETUP_COMPLETE, false);
    }
    /**
     * Clears all user data from preferences.
     *
     * Security Implications:
     * - Removes all key-value pairs
     * - Resets application to initial state
     * - Should be called during logout
     */
    public void clearUserData() {
        prefs.edit().clear().apply();
    }
}
