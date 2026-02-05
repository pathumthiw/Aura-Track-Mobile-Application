package com.nutriwise.auratracks.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.nutriwise.auratracks.MainActivity
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.models.HydrationData
import java.util.concurrent.TimeUnit

/**
 * Service for managing hydration reminders
 * Uses WorkManager to schedule periodic notifications
 */
class HydrationReminderService : Service() {
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var workManager: WorkManager
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "hydration_reminders"
        private const val WORK_NAME = "hydration_reminder_work"
    }
    
    override fun onCreate() {
        super.onCreate()
        try {
            prefsHelper = SharedPreferencesHelper(this)
            workManager = WorkManager.getInstance(this)
            createNotificationChannel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val settings = prefsHelper.getHydrationSettings()
            
            if (settings.isEnabled) {
                startForegroundService()
                scheduleReminderWork(settings.intervalMinutes)
            } else {
                stopForeground(true)
                stopSelf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error, stop the service gracefully
            stopSelf()
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.default_notification_channel_description)
                }
                
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Start foreground service with notification
     */
    private fun startForegroundService() {
        try {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AuraTracks")
                .setContentText("Hydration reminders are active")
                .setSmallIcon(R.drawable.ic_water)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
            
            startForeground(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
            // If notification fails, still start foreground service with basic notification
            try {
                val basicNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("AuraTracks")
                    .setContentText("Service running")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build()
                startForeground(NOTIFICATION_ID, basicNotification)
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }
    
    /**
     * Schedule periodic reminder work
     */
    private fun scheduleReminderWork(intervalMinutes: Int) {
        try {
            // Cancel existing work
            workManager.cancelUniqueWork(WORK_NAME)
            
            // Create constraints
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            // Ensure minimum interval is 15 minutes (Android requirement)
            val safeInterval = maxOf(intervalMinutes, 15)
            
            // Create periodic work request
            val reminderWork = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                safeInterval.toLong(), TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInitialDelay(safeInterval.toLong(), TimeUnit.MINUTES)
                .build()
            
            // Enqueue the work
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderWork
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Worker class for sending hydration reminder notifications
 */
class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    
    override fun doWork(): Result {
        return try {
            prefsHelper = SharedPreferencesHelper(applicationContext)
            
            // Check if reminders are still enabled
            val settings = prefsHelper.getHydrationSettings()
            if (!settings.isEnabled) {
                return Result.success()
            }
            
            // Send notification
            sendHydrationReminder()
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    /**
     * Send hydration reminder notification
     */
    private fun sendHydrationReminder() {
        try {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Create notification channel if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "hydration_reminders",
                    "Hydration Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            
            // Create intent to open app
            val intent = Intent(applicationContext, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Get current hydration data safely
            val hydrationData = try {
                prefsHelper.getTodayHydrationData()
            } catch (e: Exception) {
                HydrationData(date = "", glassesDrank = 0, dailyGoal = 8)
            }
            
            val progressText = "${hydrationData.glassesDrank}/${hydrationData.dailyGoal} glasses today"
            
            // Build notification
            val notification = NotificationCompat.Builder(applicationContext, "hydration_reminders")
                .setContentTitle("💧 Time to hydrate!")
                .setContentText("$progressText - Time to drink water! 💧")
                .setSmallIcon(R.drawable.ic_water)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            
            // Show notification
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
