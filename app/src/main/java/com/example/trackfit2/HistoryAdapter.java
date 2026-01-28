/**
 * HistoryAdapter.java
 * <p>
 * RecyclerView adapter that displays fitness history data from SQLite database.
 * Bridges between Cursor-based database results and RecyclerView items.
 * <p>
 * Author: Nguinfack Franck-styve
 * <p>
 * Key Responsibilities:
 * - Manages Cursor lifecycle for database results
 * - Binds fitness data to ViewHolder items
 * - Formats date and numeric values for display
 * - Handles view recycling efficiently
 * <p>
 * Data Flow:
 * 1. Receives Cursor from HistoryActivity
 * 2. Creates ViewHolders for item views
 * 3. Binds database columns to view elements
 * 4. Formats raw data for user-friendly display
 */
        package com.example.trackfit2;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Cursor cursor;
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    /**
     * Updates the data source for the adapter.
     *
     * @param newCursor New Cursor containing updated history data
     *
     * Cursor Management:
     * - Closes previous Cursor if exists
     * - Updates reference to new Cursor
     * - Notifies RecyclerView of data change
     * - Handles null Cursor scenarios
     */
    public void setCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
    /**
     * Creates new ViewHolder instances when needed.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new HistoryViewHolder that holds the inflated view
     */
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var  view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }
    /**
     * Binds data from Cursor to ViewHolder at specified position.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the data in the Cursor
     *
     * Data Binding:
     * - Formats date for display
     * - Sets step count with thousands separators
     * - Sets calorie count with thousands separators
     * - Sets active time with "min" suffix
     * - Handles potential date parsing errors
     */
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // Format date
            var dateStr = cursor.getString(cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_DATE));
            try {
                Date date = dbDateFormat.parse(dateStr);
                holder.dateTextView.setText(displayDateFormat.format(date));
            } catch (ParseException e) {
                holder.dateTextView.setText(dateStr);
            }

            // Set values
            holder.stepsTextView.setText(formatNumber(cursor.getInt(
                    cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_STEPS))));
            holder.caloriesTextView.setText(formatNumber(cursor.getInt(
                    cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_CALORIES))));
            holder.activeTimeTextView.setText(formatNumber(cursor.getInt(
                    cursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_ACTIVE_TIME))) + " min");
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
    /**
     * Formats numbers with locale-appropriate thousands separators.
     *
     * @param number The numeric value to format
     * @return Formatted string with separators
     */
    private String formatNumber(int number) {
        return String.format(Locale.getDefault(), "%,d", number);
    }
    /**
     * ViewHolder class that caches view references for RecyclerView items.
     *
     * View References:
     * - Date display TextView
     * - Steps count TextView
     * - Calories burned TextView
     * - Active time TextView
     */
    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, stepsTextView, caloriesTextView, activeTimeTextView;
        /**
         * Initializes view references for a history item.
         *
         * @param itemView The root view of the history item layout
         */
        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.historyDate);
            stepsTextView = itemView.findViewById(R.id.historySteps);
            caloriesTextView = itemView.findViewById(R.id.historyCalories);
            activeTimeTextView = itemView.findViewById(R.id.historyActiveTime);
        }
    }
}
