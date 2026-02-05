package com.nutriwise.auratracks.utils

import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.models.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Utility class for processing habit data and generating chart configurations
 */
class HabitChartManager(private val prefsHelper: SharedPreferencesHelper) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val weekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    
    /**
     * Generate overall analytics for all habits
     */
    fun generateOverallAnalytics(timePeriod: TimePeriod = TimePeriod.MONTH): OverallAnalytics {
        val habits = prefsHelper.getHabits().filter { it.isActive }
        val completions = prefsHelper.getHabitCompletions()
        val filteredCompletions = filterCompletionsByTimePeriod(completions, timePeriod)
        
        val habitAnalytics = habits.map { habit ->
            generateHabitAnalytics(habit, filteredCompletions, timePeriod)
        }
        
        val totalCompletions = habitAnalytics.sumOf { it.totalCompletions }
        val averageCompletionRate = if (habitAnalytics.isNotEmpty()) {
            habitAnalytics.map { it.completionRate }.average().toFloat()
        } else 0f
        
        val bestPerformingHabit = habitAnalytics.maxByOrNull { it.completionRate }?.habitName
        val needsAttentionHabits = habitAnalytics
            .filter { it.completionRate < 50f && it.totalDays > 7 }
            .map { it.habitName }
        
        val weeklyTrend = calculateTrend(habitAnalytics, TimePeriod.WEEK)
        val monthlyTrend = calculateTrend(habitAnalytics, TimePeriod.MONTH)
        
        return OverallAnalytics(
            totalHabits = habits.size,
            activeHabits = habits.count { it.isActive },
            totalCompletions = totalCompletions,
            averageCompletionRate = averageCompletionRate,
            bestPerformingHabit = bestPerformingHabit,
            needsAttentionHabits = needsAttentionHabits,
            weeklyTrend = weeklyTrend,
            monthlyTrend = monthlyTrend,
            habitAnalytics = habitAnalytics
        )
    }
    
    /**
     * Generate analytics for a specific habit
     */
    fun generateHabitAnalytics(
        habit: Habit, 
        completions: List<HabitCompletion>, 
        timePeriod: TimePeriod
    ): HabitAnalytics {
        val habitCompletions = completions.filter { it.habitId == habit.id }
        val completedEntries = habitCompletions.filter { it.isCompleted }
        
        val totalCompletions = completedEntries.size
        val totalDays = if (timePeriod == TimePeriod.ALL_TIME) {
            calculateDaysSinceCreation(habit.createdAt)
        } else {
            timePeriod.days
        }
        
        val completionRate = if (totalDays > 0) {
            (totalCompletions.toFloat() / totalDays) * 100f
        } else 0f
        
        val currentStreak = calculateCurrentStreak(habitCompletions)
        val longestStreak = calculateLongestStreak(habitCompletions)
        val averagePerWeek = calculateAveragePerWeek(completedEntries, timePeriod)
        val lastCompleted = completedEntries.maxByOrNull { it.completedAt ?: Date(0) }?.completedAt
        
        val weeklyData = generateWeeklyData(habitCompletions, timePeriod)
        val monthlyData = generateMonthlyData(habitCompletions, timePeriod)
        
        return HabitAnalytics(
            habitId = habit.id,
            habitName = habit.name,
            totalCompletions = totalCompletions,
            totalDays = totalDays,
            completionRate = completionRate,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            averagePerWeek = averagePerWeek,
            lastCompleted = lastCompleted,
            weeklyData = weeklyData,
            monthlyData = monthlyData,
            category = determineHabitCategory(habit.name)
        )
    }
    
    /**
     * Generate line chart configuration for habit completion over time
     */
    fun generateCompletionLineChart(
        habitAnalytics: HabitAnalytics,
        timePeriod: TimePeriod
    ): ChartConfig {
        val dataPoints = when (timePeriod) {
            TimePeriod.WEEK -> generateDailyDataPoints(habitAnalytics)
            TimePeriod.MONTH -> generateWeeklyDataPoints(habitAnalytics)
            else -> generateMonthlyDataPoints(habitAnalytics)
        }
        
        return ChartConfig(
            title = "${habitAnalytics.habitName} - Completion Trend",
            type = ChartType.LINE_CHART,
            dataPoints = dataPoints,
            colors = listOf(habitAnalytics.category.color)
        )
    }
    
    /**
     * Generate bar chart configuration for weekly completion comparison
     */
    fun generateWeeklyBarChart(overallAnalytics: OverallAnalytics): ChartConfig {
        val dataPoints = overallAnalytics.habitAnalytics
            .sortedByDescending { it.completionRate }
            .take(5)
            .map { analytics ->
                ChartDataPoint(
                    label = analytics.habitName,
                    value = analytics.completionRate,
                    color = analytics.category.color
                )
            }
        
        return ChartConfig(
            title = "Top Performing Habits (This Month)",
            type = ChartType.BAR_CHART,
            dataPoints = dataPoints
        )
    }
    
    /**
     * Generate pie chart configuration for habit category distribution
     */
    fun generateCategoryPieChart(overallAnalytics: OverallAnalytics): ChartConfig {
        val categoryData = overallAnalytics.habitAnalytics
            .groupBy { it.category }
            .map { (category, analytics) ->
                val totalCompletions = analytics.sumOf { it.totalCompletions }
                ChartDataPoint(
                    label = category.displayName,
                    value = totalCompletions.toFloat(),
                    color = category.color
                )
            }
            .sortedByDescending { it.value }
        
        return ChartConfig(
            title = "Habits by Category",
            type = ChartType.PIE_CHART,
            dataPoints = categoryData
        )
    }
    
    /**
     * Generate radar chart configuration for habit performance comparison
     */
    fun generateHabitRadarChart(overallAnalytics: OverallAnalytics): ChartConfig {
        val dataPoints = overallAnalytics.habitAnalytics
            .take(6)
            .map { analytics ->
                ChartDataPoint(
                    label = analytics.habitName,
                    value = analytics.completionRate,
                    color = analytics.category.color
                )
            }
        
        return ChartConfig(
            title = "Habit Performance Comparison",
            type = ChartType.RADAR_CHART,
            dataPoints = dataPoints
        )
    }
    
    // Private helper methods
    
    private fun filterCompletionsByTimePeriod(
        completions: List<HabitCompletion>, 
        timePeriod: TimePeriod
    ): List<HabitCompletion> {
        if (timePeriod == TimePeriod.ALL_TIME) return completions
        
        val cutoffDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -timePeriod.days)
        }.time
        
        return completions.filter { completion ->
            try {
                val completionDate = dateFormat.parse(completion.date)
                completionDate != null && completionDate.after(cutoffDate)
            } catch (e: Exception) {
                false
            }
        }
    }
    
    private fun calculateDaysSinceCreation(createdAt: Date): Int {
        val now = Date()
        val diffInMillis = now.time - createdAt.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
    
    private fun calculateCurrentStreak(completions: List<HabitCompletion>): Int {
        val sortedCompletions = completions
            .filter { it.isCompleted }
            .sortedByDescending { it.date }
        
        if (sortedCompletions.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        var streak = 0
        var currentDate = calendar.time
        
        for (completion in sortedCompletions) {
            val completionDate = dateFormat.parse(completion.date) ?: continue
            
            if (isConsecutiveDay(currentDate, completionDate)) {
                streak++
                currentDate = completionDate
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun calculateLongestStreak(completions: List<HabitCompletion>): Int {
        val sortedCompletions = completions
            .filter { it.isCompleted }
            .sortedBy { it.date }
        
        if (sortedCompletions.isEmpty()) return 0
        
        var longestStreak = 1
        var currentStreak = 1
        
        for (i in 1 until sortedCompletions.size) {
            val prevDate = dateFormat.parse(sortedCompletions[i - 1].date) ?: continue
            val currentDate = dateFormat.parse(sortedCompletions[i].date) ?: continue
            
            if (isConsecutiveDay(prevDate, currentDate)) {
                currentStreak++
                longestStreak = max(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }
        
        return longestStreak
    }
    
    private fun isConsecutiveDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date1 }
        val calendar2 = Calendar.getInstance().apply { time = date2 }
        
        calendar1.add(Calendar.DAY_OF_YEAR, 1)
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun calculateAveragePerWeek(completions: List<HabitCompletion>, timePeriod: TimePeriod): Float {
        val weeks = when (timePeriod) {
            TimePeriod.WEEK -> 1f
            TimePeriod.MONTH -> 4.33f
            TimePeriod.QUARTER -> 13f
            TimePeriod.YEAR -> 52f
            TimePeriod.ALL_TIME -> {
                val oldestCompletion = completions.minByOrNull { it.completedAt ?: Date() }
                if (oldestCompletion != null) {
                    val daysSince = calculateDaysSinceCreation(oldestCompletion.completedAt ?: Date())
                    (daysSince / 7f).coerceAtLeast(1f)
                } else 1f
            }
        }
        
        return if (weeks > 0) completions.size / weeks else 0f
    }
    
    private fun generateWeeklyData(completions: List<HabitCompletion>, timePeriod: TimePeriod): List<WeeklyData> {
        val calendar = Calendar.getInstance()
        val weeks = mutableListOf<WeeklyData>()
        
        val weeksToShow = when (timePeriod) {
            TimePeriod.WEEK -> 1
            TimePeriod.MONTH -> 4
            TimePeriod.QUARTER -> 12
            TimePeriod.YEAR -> 52
            TimePeriod.ALL_TIME -> min(12, (completions.size / 7).coerceAtLeast(1))
        }
        
        for (i in 0 until weeksToShow) {
            calendar.add(Calendar.WEEK_OF_YEAR, -i)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val weekStart = calendar.time
            
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val weekEnd = calendar.time
            
            val weekCompletions = completions.filter { completion ->
                val completionDate = dateFormat.parse(completion.date)
                completionDate != null && completionDate.after(weekStart) && completionDate.before(weekEnd)
            }
            
            weeks.add(
                WeeklyData(
                    weekStart = weekStart,
                    weekEnd = weekEnd,
                    completions = weekCompletions.count { it.isCompleted },
                    totalDays = 7,
                    completionRate = (weekCompletions.count { it.isCompleted }.toFloat() / 7) * 100f
                )
            )
        }
        
        return weeks.reversed()
    }
    
    private fun generateMonthlyData(completions: List<HabitCompletion>, timePeriod: TimePeriod): List<MonthlyData> {
        val months = mutableListOf<MonthlyData>()
        val calendar = Calendar.getInstance()
        
        val monthsToShow = when (timePeriod) {
            TimePeriod.WEEK, TimePeriod.MONTH -> 1
            TimePeriod.QUARTER -> 3
            TimePeriod.YEAR -> 12
            TimePeriod.ALL_TIME -> min(12, (completions.size / 30).coerceAtLeast(1))
        }
        
        for (i in 0 until monthsToShow) {
            calendar.add(Calendar.MONTH, -i)
            val month = monthFormat.format(calendar.time)
            val year = calendar.get(Calendar.YEAR)
            val monthNumber = calendar.get(Calendar.MONTH) + 1
            
            val monthCompletions = completions.filter { completion ->
                completion.date.startsWith(month)
            }
            
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            
            months.add(
                MonthlyData(
                    month = month,
                    year = year,
                    monthNumber = monthNumber,
                    completions = monthCompletions.count { it.isCompleted },
                    totalDays = daysInMonth,
                    completionRate = (monthCompletions.count { it.isCompleted }.toFloat() / daysInMonth) * 100f
                )
            )
        }
        
        return months.reversed()
    }
    
    private fun generateDailyDataPoints(habitAnalytics: HabitAnalytics): List<ChartDataPoint> {
        val calendar = Calendar.getInstance()
        val dataPoints = mutableListOf<ChartDataPoint>()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            val dayName = weekFormat.format(calendar.time)
            
            val completions = habitAnalytics.weeklyData.flatMap { weekData ->
                // This is a simplified approach - in a real implementation,
                // you'd need to map daily data from your completion records
                emptyList<HabitCompletion>()
            }
            
            dataPoints.add(
                ChartDataPoint(
                    label = dayName,
                    value = if (completions.any { it.date == date && it.isCompleted }) 1f else 0f,
                    date = calendar.time
                )
            )
        }
        
        return dataPoints
    }
    
    private fun generateWeeklyDataPoints(habitAnalytics: HabitAnalytics): List<ChartDataPoint> {
        return habitAnalytics.weeklyData.map { weekData ->
            ChartDataPoint(
                label = "Week ${weekData.weekStart.date}",
                value = weekData.completionRate,
                date = weekData.weekStart
            )
        }
    }
    
    private fun generateMonthlyDataPoints(habitAnalytics: HabitAnalytics): List<ChartDataPoint> {
        return habitAnalytics.monthlyData.map { monthData ->
            ChartDataPoint(
                label = "${monthData.monthNumber}/${monthData.year}",
                value = monthData.completionRate,
                date = Calendar.getInstance().apply {
                    set(monthData.year, monthData.monthNumber - 1, 1)
                }.time
            )
        }
    }
    
    private fun calculateTrend(habitAnalytics: List<HabitAnalytics>, period: TimePeriod): TrendDirection {
        if (habitAnalytics.isEmpty()) return TrendDirection.NO_DATA
        
        val recentData = habitAnalytics.map { analytics ->
            when (period) {
                TimePeriod.WEEK -> analytics.weeklyData.lastOrNull()?.completionRate ?: 0f
                TimePeriod.MONTH -> analytics.monthlyData.lastOrNull()?.completionRate ?: 0f
                else -> analytics.completionRate
            }
        }
        
        val olderData = habitAnalytics.map { analytics ->
            when (period) {
                TimePeriod.WEEK -> analytics.weeklyData.getOrNull(analytics.weeklyData.size - 2)?.completionRate ?: 0f
                TimePeriod.MONTH -> analytics.monthlyData.getOrNull(analytics.monthlyData.size - 2)?.completionRate ?: 0f
                else -> analytics.completionRate
            }
        }
        
        val recentAvg = recentData.average()
        val olderAvg = olderData.average()
        
        return when {
            recentAvg > olderAvg + 5 -> TrendDirection.IMPROVING
            recentAvg < olderAvg - 5 -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }
    
    private fun determineHabitCategory(habitName: String): HabitCategory {
        val name = habitName.lowercase()
        return when {
            name.contains("exercise") || name.contains("workout") || name.contains("fitness") || 
            name.contains("run") || name.contains("walk") || name.contains("gym") -> HabitCategory.HEALTH
            name.contains("meditation") || name.contains("mindful") || name.contains("yoga") || 
            name.contains("breath") || name.contains("calm") -> HabitCategory.MINDFULNESS
            name.contains("read") || name.contains("learn") || name.contains("study") || 
            name.contains("course") || name.contains("book") -> HabitCategory.LEARNING
            name.contains("call") || name.contains("friend") || name.contains("family") || 
            name.contains("social") || name.contains("meet") -> HabitCategory.SOCIAL
            name.contains("work") || name.contains("task") || name.contains("project") || 
            name.contains("focus") || name.contains("productivity") -> HabitCategory.PRODUCTIVITY
            else -> HabitCategory.GENERAL
        }
    }
}
