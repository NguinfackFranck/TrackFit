package com.example.trackfit2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

/**
 * ReminderReceiver
 *
 * BroadcastReceiver responsible for handling scheduled fitness reminder notifications.
 *
 * Key Functionality:
 * - Receives scheduled alarm intents to trigger daily reminders
 * - Creates and displays notifications prompting users to log their daily fitness data
 * - Implements notification channel compatibility for Android 8.0+ (API 26+)
 * - Provides direct navigation to SaveDailyDataActivity when notification is tapped
 *
 * Notification Flow:
 * 1. Triggered by scheduled AlarmManager or WorkManager events
 * 2. Creates notification channel if needed (Android O+)
 * 3. Builds notification with title, message, and click action
 * 4. Launches SaveDailyDataActivity when user interacts with notification
 *
 * Dependencies:
 * - Requires SaveDailyDataActivity for notification click destination
 * - Uses Android NotificationCompat library for backward compatibility
 *
 * Constants:
 * - CHANNEL_ID: Unique identifier for the fitness reminder notification channel
 *
 * Usage Example:
 * Typically scheduled via AlarmManager or WorkManager:
 * AlarmManager.setInexactRepeating() to trigger this receiver daily
 *
 * @version 1.0
 */
public class ReminderReceiver extends BroadcastReceiver {

    /**
     * Notification channel ID constant for Android 8.0+ devices.
     * Must match channel creation in application setup if created elsewhere.
     */
    private static final String CHANNEL_ID = "fitness_reminder_channel";

    /**
     * Called when the BroadcastReceiver receives an Intent broadcast.
     *
     * Primary Responsibilities:
     * 1. Initializes notification system components
     * 2. Ensures notification channel exists (Android O+)
     * 3. Constructs and displays the reminder notification
     * 4. Sets up navigation to SaveDailyDataActivity on notification click
     *
     * @param context The Context in which the receiver is running
     * @param intent The Intent being received (typically from AlarmManager)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get system notification service
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel for Android 8.0+ (API 26+)
        // Required for all notifications on Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,                    // Channel ID
                    "Daily Reminders",            // User-visible channel name
                    NotificationManager.IMPORTANCE_DEFAULT);  // Notification priority

            notificationManager.createNotificationChannel(channel);
        }

        // Create intent to launch SaveDailyDataActivity when notification is clicked
        Intent activityIntent = new Intent(context, SaveDailyDataActivity.class);

        // Create PendingIntent with IMMUTABLE flag for Android 12+ (API 31+)
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,                                // Request code
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE |    // Required flag for Android 12+
                        PendingIntent.FLAG_UPDATE_CURRENT // Update existing intent if present
        );

        // Build the notification using NotificationCompat for backward compatibility
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)  // TODO: Replace with app-specific icon
                .setContentTitle("Track Your Progress!")              // Notification title
                .setContentText("Don't forget to save your steps and calories for today.")  // Notification message
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)     // Notification priority level
                .setContentIntent(pendingIntent)                      // Action on notification click
                .setAutoCancel(true);                                 // Auto-dismiss when tapped

        // Display the notification with unique ID (1)
        notificationManager.notify(1, builder.build());
    }
}