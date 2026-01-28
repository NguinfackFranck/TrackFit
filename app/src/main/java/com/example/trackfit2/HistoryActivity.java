/**
 * HistoryActivity.java
 *
 * Displays a chronological history of fitness data using a RecyclerView.
 * Shows past records of steps, calories, and active time in descending date order.
 *
 * Author: Nguinfack Franck-styve
 *
 * Key Features:
 * - RecyclerView with custom HistoryAdapter
 * - LinearLayoutManager for vertical scrolling
 * - Database-backed data source
 * - Proper resource cleanup
 */
 package com.example.trackfit2;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity {
    // Database helper for fitness data access
    private FitnessDatabaseHelper dbHelper;
    // Adapter for RecyclerView data binding
    private HistoryAdapter adapter;
    /**
     * Initializes the activity and sets up the history view.
     *
     * Setup Process:
     * 1. Inflates the activity_history layout
     * 2. Initializes database helper
     * 3. Configures RecyclerView with LinearLayoutManager
     * 4. Sets up HistoryAdapter
     * 5. Loads historical data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Initialize database access
        dbHelper = new FitnessDatabaseHelper(this);
        // Create adapter for RecyclerView
        adapter = new HistoryAdapter();
        // Configure RecyclerView
        RecyclerView recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        // Loads the data from database
        loadHistoryData();
    }
    /**
     * Loads historical fitness data from database.
     *
     * Data Retrieval:
     * - Gets cursor with all historical records
     * - Orders by date in descending order
     * - Passes cursor to adapter for display
     */
    private void loadHistoryData() {
        var cursor = dbHelper.getHistory();
        adapter.setCursor(cursor);
    }
    /**
     * Cleans up resources when activity is destroyed.
     *
     * Resource Management:
     * - Closes database connection
     * - Ensures no memory leaks
     * - Follows Android lifecycle best practices
     */
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}