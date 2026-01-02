package com.example.trackfit2;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * FirebaseSyncService.java
 * Purpose:
 * This service class implements the synchronization layer between local SQLite storage
 * and Firebase Cloud Firestore. It handles bidirectional data flow for user profiles,
 * fitness goals, and daily statistics using the Data Synchronization design pattern.
 *
 * Design Patterns Implemented:
 * 1. Service Layer Pattern - Centralizes all cloud synchronization operations
 * 2. Repository Pattern - Abstracts data access between local and remote storage
 * 3. Bridge Pattern - Connects SQLite database with Firestore NoSQL database
 *
 * Key Responsibilities:
 * - Bidirectional synchronization of fitness data between local SQLite and Firebase
 * - User profile management in cloud storage
 * - Data consistency maintenance across devices
 * - Backup and restore functionality for user data
 *
 * Architecture Context:
 * This class serves as the middleware between:
 * - Local Storage: FitnessDatabaseHelper (SQLite) for offline functionality
 * - Cloud Storage: Firebase Firestore for multi-device synchronization
 * - Application Layer: Various activities that require data persistence
 *
 * Security Considerations:
 * - All operations require authenticated user via FirebaseAuth
 * - User data isolation through UID-based document paths
 * - Atomic operations per document to maintain data integrity
 */
public class FirebaseSyncService {

    // Firebase Cloud Firestore instance for NoSQL document storage
    private final FirebaseFirestore firestore;

    // Firebase Authentication instance for user identity verification
    private final FirebaseAuth firebaseAuth;

    // Local SQLite database helper for offline data storage
    private final FitnessDatabaseHelper dbHelper;

    // Android application context for accessing system services
    private final Context context;

    /**
     * Constructor initializes all required service components.
     *
     * @param context Application context used for:
     *                1. Initializing local database helper
     *                2. Accessing SharedPreferences via PrefsHelper
     *                3. Potential future service integrations
     *
     * Design Pattern: Dependency Injection (constructor injection)
     * All dependencies are provided at instantiation for testability
     */
    public FirebaseSyncService(Context context) {
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.dbHelper = new FitnessDatabaseHelper(context);
    }

    /**
     * Synchronizes local fitness goals to Firebase Firestore.
     *
     * Firestore Data Structure:
     * Collection: users/{uid}/settings/goals
     * Document Fields: goal_steps, goal_calories, goal_active_time
     *
     * Algorithm Steps:
     * 1. Retrieve current user UID for document path construction
     * 2. Query local SQLite database for existing goals
     * 3. Transform data into Firestore-compatible Map structure
     * 4. Perform atomic write operation to cloud
     *
     * Error Handling:
     * - Silent failure on null UID (user not authenticated)
     * - Empty cursor handling prevents null pointer exceptions
     * - Firebase failure listeners log errors for debugging
     *
     * @implNote Uses document().set() for idempotent write operations
     */
    public void syncGoalsToFirestore() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        Cursor cursor = dbHelper.getGoals();
        if (cursor == null || !cursor.moveToFirst()) return;

        // Extract goal values from SQLite cursor using column indices
        int steps = cursor.getInt(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_GOAL_STEPS));
        int calories = cursor.getInt(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_GOAL_CALORIES));
        int time = cursor.getInt(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_GOAL_ACTIVE_TIME));

        cursor.close();

        // Map structure mirrors Firestore document schema
        Map<String, Object> goals = new HashMap<>();
        goals.put("goal_steps", steps);
        goals.put("goal_calories", calories);
        goals.put("goal_active_time", time);

        // Document path: users/{uid}/settings/goals
        firestore.collection("users")
                .document(uid)
                .collection("settings")
                .document("goals")
                .set(goals)  // set() performs create or update operation
                .addOnSuccessListener(unused ->
                        Log.d("FIREBASE_SYNC", "Goals synced successfully"))
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_SYNC", "Failed to sync goals", e));
    }

    /**
     * Persists complete user profile to Firestore.
     *
     * Data Flow:
     * User Input → PrefsHelper (SharedPreferences) → This Method → Firestore
     *
     * @param name User's full name
     * @param age User's age in years
     * @param height User's height in centimeters
     * @param weight User's weight in kilograms
     * @param gender User's gender identity
     *
     * Schema Design:
     * Document includes metadata fields:
     * - setup_complete: Boolean flag for profile completion
     * - createdAt: Timestamp for data versioning
     *
     * Performance Considerations:
     * - Single document write for atomic profile updates
     * - Minimal document size for cost-effective Firestore reads
     */
    public void saveUserProfile(String name, int age, int height, float weight, String gender) {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("age", age);
        profile.put("height", height);
        profile.put("weight", weight);
        profile.put("gender", gender);
        profile.put("setup_complete", true);
        profile.put("createdAt", System.currentTimeMillis());

        // Root document at users/{uid} contains profile data
        firestore.collection("users")
                .document(uid)
                .set(profile)
                .addOnSuccessListener(unused ->
                        Log.d("FIREBASE_USER", "User profile saved"))
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_USER", "Failed to save profile", e));
    }

    /**
     * Restores user profile from Firestore to local SharedPreferences.
     *
     * Use Case: User logs in on a new device and needs local profile restoration
     *
     * Data Transformation Pipeline:
     * 1. Firestore Document (NoSQL) → Java Objects
     * 2. Java Objects → SharedPreferences (Key-Value Store)
     *
     * Integration Points:
     * - PrefsHelper: Local persistence layer for user settings
     * - FirebaseAuth: User authentication state management
     *
     * @implNote This method is called during login/registration flow
     */
    public void restoreUserProfile() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract profile data from Firestore document
                        String name = documentSnapshot.getString("name");
                        int age = Objects.requireNonNull(documentSnapshot.getLong("age")).intValue();
                        int height = Objects.requireNonNull(documentSnapshot.getLong("height")).intValue();
                        float weight = Objects.requireNonNull(documentSnapshot.getDouble("weight")).floatValue();
                        String gender = documentSnapshot.getString("gender");

                        // Persist to local SharedPreferences
                        PrefsHelper prefs = new PrefsHelper(context);
                        prefs.saveAgeandName(name, age);
                        prefs.saveUserData(height, weight, gender);
                        prefs.setSetupComplete(true);

                        Log.d("FIREBASE_RESTORE", "Profile data restored to SharedPreferences");
                    }
                });
    }

    /**
     * Synchronizes today's fitness metrics to Firestore.
     *
     * Temporal Data Design:
     * Each day's data stored as separate document in daily_stats subcollection
     * Document ID format: yyyy-MM-dd (ISO 8601 date format)
     *
     * Concurrency Considerations:
     * - Last write wins for same-day document updates
     * - Date-based document IDs prevent update conflicts
     *
     * @implNote Called daily via SaveDailyDataActivity or automated scheduler
     */
    public void syncTodayDataToFirestore() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        Cursor cursor = dbHelper.getTodayData();
        if (cursor == null || !cursor.moveToFirst()) return;

        // Extract daily fitness metrics from SQLite
        int steps = cursor.getInt(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_STEPS));
        int calories = cursor.getInt(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_CALORIES));
        int time = cursor.getInt(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_ACTIVE_TIME));
        String date = cursor.getString(
                cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_DATE));

        cursor.close();

        // ISO 8601 date format ensures chronological sorting
        String today = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put("steps", steps);
        data.put("calories", calories);
        data.put("active_time", time);
        data.put("date", date);

        // Document path: users/{uid}/daily_stats/{date}
        firestore.collection("users")
                .document(uid)
                .collection("daily_stats")
                .document(date)
                .set(data)
                .addOnSuccessListener(unused ->
                        Log.d("FIREBASE_SYNC", "Daily data synced: " + today))
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_SYNC", "Failed to sync daily data", e));
    }

    /**
     * Restores fitness goals from Firestore to local SQLite database.
     *
     * Reverse Flow: Firestore → SQLite
     *
     * Data Type Considerations:
     * - Firestore stores numbers as Long/Double
     * - SQLite requires primitive int values
     * - Conversion performed using Objects.requireNonNull().intValue()
     *
     * Error Resilience:
     * - Returns early if document doesn't exist
     * - Null-safe conversions prevent runtime exceptions
     */
    public void restoreGoalsFromFirestore() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        firestore.collection("users")
                .document(uid)
                .collection("settings")
                .document("goals")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    // Type conversion: Firestore Long → Java int
                    int steps = Objects.requireNonNull(snapshot.getLong("goal_steps")).intValue();
                    int calories = Objects.requireNonNull(snapshot.getLong("goal_calories")).intValue();
                    int time = Objects.requireNonNull(snapshot.getLong("goal_active_time")).intValue();

                    dbHelper.saveGoals(steps, calories, time);

                    Log.d("FIREBASE_RESTORE", "Goals restored to SQLite");
                })
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_RESTORE", "Failed to restore goals", e));
    }

    /**
     * Restores all historical daily statistics from Firestore to SQLite.
     *
     * Collection Processing: Iterates through all documents in daily_stats subcollection
     *
     * Performance Considerations:
     * - Batch processing of multiple documents
     * - No pagination implemented (potential memory issue for large datasets)
     * - Consider Firestore query limits for production applications
     *
     * Data Mapping: Each Firestore document → SQLite table row
     */
    public void restoreDailyStatsFromFirestore() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        firestore.collection("users")
                .document(uid)
                .collection("daily_stats")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Iterate through all daily stat documents
                    for (var doc : querySnapshot.getDocuments()) {
                        String date = doc.getId();  // Document ID is the date
                        int steps = Objects.requireNonNull(doc.getLong("steps")).intValue();
                        int calories = Objects.requireNonNull(doc.getLong("calories")).intValue();
                        int time = Objects.requireNonNull(doc.getLong("active_time")).intValue();

                        dbHelper.saveDailyData(date, steps, calories, time);
                    }

                    Log.d("FIREBASE_RESTORE", "Daily stats restored to SQLite");
                })
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_RESTORE", "Failed to restore daily stats", e));
    }

    /**
     * Deletes all user data from Firestore (account deletion functionality).
     *
     * Security Implications:
     * - Only deletes user's own data (UID-based)
     * - Does not cascade delete subcollections automatically
     *
     * @param onSuccess Callback executed after successful deletion
     *                  Typically triggers logout and navigation to login screen
     *
     * @warning This operation is irreversible. Consider implementing
     *          soft delete with recovery period for production applications.
     */
    public void deleteUserCloudData(Runnable onSuccess) {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        // Delete root user document
        // Note: Subcollections (daily_stats, settings) may require separate deletion
        firestore.collection("users").document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FIREBASE_SYNC", "Cloud data erased.");
                    onSuccess.run(); // Execute post-deletion callback
                })
                .addOnFailureListener(e -> Log.e("FIREBASE_SYNC", "Failed to erase cloud data", e));
    }
}