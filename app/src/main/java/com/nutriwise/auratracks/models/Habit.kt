package com.nutriwise.auratracks.models

import java.util.Date

/**
 * Data class representing a daily habit
 * @param id Unique identifier for the habit
 * @param name Name of the habit
 * @param description Optional description
 * @param category Category of the habit (Health, Fitness, Productivity, Mindfulness)
 * @param createdAt When the habit was created
 * @param isActive Whether the habit is currently active
 */
data class Habit(
    val id: String,
    val name: String,
    val description: String = "",
    val category: String = "Health",
    val createdAt: Date = Date(),
    val isActive: Boolean = true
)

/**
 * Data class representing habit completion for a specific date
 * @param habitId ID of the habit
 * @param date Date of completion
 * @param isCompleted Whether the habit was completed on this date
 * @param completedAt Timestamp when it was marked as completed
 */
data class HabitCompletion(
    val habitId: String,
    val date: String, // Format: yyyy-MM-dd
    val isCompleted: Boolean,
    val completedAt: Date? = null
)
