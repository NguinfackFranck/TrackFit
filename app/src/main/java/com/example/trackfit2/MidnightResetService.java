/**MidnightResetService.java
 * x
 * The service uses a Timer to schedule automatic resets, ensuring daily tracking consistency.
 * Author: Nguinfack Franck-styve
 */
package com.example.trackfit2;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MidnightResetService extends Service {
    private Timer timer;
    private FitnessDatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new FitnessDatabaseHelper(this);
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    /**
     * Since this service does not require binding, this method returns null.
     *
     * @param intent The intent that binds the service.
     * @return Always returns null as binding is not needed.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * Starts a timer to schedule the reset task exactly at midnight.
     */
    private void startTimer() {
        Calendar calendar = Calendar.getInstance();
        // Set the next execution time to midnight.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);// Move to the next day.
        timer = new Timer();
        // Schedule a repeating task that resets daily data at midnight.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                resetData();// perform daily reset
                // Schedule next midnight
                startTimer();
            }
        }, calendar.getTime(), 24 * 60 * 60 * 1000);
    }

    private void resetData() {
        dbHelper.resetDailyData();// Clear daily fitness tracking data.
        Log.d("MidnightReset", "Daily data reset at midnight");
    }
    /**
     * Cleans up resources when the service is destroyed.
     * Cancels the timer and closes the database connection to prevent memory leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        dbHelper.close();// Close database connections.
    }
}
