package com.nutriwise.auratracks.models

import java.util.Date

/**
 * Data class representing a notification item
 * @param id Unique identifier for the notification
 * @param title Notification title
 * @param message Notification message content
 * @param type Type of notification (achievement, reminder, system)
 * @param timestamp When the notification was created
 * @param isRead Whether the notification has been read
 * @param actionData Optional data for notification actions
 */
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val actionData: String = ""
)

/**
 * Enum class for notification types
 */
enum class NotificationType(val displayName: String, val icon: String) {
    ACHIEVEMENT("Achievement", "🏆"),
    REMINDER("Reminder", "⏰"),
    SYSTEM("System", "ℹ️"),
    HABIT("Habit", "✅"),
    MOOD("Mood", "😊"),
    HYDRATION("Hydration", "💧"),
    WELCOME("Welcome", "👋");

    companion object {
        fun fromString(value: String): NotificationType {
            return values().find { it.name == value } ?: SYSTEM
        }
    }
}
