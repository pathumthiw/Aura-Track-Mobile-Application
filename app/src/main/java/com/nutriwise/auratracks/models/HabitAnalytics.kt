package com.nutriwise.auratracks.models

import java.util.Date

/**
 * Data class representing habit analytics and statistics
 */
data class HabitAnalytics(
    val habitId: String,
    val habitName: String,
    val totalCompletions: Int,
    val totalDays: Int,
    val completionRate: Float, // Percentage (0-100)
    val currentStreak: Int,
    val longestStreak: Int,
    val averagePerWeek: Float,
    val lastCompleted: Date?,
    val weeklyData: List<WeeklyData>,
    val monthlyData: List<MonthlyData>,
    val category: HabitCategory = HabitCategory.GENERAL
)

/**
 * Weekly habit completion data
 */
data class WeeklyData(
    val weekStart: Date,
    val weekEnd: Date,
    val completions: Int,
    val totalDays: Int,
    val completionRate: Float
)

/**
 * Monthly habit completion data
 */
data class MonthlyData(
    val month: String, // Format: "2024-01"
    val year: Int,
    val monthNumber: Int,
    val completions: Int,
    val totalDays: Int,
    val completionRate: Float
)

/**
 * Overall analytics summary for all habits
 */
data class OverallAnalytics(
    val totalHabits: Int,
    val activeHabits: Int,
    val totalCompletions: Int,
    val averageCompletionRate: Float,
    val bestPerformingHabit: String?,
    val needsAttentionHabits: List<String>,
    val weeklyTrend: TrendDirection,
    val monthlyTrend: TrendDirection,
    val habitAnalytics: List<HabitAnalytics>
)

/**
 * Trend direction for analytics
 */
enum class TrendDirection {
    IMPROVING, DECLINING, STABLE, NO_DATA
}

/**
 * Habit categories for better organization
 */
enum class HabitCategory(val displayName: String, val color: String) {
    HEALTH("Health & Fitness", "#4CAF50"),
    PRODUCTIVITY("Productivity", "#2196F3"),
    MINDFULNESS("Mindfulness", "#9C27B0"),
    SOCIAL("Social", "#FF9800"),
    LEARNING("Learning", "#607D8B"),
    GENERAL("General", "#795548")
}

/**
 * Chart data point for visualization
 */
data class ChartDataPoint(
    val label: String,
    val value: Float,
    val date: Date? = null,
    val color: String? = null
)

/**
 * Chart configuration for different chart types
 */
data class ChartConfig(
    val title: String,
    val type: ChartType,
    val dataPoints: List<ChartDataPoint>,
    val showLegend: Boolean = true,
    val showGrid: Boolean = true,
    val animate: Boolean = true,
    val colors: List<String> = emptyList()
)

/**
 * Chart types supported
 */
enum class ChartType {
    LINE_CHART,
    BAR_CHART,
    PIE_CHART,
    RADAR_CHART,
    COMBINED_CHART
}

/**
 * Time period for analytics
 */
enum class TimePeriod(val displayName: String, val days: Int) {
    WEEK("Last 7 Days", 7),
    MONTH("Last 30 Days", 30),
    QUARTER("Last 90 Days", 90),
    YEAR("Last 365 Days", 365),
    ALL_TIME("All Time", -1)
}
