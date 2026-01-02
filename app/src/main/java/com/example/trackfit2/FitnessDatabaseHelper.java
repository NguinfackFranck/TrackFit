/**
 * FitnessDatabaseHelper.java
 *
 * SQLite database helper class for managing fitness tracking data and goals.
 * Implements database creation, version management, and CRUD operations.
 *
 * Author: Nguinfack Franck-styve
 *
 * Database Schema:
 * - fitness_data: Stores daily fitness metrics (date, steps, calories, active time)
 * - goals: Stores user goals (steps, calories, active time)
 *
 * Key Features:
 * - Automatic daily data tracking with date-based records
 * - Goal management with default values
 * - Data reset functionality
 * - History retrieval
 */
package com.example.trackfit2;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FitnessDatabaseHelper extends SQLiteOpenHelper {
    // Database configuration
    private static final String DATABASE_NAME = "FitnessTracker.db";
    private static final int DATABASE_VERSION = 1;
    // Table names
    private static final String TABLE_NAME = "fitness_data";
    private static final String TABLE_GOALS = "goals";
    // Column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";  // Store as YYYY-MM-DD
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_ACTIVE_TIME = "active_time";
    public static final String COLUMN_GOAL_STEPS = "goal_steps";
    public static final String COLUMN_GOAL_CALORIES = "goal_calories";
    public static final String COLUMN_GOAL_ACTIVE_TIME = "goal_active_time";
    /**
     * Constructs a new FitnessDatabaseHelper instance.
     *
     * @param context Application context for database creation
     */
    public FitnessDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * Creates database tables and initializes default values.
     *
     * @param sqLiteDatabase The database to initialize
     *
     * Schema Details:
     * - fitness_data: Tracks daily metrics with date as unique key
     * - goals: Stores single set of user goals
     * - Sets default goal values on creation
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable= "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT UNIQUE, " +
                COLUMN_STEPS + " INTEGER, " +
                COLUMN_CALORIES + " INTEGER, " +
                COLUMN_ACTIVE_TIME + " INTEGER)";
        String createGoalsTable = "CREATE TABLE " + TABLE_GOALS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_GOAL_STEPS + " INTEGER, " +
                COLUMN_GOAL_CALORIES + " INTEGER, " +
                COLUMN_GOAL_ACTIVE_TIME + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);
        sqLiteDatabase.execSQL(createGoalsTable);
        ContentValues defaultGoals = new ContentValues();
        defaultGoals.put(COLUMN_GOAL_STEPS, 10000);
        defaultGoals.put(COLUMN_GOAL_CALORIES, 500);
        defaultGoals.put(COLUMN_GOAL_ACTIVE_TIME, 30);
        sqLiteDatabase.insert(TABLE_GOALS, null, defaultGoals);
    }
    /**
     * Handles database upgrades by recreating tables.
     *
     * @param db The database to upgrade
     * @param oldVersion Previous database version
     * @param newVersion New database version
     *
     * Migration Strategy:
     * - Drops and recreates tables for version changes
     * - Preserves no data during upgrade (simple implementation)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
            onCreate(db);
        }
    }
    /**
     * Saves daily fitness data, replacing existing entry for the same date.
     *
     * @param steps Daily step count
     * @param calories Daily calories burned
     * @param activeTime Daily active minutes
     *
     * Data Integrity:
     * - Uses current date as key
     * - Replaces existing entries for same date
     * - Automatically closes database connection
     */
    public void saveDailyData( int steps, int calories, int activeTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

       String currentDate= new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date());
       values.put(COLUMN_DATE,currentDate);
        values.put(COLUMN_STEPS, steps);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_ACTIVE_TIME, activeTime);

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }/**
     * Saves daily fitness data for a specific date.
     * Used when restoring data from Firestore.
     */
    public void saveDailyData(String date, int steps, int calories, int activeTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, date);
        values.put(COLUMN_STEPS, steps);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_ACTIVE_TIME, activeTime);

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    /**
     * Updates user fitness goals.
     *
     * @param stepsGoal Daily step goal
     * @param caloriesGoal Daily calorie goal
     * @param activeTimeGoal Daily active minutes goal
     *
     * Implementation Note:
     * - Always updates the single goals record with ID=1
     */
    public void saveGoals(int stepsGoal, int caloriesGoal, int activeTimeGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_STEPS, stepsGoal);
        values.put(COLUMN_GOAL_CALORIES, caloriesGoal);
        values.put(COLUMN_GOAL_ACTIVE_TIME, activeTimeGoal);

        db.update(TABLE_GOALS, values, COLUMN_ID + " = 1", null);
        db.close();
    }
    /**
     * Retrieves today's fitness data.
     *
     * @return Cursor containing today's data or null
     *
     * Usage Note:
     * - Caller is responsible for closing the cursor
     * - Returns null if no data exists for today
     */
    public Cursor getTodayData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = ?",
                new String[]{today});
    }
    /**
     * Retrieves current step goal.
     *
     * @return Current daily step goal
     */
    public int getStepsGoal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GOAL_STEPS + " FROM " + TABLE_GOALS + " WHERE " + COLUMN_ID + " = ?", new String[]{"1"});
        int stepsGoal = 0;
        if (cursor.moveToFirst()) {
            stepsGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_STEPS));
        }
        cursor.close();
        db.close();
        return stepsGoal;}
    /**
     * Retrieves current calorie goal.
     *
     * @return Current daily calorie goal
     */
    public int getCaloricGoal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GOAL_CALORIES + " FROM " + TABLE_GOALS + " WHERE " + COLUMN_ID + " = ?", new String[]{"1"});
        int caloricGoal = 0;
        if (cursor.moveToFirst()) {
            caloricGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_CALORIES));
        }
        cursor.close();
        db.close();
        return caloricGoal;
    }
    /**
     * Retrieves current active time goal.
     *
     * @return Current daily active minutes goal
     */
    public int getActiveTimeGoal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GOAL_ACTIVE_TIME + " FROM " + TABLE_GOALS + " WHERE " + COLUMN_ID + " = ?", new String[]{"1"});
        int activeTimeGoal = 0;
        if (cursor.moveToFirst()) {
            activeTimeGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_ACTIVE_TIME));
        }
        cursor.close();
        db.close();
        return activeTimeGoal;
    }
    /**
     * Retrieves all goals as a cursor.
     *
     * @return Cursor containing goals data
     *
     * Usage Note:
     * - Caller is responsible for closing the cursor
     */
    public Cursor getGoals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GOALS + " WHERE " + COLUMN_ID + " = ?", new String[]{"1"});
    }
    /**
     * Retrieves historical fitness data in descending date order.
     *
     * @return Cursor containing all historical data
     *
     * Usage Note:
     * - Caller is responsible for closing the cursor
     */
    public Cursor getHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC", null);
    }
    /**
     * Resets today's fitness data to zero values.
     *
     * Implementation:
     * - Creates or updates record for current date
     * - Sets all metrics to zero
     */
    public void resetDailyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(COLUMN_STEPS, 0);
        values.put(COLUMN_CALORIES, 0);
        values.put(COLUMN_ACTIVE_TIME, 0);

        db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{today});
        db.close();
    }
    /**
     * Clears all fitness data and resets to default goals.
     *
     * Data Management:
     * - Deletes all records from both tables
     * - Reinitializes with default goals
     * - Wrapped in try-finally for resource safety
     */
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete(TABLE_NAME, null, null);
            db.delete(TABLE_GOALS, null, null);

// Initialize with default goals
            ContentValues defaultGoals = new ContentValues();
            defaultGoals.put(COLUMN_GOAL_STEPS, 10000);
            defaultGoals.put(COLUMN_GOAL_CALORIES, 500);
            defaultGoals.put(COLUMN_GOAL_ACTIVE_TIME, 30);
            db.insert(TABLE_GOALS, null, defaultGoals);
        } finally {
            db.close();
        }
    }
}


