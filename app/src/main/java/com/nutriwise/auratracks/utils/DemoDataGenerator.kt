package com.nutriwise.auratracks.utils

import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.models.Habit
import com.nutriwise.auratracks.models.HabitCompletion
import java.text.SimpleDateFormat
import java.util.*


object DemoDataGenerator {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    

    fun generateDemoData(prefsHelper: SharedPreferencesHelper) {
        try {

            if (prefsHelper.getHabits().isNotEmpty()) {
                return
            }
            

            val sampleHabits = listOf(
                Habit(
                    id = "habit_1",
                    name = "Morning Exercise",
                    description = "30 minutes of cardio or strength training",
                    createdAt = Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L), // 30 days ago
                    isActive = true
                ),
                Habit(
                    id = "habit_2",
                    name = "Read for 20 minutes",
                    description = "Read books, articles, or educational content",
                    createdAt = Date(System.currentTimeMillis() - 25 * 24 * 60 * 60 * 1000L), // 25 days ago
                    isActive = true
                ),
                Habit(
                    id = "habit_3",
                    name = "Meditation",
                    description = "10 minutes of mindfulness meditation",
                    createdAt = Date(System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000L), // 20 days ago
                    isActive = true
                ),
                Habit(
                    id = "habit_4",
                    name = "Drink 8 glasses of water",
                    description = "Stay hydrated throughout the day",
                    createdAt = Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L), // 15 days ago
                    isActive = true
                ),
                Habit(
                    id = "habit_5",
                    name = "Journal writing",
                    description = "Write thoughts and reflections",
                    createdAt = Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L), // 10 days ago
                    isActive = true
                )
            )
            

            prefsHelper.saveHabits(sampleHabits)
            

            val completions = mutableListOf<HabitCompletion>()
            val calendar = Calendar.getInstance()
            
            for (i in 0..29) { // Last 30 days
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val dateString = dateFormat.format(calendar.time)
                

                sampleHabits.forEach { habit ->
                    val completionRate = getCompletionRateForHabit(habit.name, i)
                    val isCompleted = Math.random() < completionRate
                    
                    if (isCompleted) {
                        completions.add(
                            HabitCompletion(
                                habitId = habit.id,
                                date = dateString,
                                isCompleted = true,
                                completedAt = Date(calendar.timeInMillis + (Math.random() * 24 * 60 * 60 * 1000).toLong())
                            )
                        )
                    } else {
                        completions.add(
                            HabitCompletion(
                                habitId = habit.id,
                                date = dateString,
                                isCompleted = false
                            )
                        )
                    }
                }
                
                calendar.add(Calendar.DAY_OF_YEAR, i) // Reset calendar
            }
            

            prefsHelper.saveHabitCompletions(completions)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun getCompletionRateForHabit(habitName: String, daysAgo: Int): Double {
        return when (habitName.lowercase()) {
            "morning exercise" -> {
                // Exercise habit - starts strong, dips in middle, improves recently
                when {
                    daysAgo > 20 -> 0.3 // Started weak
                    daysAgo > 10 -> 0.6 // Building momentum
                    else -> 0.8 // Recent improvement
                }
            }
            "read for 20 minutes" -> {
                // Reading habit - consistent but with some dips
                when {
                    daysAgo > 25 -> 0.4
                    daysAgo > 15 -> 0.7
                    daysAgo > 5 -> 0.5 // Had a dip
                    else -> 0.9 // Back on track
                }
            }
            "meditation" -> {
                // Meditation - steady improvement
                when {
                    daysAgo > 15 -> 0.2
                    daysAgo > 8 -> 0.5
                    else -> 0.7
                }
            }
            "drink 8 glasses of water" -> {
                // Water - most consistent habit
                when {
                    daysAgo > 10 -> 0.6
                    else -> 0.85
                }
            }
            "journal writing" -> {
                // Journaling - newer habit, building consistency
                when {
                    daysAgo > 7 -> 0.3
                    else -> 0.6
                }
            }
            else -> 0.5 // Default completion rate
        }
    }
    

    fun shouldGenerateDemoData(prefsHelper: SharedPreferencesHelper): Boolean {
        return prefsHelper.getHabits().isEmpty() && 
               prefsHelper.getBoolean("demo_data_generated", false).not()
    }
    

    fun markDemoDataGenerated(prefsHelper: SharedPreferencesHelper) {
        prefsHelper.saveBoolean("demo_data_generated", true)
    }
}
