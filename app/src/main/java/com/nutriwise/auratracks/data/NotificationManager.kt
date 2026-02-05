package com.nutriwise.auratracks.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nutriwise.auratracks.models.Notification
import com.nutriwise.auratracks.models.NotificationType
import java.util.*


class NotificationManager(private val context: Context) {
    
    private val prefsHelper = SharedPreferencesHelper(context)
    private val gson = Gson()
    
    companion object {
        private const val KEY_NOTIFICATIONS = "app_notifications"
    }
    

    fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        actionData: String = ""
    ) {
        try {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                title = title,
                message = message,
                type = type,
                timestamp = Date(),
                isRead = false,
                actionData = actionData
            )
            
            val notifications = getAllNotifications().toMutableList()
            notifications.add(0, notification) // Add to top
            
            // Keep only last 50 notifications
            if (notifications.size > 50) {
                notifications.subList(50, notifications.size).clear()
            }
            
            saveNotifications(notifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun getAllNotifications(): List<Notification> {
        return try {
            val notificationsJson = prefsHelper.getString(KEY_NOTIFICATIONS, "")
            if (notificationsJson.isNotEmpty()) {
                val type = object : TypeToken<List<Notification>>() {}.type
                gson.fromJson(notificationsJson, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    

    fun getUnreadCount(): Int {
        return try {
            getAllNotifications().count { !it.isRead }
        } catch (e: Exception) {
            0
        }
    }
    

    fun markAsRead(notificationId: String) {
        try {
            val notifications = getAllNotifications().map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            saveNotifications(notifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun markAllAsRead() {
        try {
            val notifications = getAllNotifications().map { notification ->
                notification.copy(isRead = true)
            }
            saveNotifications(notifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun removeNotification(notificationId: String) {
        try {
            val notifications = getAllNotifications().filter { it.id != notificationId }
            saveNotifications(notifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun clearAllNotifications() {
        try {
            saveNotifications(emptyList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun saveNotifications(notifications: List<Notification>) {
        try {
            val notificationsJson = gson.toJson(notifications)
            prefsHelper.saveString(KEY_NOTIFICATIONS, notificationsJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun createWelcomeNotifications() {
        try {
            addNotification(
                title = "Welcome to AuraTracks! 🎉",
                message = "Start your wellness journey by adding your first habit or logging your mood.",
                type = NotificationType.WELCOME
            )
            
            addNotification(
                title = "Track Your Habits 📝",
                message = "Add daily habits like exercise, meditation, or reading to build a healthier routine.",
                type = NotificationType.HABIT,
                actionData = "view_habits"
            )
            
            addNotification(
                title = "Stay Hydrated 💧",
                message = "Don't forget to drink water! Set up hydration reminders to stay healthy.",
                type = NotificationType.HYDRATION,
                actionData = "view_hydration"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun createAchievementNotification(title: String, message: String) {
        try {
            addNotification(
                title = title,
                message = message,
                type = NotificationType.ACHIEVEMENT,
                actionData = "view_habits"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun createHabitReminderNotification(habitName: String) {
        try {
            addNotification(
                title = "Habit Reminder ⏰",
                message = "Don't forget to complete your habit: $habitName",
                type = NotificationType.REMINDER,
                actionData = "view_habits"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun createMoodReminderNotification() {
        try {
            addNotification(
                title = "How are you feeling? 😊",
                message = "Take a moment to log your mood and reflect on your day.",
                type = NotificationType.MOOD,
                actionData = "view_mood"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
