package com.nutriwise.auratracks.models

import java.util.Date

/**
 * Data class representing hydration data for a specific date
 * @param date Date of the hydration record
 * @param glassesDrank Number of glasses of water consumed
 * @param dailyGoal Daily goal for glasses of water
 * @param lastUpdated When this record was last updated
 */
data class HydrationData(
    val date: String, // Format: yyyy-MM-dd
    val glassesDrank: Int = 0,
    val dailyGoal: Int = 8,
    val lastUpdated: Date = Date()
)

/**
 * Data class representing hydration reminder settings
 * @param isEnabled Whether reminders are enabled
 * @param intervalMinutes Interval between reminders in minutes
 * @param startTime Start time for reminders (24-hour format)
 * @param endTime End time for reminders (24-hour format)
 * @param lastReminderTime When the last reminder was sent
 */
data class HydrationReminderSettings(
    val isEnabled: Boolean = false,
    val intervalMinutes: Int = 60,
    val startTime: String = "08:00",
    val endTime: String = "22:00",
    val lastReminderTime: Date? = null
)
